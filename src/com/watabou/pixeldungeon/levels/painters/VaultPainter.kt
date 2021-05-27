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

object VaultPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY_SP)
        fill(level, room, 2, Terrain.EMPTY)
        val cx: Int = (room.left + room.right) / 2
        val cy: Int = (room.top + room.bottom) / 2
        val c: Int = cx + cy * Level.WIDTH
        when (Random.Int(3)) {
            0 -> {
                level.drop(prize(level), c).type = Type.LOCKED_CHEST
                level.addItemToSpawn(GoldenKey())
            }
            1 -> {
                var i1: Item
                var i2: Item
                do {
                    i1 = prize(level)
                    i2 = prize(level)
                } while (i1.getClass() === i2.getClass())
                level.drop(i1, c).type = Type.CRYSTAL_CHEST
                level.drop(i2, c + Level.NEIGHBOURS8.get(Random.Int(8))).type = Type.CRYSTAL_CHEST
                level.addItemToSpawn(GoldenKey())
            }
            2 -> {
                level.drop(prize(level), c)
                set(level, c, Terrain.PEDESTAL)
            }
        }
        room.entrance().set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }

    private fun prize(level: Level): Item {
        return Generator.random(
            Random.oneOf(
                Generator.Category.WAND,
                Generator.Category.RING
            )
        )
    }
}