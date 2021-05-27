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

import com.watabou.noosa.Game

class SewerLevel : RegularLevel() {
    override fun tilesTex(): String {
        return Assets.TILES_SEWERS
    }

    override fun waterTex(): String {
        return Assets.WATER_SEWERS
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.WATER) 0.60f else 0.45f, 5)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.GRASS) 0.60f else 0.40f, 4)
    }

    protected override fun decorate() {
        for (i in 0 until WIDTH) {
            if (map.get(i) === Terrain.WALL && map.get(i + WIDTH) === Terrain.WATER && Random.Int(4) === 0) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (map.get(i) === Terrain.WALL && map.get(i - WIDTH) === Terrain.WALL && map.get(i + WIDTH) === Terrain.WATER && Random.Int(
                    2
                ) === 0
            ) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map.get(i) === Terrain.EMPTY) {
                val count = (if (map.get(i + 1) === Terrain.WALL) 1 else 0) +
                        (if (map.get(i - 1) === Terrain.WALL) 1 else 0) +
                        (if (map.get(i + WIDTH) === Terrain.WALL) 1 else 0) +
                        if (map.get(i - WIDTH) === Terrain.WALL) 1 else 0
                if (Random.Int(16) < count * count) {
                    map.get(i) = Terrain.EMPTY_DECO
                }
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

    protected override fun createMobs() {
        super.createMobs()
        Ghost.Quest.spawn(this)
    }

    protected override fun createItems() {
        if (Dungeon.dewVial && Random.Int(4 - Dungeon.depth) === 0) {
            addItemToSpawn(DewVial())
            Dungeon.dewVial = false
        }
        super.createItems()
    }

    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
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
            Terrain.BOOKSHELF -> "The bookshelf is packed with cheap useless books. Might it burn?"
            else -> super.tileDesc(tile)
        }
    }

    private class Sink(private val pos: Int) : Emitter() {
        private var rippleDelay = 0f
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
                if (Game.elapsed.let { rippleDelay -= it; rippleDelay } <= 0) {
                    GameScene.ripple(pos + WIDTH).y -= DungeonTilemap.SIZE / 2
                    rippleDelay = Random.Float(0.2f, 0.3f)
                }
            }
        }

        companion object {
            private val factory: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(WaterParticle::class.java) as WaterParticle
                    p.reset(x, y)
                }
            }
        }

        init {
            val p: PointF = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 2, p.y + 1, 4, 0)
            pour(factory, 0.05f)
        }
    }

    class WaterParticle : PixelParticle() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            speed.set(Random.Float(-2, +2), 0)
            lifespan = 0.5f
            left = lifespan
        }

        init {
            acc.y = 50
            am = 0.5f
            color(ColorMath.random(0xb6ccc2, 0x3b6653))
            size(2)
        }
    }

    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map.get(i) === Terrain.WALL_DECO) {
                    scene.add(Sink(i))
                }
            }
        }
    }

    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }
}