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

object TunnelPainter : Painter() {
    fun paint(level: Level, room: Room) {
        val floor: Int = level.tunnelTile()
        val c: Point = room.center()
        if (room.width() > room.height() || room.width() === room.height() && Random.Int(2) === 0) {
            var from: Int = room.right - 1
            var to: Int = room.left + 1
            for (door in room.connected.values()) {
                val step = if (door.y < c.y) +1 else -1
                if (door.x === room.left) {
                    from = room.left + 1
                    var i: Int = door.y
                    while (i != c.y) {
                        set(level, from, i, floor)
                        i += step
                    }
                } else if (door.x === room.right) {
                    to = room.right - 1
                    var i: Int = door.y
                    while (i != c.y) {
                        set(level, to, i, floor)
                        i += step
                    }
                } else {
                    if (door.x < from) {
                        from = door.x
                    }
                    if (door.x > to) {
                        to = door.x
                    }
                    var i: Int = door.y + step
                    while (i != c.y) {
                        set(level, door.x, i, floor)
                        i += step
                    }
                }
            }
            for (i in from..to) {
                set(level, i, c.y, floor)
            }
        } else {
            var from: Int = room.bottom - 1
            var to: Int = room.top + 1
            for (door in room.connected.values()) {
                val step = if (door.x < c.x) +1 else -1
                if (door.y === room.top) {
                    from = room.top + 1
                    var i: Int = door.x
                    while (i != c.x) {
                        set(level, i, from, floor)
                        i += step
                    }
                } else if (door.y === room.bottom) {
                    to = room.bottom - 1
                    var i: Int = door.x
                    while (i != c.x) {
                        set(level, i, to, floor)
                        i += step
                    }
                } else {
                    if (door.y < from) {
                        from = door.y
                    }
                    if (door.y > to) {
                        to = door.y
                    }
                    var i: Int = door.x + step
                    while (i != c.x) {
                        set(level, i, door.y, floor)
                        i += step
                    }
                }
            }
            for (i in from..to) {
                set(level, c.x, i, floor)
            }
        }
        for (door in room.connected.values()) {
            door.set(Room.Door.Type.TUNNEL)
        }
    }
}