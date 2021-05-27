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

class HallsBossLevel : Level() {
    private var stairs = -1
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_HALLS
    }

    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STAIRS, stairs)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stairs = bundle.getInt(STAIRS)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }

    protected override fun build(): Boolean {
        for (i in 0..4) {
            val top: Int = Random.IntRange(2, ROOM_TOP - 1)
            val bottom: Int = Random.IntRange(ROOM_BOTTOM + 1, 22)
            Painter.fill(this, 2 + i * 4, top, 4, bottom - top + 1, Terrain.EMPTY)
            if (i == 2) {
                exit = i * 4 + 3 + (top - 1) * WIDTH
            }
            for (j in 0..3) {
                if (Random.Int(2) === 0) {
                    val y: Int = Random.IntRange(top + 1, bottom - 1)
                    map.get(i * 4 + j + y * WIDTH) = Terrain.WALL_DECO
                }
            }
        }
        map.get(exit) = Terrain.LOCKED_EXIT
        Painter.fill(
            this, ROOM_LEFT - 1, ROOM_TOP - 1,
            ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL
        )
        Painter.fill(
            this, ROOM_LEFT, ROOM_TOP,
            ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP + 1, Terrain.EMPTY
        )
        entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1) +
                Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * WIDTH
        map.get(entrance) = Terrain.ENTRANCE
        val patch: BooleanArray = Patch.generate(0.45f, 6)
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && patch[i]) {
                map.get(i) = Terrain.WATER
            }
        }
        return true
    }

    protected override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(10) === 0) {
                map.get(i) = Terrain.EMPTY_DECO
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
                pos = Random.IntRange(ROOM_LEFT, ROOM_RIGHT) + Random.IntRange(ROOM_TOP + 1, ROOM_BOTTOM) * WIDTH
            } while (pos == entrance || map.get(pos) === Terrain.SIGN)
            drop(item, pos).type = Heap.Type.SKELETON
        }
    }

    override fun randomRespawnCell(): Int {
        return -1
    }

    fun press(cell: Int, hero: Char) {
        super.press(cell, hero)
        if (!enteredArena && hero === Dungeon.hero && cell != entrance) {
            enteredArena = true
            for (i in ROOM_LEFT - 1..ROOM_RIGHT + 1) {
                doMagic((ROOM_TOP - 1) * WIDTH + i)
                doMagic((ROOM_BOTTOM + 1) * WIDTH + i)
            }
            for (i in ROOM_TOP until ROOM_BOTTOM + 1) {
                doMagic(i * WIDTH + ROOM_LEFT - 1)
                doMagic(i * WIDTH + ROOM_RIGHT + 1)
            }
            doMagic(entrance)
            GameScene.updateMap()
            Dungeon.observe()
            val boss = Yog()
            do {
                boss.pos = Random.Int(LENGTH)
            } while (!passable.get(boss.pos) ||
                Dungeon.visible.get(boss.pos)
            )
            GameScene.add(boss)
            boss.spawnFists()
            stairs = entrance
            entrance = -1
        }
    }

    private fun doMagic(cell: Int) {
        set(cell, Terrain.EMPTY_SP)
        CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.1f, 3)
    }

    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            entrance = stairs
            set(entrance, Terrain.ENTRANCE)
            GameScene.updateMap(entrance)
        }
        return super.drop(item, cell)
    }

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Cold lava"
            Terrain.GRASS -> "Embermoss"
            Terrain.HIGH_GRASS -> "Emberfungi"
            Terrain.STATUE, Terrain.STATUE_SP -> "Pillar"
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "It looks like lava, but it's cold and probably safe to touch."
            Terrain.STATUE, Terrain.STATUE_SP -> "The pillar is made of real humanoid skulls. Awesome."
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        HallsLevel.addVisuals(this, scene)
    }

    companion object {
        private val ROOM_LEFT: Int = WIDTH / 2 - 1
        private val ROOM_RIGHT: Int = WIDTH / 2 + 1
        private val ROOM_TOP: Int = HEIGHT / 2 - 1
        private val ROOM_BOTTOM: Int = HEIGHT / 2 + 1
        private const val STAIRS = "stairs"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
    }

    init {
        color1 = 0x801500
        color2 = 0xa68521
        viewDistance = 3
    }
}