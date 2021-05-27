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

class CurareDart @JvmOverloads constructor(number: Int = 1) : MissileWeapon() {
    fun min(): Int {
        return 1
    }

    fun max(): Int {
        return 3
    }

    fun proc(attacker: Char, defender: Char?, damage: Int) {
        Buff.prolong(defender, Paralysis::class.java, DURATION)
        super.proc(attacker, defender, damage)
    }

    fun desc(): String {
        return "These little evil darts don't do much damage but they can paralyze " +
                "the target leaving it helpless and motionless for some time."
    }

    override fun random(): Item {
        quantity = Random.Int(2, 5)
        return this
    }

    fun price(): Int {
        return 12 * quantity
    }

    companion object {
        const val DURATION = 3f
    }

    init {
        name = "curare dart"
        image = ItemSpriteSheet.CURARE_DART
        STR = 14
    }

    init {
        quantity = number
    }
}