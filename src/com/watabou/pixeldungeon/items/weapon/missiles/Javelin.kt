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

class Javelin @JvmOverloads constructor(number: Int = 1) : MissileWeapon() {
    fun min(): Int {
        return 2
    }

    fun max(): Int {
        return 15
    }

    fun proc(attacker: Char, defender: Char?, damage: Int) {
        super.proc(attacker, defender, damage)
        Buff.prolong(defender, Cripple::class.java, Cripple.DURATION)
    }

    fun desc(): String {
        return "This length of metal is weighted to keep the spike " +
                "at its tip foremost as it sails through the air."
    }

    override fun random(): Item {
        quantity = Random.Int(5, 15)
        return this
    }

    fun price(): Int {
        return 15 * quantity
    }

    init {
        name = "javelin"
        image = ItemSpriteSheet.JAVELIN
        STR = 15
    }

    init {
        quantity = number
    }
}