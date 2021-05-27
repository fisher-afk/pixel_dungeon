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

import com.watabou.pixeldungeon.actors.Char

class Tamahawk @JvmOverloads constructor(number: Int = 1) : MissileWeapon() {
    fun min(): Int {
        return 4
    }

    fun max(): Int {
        return 20
    }

    fun proc(attacker: Char, defender: Char?, damage: Int) {
        super.proc(attacker, defender, damage)
        Buff.affect(defender, Bleeding::class.java).set(damage)
    }

    fun desc(): String {
        return "This throwing axe is not that heavy, but it still " +
                "requires significant strength to be used effectively."
    }

    override fun random(): Item {
        quantity = Random.Int(5, 12)
        return this
    }

    fun price(): Int {
        return 20 * quantity
    }

    init {
        name = "tomahawk"
        image = ItemSpriteSheet.TOMAHAWK
        STR = 17
    }

    init {
        quantity = number
    }
}