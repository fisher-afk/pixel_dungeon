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

object PoolPainter : Painter() {
    private const val NPIRANHAS = 3
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.WATER)
        val door: Room.Door = room.entrance()
        door.set(Room.Door.Type.REGULAR)
        var x = -1
        var y = -1
        if (door.x === room.left) {
            x = room.right - 1
            y = room.top + room.height() / 2
        } else if (door.x === room.right) {
            x = room.left + 1
            y = room.top + room.height() / 2
        } else if (door.y === room.top) {
            x = room.left + room.width() / 2
            y = room.bottom - 1
        } else if (door.y === room.bottom) {
            x = room.left + room.width() / 2
            y = room.top + 1
        }
        val pos: Int = x + y * Level.WIDTH
        level.drop(prize(level), pos).type = if (Random.Int(3) === 0) Heap.Type.CHEST else Heap.Type.HEAP
        set(level, pos, Terrain.PEDESTAL)
        level.addItemToSpawn(PotionOfInvisibility())
        for (i in 0 until NPIRANHAS) {
            val piranha = Piranha()
            do {
                piranha.pos = room.random()
            } while (level.map.get(piranha.pos) !== Terrain.WATER || Actor.findChar(piranha.pos) != null)
            level.mobs.add(piranha)
            Actor.occupyCell(piranha)
        }
    }

    private fun prize(level: Level): Item? {
        var prize: Item? = level.itemToSpanAsPrize()
        if (prize != null) {
            return prize
        }
        prize = Generator.random(
            Random.oneOf(
                Generator.Category.WEAPON,
                Generator.Category.ARMOR
            )
        )
        for (i in 0..3) {
            val another: Item = Generator.random(
                Random.oneOf(
                    Generator.Category.WEAPON,
                    Generator.Category.ARMOR
                )
            )
            if (another.level() > prize.level()) {
                prize = another
            }
        }
        return prize
    }
}