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

import com.watabou.pixeldungeon.Bones

abstract class RegularLevel : Level() {
    protected var rooms: HashSet<Room?>? = null
    protected var roomEntrance: Room? = null
    protected var roomExit: Room? = null
    protected var specials: ArrayList<Room.Type>? = null
    var secretDoors = 0
    protected override fun build(): Boolean {
        if (!initRooms()) {
            return false
        }
        var distance: Int
        var retry = 0
        val minDistance = Math.sqrt(rooms!!.size.toDouble()).toInt()
        do {
            do {
                roomEntrance = Random.element(rooms)
            } while (roomEntrance.width() < 4 || roomEntrance.height() < 4)
            do {
                roomExit = Random.element(rooms)
            } while (roomExit === roomEntrance || roomExit.width() < 4 || roomExit.height() < 4)
            Graph.buildDistanceMap(rooms, roomExit)
            distance = roomEntrance.distance()
            if (retry++ > 10) {
                return false
            }
        } while (distance < minDistance)
        roomEntrance.type = Type.ENTRANCE
        roomExit.type = Type.EXIT
        val connected: HashSet<Room?> = HashSet<Room?>()
        connected.add(roomEntrance)
        Graph.buildDistanceMap(rooms, roomExit)
        var path: List<Room> = Graph.buildPath(rooms, roomEntrance, roomExit)
        var room: Room? = roomEntrance
        for (next in path) {
            room!!.connect(next)
            room = next
            connected.add(room)
        }
        Graph.setPrice(path, roomEntrance.distance)
        Graph.buildDistanceMap(rooms, roomExit)
        path = Graph.buildPath(rooms, roomEntrance, roomExit)
        room = roomEntrance
        for (next in path) {
            room!!.connect(next)
            room = next
            connected.add(room)
        }
        val nConnected = (rooms!!.size * Random.Float(0.5f, 0.7f)) as Int
        while (connected.size < nConnected) {
            val cr: Room = Random.element(connected)
            val or: Room = Random.element(cr.neigbours)
            if (!connected.contains(or)) {
                cr.connect(or)
                connected.add(or)
            }
        }
        if (Dungeon.shopOnLevel()) {
            var shop: Room? = null
            for (r in roomEntrance.connected.keySet()) {
                if (r.connected.size() === 1 && r.width() >= 5 && r.height() >= 5) {
                    shop = r
                    break
                }
            }
            if (shop == null) {
                return false
            } else {
                shop.type = Room.Type.SHOP
            }
        }
        specials = ArrayList<Room.Type>(Room.SPECIALS)
        if (Dungeon.bossLevel(Dungeon.depth + 1)) {
            specials!!.remove(Room.Type.WEAK_FLOOR)
        }
        assignRoomType()
        paint()
        paintWater()
        paintGrass()
        placeTraps()
        return true
    }

    protected fun initRooms(): Boolean {
        rooms = HashSet<Room?>()
        split(Rect(0, 0, WIDTH - 1, HEIGHT - 1))
        if (rooms!!.size < 8) {
            return false
        }
        val ra: Array<Room> = rooms!!.toArray(arrayOfNulls<Room>(0))
        for (i in 0 until ra.size - 1) {
            for (j in i + 1 until ra.size) {
                ra[i].addNeigbour(ra[j])
            }
        }
        return true
    }

