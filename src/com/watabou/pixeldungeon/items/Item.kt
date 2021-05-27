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
package com.watabou.pixeldungeon.items

import com.watabou.noosa.audio.Sample

class Item : Bundlable {
    var defaultAction: String? = null
    protected var name = "smth"
    protected var image = 0
    var stackable = false
    var quantity = 1
    private var level = 0
    private var durability = maxDurability()
    var levelKnown = false
    var cursed = false
    var cursedKnown = false
    var unique = false
    fun actions(hero: Hero?): ArrayList<String> {
        val actions = ArrayList<String>()
        actions.add(AC_DROP)
        actions.add(AC_THROW)
        return actions
    }

    fun doPickUp(hero: Hero): Boolean {
        return if (collect(hero.belongings.backpack)) {
            GameScene.pickUp(this)
            Sample.INSTANCE.play(Assets.SND_ITEM)
            hero.spendAndNext(TIME_TO_PICK_UP)
            true
        } else {
            false
        }
    }

    fun doDrop(hero: Hero) {
        hero.spendAndNext(TIME_TO_DROP)
        Dungeon.level.drop(detachAll(hero.belongings.backpack), hero.pos).sprite.drop(hero.pos)
    }

    fun doThrow(hero: Hero?) {
        GameScene.selectCell(thrower)
    }

    fun execute(hero: Hero, action: String?) {
        curUser = hero
        curItem = this
        if (action == AC_DROP) {
            doDrop(hero)
        } else if (action == AC_THROW) {
            doThrow(hero)
        }
    }

    fun execute(hero: Hero) {
        execute(hero, defaultAction)
    }

    protected fun onThrow(cell: Int) {
        val heap: Heap = Dungeon.level.drop(this, cell)
        if (!heap.isEmpty()) {
            heap.sprite.drop(cell)
        }
    }

