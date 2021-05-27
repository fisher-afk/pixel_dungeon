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

import com.watabou.pixeldungeon.actors.blobs.Foliage

object GardenPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.HIGH_GRASS)
        fill(level, room, 2, Terrain.GRASS)
        room.entrance().set(Room.Door.Type.REGULAR)
        if (Random.Int(2) === 0) {
            level.drop(Honeypot(), room.random())
        } else {
            val bushes = if (Random.Int(5) === 0) 2 else 1
            for (i in 0 until bushes) {
                val pos: Int = room.random()
                set(level, pos, Terrain.GRASS)
                level.plant(Seed(), pos)
            }
        }
        var light: Foliage? = level.blobs.get(Foliage::class.java)
        if (light == null) {
            light = Foliage()
        }
        for (i in room.top + 1 until room.bottom) {
            for (j in room.left + 1 until room.right) {
                light.seed(j + Level.WIDTH * i, 1)
            }
        }
        level.blobs.put(Foliage::class.java, light)
    }
}