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
package com.watabou.pixeldungeon.levels.traps

import com.watabou.pixeldungeon.Dungeon

object GrippingTrap {
    fun trigger(pos: Int, c: Char?) {
        if (c != null) {
            val damage = Math.max(0, Dungeon.depth + 3 - Random.IntRange(0, c.dr() / 2))
            Buff.affect(c, Bleeding::class.java).set(damage)
            Buff.prolong(c, Cripple::class.java, Cripple.DURATION)
            Wound.hit(c)
        } else {
            Wound.hit(pos)
        }
    }
}