    @JvmOverloads
    fun collect(container: Bag = Dungeon.hero.belongings.backpack): Boolean {
        val items: ArrayList<Item> = container.items
        if (items.contains(this)) {
            return true
        }
        for (item in items) {
            if (item is Bag && (item as Bag).grab(this)) {
                return collect(item as Bag)
            }
        }
        if (stackable) {
            val c: Class<*> = javaClass
            for (item in items) {
                if (item.javaClass == c) {
                    item.quantity += quantity
                    item.updateQuickslot()
                    return true
                }
            }
        }
        return if (items.size < container.size) {
            if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
                Badges.validateItemLevelAquired(this)
            }
            items.add(this)
            QuickSlot.refresh()
            Collections.sort(items, itemComparator)
            true
        } else {
            GLog.n(TXT_PACK_FULL, name())
            false
        }
    }

    fun detach(container: Bag): Item? {
        return if (quantity <= 0) {
            null
        } else if (quantity == 1) {
            detachAll(container)
        } else {
            quantity--
            updateQuickslot()
            try {
                val detached = javaClass.newInstance()
                detached.onDetach()
                detached
            } catch (e: Exception) {
                null
            }
        }
    }

    fun detachAll(container: Bag): Item {
        for (item in container.items) {
            if (item === this) {
                container.items.remove(this)
                item.onDetach()
                QuickSlot.refresh()
                return this
            } else if (item is Bag) {
                val bag: Bag = item as Bag
                if (bag.contains(this)) {
                    return detachAll(bag)
                }
            }
        }
        return this
    }

    protected fun onDetach() {}
    fun level(): Int {
        return level
    }

    fun level(value: Int) {
        level = value
    }

    fun effectiveLevel(): Int {
        return if (isBroken) 0 else level
    }

    fun upgrade(): Item {
        cursed = false
        cursedKnown = true
        level++
        fix()
        return this
    }

    fun upgrade(n: Int): Item {
        for (i in 0 until n) {
            upgrade()
        }
        return this
    }

    fun degrade(): Item {
        level--
        fix()
        return this
    }

    fun degrade(n: Int): Item {
        for (i in 0 until n) {
            degrade()
        }
        return this
    }

    fun use() {
        if (level > 0 && !isBroken) {
            val threshold = (maxDurability() * DURABILITY_WARNING_LEVEL).toInt()
            if (durability-- >= threshold && threshold > durability && levelKnown) {
                GLog.w(TXT_GONNA_BREAK, name())
            }
            if (isBroken) {
                getBroken()
                if (levelKnown) {
                    GLog.n(TXT_BROKEN, name())
                    Dungeon.hero.interrupt()
                    val sprite: CharSprite = Dungeon.hero.sprite
                    val point: PointF = sprite.center().offset(0, -16)
                    if (this is Weapon) {
                        sprite.parent.add(Degradation.weapon(point))
                    } else if (this is Armor) {
                        sprite.parent.add(Degradation.armor(point))
                    } else if (this is Ring) {
                        sprite.parent.add(Degradation.ring(point))
                    } else if (this is Wand) {
                        sprite.parent.add(Degradation.wand(point))
                    }
                    Sample.INSTANCE.play(Assets.SND_DEGRADE)
                }
            }
        }
    }

    val isBroken: Boolean
        get() = durability <= 0

    fun getBroken() {}
    fun fix() {
        durability = maxDurability()
    }

    fun polish() {
        if (durability < maxDurability()) {
            durability++
        }
    }

    fun durability(): Int {
        return durability
    }

    @JvmOverloads
    fun maxDurability(lvl: Int = level): Int {
        return 1
    }

    fun visiblyUpgraded(): Int {
        return if (levelKnown) level else 0
    }

    fun visiblyCursed(): Boolean {
        return cursed && cursedKnown
    }

    fun visiblyBroken(): Boolean {
        return levelKnown && isBroken
    }

    val isUpgradable: Boolean
        get() = true
    val isIdentified: Boolean
        get() = levelKnown && cursedKnown

    fun isEquipped(hero: Hero?): Boolean {
        return false
    }

    fun identify(): Item {
        levelKnown = true
        cursedKnown = true
        return this
    }

    override fun toString(): String {
        return if (levelKnown && level != 0) {
            if (quantity > 1) {
                Utils.format(TXT_TO_STRING_LVL_X, name(), level, quantity)
            } else {
                Utils.format(TXT_TO_STRING_LVL, name(), level)
            }
        } else {
            if (quantity > 1) {
                Utils.format(TXT_TO_STRING_X, name(), quantity)
            } else {
                Utils.format(TXT_TO_STRING, name())
            }
        }
    }

    fun name(): String {
        return name
    }

    fun trueName(): String {
        return name
    }

    fun image(): Int {
        return image
    }

    fun glowing(): ItemSprite.Glowing? {
        return null
    }

    fun info(): String {
        return desc()
    }

    fun desc(): String {
        return ""
    }

    fun quantity(): Int {
        return quantity
    }

    fun quantity(value: Int) {
        quantity = value
    }

    fun price(): Int {
        return 0
    }

    fun considerState(price: Int): Int {
        var price = price
        if (cursed && cursedKnown) {
            price /= 2
        }
        if (levelKnown) {
            if (level > 0) {
                price *= level + 1
                if (isBroken) {
                    price /= 2
                }
            } else if (level < 0) {
                price /= 1 - level
            }
        }
        if (price < 1) {
            price = 1
        }
        return price
    }

    fun random(): Item {
        return this
    }

    fun status(): String? {
        return if (quantity != 1) Integer.toString(quantity) else null
    }

    fun updateQuickslot() {
        if (stackable) {
            val cl: Class<out Item> = javaClass
            if (QuickSlot.primaryValue === cl || QuickSlot.secondaryValue === cl) {
                QuickSlot.refresh()
            }
        } else if (QuickSlot.primaryValue === this || QuickSlot.secondaryValue === this) {
            QuickSlot.refresh()
        }
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(QUANTITY, quantity)
        bundle.put(LEVEL, level)
        bundle.put(LEVEL_KNOWN, levelKnown)
        bundle.put(CURSED, cursed)
        bundle.put(CURSED_KNOWN, cursedKnown)
        if (isUpgradable) {
            bundle.put(DURABILITY, durability)
        }
        QuickSlot.save(bundle, this)
    }

    fun restoreFromBundle(bundle: Bundle) {
        quantity = bundle.getInt(QUANTITY)
        levelKnown = bundle.getBoolean(LEVEL_KNOWN)
        cursedKnown = bundle.getBoolean(CURSED_KNOWN)
        val level: Int = bundle.getInt(LEVEL)
        if (level > 0) {
            upgrade(level)
        } else if (level < 0) {
            degrade(-level)
        }
        cursed = bundle.getBoolean(CURSED)
        if (isUpgradable) {
            durability = bundle.getInt(DURABILITY)
        }
        QuickSlot.restore(bundle, this)
    }

    fun cast(user: Hero?, dst: Int) {
        val cell: Int = Ballistica.cast(user.pos, dst, false, true)
        user.sprite.zap(cell)
        user.busy()
        Sample.INSTANCE.play(Assets.SND_MISS, 0.6f, 0.6f, 1.5f)
        val enemy: Char = Actor.findChar(cell)
        QuickSlot.target(this, enemy)

        // FIXME!!!
        var delay = TIME_TO_THROW
        if (this is MissileWeapon) {
            delay *= (this as MissileWeapon).speedFactor(user)
            if (enemy != null) {
                val mark: SnipersMark = user.buff(SnipersMark::class.java)
                if (mark != null) {
                    if (mark.`object` === enemy.id()) {
                        delay *= 0.5f
                    }
                    user.remove(mark)
                }
            }
        }
        val finalDelay = delay
        (user.sprite.parent.recycle(MissileSprite::class.java) as MissileSprite).reset(
            user.pos,
            cell,
            this,
            object : Callback() {
                fun call() {
                    detach(user.belongings.backpack)!!.onThrow(cell)
                    user.spendAndNext(finalDelay)
                }
            })
    }

    companion object {
        private const val TXT_PACK_FULL = "Your pack is too full for the %s"
        private const val TXT_BROKEN = "Because of frequent use, your %s has broken."
        private const val TXT_GONNA_BREAK = "Because of frequent use, your %s is going to break soon."
        private const val TXT_TO_STRING = "%s"
        private const val TXT_TO_STRING_X = "%s x%d"
        private const val TXT_TO_STRING_LVL = "%s%+d"
        private const val TXT_TO_STRING_LVL_X = "%s%+d x%d"
        private const val DURABILITY_WARNING_LEVEL = 1 / 6f
        protected const val TIME_TO_THROW = 1.0f
        protected const val TIME_TO_PICK_UP = 1.0f
        protected const val TIME_TO_DROP = 0.5f
        const val AC_DROP = "DROP"
        const val AC_THROW = "THROW"
        private val itemComparator: Comparator<Item> =
            Comparator<Item?> { lhs, rhs -> Generator.Category.order(lhs) - Generator.Category.order(rhs) }

        fun evoke(hero: Hero) {
            hero.sprite.emitter().burst(Speck.factory(Speck.EVOKE), 5)
        }

        fun virtual(cl: Class<out Item>): Item? {
            return try {
                val item = cl.newInstance() as Item
                item.quantity = 0
                item
            } catch (e: Exception) {
                null
            }
        }

        private const val QUANTITY = "quantity"
        private const val LEVEL = "level"
        private const val LEVEL_KNOWN = "levelKnown"
        private const val CURSED = "cursed"
        private const val CURSED_KNOWN = "cursedKnown"
        private const val DURABILITY = "durability"
        protected var curUser: Hero? = null
        protected var curItem: Item? = null
        protected var thrower: CellSelector.Listener = object : Listener() {
            fun onSelect(target: Int?) {
                if (target != null) {
                    curItem!!.cast(curUser, target)
                }
            }

            fun prompt(): String {
                return "Choose direction of throw"
            }
        }
    }
}