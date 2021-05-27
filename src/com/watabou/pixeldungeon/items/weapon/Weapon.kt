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
package com.watabou.pixeldungeon.items.weapon

import com.watabou.pixeldungeon.Badges

abstract class Weapon : KindOfWeapon() {
    var STR = 10
    var ACU = 1f
    var DLY = 1f

    enum class Imbue {
        NONE, SPEED, ACCURACY
    }

    var imbue = Imbue.NONE
    private var hitsToKnow = HITS_TO_KNOW
    protected var enchantment: Enchantment? = null
    fun proc(attacker: Char?, defender: Char?, damage: Int) {
        if (enchantment != null) {
            enchantment!!.proc(this, attacker, defender, damage)
        }
        if (!levelKnown) {
            if (--hitsToKnow <= 0) {
                levelKnown = true
                GLog.i(TXT_IDENTIFY, name(), toString())
                Badges.validateItemLevelAquired(this)
            }
        }
        use()
    }

    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, hitsToKnow)
        bundle.put(ENCHANTMENT, enchantment)
        bundle.put(IMBUE, imbue)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (bundle.getInt(UNFAMILIRIARITY).also { hitsToKnow = it } == 0) {
            hitsToKnow = HITS_TO_KNOW
        }
        enchantment = bundle.get(ENCHANTMENT)
        imbue = bundle.getEnum(IMBUE, Imbue::class.java)
    }

    fun acuracyFactor(hero: Hero): Float {
        var encumbrance: Int = STR - hero.STR()
        if (this is MissileWeapon) {
            when (hero.heroClass) {
                WARRIOR -> encumbrance += 3
                HUNTRESS -> encumbrance -= 2
                else -> {
                }
            }
        }
        return (if (encumbrance > 0) (ACU / Math.pow(1.5, encumbrance.toDouble())).toFloat() else ACU) *
                if (imbue == Imbue.ACCURACY) 1.5f else 1.0f
    }

    fun speedFactor(hero: Hero): Float {
        var encumrance: Int = STR - hero.STR()
        if (this is MissileWeapon && hero.heroClass === HeroClass.HUNTRESS) {
            encumrance -= 2
        }
        return (if (encumrance > 0) (DLY * Math.pow(1.2, encumrance.toDouble())).toFloat() else DLY) *
                if (imbue == Imbue.SPEED) 0.6f else 1.0f
    }

    fun damageRoll(hero: Hero): Int {
        var damage: Int = super.damageRoll(hero)
        if (hero.rangedWeapon != null == (hero.heroClass === HeroClass.HUNTRESS)) {
            val exStr: Int = hero.STR() - STR
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr)
            }
        }
        return damage
    }

    fun upgrade(enchant: Boolean): Item {
        if (enchantment != null) {
            if (!enchant && Random.Int(level()) > 0) {
                GLog.w(TXT_INCOMPATIBLE)
                enchant(null)
            }
        } else {
            if (enchant) {
                enchant()
            }
        }
        return super.upgrade()
    }

    fun maxDurability(lvl: Int): Int {
        return 5 * if (lvl < 16) 16 - lvl else 1
    }

    override fun toString(): String {
        return if (levelKnown) Utils.format(
            if (isBroken()) TXT_BROKEN else TXT_TO_STRING,
            super.toString(),
            STR
        ) else super.toString()
    }

    fun name(): String {
        return if (enchantment == null) super.name() else enchantment!!.name(super.name())
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
        return this
    }

    fun enchant(ench: Enchantment?): Weapon {
        enchantment = ench
        return this
    }

    fun enchant(): Weapon {
        val oldEnchantment: Class<out Enchantment>? = if (enchantment != null) enchantment!!.javaClass else null
        var ench = Enchantment.random()
        while (ench!!.javaClass == oldEnchantment) {
            ench = Enchantment.random()
        }
        return enchant(ench)
    }

    val isEnchanted: Boolean
        get() = enchantment != null

    fun glowing(): ItemSprite.Glowing? {
        return if (enchantment != null) enchantment!!.glowing() else null
    }

    abstract class Enchantment : Bundlable {
        abstract fun proc(weapon: Weapon?, attacker: Char?, defender: Char?, damage: Int): Boolean
        fun name(weaponName: String): String {
            return weaponName
        }

        fun restoreFromBundle(bundle: Bundle?) {}
        fun storeInBundle(bundle: Bundle?) {}
        fun glowing(): ItemSprite.Glowing {
            return ItemSprite.Glowing.WHITE
        }

        companion object {
            private val enchants = arrayOf<Class<*>>(
                Fire::class.java, Poison::class.java, Death::class.java, Paralysis::class.java, Leech::class.java,
                Slow::class.java, Shock::class.java, Instability::class.java, Horror::class.java, Luck::class.java,
                Tempering::class.java
            )
            private val chances = floatArrayOf(10f, 10f, 1f, 2f, 1f, 2f, 6f, 3f, 2f, 2f, 3f)
            fun random(): Enchantment? {
                return try {
                    (enchants[Random.chances(chances)] as Class<Enchantment?>).newInstance()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    companion object {
        private const val HITS_TO_KNOW = 20
        private const val TXT_IDENTIFY = "You are now familiar enough with your %s to identify it. It is %s."
        private const val TXT_INCOMPATIBLE =
            "Interaction of different types of magic has negated the enchantment on this weapon!"
        private const val TXT_TO_STRING = "%s :%d"
        private const val TXT_BROKEN = "broken %s :%d"
        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val ENCHANTMENT = "enchantment"
        private const val IMBUE = "imbue"
    }
}