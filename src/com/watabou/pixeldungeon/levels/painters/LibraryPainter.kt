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

import com.watabou.pixeldungeon.items.Generator

object LibraryPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY)
        val entrance: Room.Door = room.entrance()
        var a: Point? = null
        var b: Point? = null
        if (entrance.x === room.left) {
            a = Point(room.left + 1, entrance.y - 1)
            b = Point(room.left + 1, entrance.y + 1)
            fill(level, room.right - 1, room.top + 1, 1, room.height() - 1, Terrain.BOOKSHELF)
        } else if (entrance.x === room.right) {
            a = Point(room.right - 1, entrance.y - 1)
            b = Point(room.right - 1, entrance.y + 1)
            fill(level, room.left + 1, room.top + 1, 1, room.height() - 1, Terrain.BOOKSHELF)
        } else if (entrance.y === room.top) {
            a = Point(entrance.x + 1, room.top + 1)
            b = Point(entrance.x - 1, room.top + 1)
            fill(level, room.left + 1, room.bottom - 1, room.width() - 1, 1, Terrain.BOOKSHELF)
        } else if (entrance.y === room.bottom) {
            a = Point(entrance.x + 1, room.bottom - 1)
            b = Point(entrance.x - 1, room.bottom - 1)
            fill(level, room.left + 1, room.top + 1, room.width() - 1, 1, Terrain.BOOKSHELF)
        }
        if (a != null && level.map.get(a.x + a.y * Level.WIDTH) === Terrain.EMPTY) {
            set(level, a, Terrain.STATUE)
        }
        if (b != null && level.map.get(b.x + b.y * Level.WIDTH) === Terrain.EMPTY) {
            set(level, b, Terrain.STATUE)
        }
        val n: Int = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map.get(pos) !== Terrain.EMPTY || level.heaps.get(pos) != null)
            level.drop(prize(level), pos)
        }
        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }

    private fun prize(level: Level): Item? {
        val prize: Item = level.itemToSpanAsPrize()
        if (prize is Scroll) {
            return prize
        } else if (prize != null) {
            level.addItemToSpawn(prize)
        }
        return Generator.random(Generator.Category.SCROLL)
    }
}