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

import com.watabou.pixeldungeon.Badges

class Senior : Monk() {
    override fun damageRoll(): Int {
        return Random.NormalIntRange(12, 20)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(10) === 0) {
            Buff.prolong(enemy, Paralysis::class.java, 1.1f)
        }
        return super.attackProc(enemy, damage)
    }

    override fun die(cause: Any?) {
        super.die(cause)
        Badges.validateRare(this)
    }

    init {
        name = "senior monk"
        spriteClass = SeniorSprite::class.java
    }
}