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

import com.watabou.pixeldungeon.levels.Level

object PassagePainter : Painter() {
    private var pasWidth = 0
    private var pasHeight = 0
    fun paint(level: Level, room: Room) {
        pasWidth = room.width() - 2
        pasHeight = room.height() - 2
        val floor: Int = level.tunnelTile()
        val joints = ArrayList<Int>()
        for (door in room.connected.values()) {
            joints.add(xy2p(room, door))
        }
        Collections.sort<Int>(joints)
        val nJoints = joints.size
        val perimeter = pasWidth * 2 + pasHeight * 2
        var start = 0
        var maxD = joints[0] + perimeter - joints[nJoints - 1]
        for (i in 1 until nJoints) {
            val d = joints[i] - joints[i - 1]
            if (d > maxD) {
                maxD = d
                start = i
            }
        }
        val end = (start + nJoints - 1) % nJoints
        var p = joints[start]
        do {
            set(level, p2xy(room, p), floor)
            p = (p + 1) % perimeter
        } while (p != joints[end])
        set(level, p2xy(room, p), floor)
        for (door in room.connected.values()) {
            door.set(Room.Door.Type.TUNNEL)
        }
    }

    private fun xy2p(room: Room, xy: Point): Int {
        return if (xy.y === room.top) {
            xy.x - room.left - 1
        } else if (xy.x === room.right) {
            xy.y - room.top - 1 + pasWidth
        } else if (xy.y === room.bottom) {
            room.right - xy.x - 1 + pasWidth + pasHeight
        } else  /*if (xy.x == room.left)*/ {
            if (xy.y === room.top + 1) {
                0
            } else {
                room.bottom - xy.y - 1 + pasWidth * 2 + pasHeight
            }
        }
    }

    private fun p2xy(room: Room, p: Int): Point {
        return if (p < pasWidth) {
            Point(room.left + 1 + p, room.top + 1)
        } else if (p < pasWidth + pasHeight) {
            Point(room.right - 1, room.top + 1 + (p - pasWidth))
        } else if (p < pasWidth * 2 + pasHeight) {
            Point(
                room.right - 1 - (p - (pasWidth + pasHeight)),
                room.bottom - 1
            )
        } else {
            Point(
                room.left + 1,
                room.bottom - 1 - (p - (pasWidth * 2 + pasHeight))
            )
        }
    }
}