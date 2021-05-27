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
package com.watabou.pixeldungeon.items.rings

import com.watabou.pixeldungeon.Badges

class Ring : EquipableItem() {
    protected var buff: Buff? = null
    private var gem: String? = null
    private var ticksToKnow = TICKS_TO_KNOW
    fun syncGem() {
        image = handler.image(this)
        gem = handler.label(this)
    }

    fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }

    fun doEquip(hero: Hero): Boolean {
        return if (hero.belongings.ring1 != null && hero.belongings.ring2 != null) {
            val r1: Ring = hero.belongings.ring1
            val r2: Ring = hero.belongings.ring2
            PixelDungeon.scene().add(
                object : WndOptions(
                    TXT_UNEQUIP_TITLE, TXT_UNEQUIP_MESSAGE,
                    Utils.capitalize(r1.toString()),
                    Utils.capitalize(r2.toString())
                ) {
                    protected fun onSelect(index: Int) {
                        detach(hero.belongings.backpack)
                        val equipped = if (index == 0) r1 else r2
                        if (equipped.doUnequip(hero, true, false)) {
                            doEquip(hero)
                        } else {
                            collect(hero.belongings.backpack)
                        }
                    }
                })
            false
        } else {
            if (hero.belongings.ring1 == null) {
                hero.belongings.ring1 = this
            } else {
                hero.belongings.ring2 = this
            }
            detach(hero.belongings.backpack)
            activate(hero)
            cursedKnown = true
            if (cursed) {
                equipCursed(hero)
                GLog.n("your $this tightens around your finger painfully")
            }
            hero.spendAndNext(TIME_TO_EQUIP)
            true
        }
    }

    fun activate(ch: Char?) {
        buff = buff()
        buff.attachTo(ch)
    }

    fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        return if (super.doUnequip(hero, collect, single)) {
            if (hero.belongings.ring1 === this) {
                hero.belongings.ring1 = null
            } else {
                hero.belongings.ring2 = null
            }
            hero.remove(buff)
            buff = null
            true
        } else {
            false
        }
    }

    fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.ring1 === this || hero.belongings.ring2 === this
    }

    fun effectiveLevel(): Int {
        return if (isBroken()) 1 else level()
    }

    private fun renewBuff() {
        if (buff != null) {
            val owner: Char = buff.target
            buff.detach()
            if (buff().also { buff = it } != null) {
                buff.attachTo(owner)
            }
        }
    }

    val broken: Unit
        get() {
            renewBuff()
            super.getBroken()
        }

    fun fix() {
        super.fix()
        renewBuff()
    }

    fun maxDurability(lvl: Int): Int {
        return if (lvl <= 1) {
            Int.MAX_VALUE
        } else {
            100 * if (lvl < 16) 16 - lvl else 1
        }
    }

    val isKnown: Boolean
        get() = handler.isKnown(this)

    protected fun setKnown() {
        if (!isKnown) {
            handler.know(this)
        }
        Badges.validateAllRingsIdentified()
    }

    override fun toString(): String {
        return if (levelKnown && isBroken()) "broken " + super.toString() else super.toString()
    }

    fun name(): String {
        return if (isKnown) name else "$gem ring"
    }

    fun desc(): String {
        return "This metal band is adorned with a large " + gem + " gem " +
                "that glitters in the darkness. Who knows what effect it has when worn?"
    }

    fun info(): String {
        return if (isEquipped(Dungeon.hero)) {
            """${desc()}

The ${name()} is on your finger${if (cursed) ", and because it is cursed, you are powerless to remove it." else "."}"""
        } else if (cursed && cursedKnown) {
            """
     ${desc()}
     
     You can feel a malevolent magic lurking within the ${name()}.
     """.trimIndent()
        } else {
            desc()
        }
    }

    val isIdentified: Boolean
        get() = super.isIdentified() && isKnown

    fun identify(): Item {
        setKnown()
        return super.identify()
    }

    fun random(): Item {
        val lvl: Int = Random.Int(1, 3)
        if (Random.Float() < 0.3f) {
            degrade(lvl)
            cursed = true
        } else {
            upgrade(lvl)
        }
        return this
    }

    fun price(): Int {
        return considerState(80)
    }

    protected fun buff(): RingBuff? {
        return null
    }

    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, ticksToKnow)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (bundle.getInt(UNFAMILIRIARITY).also { ticksToKnow = it } == 0) {
            ticksToKnow = TICKS_TO_KNOW
        }
    }

    inner class RingBuff : Buff() {
        var level: Int
        fun attachTo(target: Char): Boolean {
            if (target is Hero && (target as Hero).heroClass === HeroClass.ROGUE && !isKnown) {
                setKnown()
                GLog.i(Companion.TXT_KNOWN, name())
                Badges.validateItemLevelAquired(this@Ring)
            }
            return super.attachTo(target)
        }

        fun act(): Boolean {
            if (!isIdentified && --ticksToKnow <= 0) {
                val gemName = name()
                identify()
                GLog.w(TXT_IDENTIFY, gemName, this@Ring.toString())
                Badges.validateItemLevelAquired(this@Ring)
            }
            use()
            spend(TICK)
            return true
        }

        companion object {
            private const val TXT_KNOWN = "This is a %s"
        }

        init {
            level = effectiveLevel()
        }
    }

    companion object {
        private const val TICKS_TO_KNOW = 200
        private const val TIME_TO_EQUIP = 1f
        private const val TXT_IDENTIFY = "you are now familiar enough with your %s to identify it. It is %s."
        private const val TXT_UNEQUIP_TITLE = "Unequip one ring"
        private const val TXT_UNEQUIP_MESSAGE = "You can only wear two rings at a time. " +
                "Unequip one of your equipped rings."
        private val rings = arrayOf<Class<*>>(
            RingOfMending::class.java,
            RingOfDetection::class.java,
            RingOfShadows::class.java,
            RingOfPower::class.java,
            RingOfHerbalism::class.java,
            RingOfAccuracy::class.java,
            RingOfEvasion::class.java,
            RingOfSatiety::class.java,
            RingOfHaste::class.java,
            RingOfHaggler::class.java,
            RingOfElements::class.java,
            RingOfThorns::class.java
        )
        private val gems = arrayOf(
            "diamond",
            "opal",
            "garnet",
            "ruby",
            "amethyst",
            "topaz",
            "onyx",
            "tourmaline",
            "emerald",
            "sapphire",
            "quartz",
            "agate"
        )
        private val images = arrayOf<Int>(
            ItemSpriteSheet.RING_DIAMOND,
            ItemSpriteSheet.RING_OPAL,
            ItemSpriteSheet.RING_GARNET,
            ItemSpriteSheet.RING_RUBY,
            ItemSpriteSheet.RING_AMETHYST,
            ItemSpriteSheet.RING_TOPAZ,
            ItemSpriteSheet.RING_ONYX,
            ItemSpriteSheet.RING_TOURMALINE,
            ItemSpriteSheet.RING_EMERALD,
            ItemSpriteSheet.RING_SAPPHIRE,
            ItemSpriteSheet.RING_QUARTZ,
            ItemSpriteSheet.RING_AGATE
        )
        private var handler: ItemStatusHandler<Ring>? = null
        fun initGems() {
            handler = ItemStatusHandler<Ring>(rings as Array<Class<out Ring?>>, gems, images)
        }

        fun save(bundle: Bundle?) {
            handler.save(bundle)
        }

        fun restore(bundle: Bundle?) {
            handler = ItemStatusHandler<Ring>(rings as Array<Class<out Ring?>>, gems, images, bundle)
        }

        fun allKnown(): Boolean {
            return handler.known().size() === rings.size - 2
        }

        private const val UNFAMILIRIARITY = "unfamiliarity"
    }

    init {
        syncGem()
    }
}