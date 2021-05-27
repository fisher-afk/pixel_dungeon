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
package com.watabou.pixeldungeon.items.weapon.missiles

import com.watabou.pixeldungeon.items.Item

class Shuriken @JvmOverloads constructor(number: Int = 1) : MissileWeapon() {
    fun min(): Int {
        return 2
    }

    fun max(): Int {
        return 6
    }

    fun desc(): String {
        return "Star-shaped pieces of metal with razor-sharp blades do significant damage " +
                "when they hit a target. They can be thrown at very high rate."
    }

    override fun random(): Item {
        quantity = Random.Int(5, 15)
        return this
    }

    fun price(): Int {
        return 15 * quantity
    }

    init {
        name = "shuriken"
        image = ItemSpriteSheet.SHURIKEN
        STR = 13
        DLY = 0.5f
    }

    init {
        quantity = number
    }
}