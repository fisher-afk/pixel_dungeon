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

object PitPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY)
        val entrance: Room.Door = room.entrance()
        entrance.set(Room.Door.Type.LOCKED)
        var well: Point? = null
        if (entrance.x === room.left) {
            well = Point(room.right - 1, if (Random.Int(2) === 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.x === room.right) {
            well = Point(room.left + 1, if (Random.Int(2) === 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.y === room.top) {
            well = Point(if (Random.Int(2) === 0) room.left + 1 else room.right - 1, room.bottom - 1)
        } else if (entrance.y === room.bottom) {
            well = Point(if (Random.Int(2) === 0) room.left + 1 else room.right - 1, room.top + 1)
        }
        set(level, well, Terrain.EMPTY_WELL)
        var remains: Int = room.random()
        while (level.map.get(remains) === Terrain.EMPTY_WELL) {
            remains = room.random()
        }
        level.drop(IronKey(), remains).type = Type.SKELETON
        if (Random.Int(5) === 0) {
            level.drop(Generator.random(Generator.Category.RING), remains)
        } else {
            level.drop(
                Generator.random(
                    Random.oneOf(
                        Generator.Category.WEAPON,
                        Generator.Category.ARMOR
                    )
                ), remains
            )
        }
        val n: Int = Random.IntRange(1, 2)
        for (i in 0 until n) {
            level.drop(prize(level), remains)
        }
    }

    private fun prize(level: Level): Item {
        val prize: Item = level.itemToSpanAsPrize()
        return if (prize != null) {
            prize
        } else Generator.random(
            Random.oneOf(
                Generator.Category.POTION,
                Generator.Category.SCROLL,
                Generator.Category.FOOD,
                Generator.Category.GOLD
            )
        )
    }
}