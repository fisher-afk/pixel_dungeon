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

class HallsLevel : RegularLevel() {
    override fun create() {
        addItemToSpawn(Torch())
        super.create()
    }

    override fun tilesTex(): String {
        return Assets.TILES_HALLS
    }

    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.WATER) 0.55f else 0.40f, 6)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.GRASS) 0.55f else 0.30f, 3)
    }

    protected override fun decorate() {
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map.get(i) === Terrain.EMPTY) {
                var count = 0
                for (j in 0 until NEIGHBOURS8.length) {
                    if (Terrain.flags.get(map.get(i + NEIGHBOURS8.get(j))) and Terrain.PASSABLE > 0) {
                        count++
                    }
                }
                if (Random.Int(80) < count) {
                    map.get(i) = Terrain.EMPTY_DECO
                }
            } else if (map.get(i) === Terrain.WALL && map.get(i - 1) !== Terrain.WALL_DECO && map.get(i - WIDTH) !== Terrain.WALL_DECO && Random.Int(
                    20
                ) === 0
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
            Terrain.BOOKSHELF -> "Books in ancient languages smoulder in the bookshelf."
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }

    private class Stream(private val pos: Int) : Group() {
        private var delay: Float
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
                if (Game.elapsed.let { delay -= it; delay } <= 0) {
                    delay = Random.Float(2)
                    val p: PointF = DungeonTilemap.tileToWorld(pos)
                    (recycle(FireParticle::class.java) as FireParticle).reset(
                        p.x + Random.Float(DungeonTilemap.SIZE),
                        p.y + Random.Float(DungeonTilemap.SIZE)
                    )
                }
            }
        }

        fun draw() {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            super.draw()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        }

        init {
            delay = Random.Float(2)
        }
    }

    class FireParticle : PixelParticle.Shrinking() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            left = lifespan
            speed.set(0, -40)
            size = 4
        }

        fun update() {
            super.update()
            val p: Float = left / lifespan
            am = if (p > 0.8f) (1 - p) * 5 else 1
        }

        init {
            color(0xEE7722)
            lifespan = 1f
            acc.set(0, +80)
        }
    }

    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map.get(i) === 63) {
                    scene.add(Stream(i))
                }
            }
        }
    }

    init {
        minRoomSize = 6
        viewDistance = Math.max(25 - Dungeon.depth, 1)
        color1 = 0x801500
        color2 = 0xa68521
    }
}