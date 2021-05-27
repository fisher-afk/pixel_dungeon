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
package com.watabou.pixeldungeon.items.wands

import com.watabou.noosa.audio.Sample

abstract class Wand : KindOfWeapon() {
    var maxCharges = initialCharges()
    var curCharges = maxCharges
    protected var charger: Charger? = null
    private var curChargeKnown = false
    private var usagesToKnow = USAGES_TO_KNOW
    protected var hitChars = true
    private var wood: String? = null
    fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        if (curCharges > 0 || !curChargeKnown) {
            actions.add(AC_ZAP)
        }
        if (hero.heroClass !== HeroClass.MAGE) {
            actions.remove(AC_EQUIP)
            actions.remove(AC_UNEQUIP)
        }
        return actions
    }

    fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        onDetach()
        return super.doUnequip(hero, collect, single)
    }

    fun activate(hero: Hero?) {
        charge(hero)
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_ZAP) {
            curUser = hero
            curItem = this
            GameScene.selectCell(zapper)
        } else {
            super.execute(hero, action)
        }
    }

    protected abstract fun onZap(cell: Int)
    fun collect(container: Bag): Boolean {
        return if (super.collect(container)) {
            if (container.owner != null) {
                charge(container.owner)
            }
            true
        } else {
            false
        }
    }

    fun charge(owner: Char?) {
        if (charger == null) {
            Charger().also { charger = it }.attachTo(owner)
        }
    }

    fun onDetach() {
        stopCharging()
    }

    fun stopCharging() {
        if (charger != null) {
            charger.detach()
            charger = null
        }
    }

    fun power(): Int {
        val eLevel: Int = effectiveLevel()
        return if (charger != null) {
            val power: Power = charger.target.buff(Power::class.java)
            if (power == null) eLevel else Math.max(eLevel + power.level, 0)
        } else {
            eLevel
        }
    }

    protected val isKnown: Boolean
        protected get() = handler.isKnown(this)

    fun setKnown() {
        if (!isKnown) {
            handler.know(this)
        }
        Badges.validateAllWandsIdentified()
    }

    fun identify(): Item {
        setKnown()
        curChargeKnown = true
        super.identify()
        updateQuickslot()
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder(super.toString())
        val status = status()
        if (status != null) {
            sb.append(" ($status)")
        }
        if (isBroken()) {
            sb.insert(0, "broken ")
        }
        return sb.toString()
    }

    fun name(): String {
        return if (isKnown) name else "$wood wand"
    }

    fun info(): String {
        val info = StringBuilder(if (isKnown) desc() else String.format(TXT_WOOD, wood))
        if (Dungeon.hero.heroClass === HeroClass.MAGE) {
            info.append("\n\n")
            if (levelKnown) {
                val min = min()
                info.append(String.format(TXT_DAMAGE, min + (max() - min) / 2))
            } else {
                info.append(String.format(TXT_WEAPON))
            }
        }
        return info.toString()
    }

    val isIdentified: Boolean
        get() = super.isIdentified() && isKnown && curChargeKnown

    fun status(): String? {
        return if (levelKnown) {
            (if (curChargeKnown) curCharges else "?").toString() + "/" + maxCharges
        } else {
            null
        }
    }

    fun upgrade(): Item {
        super.upgrade()
        updateLevel()
        curCharges = Math.min(curCharges + 1, maxCharges)
        updateQuickslot()
        return this
    }

    fun degrade(): Item {
        super.degrade()
        updateLevel()
        updateQuickslot()
        return this
    }

    fun maxDurability(lvl: Int): Int {
        return 6 * if (lvl < 16) 16 - lvl else 1
    }

    protected fun updateLevel() {
        maxCharges = Math.min(initialCharges() + level(), 9)
        curCharges = Math.min(curCharges, maxCharges)
    }

    protected fun initialCharges(): Int {
        return 2
    }

    fun min(): Int {
        return 1 + effectiveLevel() / 3
    }

    fun max(): Int {
        val level: Int = effectiveLevel()
        val tier = 1 + level / 3
        return (tier * tier - tier + 10) / 2 + level
    }

    protected fun fx(cell: Int, callback: Callback?) {
        MagicMissile.blueLight(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    protected fun wandUsed() {
        curCharges--
        if (!isIdentified && --usagesToKnow <= 0) {
            identify()
            GLog.w(TXT_IDENTIFY, name())
        } else {
            updateQuickslot()
        }
        use()
        curUser.spendAndNext(TIME_TO_ZAP)
    }

    fun random(): Item {
        if (Random.Float() < 0.5f) {
            upgrade()
            if (Random.Float() < 0.15f) {
                upgrade()
            }
        }
        return this
    }

    fun price(): Int {
        return considerState(50)
    }

    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, usagesToKnow)
        bundle.put(MAX_CHARGES, maxCharges)
        bundle.put(CUR_CHARGES, curCharges)
        bundle.put(CUR_CHARGE_KNOWN, curChargeKnown)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (bundle.getInt(UNFAMILIRIARITY).also { usagesToKnow = it } == 0) {
            usagesToKnow = USAGES_TO_KNOW
        }
        maxCharges = bundle.getInt(MAX_CHARGES)
        curCharges = bundle.getInt(CUR_CHARGES)
        curChargeKnown = bundle.getBoolean(CUR_CHARGE_KNOWN)
    }

    protected inner class Charger : Buff() {
        fun attachTo(target: Char?): Boolean {
            super.attachTo(target)
            delay()
            return true
        }

        fun act(): Boolean {
            if (curCharges < maxCharges) {
                curCharges++
                updateQuickslot()
            }
            delay()
            return true
        }

        protected fun delay() {
            val time2charge =
                if ((target as Hero).heroClass === HeroClass.MAGE) Companion.TIME_TO_CHARGE / Math.sqrt(1 + effectiveLevel())
                    .toFloat() else Companion.TIME_TO_CHARGE
            spend(time2charge)
        }

        companion object {
            private const val TIME_TO_CHARGE = 40f
        }
    }

    companion object {
        private const val USAGES_TO_KNOW = 40
        const val AC_ZAP = "ZAP"
        private const val TXT_WOOD = "This thin %s wand is warm to the touch. Who knows what it will do when used?"
        private const val TXT_DAMAGE =
            "When this wand is used as a melee weapon, its average damage is %d points per hit."
        private const val TXT_WEAPON = "You can use this wand as a melee weapon."
        private const val TXT_FIZZLES = "your wand fizzles; it must be out of charges for now"
        private const val TXT_SELF_TARGET = "You can't target yourself"
        private const val TXT_IDENTIFY = "You are now familiar enough with your %s."
        private const val TIME_TO_ZAP = 1f
        private val wands = arrayOf<Class<*>>(
            WandOfTeleportation::class.java,
            WandOfSlowness::class.java,
            WandOfFirebolt::class.java,
            WandOfPoison::class.java,
            WandOfRegrowth::class.java,
            WandOfBlink::class.java,
            WandOfLightning::class.java,
            WandOfAmok::class.java,
            WandOfReach::class.java,
            WandOfFlock::class.java,
            WandOfDisintegration::class.java,
            WandOfAvalanche::class.java
        )
        private val woods = arrayOf(
            "holly",
            "yew",
            "ebony",
            "cherry",
            "teak",
            "rowan",
            "willow",
            "mahogany",
            "bamboo",
            "purpleheart",
            "oak",
            "birch"
        )
        private val images = arrayOf<Int>(
            ItemSpriteSheet.WAND_HOLLY,
            ItemSpriteSheet.WAND_YEW,
            ItemSpriteSheet.WAND_EBONY,
            ItemSpriteSheet.WAND_CHERRY,
            ItemSpriteSheet.WAND_TEAK,
            ItemSpriteSheet.WAND_ROWAN,
            ItemSpriteSheet.WAND_WILLOW,
            ItemSpriteSheet.WAND_MAHOGANY,
            ItemSpriteSheet.WAND_BAMBOO,
            ItemSpriteSheet.WAND_PURPLEHEART,
            ItemSpriteSheet.WAND_OAK,
            ItemSpriteSheet.WAND_BIRCH
        )
        private var handler: ItemStatusHandler<Wand>? = null
        fun initWoods() {
            handler = ItemStatusHandler<Wand>(wands as Array<Class<out Wand?>>, woods, images)
        }

        fun save(bundle: Bundle?) {
            handler.save(bundle)
        }

        fun restore(bundle: Bundle?) {
            handler = ItemStatusHandler<Wand>(wands as Array<Class<out Wand?>>, woods, images, bundle)
        }

        fun allKnown(): Boolean {
            return handler.known().size() === wands.size
        }

        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val MAX_CHARGES = "maxCharges"
        private const val CUR_CHARGES = "curCharges"
        private const val CUR_CHARGE_KNOWN = "curChargeKnown"
        protected var zapper: CellSelector.Listener = object : Listener() {
            fun onSelect(target: Int?) {
                if (target != null) {
                    if (target === curUser.pos) {
                        GLog.i(TXT_SELF_TARGET)
                        return
                    }
                    val curWand = curItem as Wand
                    curWand.setKnown()
                    val cell: Int = Ballistica.cast(curUser.pos, target, true, curWand.hitChars)
                    curUser.sprite.zap(cell)
                    QuickSlot.target(curItem, Actor.findChar(cell))
                    if (curWand.curCharges > 0) {
                        curUser.busy()
                        curWand.fx(cell, object : Callback() {
                            fun call() {
                                curWand.onZap(cell)
                                curWand.wandUsed()
                            }
                        })
                        Invisibility.dispel()
                    } else {
                        curUser.spendAndNext(TIME_TO_ZAP)
                        GLog.w(TXT_FIZZLES)
                        curWand.levelKnown = true
                        curWand.updateQuickslot()
                    }
                }
            }

            fun prompt(): String {
                return "Choose direction to zap"
            }
        }
    }

    init {
        defaultAction = AC_ZAP
    }

    init {
        try {
            image = handler.image(this)
            wood = handler.label(this)
        } catch (e: Exception) {
            // Wand of Magic Missile
        }
    }
}