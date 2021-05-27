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

import com.watabou.pixeldungeon.Dungeon

object TrapsPainter : Painter() {
    fun paint(level: Level, room: Room) {
        val traps: Array<Int> = arrayOf(
            Terrain.TOXIC_TRAP, Terrain.TOXIC_TRAP, Terrain.TOXIC_TRAP,
            Terrain.PARALYTIC_TRAP, Terrain.PARALYTIC_TRAP,
            if (!Dungeon.bossLevel(Dungeon.depth + 1)) Terrain.CHASM else Terrain.SUMMONING_TRAP
        )
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Random.element(traps))
        val door: Room.Door = room.entrance()
        door.set(Room.Door.Type.REGULAR)
        val lastRow: Int =
            if (level.map.get(room.left + 1 + (room.top + 1) * Level.WIDTH) === Terrain.CHASM) Terrain.CHASM else Terrain.EMPTY
        var x = -1
        var y = -1
        if (door.x === room.left) {
            x = room.right - 1
            y = room.top + room.height() / 2
            fill(level, x, room.top + 1, 1, room.height() - 1, lastRow)
        } else if (door.x === room.right) {
            x = room.left + 1
            y = room.top + room.height() / 2
            fill(level, x, room.top + 1, 1, room.height() - 1, lastRow)
        } else if (door.y === room.top) {
            x = room.left + room.width() / 2
            y = room.bottom - 1
            fill(level, room.left + 1, y, room.width() - 1, 1, lastRow)
        } else if (door.y === room.bottom) {
            x = room.left + room.width() / 2
            y = room.top + 1
            fill(level, room.left + 1, y, room.width() - 1, 1, lastRow)
        }
        val pos: Int = x + y * Level.WIDTH
        if (Random.Int(3) === 0) {
            if (lastRow == Terrain.CHASM) {
                set(level, pos, Terrain.EMPTY)
            }
            level.drop(prize(level), pos).type = Heap.Type.CHEST
        } else {
            set(level, pos, Terrain.PEDESTAL)
            level.drop(prize(level), pos)
        }
        level.addItemToSpawn(PotionOfLevitation())
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
        for (i in 0..2) {
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