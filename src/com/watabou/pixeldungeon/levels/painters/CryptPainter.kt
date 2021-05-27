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

object CryptPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY)
        val c: Point = room.center()
        var cx: Int = c.x
        var cy: Int = c.y
        val entrance: Room.Door = room.entrance()
        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
        if (entrance.x === room.left) {
            set(level, Point(room.right - 1, room.top + 1), Terrain.STATUE)
            set(level, Point(room.right - 1, room.bottom - 1), Terrain.STATUE)
            cx = room.right - 2
        } else if (entrance.x === room.right) {
            set(level, Point(room.left + 1, room.top + 1), Terrain.STATUE)
            set(level, Point(room.left + 1, room.bottom - 1), Terrain.STATUE)
            cx = room.left + 2
        } else if (entrance.y === room.top) {
            set(level, Point(room.left + 1, room.bottom - 1), Terrain.STATUE)
            set(level, Point(room.right - 1, room.bottom - 1), Terrain.STATUE)
            cy = room.bottom - 2
        } else if (entrance.y === room.bottom) {
            set(level, Point(room.left + 1, room.top + 1), Terrain.STATUE)
            set(level, Point(room.right - 1, room.top + 1), Terrain.STATUE)
            cy = room.top + 2
        }
        level.drop(prize(level), cx + cy * Level.WIDTH).type = Type.TOMB
    }

    private fun prize(level: Level): Item {
        var prize: Item = Generator.random(Generator.Category.ARMOR)
        for (i in 0..2) {
            val another: Item = Generator.random(Generator.Category.ARMOR)
            if (another.level() > prize.level()) {
                prize = another
            }
        }
        return prize
    }
}