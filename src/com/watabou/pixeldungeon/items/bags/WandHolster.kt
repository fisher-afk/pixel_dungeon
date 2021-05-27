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

import com.watabou.pixeldungeon.items.Item

class WandHolster : Bag() {
    override fun grab(item: Item?): Boolean {
        return item is Wand
    }

    override fun collect(container: Bag): Boolean {
        return if (super.collect(container)) {
            if (owner != null) {
                for (item in items) {
                    (item as Wand).charge(owner)
                }
            }
            true
        } else {
            false
        }
    }

    override fun onDetach() {
        for (item in items) {
            (item as Wand).stopCharging()
        }
    }

    fun price(): Int {
        return 50
    }

    fun info(): String {
        return "This slim holder is made of leather of some exotic animal. " +
                "It allows to compactly carry up to " + size + " wands."
    }

    init {
        name = "wand holster"
        image = ItemSpriteSheet.HOLSTER
        size = 12
    }
}