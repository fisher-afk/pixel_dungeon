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

import com.watabou.pixeldungeon.items.Gold

object TreasuryPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY)
        set(level, room.center(), Terrain.STATUE)
        val heapType: Heap.Type = if (Random.Int(2) === 0) Heap.Type.CHEST else Heap.Type.HEAP
        val n: Int = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map.get(pos) !== Terrain.EMPTY || level.heaps.get(pos) != null)
            level.drop(Gold().random(), pos).type =
                if (i == 0 && heapType === Heap.Type.CHEST) Heap.Type.MIMIC else heapType
        }
        if (heapType === Heap.Type.HEAP) {
            for (i in 0..5) {
                var pos: Int
                do {
                    pos = room.random()
                } while (level.map.get(pos) !== Terrain.EMPTY)
                level.drop(Gold(Random.IntRange(1, 3)), pos)
            }
        }
        room.entrance().set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
}