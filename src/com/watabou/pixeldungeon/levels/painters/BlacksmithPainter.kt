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

object BlacksmithPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.FIRE_TRAP)
        fill(level, room, 2, Terrain.EMPTY_SP)
        for (i in 0..1) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map.get(pos) !== Terrain.EMPTY_SP)
            level.drop(
                Generator.random(
                    Random.oneOf(
                        Generator.Category.ARMOR,
                        Generator.Category.WEAPON
                    )
                ), pos
            )
        }
        for (door in room.connected.values()) {
            door.set(Room.Door.Type.UNLOCKED)
            drawInside(level, room, door, 1, Terrain.EMPTY)
        }
        val npc = Blacksmith()
        do {
            npc.pos = room.random(1)
        } while (level.heaps.get(npc.pos) != null)
        level.mobs.add(npc)
        Actor.occupyCell(npc)
    }
}