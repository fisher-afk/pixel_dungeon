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

object AltarPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, if (Dungeon.bossLevel(Dungeon.depth + 1)) Terrain.HIGH_GRASS else Terrain.CHASM)
        val c: Point = room.center()
        val door: Room.Door = room.entrance()
        if (door.x === room.left || door.x === room.right) {
            val p: Point = drawInside(level, room, door, Math.abs(door.x - c.x) - 2, Terrain.EMPTY_SP)
            while (p.y !== c.y) {
                set(level, p, Terrain.EMPTY_SP)
                p.y += if (p.y < c.y) +1 else -1
            }
        } else {
            val p: Point = drawInside(level, room, door, Math.abs(door.y - c.y) - 2, Terrain.EMPTY_SP)
            while (p.x !== c.x) {
                set(level, p, Terrain.EMPTY_SP)
                p.x += if (p.x < c.x) +1 else -1
            }
        }
        fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMBERS)
        set(level, c, Terrain.PEDESTAL)
        var fire: SacrificialFire? = level.blobs.get(SacrificialFire::class.java) as SacrificialFire
        if (fire == null) {
            fire = SacrificialFire()
        }
        fire.seed(c.x + c.y * Level.WIDTH, 5 + Dungeon.depth * 5)
        level.blobs.put(SacrificialFire::class.java, fire)
        door.set(Room.Door.Type.EMPTY)
    }
}