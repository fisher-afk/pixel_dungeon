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

abstract class Level : Bundlable {
    enum class Feeling {
        NONE, CHASM, WATER, GRASS
    }

    var map: IntArray
    var visited: BooleanArray
    var mapped: BooleanArray
    var viewDistance = if (Dungeon.isChallenged(Challenges.DARKNESS)) 3 else 8
    var feeling = Feeling.NONE
    var entrance = 0
    var exit = 0
    var mobs: HashSet<Mob?>? = null
    var heaps: SparseArray<Heap>? = null
    var blobs: HashMap<Class<out Blob?>, Blob>? = null
    var plants: SparseArray<Plant>? = null
    protected var itemsToSpawn: ArrayList<Item> = ArrayList<Item>()
    var color1 = 0x004400
    var color2 = 0x88CC44
    fun create() {
        resizingNeeded = false
        map = IntArray(LENGTH)
        visited = BooleanArray(LENGTH)
        Arrays.fill(visited, false)
        mapped = BooleanArray(LENGTH)
        Arrays.fill(mapped, false)
        mobs = HashSet<Mob?>()
        heaps = SparseArray<Heap>()
        blobs = HashMap<Class<out Blob?>, Blob>()
        plants = SparseArray<Plant>()
        if (!Dungeon.bossLevel()) {
            addItemToSpawn(Generator.random(Generator.Category.FOOD))
            if (Dungeon.posNeeded()) {
                addItemToSpawn(PotionOfStrength())
                Dungeon.potionOfStrength++
            }
            if (Dungeon.souNeeded()) {
                addItemToSpawn(ScrollOfUpgrade())
                Dungeon.scrollsOfUpgrade++
            }
            if (Dungeon.soeNeeded()) {
                addItemToSpawn(ScrollOfEnchantment())
                Dungeon.scrollsOfEnchantment++
            }
            if (Dungeon.depth > 1) {
                when (Random.Int(10)) {
                    0 -> if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
                        feeling = Feeling.CHASM
                    }
                    1 -> feeling = Feeling.WATER
                    2 -> feeling = Feeling.GRASS
                }
            }
        }
        val pitNeeded = Dungeon.depth > 1 && weakFloorCreated
        do {
            Arrays.fill(map, if (feeling == Feeling.CHASM) Terrain.CHASM else Terrain.WALL)
            pitRoomNeeded = pitNeeded
            weakFloorCreated = false
        } while (!build())
        decorate()
        buildFlagMaps()
        cleanWalls()
        createMobs()
        createItems()
    }

    fun reset() {
        for (mob in mobs.toArray(arrayOfNulls<Mob>(0))) {
            if (!mob.reset()) {
                mobs!!.remove(mob)
            }
        }
        createMobs()
    }

    fun restoreFromBundle(bundle: Bundle) {
        mobs = HashSet<Mob?>()
        heaps = SparseArray<Heap>()
        blobs = HashMap<Class<out Blob?>, Blob>()
        plants = SparseArray<Plant>()
        map = bundle.getIntArray(MAP)
        visited = bundle.getBooleanArray(VISITED)
        mapped = bundle.getBooleanArray(MAPPED)
        entrance = bundle.getInt(ENTRANCE)
        exit = bundle.getInt(EXIT)
        weakFloorCreated = false
        adjustMapSize()
        var collection: Collection<Bundlable> = bundle.getCollection(HEAPS)
        for (h in collection) {
            val heap: Heap = h as Heap
            if (resizingNeeded) {
                heap.pos = adjustPos(heap.pos)
            }
            heaps.put(heap.pos, heap)
        }
        collection = bundle.getCollection(PLANTS)
        for (p in collection) {
            val plant: Plant = p as Plant
            if (resizingNeeded) {
                plant.pos = adjustPos(plant.pos)
            }
            plants.put(plant.pos, plant)
        }
        collection = bundle.getCollection(MOBS)
        for (m in collection) {
            val mob: Mob = m as Mob
            if (mob != null) {
                if (resizingNeeded) {
                    mob.pos = adjustPos(mob.pos)
                }
                mobs!!.add(mob)
            }
        }
        collection = bundle.getCollection(BLOBS)
        for (b in collection) {
            val blob: Blob = b as Blob
            blobs!![blob.getClass()] = blob
        }
        buildFlagMaps()
        cleanWalls()
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(MAP, map)
        bundle.put(VISITED, visited)
        bundle.put(MAPPED, mapped)
        bundle.put(ENTRANCE, entrance)
        bundle.put(EXIT, exit)
        bundle.put(HEAPS, heaps.values())
        bundle.put(PLANTS, plants.values())
        bundle.put(MOBS, mobs)
        bundle.put(BLOBS, blobs!!.values)
    }

    fun tunnelTile(): Int {
        return if (feeling == Feeling.CHASM) Terrain.EMPTY_SP else Terrain.EMPTY
    }

    private fun adjustMapSize() {
        // For levels saved before 1.6.3
        if (map.size < LENGTH) {
            resizingNeeded = true
            loadedMapSize = Math.sqrt(map.size.toDouble()).toInt()
            val map = IntArray(LENGTH)
            Arrays.fill(map, Terrain.WALL)
            val visited = BooleanArray(LENGTH)
            Arrays.fill(visited, false)
            val mapped = BooleanArray(LENGTH)
            Arrays.fill(mapped, false)
            for (i in 0 until loadedMapSize) {
                System.arraycopy(this.map, i * loadedMapSize, map, i * WIDTH, loadedMapSize)
                System.arraycopy(this.visited, i * loadedMapSize, visited, i * WIDTH, loadedMapSize)
                System.arraycopy(this.mapped, i * loadedMapSize, mapped, i * WIDTH, loadedMapSize)
            }
            this.map = map
            this.visited = visited
            this.mapped = mapped
            entrance = adjustPos(entrance)
            exit = adjustPos(exit)
        } else {
            resizingNeeded = false
        }
    }

    fun adjustPos(pos: Int): Int {
        return pos / loadedMapSize * WIDTH + pos % loadedMapSize
    }

    fun tilesTex(): String? {
        return null
    }

    fun waterTex(): String? {
        return null
    }

    protected abstract fun build(): Boolean
    protected abstract fun decorate()
    protected abstract fun createMobs()
    protected abstract fun createItems()
    fun addVisuals(scene: Scene) {
        for (i in 0 until LENGTH) {
            if (pit[i]) {
                scene.add(Wind(i))
                if (i >= WIDTH && water[i - WIDTH]) {
                    scene.add(Flow(i - WIDTH))
                }
            }
        }
    }

    fun nMobs(): Int {
        return 0
    }

    fun respawner(): Actor {
        return object : Actor() {
            protected fun act(): Boolean {
                if (mobs!!.size < nMobs()) {
                    val mob: Mob = Bestiary.mutable(Dungeon.depth)
                    mob.state = mob.WANDERING
                    mob.pos = randomRespawnCell()
                    if (Dungeon.hero.isAlive() && mob.pos !== -1) {
                        GameScene.add(mob)
                        if (Statistics.amuletObtained) {
                            mob.beckon(Dungeon.hero.pos)
                        }
                    }
                }
                spend(if (Dungeon.nightMode || Statistics.amuletObtained) TIME_TO_RESPAWN / 2 else TIME_TO_RESPAWN)
                return true
            }
        }
    }

    fun randomRespawnCell(): Int {
        var cell: Int
        do {
            cell = Random.Int(LENGTH)
        } while (!passable[cell] || Dungeon.visible.get(cell) || Actor.findChar(cell) != null)
        return cell
    }

    fun randomDestination(): Int {
        var cell: Int
        do {
            cell = Random.Int(LENGTH)
        } while (!passable[cell])
        return cell
    }

    fun addItemToSpawn(item: Item?) {
        if (item != null) {
            itemsToSpawn.add(item)
        }
    }

    fun itemToSpanAsPrize(): Item? {
        return if (Random.Int(itemsToSpawn.size + 1) > 0) {
            val item: Item = Random.element(itemsToSpawn)
            itemsToSpawn.remove(item)
            item
        } else {
            null
        }
    }

    private fun buildFlagMaps() {
        for (i in 0 until LENGTH) {
            val flags: Int = Terrain.flags.get(map[i])
            passable[i] = flags and Terrain.PASSABLE !== 0
            losBlocking[i] = flags and Terrain.LOS_BLOCKING !== 0
            flamable[i] = flags and Terrain.FLAMABLE !== 0
            secret[i] = flags and Terrain.SECRET !== 0
            solid[i] = flags and Terrain.SOLID !== 0
            avoid[i] = flags and Terrain.AVOID !== 0
            water[i] = flags and Terrain.LIQUID !== 0
            pit[i] = flags and Terrain.PIT !== 0
        }
        val lastRow = LENGTH - WIDTH
        for (i in 0 until WIDTH) {
            avoid[i] = false
            passable[i] = avoid[i]
            avoid[lastRow + i] = false
            passable[lastRow + i] = avoid[lastRow + i]
        }
        run {
            var i = WIDTH
            while (i < lastRow) {
                avoid[i] = false
                passable[i] = avoid[i]
                avoid[i + WIDTH - 1] = false
                passable[i + WIDTH - 1] = avoid[i + WIDTH - 1]
                i += WIDTH
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (water[i]) {
                map[i] = getWaterTile(i)
            }
            if (pit[i]) {
                if (!pit[i - WIDTH]) {
                    val c = map[i - WIDTH]
                    if (c == Terrain.EMPTY_SP || c == Terrain.STATUE_SP) {
                        map[i] = Terrain.CHASM_FLOOR_SP
                    } else if (water[i - WIDTH]) {
                        map[i] = Terrain.CHASM_WATER
                    } else if (Terrain.flags.get(c) and Terrain.UNSTITCHABLE !== 0) {
                        map[i] = Terrain.CHASM_WALL
                    } else {
                        map[i] = Terrain.CHASM_FLOOR
                    }
                }
            }
        }
    }

    private fun getWaterTile(pos: Int): Int {
        var t: Int = Terrain.WATER_TILES
        for (j in NEIGHBOURS4.indices) {
            if (Terrain.flags.get(map[pos + NEIGHBOURS4[j]]) and Terrain.UNSTITCHABLE !== 0) {
                t += 1 shl j
            }
        }
        return t
    }

    fun destroy(pos: Int) {
        if (Terrain.flags.get(map[pos]) and Terrain.UNSTITCHABLE === 0) {
            Companion[pos] = Terrain.EMBERS
        } else {
            var flood = false
            for (j in NEIGHBOURS4.indices) {
                if (water[pos + NEIGHBOURS4[j]]) {
                    flood = true
                    break
                }
            }
            if (flood) {
                Companion[pos] = getWaterTile(pos)
            } else {
                Companion[pos] = Terrain.EMBERS
            }
        }
    }

    private fun cleanWalls() {
        for (i in 0 until LENGTH) {
            var d = false
            for (j in NEIGHBOURS9.indices) {
                val n = i + NEIGHBOURS9[j]
                if (n >= 0 && n < LENGTH && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
                    d = true
                    break
                }
            }
            if (d) {
                d = false
                for (j in NEIGHBOURS9.indices) {
                    val n = i + NEIGHBOURS9[j]
                    if (n >= 0 && n < LENGTH && !pit[n]) {
                        d = true
                        break
                    }
                }
            }
            discoverable[i] = d
        }
    }

    fun drop(item: Item, cell: Int): Heap {
        var item: Item = item
        var cell = cell
        if (Dungeon.isChallenged(Challenges.NO_FOOD) && item is Food) {
            item = Gold(item.price())
        } else if (Dungeon.isChallenged(Challenges.NO_ARMOR) && item is Armor) {
            item = Gold(item.price())
        } else if (Dungeon.isChallenged(Challenges.NO_HEALING) && item is PotionOfHealing) {
            item = Gold(item.price())
        } else if (Dungeon.isChallenged(Challenges.NO_HERBALISM) && item is SeedPouch) {
            item = Gold(item.price())
        } else if (Dungeon.isChallenged(Challenges.NO_SCROLLS) && (item is Scroll || item is ScrollHolder)) {
            if (item is ScrollOfUpgrade) {
                // These scrolls still can be found
            } else {
                item = Gold(item.price())
            }
        }
        if (map[cell] == Terrain.ALCHEMY && item !is Plant.Seed) {
            var n: Int
            do {
                n = cell + NEIGHBOURS8[Random.Int(8)]
            } while (map[n] != Terrain.EMPTY_SP)
            cell = n
        }
        var heap: Heap? = heaps.get(cell)
        if (heap == null) {
            heap = Heap()
            heap.pos = cell
            if (map[cell] == Terrain.CHASM || Dungeon.level != null && pit[cell]) {
                Dungeon.dropToChasm(item)
                GameScene.discard(heap)
            } else {
                heaps.put(cell, heap)
                GameScene.add(heap)
            }
        } else if (heap.type === Heap.Type.LOCKED_CHEST || heap.type === Heap.Type.CRYSTAL_CHEST) {
            var n: Int
            do {
                n = cell + NEIGHBOURS8[Random.Int(8)]
            } while (!passable[n] && !avoid[n])
            return drop(item, n)
        }
        heap.drop(item)
        if (Dungeon.level != null) {
            press(cell, null)
        }
        return heap
    }

    fun plant(seed: Plant.Seed, pos: Int): Plant? {
        var plant: Plant = plants.get(pos)
        if (plant != null) {
            plant.wither()
        }
        plant = seed.couch(pos)
        plants.put(pos, plant)
        GameScene.add(plant)
        return plant
    }

    fun uproot(pos: Int) {
        plants.delete(pos)
    }

    fun pitCell(): Int {
        return randomRespawnCell()
    }

    fun press(cell: Int, ch: Char?) {
        if (pit[cell] && ch === Dungeon.hero) {
            Chasm.heroFall(cell)
            return
        }
        var trap = false
        when (map[cell]) {
            Terrain.SECRET_TOXIC_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                ToxicTrap.trigger(cell, ch)
            }
            Terrain.TOXIC_TRAP -> {
                trap = true
                ToxicTrap.trigger(cell, ch)
            }
            Terrain.SECRET_FIRE_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                FireTrap.trigger(cell, ch)
            }
            Terrain.FIRE_TRAP -> {
                trap = true
                FireTrap.trigger(cell, ch)
            }
            Terrain.SECRET_PARALYTIC_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                ParalyticTrap.trigger(cell, ch)
            }
            Terrain.PARALYTIC_TRAP -> {
                trap = true
                ParalyticTrap.trigger(cell, ch)
            }
            Terrain.SECRET_POISON_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                PoisonTrap.trigger(cell, ch)
            }
            Terrain.POISON_TRAP -> {
                trap = true
                PoisonTrap.trigger(cell, ch)
            }
            Terrain.SECRET_ALARM_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                AlarmTrap.trigger(cell, ch)
            }
            Terrain.ALARM_TRAP -> {
                trap = true
                AlarmTrap.trigger(cell, ch)
            }
            Terrain.SECRET_LIGHTNING_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                LightningTrap.trigger(cell, ch)
            }
            Terrain.LIGHTNING_TRAP -> {
                trap = true
                LightningTrap.trigger(cell, ch)
            }
            Terrain.SECRET_GRIPPING_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                GrippingTrap.trigger(cell, ch)
            }
            Terrain.GRIPPING_TRAP -> {
                trap = true
                GrippingTrap.trigger(cell, ch)
            }
            Terrain.SECRET_SUMMONING_TRAP -> {
                GLog.i(TXT_HIDDEN_PLATE_CLICKS)
                trap = true
                SummoningTrap.trigger(cell, ch)
            }
            Terrain.SUMMONING_TRAP -> {
                trap = true
                SummoningTrap.trigger(cell, ch)
            }
            Terrain.HIGH_GRASS -> HighGrass.trample(this, cell, ch)
            Terrain.WELL -> WellWater.affectCell(cell)
            Terrain.ALCHEMY -> if (ch == null) {
                Alchemy.transmute(cell)
            }
            Terrain.DOOR -> Door.enter(cell)
        }
        if (trap) {
            Sample.INSTANCE.play(Assets.SND_TRAP)
            if (ch === Dungeon.hero) {
                Dungeon.hero.interrupt()
            }
            Companion[cell] = Terrain.INACTIVE_TRAP
            GameScene.updateMap(cell)
        }
        val plant: Plant = plants.get(cell)
        if (plant != null) {
            plant.activate(ch)
        }
    }

    fun mobPress(mob: Mob) {
        val cell: Int = mob.pos
        if (pit[cell] && !mob.flying) {
            Chasm.mobFall(mob)
            return
        }
        var trap = true
        when (map[cell]) {
            Terrain.TOXIC_TRAP -> ToxicTrap.trigger(cell, mob)
            Terrain.FIRE_TRAP -> FireTrap.trigger(cell, mob)
            Terrain.PARALYTIC_TRAP -> ParalyticTrap.trigger(cell, mob)
            Terrain.POISON_TRAP -> PoisonTrap.trigger(cell, mob)
            Terrain.ALARM_TRAP -> AlarmTrap.trigger(cell, mob)
            Terrain.LIGHTNING_TRAP -> LightningTrap.trigger(cell, mob)
            Terrain.GRIPPING_TRAP -> GrippingTrap.trigger(cell, mob)
            Terrain.SUMMONING_TRAP -> SummoningTrap.trigger(cell, mob)
            Terrain.DOOR -> {
                Door.enter(cell)
                trap = false
            }
            else -> trap = false
        }
        if (trap) {
            if (Dungeon.visible.get(cell)) {
                Sample.INSTANCE.play(Assets.SND_TRAP)
            }
            Companion[cell] = Terrain.INACTIVE_TRAP
            GameScene.updateMap(cell)
        }
        val plant: Plant = plants.get(cell)
        if (plant != null) {
            plant.activate(mob)
        }
    }

    fun updateFieldOfView(c: Char): BooleanArray {
        val cx: Int = c.pos % WIDTH
        val cy: Int = c.pos / WIDTH
        val sighted = c.buff(Blindness::class.java) == null && c.buff(Shadows::class.java) == null && c.isAlive()
        if (sighted) {
            ShadowCaster.castShadow(cx, cy, fieldOfView, c.viewDistance)
        } else {
            Arrays.fill(fieldOfView, false)
        }
        var sense = 1
        if (c.isAlive()) {
            for (b in c.buffs(MindVision::class.java)) {
                sense = Math.max((b as MindVision).distance, sense)
            }
        }
        if (sighted && sense > 1 || !sighted) {
            val ax = Math.max(0, cx - sense)
            val bx = Math.min(cx + sense, WIDTH - 1)
            val ay = Math.max(0, cy - sense)
            val by = Math.min(cy + sense, HEIGHT - 1)
            val len = bx - ax + 1
            var pos = ax + ay * WIDTH
            var y = ay
            while (y <= by) {
                Arrays.fill(fieldOfView, pos, pos + len, true)
                y++
                pos += WIDTH
            }
            for (i in 0 until LENGTH) {
                fieldOfView[i] = fieldOfView[i] and discoverable[i]
            }
        }
        if (c.isAlive()) {
            if (c.buff(MindVision::class.java) != null) {
                for (mob in mobs!!) {
                    val p: Int = mob.pos
                    fieldOfView[p] = true
                    fieldOfView[p + 1] = true
                    fieldOfView[p - 1] = true
                    fieldOfView[p + WIDTH + 1] = true
                    fieldOfView[p + WIDTH - 1] = true
                    fieldOfView[p - WIDTH + 1] = true
                    fieldOfView[p - WIDTH - 1] = true
                    fieldOfView[p + WIDTH] = true
                    fieldOfView[p - WIDTH] = true
                }
            } else if (c === Dungeon.hero && (c as Hero).heroClass === HeroClass.HUNTRESS) {
                for (mob in mobs!!) {
                    val p: Int = mob.pos
                    if (distance(c.pos, p) == 2) {
                        fieldOfView[p] = true
                        fieldOfView[p + 1] = true
                        fieldOfView[p - 1] = true
                        fieldOfView[p + WIDTH + 1] = true
                        fieldOfView[p + WIDTH - 1] = true
                        fieldOfView[p - WIDTH + 1] = true
                        fieldOfView[p - WIDTH - 1] = true
                        fieldOfView[p + WIDTH] = true
                        fieldOfView[p - WIDTH] = true
                    }
                }
            }
            if (c.buff(Awareness::class.java) != null) {
                for (heap in heaps.values()) {
                    val p: Int = heap.pos
                    fieldOfView[p] = true
                    fieldOfView[p + 1] = true
                    fieldOfView[p - 1] = true
                    fieldOfView[p + WIDTH + 1] = true
                    fieldOfView[p + WIDTH - 1] = true
                    fieldOfView[p - WIDTH + 1] = true
                    fieldOfView[p - WIDTH - 1] = true
                    fieldOfView[p + WIDTH] = true
                    fieldOfView[p - WIDTH] = true
                }
            }
        }
        return fieldOfView
    }

    fun tileName(tile: Int): String {
        if (tile >= Terrain.WATER_TILES) {
            return tileName(Terrain.WATER)
        }
        return if (tile != Terrain.CHASM && Terrain.flags.get(tile) and Terrain.PIT !== 0) {
            tileName(Terrain.CHASM)
        } else when (tile) {
            Terrain.CHASM -> "Chasm"
            Terrain.EMPTY, Terrain.EMPTY_SP, Terrain.EMPTY_DECO, Terrain.SECRET_TOXIC_TRAP, Terrain.SECRET_FIRE_TRAP, Terrain.SECRET_PARALYTIC_TRAP, Terrain.SECRET_POISON_TRAP, Terrain.SECRET_ALARM_TRAP, Terrain.SECRET_LIGHTNING_TRAP -> "Floor"
            Terrain.GRASS -> "Grass"
            Terrain.WATER -> "Water"
            Terrain.WALL, Terrain.WALL_DECO, Terrain.SECRET_DOOR -> "Wall"
            Terrain.DOOR -> "Closed door"
            Terrain.OPEN_DOOR -> "Open door"
            Terrain.ENTRANCE -> "Depth entrance"
            Terrain.EXIT -> "Depth exit"
            Terrain.EMBERS -> "Embers"
            Terrain.LOCKED_DOOR -> "Locked door"
            Terrain.PEDESTAL -> "Pedestal"
            Terrain.BARRICADE -> "Barricade"
            Terrain.HIGH_GRASS -> "High grass"
            Terrain.LOCKED_EXIT -> "Locked depth exit"
            Terrain.UNLOCKED_EXIT -> "Unlocked depth exit"
            Terrain.SIGN -> "Sign"
            Terrain.WELL -> "Well"
            Terrain.EMPTY_WELL -> "Empty well"
            Terrain.STATUE, Terrain.STATUE_SP -> "Statue"
            Terrain.TOXIC_TRAP -> "Toxic gas trap"
            Terrain.FIRE_TRAP -> "Fire trap"
            Terrain.PARALYTIC_TRAP -> "Paralytic gas trap"
            Terrain.POISON_TRAP -> "Poison dart trap"
            Terrain.ALARM_TRAP -> "Alarm trap"
            Terrain.LIGHTNING_TRAP -> "Lightning trap"
            Terrain.GRIPPING_TRAP -> "Gripping trap"
            Terrain.SUMMONING_TRAP -> "Summoning trap"
            Terrain.INACTIVE_TRAP -> "Triggered trap"
            Terrain.BOOKSHELF -> "Bookshelf"
            Terrain.ALCHEMY -> "Alchemy pot"
            else -> "???"
        }
    }

    fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.CHASM -> "You can't see the bottom."
            Terrain.WATER -> "In case of burning step into the water to extinguish the fire."
            Terrain.ENTRANCE -> "Stairs lead up to the upper depth."
            Terrain.EXIT, Terrain.UNLOCKED_EXIT -> "Stairs lead down to the lower depth."
            Terrain.EMBERS -> "Embers cover the floor."
            Terrain.HIGH_GRASS -> "Dense vegetation blocks the view."
            Terrain.LOCKED_DOOR -> "This door is locked, you need a matching key to unlock it."
            Terrain.LOCKED_EXIT -> "Heavy bars block the stairs leading down."
            Terrain.BARRICADE -> "The wooden barricade is firmly set but has dried over the years. Might it burn?"
            Terrain.SIGN -> "You can't read the text from here."
            Terrain.TOXIC_TRAP, Terrain.FIRE_TRAP, Terrain.PARALYTIC_TRAP, Terrain.POISON_TRAP, Terrain.ALARM_TRAP, Terrain.LIGHTNING_TRAP, Terrain.GRIPPING_TRAP, Terrain.SUMMONING_TRAP -> "Stepping onto a hidden pressure plate will activate the trap."
            Terrain.INACTIVE_TRAP -> "The trap has been triggered before and it's not dangerous anymore."
            Terrain.STATUE, Terrain.STATUE_SP -> "Someone wanted to adorn this place, but failed, obviously."
            Terrain.ALCHEMY -> "Drop some seeds here to cook a potion."
            Terrain.EMPTY_WELL -> "The well has run dry."
            else -> {
                if (tile >= Terrain.WATER_TILES) {
                    return tileDesc(Terrain.WATER)
                }
                if (Terrain.flags.get(tile) and Terrain.PIT !== 0) {
                    tileDesc(Terrain.CHASM)
                } else ""
            }
        }
    }

    companion object {
        const val WIDTH = 32
        const val HEIGHT = 32
        const val LENGTH = WIDTH * HEIGHT
        val NEIGHBOURS4 = intArrayOf(-WIDTH, +1, +WIDTH, -1)
        val NEIGHBOURS8 = intArrayOf(+1, -1, +WIDTH, -WIDTH, +1 + WIDTH, +1 - WIDTH, -1 + WIDTH, -1 - WIDTH)
        val NEIGHBOURS9 = intArrayOf(0, +1, -1, +WIDTH, -WIDTH, +1 + WIDTH, +1 - WIDTH, -1 + WIDTH, -1 - WIDTH)
        protected const val TIME_TO_RESPAWN = 50f
        private const val TXT_HIDDEN_PLATE_CLICKS = "A hidden pressure plate clicks!"
        var resizingNeeded = false
        var loadedMapSize = 0
        var fieldOfView = BooleanArray(LENGTH)
        var passable = BooleanArray(LENGTH)
        var losBlocking = BooleanArray(LENGTH)
        var flamable = BooleanArray(LENGTH)
        var secret = BooleanArray(LENGTH)
        var solid = BooleanArray(LENGTH)
        var avoid = BooleanArray(LENGTH)
        var water = BooleanArray(LENGTH)
        var pit = BooleanArray(LENGTH)
        var discoverable = BooleanArray(LENGTH)
        protected var pitRoomNeeded = false
        protected var weakFloorCreated = false
        private const val MAP = "map"
        private const val VISITED = "visited"
        private const val MAPPED = "mapped"
        private const val ENTRANCE = "entrance"
        private const val EXIT = "exit"
        private const val HEAPS = "heaps"
        private const val PLANTS = "plants"
        private const val MOBS = "mobs"
        private const val BLOBS = "blobs"
        operator fun set(cell: Int, terrain: Int) {
            Painter.set(Dungeon.level, cell, terrain)
            val flags: Int = Terrain.flags.get(terrain)
            passable[cell] = flags and Terrain.PASSABLE !== 0
            losBlocking[cell] = flags and Terrain.LOS_BLOCKING !== 0
            flamable[cell] = flags and Terrain.FLAMABLE !== 0
            secret[cell] = flags and Terrain.SECRET !== 0
            solid[cell] = flags and Terrain.SOLID !== 0
            avoid[cell] = flags and Terrain.AVOID !== 0
            pit[cell] = flags and Terrain.PIT !== 0
            water[cell] = terrain == Terrain.WATER || terrain >= Terrain.WATER_TILES
        }

        fun distance(a: Int, b: Int): Int {
            val ax = a % WIDTH
            val ay = a / WIDTH
            val bx = b % WIDTH
            val by = b / WIDTH
            return Math.max(Math.abs(ax - bx), Math.abs(ay - by))
        }

        fun adjacent(a: Int, b: Int): Boolean {
            val diff = Math.abs(a - b)
            return diff == 1 || diff == WIDTH || diff == WIDTH + 1 || diff == WIDTH - 1
        }
    }
}