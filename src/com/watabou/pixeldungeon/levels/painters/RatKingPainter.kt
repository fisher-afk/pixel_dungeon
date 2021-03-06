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

import com.watabou.pixeldungeon.actors.mobs.npcs.RatKing

object RatKingPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY_SP)
        val entrance: Room.Door = room.entrance()
        entrance.set(Room.Door.Type.HIDDEN)
        val door: Int = entrance.x + entrance.y * Level.WIDTH
        for (i in room.left + 1 until room.right) {
            addChest(level, (room.top + 1) * Level.WIDTH + i, door)
            addChest(level, (room.bottom - 1) * Level.WIDTH + i, door)
        }
        for (i in room.top + 2 until room.bottom - 1) {
            addChest(level, i * Level.WIDTH + room.left + 1, door)
            addChest(level, i * Level.WIDTH + room.right - 1, door)
        }
        while (true) {
            val chest: Heap = level.heaps.get(room.random())
            if (chest != null) {
                chest.type = Heap.Type.MIMIC
                break
            }
        }
        val king = RatKing()
        king.pos = room.random(1)
        level.mobs.add(king)
    }

    private fun addChest(level: Level, pos: Int, door: Int) {
        if (pos == door - 1 || pos == door + 1 || pos == door - Level.WIDTH || pos == door + Level.WIDTH) {
            return
        }
        val prize: Item
        when (Random.Int(10)) {
            0 -> {
                prize = Generator.random(Generator.Category.WEAPON)
                if (prize is MissileWeapon) {
                    prize.quantity(1)
                } else {
                    prize.degrade(Random.Int(3))
                }
            }
            1 -> prize = Generator.random(Generator.Category.ARMOR).degrade(Random.Int(3))
            else -> prize = Gold(Random.IntRange(1, 5))
        }
        level.drop(prize, pos).type = Heap.Type.CHEST
    }
}