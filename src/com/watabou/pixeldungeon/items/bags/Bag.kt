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
package com.watabou.pixeldungeon.items.bags

import com.watabou.pixeldungeon.Badges

class Bag : Item(), Iterable<Item?> {
    var owner: Char? = null
    var items: ArrayList<Item> = ArrayList<Item>()
    var size = 1
    fun actions(hero: Hero?): ArrayList<String> {
        return super.actions(hero)
    }

    fun execute(hero: Hero?, action: String) {
        if (action == AC_OPEN) {
            GameScene.show(WndBag(this, null, WndBag.Mode.ALL, null))
        } else {
            super.execute(hero, action)
        }
    }

    fun collect(container: Bag): Boolean {
        return if (super.collect(container)) {
            owner = container.owner
            for (item in container.items.toTypedArray()) {
                if (grab(item)) {
                    item.detachAll(container)
                    item.collect(this)
                }
            }
            Badges.validateAllBagsBought(this)
            true
        } else {
            false
        }
    }

    fun onDetach() {
        owner = null
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    fun clear() {
        items.clear()
    }

    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEMS, items)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (item in bundle.getCollection(ITEMS)) {
            (item as Item).collect(this)
        }
    }

    operator fun contains(item: Item): Boolean {
        for (i in items) {
            if (i === item) {
                return true
            } else if (i is Bag && (i as Bag).contains(item)) {
                return true
            }
        }
        return false
    }

    fun grab(item: Item?): Boolean {
        return false
    }

    override fun iterator(): MutableIterator<Item> {
        return ItemIterator()
    }

    private inner class ItemIterator : MutableIterator<Item?> {
        private var index = 0
        private var nested: MutableIterator<Item>? = null
        override fun hasNext(): Boolean {
            return if (nested != null) {
                nested!!.hasNext() || index < items.size
            } else {
                index < items.size
            }
        }

        override fun next(): Item {
            return if (nested != null && nested!!.hasNext()) {
                nested!!.next()
            } else {
                nested = null
                val item: Item = items[index++]
                if (item is Bag) {
                    nested = (item as Bag).iterator()
                }
                item
            }
        }

        override fun remove() {
            if (nested != null) {
                nested!!.remove()
            } else {
                items.removeAt(index)
            }
        }
    }

    companion object {
        const val AC_OPEN = "OPEN"
        private const val ITEMS = "inventory"
    }

    init {
        image = 11
        defaultAction = AC_OPEN
    }
}