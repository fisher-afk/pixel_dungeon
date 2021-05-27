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

import com.watabou.pixeldungeon.items.Bomb

object ArmoryPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY)
        val entrance: Room.Door = room.entrance()
        var statue: Point? = null
        if (entrance.x === room.left) {
            statue = Point(room.right - 1, if (Random.Int(2) === 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.x === room.right) {
            statue = Point(room.left + 1, if (Random.Int(2) === 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.y === room.top) {
            statue = Point(if (Random.Int(2) === 0) room.left + 1 else room.right - 1, room.bottom - 1)
        } else if (entrance.y === room.bottom) {
            statue = Point(if (Random.Int(2) === 0) room.left + 1 else room.right - 1, room.top + 1)
        }
        if (statue != null) {
            set(level, statue, Terrain.STATUE)
        }
        val n = 3 + if (Random.Int(4) === 0) 1 else 0
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

    private fun prize(level: Level): Item {
        return if (Random.Int(6) === 0) Bomb().random() else Generator.random(
            Random.oneOf(
                Generator.Category.ARMOR,
                Generator.Category.WEAPON
            )
        )
    }
}