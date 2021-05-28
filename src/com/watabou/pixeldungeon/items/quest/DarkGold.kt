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
package com.watabou.pixeldungeon.items.quest

import com.watabou.pixeldungeon.items.Item

class DarkGold : Item() {
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun info(): String {
        return "This metal is called dark not because of its color (it doesn't differ from the normal gold), " +
                "but because it melts under the daylight, making it useless on the surface."
    }

    override fun price(): Int {
        return quantity
    }

    init {
        name = "dark gold ore"
        image = ItemSpriteSheet.ORE
        stackable = true
        unique = true
    }
}