/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.items.armor

import com.watabou.pixeldungeon.Badges

class Armor(var tier: Int) : EquipableItem() {
    var STR: Int
    private var hitsToKnow = HITS_TO_KNOW
    var glyph: Glyph? = null
    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, hitsToKnow)
        bundle.put(GLYPH, glyph)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (bundle.getInt(UNFAMILIRIARITY).also { hitsToKnow = it } == 0) {
            hitsToKnow = HITS_TO_KNOW
        }
        inscribe(bundle.get(GLYPH) as Glyph)
    }

    fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }

    fun doEquip(hero: Hero): Boolean {
        detach(hero.belongings.backpack)
        return if (hero.belongings.armor == null || hero.belongings.armor.doUnequip(hero, true, false)) {
            hero.belongings.armor = this
            cursedKnown = true
            if (cursed) {
                equipCursed(hero)
                GLog.n(TXT_EQUIP_CURSED, toString())
            }
            (hero.sprite as HeroSprite).updateArmor()
            hero.spendAndNext(time2equip(hero))
            true
        } else {
            collect(hero.belongings.backpack)
            false
        }
    }

    protected fun time2equip(hero: Hero): Float {
        return 2 / hero.speed()
    }

    fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        return if (super.doUnequip(hero, collect, single)) {
            hero.belongings.armor = null
            (hero.sprite as HeroSprite).updateArmor()
            true
        } else {
            false
        }
    }

    fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.armor === this
    }

    fun DR(): Int {
        return tier * (2 + effectiveLevel() + if (glyph == null) 0 else 1)
    }

    fun upgrade(): Item {
        return upgrade(false)
    }

    fun upgrade(inscribe: Boolean): Item {
        if (glyph != null) {
            if (!inscribe && Random.Int(level()) > 0) {
                GLog.w(TXT_INCOMPATIBLE)
                inscribe(null)
            }
        } else {
            if (inscribe) {
                inscribe()
            }
        }
        STR--
        return super.upgrade()
    }

    fun safeUpgrade(): Item {
        return upgrade(glyph != null)
    }

    fun degrade(): Item {
        STR++
        return super.degrade()
    }

    fun maxDurability(lvl: Int): Int {
        return 6 * if (lvl < 16) 16 - lvl else 1
    }

    fun proc(attacker: Char?, defender: Char?, damage: Int): Int {
        var damage = damage
        if (glyph != null) {
            damage = glyph!!.proc(this, attacker, defender, damage)
        }
        if (!levelKnown) {
            if (--hitsToKnow <= 0) {
                levelKnown = true
                GLog.w(TXT_IDENTIFY, name(), toString())
                Badges.validateItemLevelAquired(this)
            }
        }
        use()
        return damage
    }

    override fun toString(): String {
        return if (levelKnown) Utils.format(
            if (isBroken()) TXT_BROKEN else TXT_TO_STRING,
            super.toString(),
            STR
        ) else super.toString()
    }

    fun name(): String {
        return if (glyph == null) super.name() else glyph!!.name(super.name())
    }

    fun info(): String {
        val name = name()
        val info: StringBuilder = StringBuilder(desc())
        if (levelKnown) {
            info.append(
                """

This $name provides damage absorption up to ${Math.max(DR(), 0)} points per attack. """
            )
            if (STR > Dungeon.hero.STR()) {
                if (isEquipped(Dungeon.hero)) {
                    info.append(
                        """
                            
                            
                            Because of your inadequate strength your movement speed and defense skill is decreased. 
                            """.trimIndent()
                    )
                } else {
                    info.append(
                        """
                            
                            
                            Because of your inadequate strength wearing this armor will decrease your movement speed and defense skill. 
                            """.trimIndent()
                    )
                }
            }
        } else {
            info.append(
                """

Typical $name provides damage absorption up to ${typicalDR()} points per attack  and requires ${typicalSTR()} points of strength. """
            )
            if (typicalSTR() > Dungeon.hero.STR()) {
                info.append("Probably this armor is too heavy for you. ")
            }
        }
        if (glyph != null) {
            info.append("It is enchanted.")
        }
        if (isEquipped(Dungeon.hero)) {
            info.append(
                """
    
    
    You are wearing the $name${if (cursed) ", and because it is cursed, you are powerless to remove it." else "."}
    """.trimIndent()
            )
        } else {
            if (cursedKnown && cursed) {
                info.append("\n\nYou can feel a malevolent magic lurking within the $name.")
            }
        }
        return info.toString()
    }

    fun random(): Item {
        if (Random.Float() < 0.4) {
            var n = 1
            if (Random.Int(3) === 0) {
                n++
                if (Random.Int(3) === 0) {
                    n++
                }
            }
            if (Random.Int(2) === 0) {
                upgrade(n.toBoolean())
            } else {
                degrade(n)
                cursed = true
            }
        }
        if (Random.Int(10) === 0) {
            inscribe()
        }
        return this
    }

    fun typicalSTR(): Int {
        return 7 + tier * 2
    }

    fun typicalDR(): Int {
        return tier * 2
    }

    fun price(): Int {
        var price = 10 * (1 shl tier - 1)
        if (glyph != null) {
            (price *= 1.5).toInt()
        }
        return considerState(price)
    }

    fun inscribe(glyph: Glyph?): Armor {
        this.glyph = glyph
        return this
    }

    fun inscribe(): Armor {
        val oldGlyphClass: Class<out Glyph>? = if (glyph != null) glyph!!.javaClass else null
        var gl = Glyph.random()
        while (gl!!.javaClass == oldGlyphClass) {
            gl = Glyph.random()
        }
        return inscribe(gl)
    }

    val isInscribed: Boolean
        get() = glyph != null

    fun glowing(): ItemSprite.Glowing? {
        return if (glyph != null) glyph!!.glowing() else null
    }

    abstract class Glyph : Bundlable {
        abstract fun proc(armor: Armor?, attacker: Char?, defender: Char?, damage: Int): Int
        fun name(armorName: String): String {
            return armorName
        }

        fun restoreFromBundle(bundle: Bundle?) {}
        fun storeInBundle(bundle: Bundle?) {}
        fun glowing(): ItemSprite.Glowing {
            return ItemSprite.Glowing.WHITE
        }

        fun checkOwner(owner: Char): Boolean {
            return if (!owner.isAlive() && owner is Hero) {
                (owner as Hero).killerGlyph = this
                Badges.validateDeathFromGlyph()
                true
            } else {
                false
            }
        }

        companion object {
            private val glyphs = arrayOf<Class<*>>(
                Bounce::class.java, Affection::class.java, AntiEntropy::class.java, Multiplicity::class.java,
                Potential::class.java, Metabolism::class.java, Stench::class.java, Viscosity::class.java,
                Displacement::class.java, Entanglement::class.java, AutoRepair::class.java
            )
            private val chances = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            fun random(): Glyph? {
                return try {
                    (glyphs[Random.chances(chances)] as Class<Glyph?>).newInstance()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    companion object {
        private const val HITS_TO_KNOW = 10
        private const val TXT_EQUIP_CURSED = "your %s constricts around you painfully"
        private const val TXT_IDENTIFY = "you are now familiar enough with your %s to identify it. It is %s."
        private const val TXT_TO_STRING = "%s :%d"
        private const val TXT_BROKEN = "broken %s :%d"
        private const val TXT_INCOMPATIBLE =
            "Interaction of different types of magic has erased the glyph on this armor!"
        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val GLYPH = "glyph"
    }

    init {
        STR = typicalSTR()
    }
}