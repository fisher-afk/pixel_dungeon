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

class CityBossLevel : Level() {
    private var arenaDoor = 0
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_CITY
    }

    override fun waterTex(): String {
        return Assets.WATER_CITY
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
        Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY)
        Painter.fill(this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY_SP)
        var y = TOP + 1
        while (y < TOP + HALL_HEIGHT) {
            map.get(y * WIDTH + CENTER - 2) = Terrain.STATUE_SP
            map.get(y * WIDTH + CENTER + 2) = Terrain.STATUE_SP
            y += 2
        }
        val left = pedestal(true)
        val right = pedestal(false)
        map.get(right) = Terrain.PEDESTAL
        map.get(left) = map.get(right)
        for (i in left + 1 until right) {
            map.get(i) = Terrain.EMPTY_SP
        }
        exit = (TOP - 1) * WIDTH + CENTER
        map.get(exit) = Terrain.LOCKED_EXIT
        arenaDoor = (TOP + HALL_HEIGHT) * WIDTH + CENTER
        map.get(arenaDoor) = Terrain.DOOR
        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY)
        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF)
        Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF)
        entrance = (TOP + HALL_HEIGHT + 2 + Random.Int(CHAMBER_HEIGHT - 1)) * WIDTH + LEFT + /*1 +*/Random.Int(
            HALL_WIDTH - 2
        )
        map.get(entrance) = Terrain.ENTRANCE
        return true
    }

    protected override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(10) === 0) {
                map.get(i) = Terrain.EMPTY_DECO
            } else if (map.get(i) === Terrain.WALL && Random.Int(8) === 0) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        val sign: Int = arenaDoor + WIDTH + 1
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
                pos = Random.IntRange(LEFT + 1, LEFT + HALL_WIDTH - 2) +
                        Random.IntRange(TOP + HALL_HEIGHT + 1, TOP + HALL_HEIGHT + CHAMBER_HEIGHT) * WIDTH
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
            var count = 0
            do {
                boss.pos = Random.Int(LENGTH)
            } while (!passable.get(boss.pos) ||
                !outsideEntraceRoom(boss.pos) ||
                Dungeon.visible.get(boss.pos) && count++ < 20
            )
            GameScene.add(boss)
            if (Dungeon.visible.get(boss.pos)) {
                boss.notice()
                boss.sprite.alpha(0)
                boss.sprite.parent.add(AlphaTweener(boss.sprite, 1, 0.1f))
            }
            set(arenaDoor, Terrain.LOCKED_DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
    }

    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            set(arenaDoor, Terrain.DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
        return super.drop(item, cell)
    }

    private fun outsideEntraceRoom(cell: Int): Boolean {
        return cell / WIDTH < arenaDoor / WIDTH
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
            Terrain.EXIT -> "A ramp leads down to the lower depth."
            Terrain.WALL_DECO, Terrain.EMPTY_DECO -> "Several tiles are missing here."
            Terrain.EMPTY_SP -> "Thick carpet covers the floor."
            Terrain.STATUE, Terrain.STATUE_SP -> "The statue depicts some dwarf standing in a heroic stance."
            Terrain.BOOKSHELF -> "The rows of books on different disciplines fill the bookshelf."
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        CityLevel.addVisuals(this, scene)
    }

    companion object {
        private const val TOP = 2
        private const val HALL_WIDTH = 7
        private const val HALL_HEIGHT = 15
        private const val CHAMBER_HEIGHT = 3
        private val LEFT: Int = (WIDTH - HALL_WIDTH) / 2
        private val CENTER = LEFT + HALL_WIDTH / 2
        private const val DOOR = "door"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
        fun pedestal(left: Boolean): Int {
            return if (left) {
                (TOP + HALL_HEIGHT / 2) * WIDTH + CENTER - 2
            } else {
                (TOP + HALL_HEIGHT / 2) * WIDTH + CENTER + 2
            }
        }
    }

    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }
}