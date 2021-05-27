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

class SewerBossLevel : RegularLevel() {
    private var stairs = 0
    override fun tilesTex(): String {
        return Assets.TILES_SEWERS
    }

    override fun waterTex(): String {
        return Assets.WATER_SEWERS
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
            distance = roomEntrance.distance()
            if (retry++ > 10) {
                return false
            }
        } while (distance < minDistance)
        roomEntrance.type = Type.ENTRANCE
        roomExit.type = Type.BOSS_EXIT
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
        room = roomExit.connected.keySet().toArray().get(0)
        if (roomExit.top === room.bottom) {
            return false
        }
        for (r in rooms) {
            if (r.type === Type.NULL && r.connected.size() > 0) {
                r.type = Type.TUNNEL
            }
        }
        val candidates: ArrayList<Room> = ArrayList<Room>()
        for (r in roomExit.neigbours) {
            if (!roomExit.connected.containsKey(r) &&
                (roomExit.left === r.right || roomExit.right === r.left || roomExit.bottom === r.top)
            ) {
                candidates.add(r)
            }
        }
        if (candidates.size > 0) {
            val kingsRoom: Room = Random.element(candidates)
            kingsRoom.connect(roomExit)
            kingsRoom.type = Room.Type.RAT_KING
        }
        paint()
        paintWater()
        paintGrass()
        placeTraps()
        return true
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(0.5f, 5)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(0.40f, 4)
    }

    protected override fun decorate() {
        val start: Int = roomExit.top * WIDTH + roomExit.left + 1
        val end: Int = start + roomExit.width() - 1
        for (i in start until end) {
            if (i != exit) {
                map.get(i) = Terrain.WALL_DECO
                map.get(i + WIDTH) = Terrain.WATER
            } else {
                map.get(i + WIDTH) = Terrain.EMPTY
            }
        }
        while (true) {
            val pos: Int = roomEntrance!!.random()
            if (pos != entrance) {
                map.get(pos) = Terrain.SIGN
                break
            }
        }
    }

    override fun addVisuals(scene: Scene) {
        SewerLevel.addVisuals(this, scene)
    }

    protected override fun createMobs() {
        val mob: Mob = Bestiary.mob(Dungeon.depth)
        mob.pos = roomExit!!.random()
        mobs!!.add(mob)
    }

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

    fun seal() {
        if (entrance !== 0) {
            set(entrance, Terrain.WATER_TILES)
            GameScene.updateMap(entrance)
            GameScene.ripple(entrance)
            stairs = entrance
            entrance = 0
        }
    }

    fun unseal() {
        if (stairs != 0) {
            entrance = stairs
            stairs = 0
            set(entrance, Terrain.ENTRANCE)
            GameScene.updateMap(entrance)
        }
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STAIRS, stairs)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stairs = bundle.getInt(STAIRS)
    }

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Murky water"
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.EMPTY_DECO -> "Wet yellowish moss covers the floor."
            else -> super.tileDesc(tile)
        }
    }

    companion object {
        private const val STAIRS = "stairs"
    }

    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }
}