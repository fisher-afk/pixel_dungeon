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
package com.watabou.pixeldungeon.levels

import com.watabou.noosa.Scene

class LastShopLevel : RegularLevel() {
    override fun tilesTex(): String {
        return Assets.TILES_CITY
    }

    override fun waterTex(): String {
        return Assets.WATER_CITY
    }

    protected override fun build(): Boolean {
        initRooms()
        var distance: Int
        var retry = 0
        val minDistance = Math.sqrt(rooms!!.size().toDouble()).toInt()
        do {
            var innerRetry = 0
            do {
                if (innerRetry++ > 10) {
                    return false
                }
                roomEntrance = Random.element(rooms)
            } while (roomEntrance.width() < 4 || roomEntrance.height() < 4)
            innerRetry = 0
            do {
                if (innerRetry++ > 10) {
                    return false
                }
                roomExit = Random.element(rooms)
            } while (roomExit === roomEntrance || roomExit.width() < 6 || roomExit.height() < 6 || roomExit.top === 0)
            Graph.buildDistanceMap(rooms, roomExit)
            distance = Graph.buildPath(rooms, roomEntrance, roomExit).size()
            if (retry++ > 10) {
                return false
            }
        } while (distance < minDistance)
        roomEntrance.type = Type.ENTRANCE
        roomExit.type = Type.EXIT
        Graph.buildDistanceMap(rooms, roomExit)
        var path: List<Room> = Graph.buildPath(rooms, roomEntrance, roomExit)
        Graph.setPrice(path, roomEntrance.distance)
        Graph.buildDistanceMap(rooms, roomExit)
        path = Graph.buildPath(rooms, roomEntrance, roomExit)
        var room: Room = roomEntrance
        for (next in path) {
            room.connect(next)
            room = next
        }
        var roomShop: Room? = null
        var shopSquare = 0
        for (r in rooms) {
            if (r.type === Type.NULL && r.connected.size() > 0) {
                r.type = Type.PASSAGE
                if (r.square() > shopSquare) {
                    roomShop = r
                    shopSquare = r.square()
                }
            }
        }
        if (roomShop == null || shopSquare < 30) {
            return false
        } else {
            roomShop.type = if (Imp.Quest.isCompleted()) Room.Type.SHOP else Room.Type.STANDARD
        }
        paint()
        paintWater()
        paintGrass()
        return true
    }

    protected override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(10) === 0) {
                map.get(i) = Terrain.EMPTY_DECO
            } else if (map.get(i) === Terrain.WALL && Random.Int(8) === 0) {
                map.get(i) = Terrain.WALL_DECO
            } else if (map.get(i) === Terrain.SECRET_DOOR) {
                map.get(i) = Terrain.DOOR
            }
        }
        if (Imp.Quest.isCompleted()) {
            while (true) {
                val pos: Int = roomEntrance!!.random()
                if (pos != entrance) {
                    map.get(pos) = Terrain.SIGN
                    break
                }
            }
        }
    }

    protected override fun createMobs() {}
    override fun respawner(): Actor? {
        return null
    }

    protected override fun createItems() {
        val item: Item = Bones.get()
        if (item != null) {
            var pos: Int
            do {
                pos = roomEntrance!!.random()
            } while (pos == entrance || map.get(pos) === Terrain.SIGN)
            drop(item, pos).type = Heap.Type.SKELETON
        }
    }

    override fun randomRespawnCell(): Int {
        return -1
    }

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Suspiciously colored water"
            Terrain.HIGH_GRASS -> "High blooming flowers"
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "A ramp leads up to the upper depth."
            Terrain.EXIT -> "A ramp leads down to the Inferno."
            Terrain.WALL_DECO, Terrain.EMPTY_DECO -> "Several tiles are missing here."
            Terrain.EMPTY_SP -> "Thick carpet covers the floor."
            else -> super.tileDesc(tile)
        }
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(0.35f, 4)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(0.30f, 3)
    }

    override fun addVisuals(scene: Scene) {
        CityLevel.addVisuals(this, scene)
    }

    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }
}