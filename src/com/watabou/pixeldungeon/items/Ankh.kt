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

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Ankh : Item() {
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun info(): String {
        return "The ancient symbol of immortality grants an ability to return to life after death. " +
                "Upon resurrection all non-equipped items are lost."
    }

    override fun price(): Int {
        return 50 * quantity
    }

    init {
        stackable = true
        name = "Ankh"
        image = ItemSpriteSheet.ANKH
    }
}