    protected fun assignRoomType() {
        var specialRooms = 0
        for (r in rooms!!) {
            if (r!!.type === Type.NULL &&
                r!!.connected.size() === 1
            ) {
                if (specials!!.size > 0 && r.width() > 3 && r.height() > 3 && Random.Int(specialRooms * specialRooms + 2) === 0) {
                    if (pitRoomNeeded) {
                        r!!.type = Type.PIT
                        pitRoomNeeded = false
                        specials!!.remove(Type.ARMORY)
                        specials!!.remove(Type.CRYPT)
                        specials!!.remove(Type.LABORATORY)
                        specials!!.remove(Type.LIBRARY)
                        specials!!.remove(Type.STATUE)
                        specials!!.remove(Type.TREASURY)
                        specials!!.remove(Type.VAULT)
                        specials!!.remove(Type.WEAK_FLOOR)
                    } else if (Dungeon.depth % 5 === 2 && specials!!.contains(Type.LABORATORY)) {
                        r!!.type = Type.LABORATORY
                    } else {
                        val n = specials!!.size
                        r!!.type = specials!![Math.min(Random.Int(n), Random.Int(n))]
                        if (r!!.type === Type.WEAK_FLOOR) {
                            weakFloorCreated = true
                        }
                    }
                    Room.useType(r!!.type)
                    specials!!.remove(r!!.type)
                    specialRooms++
                } else if (Random.Int(2) === 0) {
                    val neigbours: HashSet<Room> = HashSet<Room>()
                    for (n in r!!.neigbours) {
                        if (!r!!.connected.containsKey(n) &&
                            !Room.SPECIALS.contains(n.type) && n.type !== Type.PIT
                        ) {
                            neigbours.add(n)
                        }
                    }
                    if (neigbours.size > 1) {
                        r!!.connect(Random.element(neigbours))
                    }
                }
            }
        }
        var count = 0
        for (r in rooms!!) {
            if (r!!.type === Type.NULL) {
                val connections: Int = r!!.connected.size()
                if (connections == 0) {
                } else if (Random.Int(connections * connections) === 0) {
                    r!!.type = Type.STANDARD
                    count++
                } else {
                    r!!.type = Type.TUNNEL
                }
            }
        }
        while (count < 4) {
            val r: Room? = randomRoom(Type.TUNNEL, 1)
            if (r != null) {
                r.type = Type.STANDARD
                count++
            }
        }
    }

