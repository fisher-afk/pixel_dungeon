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
package com.watabou.pixeldungeon.actors.mobs

class Rat : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(1, 5)
    }

    fun attackSkill(target: Char?): Int {
        return 8
    }

    fun dr(): Int {
        return 1
    }

    override fun die(cause: Any?) {
        Ghost.Quest.processSewersKill(pos)
        super.die(cause)
    }

    override fun description(): String {
        return "Marsupial rats are aggressive, but rather weak denizens " +
                "of the sewers. They can be dangerous only in big numbers."
    }

    init {
        name = "marsupial rat"
        spriteClass = RatSprite::class.java
        HT = 8
        HP = HT
        defenseSkill = 3
        maxLvl = 5
    }
}