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

class CityLevel : RegularLevel() {
    override fun tilesTex(): String {
        return Assets.TILES_CITY
    }

    override fun waterTex(): String {
        return Assets.WATER_CITY
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.WATER) 0.65f else 0.45f, 4)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.GRASS) 0.60f else 0.40f, 3)
    }

    protected override fun assignRoomType() {
        super.assignRoomType()
        for (r in rooms) {
            if (r.type === Type.TUNNEL) {
                r.type = Type.PASSAGE
            }
        }
    }

    protected override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(10) === 0) {
                map.get(i) = Terrain.EMPTY_DECO
            } else if (map.get(i) === Terrain.WALL && Random.Int(8) === 0) {
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
    }

    protected override fun createItems() {
        super.createItems()
        Imp.Quest.spawn(this, roomEntrance)
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
        super.addVisuals(scene)
        addVisuals(this, scene)
    }

    private class Smoke(private val pos: Int) : Emitter() {
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
            }
        }

        companion object {
            private val factory: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(SmokeParticle::class.java) as SmokeParticle
                    p.reset(x, y)
                }
            }
        }

        init {
            val p: PointF = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 4, p.y - 2, 4, 0)
            pour(factory, 0.2f)
        }
    }

    class SmokeParticle : PixelParticle() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            lifespan = 2f
            left = lifespan
        }

        fun update() {
            super.update()
            val p: Float = left / lifespan
            am = if (p > 0.8f) 1 - p else p * 0.25f
            size(8 - p * 4)
        }

        init {
            color(0x000000)
            speed.set(Random.Float(8), -Random.Float(8))
        }
    }

    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map.get(i) === Terrain.WALL_DECO) {
                    scene.add(Smoke(i))
                }
            }
        }
    }

    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }
}