    protected fun paintWater() {
        val lake = water()
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && lake[i]) {
                map.get(i) = Terrain.WATER
            }
        }
    }

    protected fun paintGrass() {
        val grass = grass()
        if (feeling === Feeling.GRASS) {
            for (room in rooms!!) {
                if (room!!.type !== Type.NULL && room!!.type !== Type.PASSAGE && room!!.type !== Type.TUNNEL) {
                    grass[room.left + 1 + (room.top + 1) * WIDTH] = true
                    grass[room.right - 1 + (room.top + 1) * WIDTH] = true
                    grass[room.left + 1 + (room.bottom - 1) * WIDTH] = true
                    grass[room.right - 1 + (room.bottom - 1) * WIDTH] = true
                }
            }
        }
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map.get(i) === Terrain.EMPTY && grass[i]) {
                var count = 1
                for (n in NEIGHBOURS8) {
                    if (grass[i + n]) {
                        count++
                    }
                }
                map.get(i) = if (Random.Float() < count / 12f) Terrain.HIGH_GRASS else Terrain.GRASS
            }
        }
    }

    protected abstract fun water(): BooleanArray
    protected abstract fun grass(): BooleanArray
    protected fun placeTraps() {
        val nTraps = nTraps()
        val trapChances = trapChances()
        for (i in 0 until nTraps) {
            val trapPos: Int = Random.Int(LENGTH)
            if (map.get(trapPos) === Terrain.EMPTY) {
                when (Random.chances(trapChances)) {
                    0 -> map.get(trapPos) = Terrain.SECRET_TOXIC_TRAP
                    1 -> map.get(trapPos) = Terrain.SECRET_FIRE_TRAP
                    2 -> map.get(trapPos) = Terrain.SECRET_PARALYTIC_TRAP
                    3 -> map.get(trapPos) = Terrain.SECRET_POISON_TRAP
                    4 -> map.get(trapPos) = Terrain.SECRET_ALARM_TRAP
                    5 -> map.get(trapPos) = Terrain.SECRET_LIGHTNING_TRAP
                    6 -> map.get(trapPos) = Terrain.SECRET_GRIPPING_TRAP
                    7 -> map.get(trapPos) = Terrain.SECRET_SUMMONING_TRAP
                }
            }
        }
    }

    protected fun nTraps(): Int {
        return if (Dungeon.depth <= 1) 0 else Random.Int(1, rooms!!.size + Dungeon.depth)
    }

    protected fun trapChances(): FloatArray {
        return floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
    }

    protected var minRoomSize = 7
    protected var maxRoomSize = 9
    protected fun split(rect: Rect) {
        val w: Int = rect.width()
        val h: Int = rect.height()
        if (w > maxRoomSize && h < minRoomSize) {
            val vw: Int = Random.Int(rect.left + 3, rect.right - 3)
            split(Rect(rect.left, rect.top, vw, rect.bottom))
            split(Rect(vw, rect.top, rect.right, rect.bottom))
        } else if (h > maxRoomSize && w < minRoomSize) {
            val vh: Int = Random.Int(rect.top + 3, rect.bottom - 3)
            split(Rect(rect.left, rect.top, rect.right, vh))
            split(Rect(rect.left, vh, rect.right, rect.bottom))
        } else if (Math.random() <= minRoomSize * minRoomSize / rect.square() && w <= maxRoomSize && h <= maxRoomSize || w < minRoomSize || h < minRoomSize) {
            rooms!!.add(Room().set(rect) as Room)
        } else {
            if (Random.Float() < (w - 2).toFloat() / (w + h - 4)) {
                val vw: Int = Random.Int(rect.left + 3, rect.right - 3)
                split(Rect(rect.left, rect.top, vw, rect.bottom))
                split(Rect(vw, rect.top, rect.right, rect.bottom))
            } else {
                val vh: Int = Random.Int(rect.top + 3, rect.bottom - 3)
                split(Rect(rect.left, rect.top, rect.right, vh))
                split(Rect(rect.left, vh, rect.right, rect.bottom))
            }
        }
    }

    protected fun paint() {
        for (r in rooms!!) {
            if (r!!.type !== Type.NULL) {
                placeDoors(r)
                r!!.type.paint(this, r)
            } else {
                if (feeling === Feeling.CHASM && Random.Int(2) === 0) {
                    Painter.fill(this, r, Terrain.WALL)
                }
            }
        }
        for (r in rooms!!) {
            paintDoors(r)
        }
    }

    private fun placeDoors(r: Room?) {
        for (n in r!!.connected.keySet()) {
            var door: Room.Door? = r!!.connected.get(n)
            if (door == null) {
                val i: Rect = r.intersect(n)
                if (i.width() === 0) {
                    door = Door(
                        i.left,
                        Random.Int(i.top + 1, i.bottom)
                    )
                } else {
                    door = Door(
                        Random.Int(i.left + 1, i.right),
                        i.top
                    )
                }
                r!!.connected.put(n, door)
                n.connected.put(r, door)
            }
        }
    }

    protected fun paintDoors(r: Room?) {
        for (n in r!!.connected.keySet()) {
            if (joinRooms(r, n)) {
                continue
            }
            val d: Room.Door = r!!.connected.get(n)
            val door: Int = d.x + d.y * WIDTH
            when (d!!.type) {
                EMPTY -> map.get(door) = Terrain.EMPTY
                TUNNEL -> map.get(door) = tunnelTile()
                REGULAR -> if (Dungeon.depth <= 1) {
                    map.get(door) = Terrain.DOOR
                } else {
                    val secret = (if (Dungeon.depth < 6) Random.Int(12 - Dungeon.depth) else Random.Int(6)) === 0
                    map.get(door) = if (secret) Terrain.SECRET_DOOR else Terrain.DOOR
                    if (secret) {
                        secretDoors++
                    }
                }
                UNLOCKED -> map.get(door) = Terrain.DOOR
                HIDDEN -> {
                    map.get(door) = Terrain.SECRET_DOOR
                    secretDoors++
                }
                BARRICADE -> map.get(door) = if (Random.Int(3) === 0) Terrain.BOOKSHELF else Terrain.BARRICADE
                LOCKED -> map.get(door) = Terrain.LOCKED_DOOR
            }
        }
    }

    protected fun joinRooms(r: Room?, n: Room): Boolean {
        if (r!!.type !== Room.Type.STANDARD || n.type !== Room.Type.STANDARD) {
            return false
        }
        val w: Rect = r.intersect(n)
        if (w.left === w.right) {
            if (w.bottom - w.top < 3) {
                return false
            }
            if (w.height() === Math.max(r.height(), n.height())) {
                return false
            }
            if (r.width() + n.width() > maxRoomSize) {
                return false
            }
            w.top += 1
            w.bottom -= 0
            w.right++
            Painter.fill(this, w.left, w.top, 1, w.height(), Terrain.EMPTY)
        } else {
            if (w.right - w.left < 3) {
                return false
            }
            if (w.width() === Math.max(r.width(), n.width())) {
                return false
            }
            if (r.height() + n.height() > maxRoomSize) {
                return false
            }
            w.left += 1
            w.right -= 0
            w.bottom++
            Painter.fill(this, w.left, w.top, w.width(), 1, Terrain.EMPTY)
        }
        return true
    }

    override fun nMobs(): Int {
        return 2 + Dungeon.depth % 5 + Random.Int(3)
    }

    protected override fun createMobs() {
        val nMobs = nMobs()
        for (i in 0 until nMobs) {
            val mob: Mob = Bestiary.mob(Dungeon.depth)
            do {
                mob.pos = randomRespawnCell()
            } while (mob.pos === -1)
            mobs!!.add(mob)
            Actor.occupyCell(mob)
        }
    }

    override fun randomRespawnCell(): Int {
        var count = 0
        var cell = -1
        while (true) {
            if (++count > 10) {
                return -1
            }
            val room: Room = randomRoom(Room.Type.STANDARD, 10) ?: continue
            cell = room.random()
            if (!Dungeon.visible.get(cell) && Actor.findChar(cell) == null && Level.passable.get(cell)) {
                return cell
            }
        }
    }

    override fun randomDestination(): Int {
        var cell = -1
        while (true) {
            val room: Room = Random.element(rooms) ?: continue
            cell = room.random()
            if (Level.passable.get(cell)) {
                return cell
            }
        }
    }

    protected override fun createItems() {
        var nItems = 3
        while (Random.Float() < 0.4f) {
            nItems++
        }
        for (i in 0 until nItems) {
            var type: Heap.Type? = null
            type = when (Random.Int(20)) {
                0 -> Heap.Type.SKELETON
                1, 2, 3, 4 -> Heap.Type.CHEST
                5 -> if (Dungeon.depth > 1) Heap.Type.MIMIC else Heap.Type.CHEST
                else -> Heap.Type.HEAP
            }
            drop(Generator.random(), randomDropCell()).type = type
        }
        for (item in itemsToSpawn) {
            var cell = randomDropCell()
            if (item is ScrollOfUpgrade) {
                while (map.get(cell) === Terrain.FIRE_TRAP || map.get(cell) === Terrain.SECRET_FIRE_TRAP) {
                    cell = randomDropCell()
                }
            }
            drop(item, cell).type = Heap.Type.HEAP
        }
        val item: Item = Bones.get()
        if (item != null) {
            drop(item, randomDropCell()).type = Heap.Type.SKELETON
        }
    }

    protected fun randomRoom(type: Room.Type, tries: Int): Room? {
        for (i in 0 until tries) {
            val room: Room = Random.element(rooms)
            if (room.type === type) {
                return room
            }
        }
        return null
    }

    fun room(pos: Int): Room? {
        for (room in rooms!!) {
            if (room!!.type !== Type.NULL && room!!.inside(pos)) {
                return room
            }
        }
        return null
    }

    protected fun randomDropCell(): Int {
        while (true) {
            val room: Room? = randomRoom(Room.Type.STANDARD, 1)
            if (room != null) {
                val pos: Int = room.random()
                if (passable.get(pos)) {
                    return pos
                }
            }
        }
    }

    override fun pitCell(): Int {
        for (room in rooms!!) {
            if (room!!.type === Type.PIT) {
                return room!!.random()
            }
        }
        return super.pitCell()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put("rooms", rooms)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        rooms = HashSet<Room?>(bundle.getCollection("rooms") as Collection<Room?>)
        for (r in rooms!!) {
            if (r!!.type === Type.WEAK_FLOOR) {
                weakFloorCreated = true
                break
            }
        }
    }
}