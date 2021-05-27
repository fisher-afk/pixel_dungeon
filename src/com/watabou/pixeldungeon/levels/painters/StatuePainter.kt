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

import com.watabou.pixeldungeon.actors.Actor

object StatuePainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY)
        val c: Point = room.center()
        var cx: Int = c.x
        var cy: Int = c.y
        val door: Room.Door = room.entrance()
        door.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
        if (door.x === room.left) {
            fill(level, room.right - 1, room.top + 1, 1, room.height() - 1, Terrain.STATUE)
            cx = room.right - 2
        } else if (door.x === room.right) {
            fill(level, room.left + 1, room.top + 1, 1, room.height() - 1, Terrain.STATUE)
            cx = room.left + 2
        } else if (door.y === room.top) {
            fill(level, room.left + 1, room.bottom - 1, room.width() - 1, 1, Terrain.STATUE)
            cy = room.bottom - 2
        } else if (door.y === room.bottom) {
            fill(level, room.left + 1, room.top + 1, room.width() - 1, 1, Terrain.STATUE)
            cy = room.top + 2
        }
        val statue = Statue()
        statue.pos = cx + cy * Level.WIDTH
        level.mobs.add(statue)
        Actor.occupyCell(statue)
    }
}