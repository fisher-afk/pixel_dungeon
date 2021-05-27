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
package com.watabou.pixeldungeon.mechanics

import com.watabou.pixeldungeon.actors.Actor

object Ballistica {
    var trace = IntArray(Math.max(Level.WIDTH, Level.HEIGHT))
    var distance = 0
    fun cast(from: Int, to: Int, magic: Boolean, hitChars: Boolean): Int {
        val w: Int = Level.WIDTH
        val x0 = from % w
        val x1 = to % w
        val y0 = from / w
        val y1 = to / w
        var dx = x1 - x0
        var dy = y1 - y0
        val stepX = if (dx > 0) +1 else -1
        val stepY = if (dy > 0) +1 else -1
        dx = Math.abs(dx)
        dy = Math.abs(dy)
        val stepA: Int
        val stepB: Int
        val dA: Int
        val dB: Int
        if (dx > dy) {
            stepA = stepX
            stepB = stepY * w
            dA = dx
            dB = dy
        } else {
            stepA = stepY * w
            stepB = stepX
            dA = dy
            dB = dx
        }
        distance = 1
        trace[0] = from
        var cell = from
        var err = dA / 2
        while (cell != to || magic) {
            cell += stepA
            err += dB
            if (err >= dA) {
                err = err - dA
                cell = cell + stepB
            }
            trace[distance++] = cell
            if (!Level.passable.get(cell) && !Level.avoid.get(cell)) {
                return trace[--distance - 1]
            }
            if (Level.losBlocking.get(cell) || hitChars && Actor.findChar(cell) != null) {
                return cell
            }
        }
        trace[distance++] = cell
        return to
    }
}