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

import com.watabou.noosa.Camera

class CavesBossLevel : Level() {
    private var arenaDoor = 0
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_CAVES
    }

    override fun waterTex(): String {
        return Assets.WATER_CAVES
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DOOR, arenaDoor)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        arenaDoor = bundle.getInt(DOOR)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }

    protected override fun build(): Boolean {
        var topMost = Int.MAX_VALUE
        for (i in 0..7) {
            var left: Int
            var right: Int
            var top: Int
            var bottom: Int
            if (Random.Int(2) === 0) {
                left = Random.Int(1, ROOM_LEFT - 3)
                right = ROOM_RIGHT + 3
            } else {
                left = ROOM_LEFT - 3
                right = Random.Int(ROOM_RIGHT + 3, WIDTH - 1)
            }
            if (Random.Int(2) === 0) {
                top = Random.Int(2, ROOM_TOP - 3)
                bottom = ROOM_BOTTOM + 3
            } else {
                top = ROOM_LEFT - 3
                bottom = Random.Int(ROOM_TOP + 3, HEIGHT - 1)
            }
            Painter.fill(this, left, top, right - left + 1, bottom - top + 1, Terrain.EMPTY)
            if (top < topMost) {
                topMost = top
                exit = Random.Int(left, right) + (top - 1) * WIDTH
            }
        }
        map.get(exit) = Terrain.LOCKED_EXIT
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(6) === 0) {
                map.get(i) = Terrain.INACTIVE_TRAP
            }
        }
        Painter.fill(
            this, ROOM_LEFT - 1, ROOM_TOP - 1,
            ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL
        )
        Painter.fill(
            this, ROOM_LEFT, ROOM_TOP + 1,
            ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY
        )
        Painter.fill(
            this, ROOM_LEFT, ROOM_TOP,
            ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.TOXIC_TRAP
        )
        arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * WIDTH
        map.get(arenaDoor) = Terrain.DOOR
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
        for (i in WIDTH + 1 until LENGTH - WIDTH) {
            if (map.get(i) === Terrain.EMPTY) {
                var n = 0
                if (map.get(i + 1) === Terrain.WALL) {
                    n++
                }
                if (map.get(i - 1) === Terrain.WALL) {
                    n++
                }
                if (map.get(i + WIDTH) === Terrain.WALL) {
                    n++
                }
                if (map.get(i - WIDTH) === Terrain.WALL) {
                    n++
                }
                if (Random.Int(8) <= n) {
                    map.get(i) = Terrain.EMPTY_DECO
                }
            }
        }
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.WALL && Random.Int(8) === 0) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        var sign: Int
        do {
            sign = Random.Int(ROOM_LEFT, ROOM_RIGHT) + Random.Int(ROOM_TOP, ROOM_BOTTOM) * WIDTH
        } while (sign == entrance)
        map.get(sign) = Terrain.SIGN
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
        if (!enteredArena && outsideEntraceRoom(cell) && hero === Dungeon.hero) {
            enteredArena = true
            val boss: Mob = Bestiary.mob(Dungeon.depth)
            boss.state = boss.HUNTING
            do {
                boss.pos = Random.Int(LENGTH)
            } while (!passable.get(boss.pos) ||
                !outsideEntraceRoom(boss.pos) ||
                Dungeon.visible.get(boss.pos)
            )
            GameScene.add(boss)
            set(arenaDoor, Terrain.WALL)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            Camera.main.shake(3, 0.7f)
            Sample.INSTANCE.play(Assets.SND_ROCKS)
        }
    }

    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            set(arenaDoor, Terrain.EMPTY_DECO)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
        return super.drop(item, cell)
    }

    private fun outsideEntraceRoom(cell: Int): Boolean {
        val cx: Int = cell % WIDTH
        val cy: Int = cell / WIDTH
        return cx < ROOM_LEFT - 1 || cx > ROOM_RIGHT + 1 || cy < ROOM_TOP - 1 || cy > ROOM_BOTTOM + 1
    }

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.GRASS -> "Fluorescent moss"
            Terrain.HIGH_GRASS -> "Fluorescent mushrooms"
            Terrain.WATER -> "Freezing cold water."
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "The ladder leads up to the upper depth."
            Terrain.EXIT -> "The ladder leads down to the lower depth."
            Terrain.HIGH_GRASS -> "Huge mushrooms block the view."
            Terrain.WALL_DECO -> "A vein of some ore is visible on the wall. Gold?"
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        CavesLevel.addVisuals(this, scene)
    }

    companion object {
        private val ROOM_LEFT: Int = WIDTH / 2 - 2
        private val ROOM_RIGHT: Int = WIDTH / 2 + 2
        private val ROOM_TOP: Int = HEIGHT / 2 - 2
        private val ROOM_BOTTOM: Int = HEIGHT / 2 + 2
        private const val DOOR = "door"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
    }

    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
        viewDistance = 6
    }
}