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

object WeakFloorPainter : Painter() {
    fun paint(level: Level?, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.CHASM)
        val door: Room.Door = room.entrance()
        door.set(Room.Door.Type.REGULAR)
        if (door.x === room.left) {
            for (i in room.top + 1 until room.bottom) {
                drawInside(level, room, Point(room.left, i), Random.IntRange(1, room.width() - 2), Terrain.EMPTY_SP)
            }
        } else if (door.x === room.right) {
            for (i in room.top + 1 until room.bottom) {
                drawInside(level, room, Point(room.right, i), Random.IntRange(1, room.width() - 2), Terrain.EMPTY_SP)
            }
        } else if (door.y === room.top) {
            for (i in room.left + 1 until room.right) {
                drawInside(level, room, Point(i, room.top), Random.IntRange(1, room.height() - 2), Terrain.EMPTY_SP)
            }
        } else if (door.y === room.bottom) {
            for (i in room.left + 1 until room.right) {
                drawInside(level, room, Point(i, room.bottom), Random.IntRange(1, room.height() - 2), Terrain.EMPTY_SP)
            }
        }
    }
}