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

class PrisonBossLevel : RegularLevel() {
    private var anteroom: Room? = null
    private var arenaDoor = 0
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_PRISON
    }

    override fun waterTex(): String {
        return Assets.WATER_PRISON
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ARENA, roomExit)
        bundle.put(DOOR, arenaDoor)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        roomExit = bundle.get(ARENA)
        arenaDoor = bundle.getInt(DOOR)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }

    protected override fun build(): Boolean {
        initRooms()
        var distance: Int
        var retry = 0
        do {
            if (retry++ > 10) {
                return false
            }
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
            } while (roomExit === roomEntrance || roomExit.width() < 7 || roomExit.height() < 7 || roomExit.top === 0)
            Graph.buildDistanceMap(rooms, roomExit)
            distance = Graph.buildPath(rooms, roomEntrance, roomExit).size()
        } while (distance < 3)
        roomEntrance.type = Type.ENTRANCE
        roomExit.type = Type.BOSS_EXIT
        var path: List<Room> = Graph.buildPath(rooms, roomEntrance, roomExit)
        Graph.setPrice(path, roomEntrance.distance)
        Graph.buildDistanceMap(rooms, roomExit)
        path = Graph.buildPath(rooms, roomEntrance, roomExit)
        anteroom = path[path.size - 2]
        anteroom.type = Type.STANDARD
        var room: Room = roomEntrance
        for (next in path) {
            room.connect(next)
            room = next
        }
        for (r in rooms) {
            if (r.type === Type.NULL && r.connected.size() > 0) {
                r.type = Type.PASSAGE
            }
        }
        paint()
        if (roomExit.connected.get(roomExit.connected.keySet().toArray().get(0)).y === roomExit.top) {
            return false
        }
        paintWater()
        paintGrass()
        placeTraps()
        return true
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(0.45f, 5)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(0.30f, 4)
    }

    protected fun paintDoors(r: Room) {
        for (n in r.connected.keySet()) {
            if (r.type === Type.NULL) {
                continue
            }
            val door: Point = r.connected.get(n)
            if (r.type === Room.Type.PASSAGE && n.type === Room.Type.PASSAGE) {
                Painter.set(this, door, Terrain.EMPTY)
            } else {
                Painter.set(this, door, Terrain.DOOR)
            }
        }
    }

    protected override fun placeTraps() {
        val nTraps: Int = nTraps()
        for (i in 0 until nTraps) {
            val trapPos: Int = Random.Int(LENGTH)
            if (map.get(trapPos) === Terrain.EMPTY) {
                map.get(trapPos) = Terrain.POISON_TRAP
            }
        }
    }

    protected override fun decorate() {
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map.get(i) === Terrain.EMPTY) {
                var c = 0.15f
                if (map.get(i + 1) === Terrain.WALL && map.get(i + WIDTH) === Terrain.WALL) {
                    c += 0.2f
                }
                if (map.get(i - 1) === Terrain.WALL && map.get(i + WIDTH) === Terrain.WALL) {
                    c += 0.2f
                }
                if (map.get(i + 1) === Terrain.WALL && map.get(i - WIDTH) === Terrain.WALL) {
                    c += 0.2f
                }
                if (map.get(i - 1) === Terrain.WALL && map.get(i - WIDTH) === Terrain.WALL) {
                    c += 0.2f
                }
                if (Random.Float() < c) {
                    map.get(i) = Terrain.EMPTY_DECO
                }
            }
        }
        for (i in 0 until WIDTH) {
            if (map.get(i) === Terrain.WALL &&
                (map.get(i + WIDTH) === Terrain.EMPTY || map.get(i + WIDTH) === Terrain.EMPTY_SP) && Random.Int(4) === 0
            ) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (map.get(i) === Terrain.WALL && map.get(i - WIDTH) === Terrain.WALL &&
                (map.get(i + WIDTH) === Terrain.EMPTY || map.get(i + WIDTH) === Terrain.EMPTY_SP) && Random.Int(2) === 0
            ) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        while (true) {
            val pos: Int = roomEntrance!!.random()
            if (pos != entrance) {
                map.get(pos) = Terrain.SIGN
                break
            }
        }
        val door: Point = roomExit!!.entrance()
        arenaDoor = door.x + door.y * WIDTH
        Painter.set(this, arenaDoor, Terrain.LOCKED_DOOR)
        Painter.fill(
            this,
            roomExit.left + 2,
            roomExit.top + 2,
            roomExit.width() - 3,
            roomExit.height() - 3,
            Terrain.INACTIVE_TRAP
        )
    }

    protected override fun createMobs() {}
    override fun respawner(): Actor? {
        return null
    }

    protected override fun createItems() {
        var keyPos: Int = anteroom!!.random()
        while (!passable.get(keyPos)) {
            keyPos = anteroom!!.random()
        }
        drop(IronKey(), keyPos).type = Heap.Type.CHEST
        val item: Item = Bones.get()
        if (item != null) {
            var pos: Int
            do {
                pos = roomEntrance!!.random()
            } while (pos == entrance || map.get(pos) === Terrain.SIGN)
            drop(item, pos).type = Heap.Type.SKELETON
        }
    }

    fun press(cell: Int, ch: Char) {
        super.press(cell, ch)
        if (ch === Dungeon.hero && !enteredArena && roomExit!!.inside(cell)) {
            enteredArena = true
            var pos: Int
            do {
                pos = roomExit!!.random()
            } while (pos == cell || Actor.findChar(pos) != null)
            val boss: Mob = Bestiary.mob(Dungeon.depth)
            boss.state = boss.HUNTING
            boss.pos = pos
            GameScene.add(boss)
            boss.notice()
            mobPress(boss)
            set(arenaDoor, Terrain.LOCKED_DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
    }

    override fun drop(item: Item?, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            set(arenaDoor, Terrain.DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
        return super.drop(item, cell)
    }

    override fun randomRespawnCell(): Int {
        return -1
    }

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Dark cold water."
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.EMPTY_DECO -> "There are old blood stains on the floor."
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        PrisonLevel.addVisuals(this, scene)
    }

    companion object {
        private const val ARENA = "arena"
        private const val DOOR = "door"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
    }

    init {
        color1 = 0x6a723d
        color2 = 0x88924c
    }
}