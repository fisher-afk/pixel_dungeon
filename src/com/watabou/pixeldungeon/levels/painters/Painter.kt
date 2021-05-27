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
package com.watabou.pixeldungeon.levels.painters

import com.watabou.pixeldungeon.levels.Level

object Painter {
    operator fun set(level: Level, cell: Int, value: Int) {
        level.map.get(cell) = value
    }

    operator fun set(level: Level?, x: Int, y: Int, value: Int) {
        set(level, x + y * Level.WIDTH, value)
    }

    operator fun set(level: Level?, p: Point, value: Int) {
        Painter[level, p.x, p.y] = value
    }

    fun fill(level: Level, x: Int, y: Int, w: Int, h: Int, value: Int) {
        val width: Int = Level.WIDTH
        var pos = y * width + x
        var i = y
        while (i < y + h) {
            Arrays.fill(level.map, pos, pos + w, value)
            i++
            pos += width
        }
    }

    fun fill(level: Level, rect: Rect, value: Int) {
        fill(level, rect.left, rect.top, rect.width() + 1, rect.height() + 1, value)
    }

    fun fill(level: Level, rect: Rect, m: Int, value: Int) {
        fill(level, rect.left + m, rect.top + m, rect.width() + 1 - m * 2, rect.height() + 1 - m * 2, value)
    }

    fun fill(level: Level, rect: Rect, l: Int, t: Int, r: Int, b: Int, value: Int) {
        fill(level, rect.left + l, rect.top + t, rect.width() + 1 - (l + r), rect.height() + 1 - (t + b), value)
    }

    fun drawInside(level: Level?, room: Room, from: Point, n: Int, value: Int): Point {
        val step = Point()
        if (from.x === room.left) {
            step.set(+1, 0)
        } else if (from.x === room.right) {
            step.set(-1, 0)
        } else if (from.y === room.top) {
            step.set(0, +1)
        } else if (from.y === room.bottom) {
            step.set(0, -1)
        }
        val p: Point = Point(from).offset(step)
        for (i in 0 until n) {
            if (value != -1) {
                set(level, p, value)
            }
            p.offset(step)
        }
        return p
    }
}