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
package com.watabou.pixeldungeon.actors.hero

import com.watabou.pixeldungeon.Badges

class Belongings(owner: Hero) : Iterable<Item?> {
    private val owner: Hero
    var backpack: Bag
    var weapon: KindOfWeapon? = null
    var armor: Armor? = null
    var ring1: Ring? = null
    var ring2: Ring? = null
    fun storeInBundle(bundle: Bundle) {
        backpack.storeInBundle(bundle)
        bundle.put(WEAPON, weapon)
        bundle.put(ARMOR, armor)
        bundle.put(RING1, ring1)
        bundle.put(RING2, ring2)
    }

    fun restoreFromBundle(bundle: Bundle) {
        backpack.clear()
        backpack.restoreFromBundle(bundle)
        weapon = bundle.get(WEAPON) as KindOfWeapon
        if (weapon != null) {
            weapon.activate(owner)
        }
        armor = bundle.get(ARMOR) as Armor
        ring1 = bundle.get(RING1) as Ring
        if (ring1 != null) {
            ring1.activate(owner)
        }
        ring2 = bundle.get(RING2) as Ring
        if (ring2 != null) {
            ring2.activate(owner)
        }
    }

    fun <T : Item?> getItem(itemClass: Class<T>): T? {
        for (item in this) {
            if (itemClass.isInstance(item)) {
                return item
            }
        }
        return null
    }

    fun <T : Key?> getKey(kind: Class<T>, depth: Int): T? {
        for (item in backpack) {
            if (item.getClass() === kind && (item as Key).depth === depth) {
                return item
            }
        }
        return null
    }

    fun countIronKeys() {
        IronKey.curDepthQuantity = 0
        for (item in backpack) {
            if (item is IronKey && (item as IronKey).depth === Dungeon.depth) {
                IronKey.curDepthQuantity++
            }
        }
    }

    fun identify() {
        for (item in this) {
            item.identify()
        }
    }

    fun observe() {
        if (weapon != null) {
            weapon.identify()
            Badges.validateItemLevelAquired(weapon)
        }
        if (armor != null) {
            armor.identify()
            Badges.validateItemLevelAquired(armor)
        }
        if (ring1 != null) {
            ring1.identify()
            Badges.validateItemLevelAquired(ring1)
        }
        if (ring2 != null) {
            ring2.identify()
            Badges.validateItemLevelAquired(ring2)
        }
        for (item in backpack) {
            item.cursedKnown = true
        }
    }

    fun uncurseEquipped() {
        ScrollOfRemoveCurse.uncurse(owner, armor, weapon, ring1, ring2)
    }

    fun randomUnequipped(): Item {
        return Random.element(backpack.items)
    }

    fun resurrect(depth: Int) {
        for (item in backpack.items.toArray(arrayOfNulls<Item>(0))) {
            if (item is Key) {
                if ((item as Key).depth === depth) {
                    item.detachAll(backpack)
                }
            } else if (item.unique) {
                // Keep unique items
            } else if (!item.isEquipped(owner)) {
                item.detachAll(backpack)
            }
        }
        if (weapon != null) {
            weapon.cursed = false
            weapon.activate(owner)
        }
        if (armor != null) {
            armor.cursed = false
        }
        if (ring1 != null) {
            ring1.cursed = false
            ring1.activate(owner)
        }
        if (ring2 != null) {
            ring2.cursed = false
            ring2.activate(owner)
        }
    }

    fun charge(full: Boolean): Int {
        var count = 0
        for (item in this) {
            if (item is Wand) {
                val wand: Wand = item as Wand
                if (wand.curCharges < wand.maxCharges) {
                    wand.curCharges = if (full) wand.maxCharges else wand.curCharges + 1
                    count++
                    wand.updateQuickslot()
                }
            }
        }
        return count
    }

    fun discharge(): Int {
        var count = 0
        for (item in this) {
            if (item is Wand) {
                val wand: Wand = item as Wand
                if (wand.curCharges > 0) {
                    wand.curCharges--
                    count++
                    wand.updateQuickslot()
                }
            }
        }
        return count
    }

    override fun iterator(): MutableIterator<Item> {
        return ItemIterator()
    }

    private inner class ItemIterator : MutableIterator<Item?> {
        private var index = 0
        private val backpackIterator: MutableIterator<Item> = backpack.iterator()
        private val equipped: Array<Item?> = arrayOf<Item?>(weapon, armor, ring1, ring2)
        private val backpackIndex = equipped.size
        override fun hasNext(): Boolean {
            for (i in index until backpackIndex) {
                if (equipped[i] != null) {
                    return true
                }
            }
            return backpackIterator.hasNext()
        }

        override fun next(): Item {
            while (index < backpackIndex) {
                val item: Item? = equipped[index++]
                if (item != null) {
                    return item
                }
            }
            return backpackIterator.next()
        }

        override fun remove() {
            when (index) {
                0 -> {
                    weapon = null
                    equipped[0] = weapon
                }
                1 -> {
                    armor = null
                    equipped[1] = armor
                }
                2 -> {
                    ring1 = null
                    equipped[2] = ring1
                }
                3 -> {
                    ring2 = null
                    equipped[3] = ring2
                }
                else -> backpackIterator.remove()
            }
        }
    }

    companion object {
        const val BACKPACK_SIZE = 19
        private const val WEAPON = "weapon"
        private const val ARMOR = "armor"
        private const val RING1 = "ring1"
        private const val RING2 = "ring2"
    }

    init {
        this.owner = owner
        backpack = object : Bag() {
            init {
                name = "backpack"
                size = BACKPACK_SIZE
            }
        }
        backpack.owner = owner
    }
}