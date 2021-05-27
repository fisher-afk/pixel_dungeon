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

object StandardPainter : Painter() {
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        for (door in room.connected.values()) {
            door.set(Room.Door.Type.REGULAR)
        }
        if (!Dungeon.bossLevel() && Random.Int(5) === 0) {
            when (Random.Int(6)) {
                0 -> {
                    if (level.feeling !== Level.Feeling.GRASS) {
                        if (Math.min(room.width(), room.height()) >= 4 && Math.max(room.width(), room.height()) >= 6) {
                            paintGraveyard(level, room)
                            return
                        }
                        break
                    } else {
                        // Burned room
                    }
                    if (Dungeon.depth > 1) {
                        paintBurned(level, room)
                        return
                    }
                }
                1 -> if (Dungeon.depth > 1) {
                    paintBurned(level, room)
                    return
                }
                2 -> if (Math.max(room.width(), room.height()) >= 4) {
                    paintStriped(level, room)
                    return
                }
                3 -> if (room.width() >= 6 && room.height() >= 6) {
                    paintStudy(level, room)
                    return
                }
                4 -> {
                    if (level.feeling !== Level.Feeling.WATER) {
                        if (room.connected.size() === 2 && room.width() >= 4 && room.height() >= 4) {
                            paintBridge(level, room)
                            return
                        }
                        break
                    } else {
                        // Fissure
                    }
                    if (!Dungeon.bossLevel() && !Dungeon.bossLevel(Dungeon.depth + 1) && Math.min(
                            room.width(),
                            room.height()
                        ) >= 5
                    ) {
                        paintFissure(level, room)
                        return
                    }
                }
                5 -> if (!Dungeon.bossLevel() && !Dungeon.bossLevel(Dungeon.depth + 1) && Math.min(
                        room.width(),
                        room.height()
                    ) >= 5
                ) {
                    paintFissure(level, room)
                    return
                }
            }
        }
        fill(level, room, 1, Terrain.EMPTY)
    }

    private fun paintBurned(level: Level, room: Room) {
        for (i in room.top + 1 until room.bottom) {
            for (j in room.left + 1 until room.right) {
                var t: Int = Terrain.EMBERS
                when (Random.Int(5)) {
                    0 -> t = Terrain.EMPTY
                    1 -> t = Terrain.FIRE_TRAP
                    2 -> t = Terrain.SECRET_FIRE_TRAP
                    3 -> t = Terrain.INACTIVE_TRAP
                }
                level.map.get(i * Level.WIDTH + j) = t
            }
        }
    }

    private fun paintGraveyard(level: Level, room: Room) {
        fill(level, room.left + 1, room.top + 1, room.width() - 1, room.height() - 1, Terrain.GRASS)
        val w: Int = room.width() - 1
        val h: Int = room.height() - 1
        val nGraves = Math.max(w, h) / 2
        val index: Int = Random.Int(nGraves)
        val shift: Int = Random.Int(2)
        for (i in 0 until nGraves) {
            val pos: Int =
                if (w > h) room.left + 1 + shift + i * 2 + (room.top + 2 + Random.Int(h - 2)) * Level.WIDTH else room.left + 2 + Random.Int(
                    w - 2
                ) + (room.top + 1 + shift + i * 2) * Level.WIDTH
            level.drop(if (i == index) Generator.random() else Gold(), pos).type = Heap.Type.TOMB
        }
    }

    private fun paintStriped(level: Level, room: Room) {
        fill(level, room.left + 1, room.top + 1, room.width() - 1, room.height() - 1, Terrain.EMPTY_SP)
        if (room.width() > room.height()) {
            var i: Int = room.left + 2
            while (i < room.right) {
                fill(level, i, room.top + 1, 1, room.height() - 1, Terrain.HIGH_GRASS)
                i += 2
            }
        } else {
            var i: Int = room.top + 2
            while (i < room.bottom) {
                fill(level, room.left + 1, i, room.width() - 1, 1, Terrain.HIGH_GRASS)
                i += 2
            }
        }
    }

    private fun paintStudy(level: Level, room: Room) {
        fill(level, room.left + 1, room.top + 1, room.width() - 1, room.height() - 1, Terrain.BOOKSHELF)
        fill(level, room.left + 2, room.top + 2, room.width() - 3, room.height() - 3, Terrain.EMPTY_SP)
        for (door in room.connected.values()) {
            if (door.x === room.left) {
                set(level, door.x + 1, door.y, Terrain.EMPTY)
            } else if (door.x === room.right) {
                set(level, door.x - 1, door.y, Terrain.EMPTY)
            } else if (door.y === room.top) {
                set(level, door.x, door.y + 1, Terrain.EMPTY)
            } else if (door.y === room.bottom) {
                set(level, door.x, door.y - 1, Terrain.EMPTY)
            }
        }
        set(level, room.center(), Terrain.PEDESTAL)
    }

    private fun paintBridge(level: Level, room: Room) {
        fill(
            level, room.left + 1, room.top + 1, room.width() - 1, room.height() - 1,
            if (!Dungeon.bossLevel() && !Dungeon.bossLevel(Dungeon.depth + 1) && Random.Int(3) === 0) Terrain.CHASM else Terrain.WATER
        )
        var door1: Point? = null
        var door2: Point? = null
        for (p in room.connected.values()) {
            if (door1 == null) {
                door1 = p
            } else {
                door2 = p
            }
        }
        if (door1.x === room.left && door2.x === room.right ||
            door1.x === room.right && door2.x === room.left
        ) {
            val s: Int = room.width() / 2
            drawInside(level, room, door1, s, Terrain.EMPTY_SP)
            drawInside(level, room, door2, s, Terrain.EMPTY_SP)
            fill(
                level,
                room.center().x,
                Math.min(door1.y, door2.y),
                1,
                Math.abs(door1.y - door2.y) + 1,
                Terrain.EMPTY_SP
            )
        } else if (door1.y === room.top && door2.y === room.bottom ||
            door1.y === room.bottom && door2.y === room.top
        ) {
            val s: Int = room.height() / 2
            drawInside(level, room, door1, s, Terrain.EMPTY_SP)
            drawInside(level, room, door2, s, Terrain.EMPTY_SP)
            fill(
                level,
                Math.min(door1.x, door2.x),
                room.center().y,
                Math.abs(door1.x - door2.x) + 1,
                1,
                Terrain.EMPTY_SP
            )
        } else if (door1.x === door2.x) {
            fill(
                level,
                if (door1.x === room.left) room.left + 1 else room.right - 1,
                Math.min(door1.y, door2.y),
                1,
                Math.abs(door1.y - door2.y) + 1,
                Terrain.EMPTY_SP
            )
        } else if (door1.y === door2.y) {
            fill(
                level,
                Math.min(door1.x, door2.x),
                if (door1.y === room.top) room.top + 1 else room.bottom - 1,
                Math.abs(door1.x - door2.x) + 1,
                1,
                Terrain.EMPTY_SP
            )
        } else if (door1.y === room.top || door1.y === room.bottom) {
            drawInside(level, room, door1, Math.abs(door1.y - door2.y), Terrain.EMPTY_SP)
            drawInside(level, room, door2, Math.abs(door1.x - door2.x), Terrain.EMPTY_SP)
        } else if (door1.x === room.left || door1.x === room.right) {
            drawInside(level, room, door1, Math.abs(door1.x - door2.x), Terrain.EMPTY_SP)
            drawInside(level, room, door2, Math.abs(door1.y - door2.y), Terrain.EMPTY_SP)
        }
    }

    private fun paintFissure(level: Level, room: Room) {
        fill(level, room.left + 1, room.top + 1, room.width() - 1, room.height() - 1, Terrain.EMPTY)
        for (i in room.top + 2 until room.bottom - 1) {
            for (j in room.left + 2 until room.right - 1) {
                val v: Int = Math.min(i - room.top, room.bottom - i)
                val h: Int = Math.min(j - room.left, room.right - j)
                if (Math.min(v, h) > 2 || Random.Int(2) === 0) {
                    set(level, j, i, Terrain.CHASM)
                }
            }
        }
    }
}