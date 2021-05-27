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

class PrisonLevel : RegularLevel() {
    override fun tilesTex(): String {
        return Assets.TILES_PRISON
    }

    override fun waterTex(): String {
        return Assets.WATER_PRISON
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

    protected override fun createMobs() {
        super.createMobs()
        Wandmaker.Quest.spawn(this, roomEntrance)
    }

    protected override fun decorate() {
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map.get(i) === Terrain.EMPTY) {
                var c = 0.05f
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
                (map.get(i + WIDTH) === Terrain.EMPTY || map.get(i + WIDTH) === Terrain.EMPTY_SP) && Random.Int(6) === 0
            ) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (map.get(i) === Terrain.WALL && map.get(i - WIDTH) === Terrain.WALL &&
                (map.get(i + WIDTH) === Terrain.EMPTY || map.get(i + WIDTH) === Terrain.EMPTY_SP) && Random.Int(3) === 0
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
            Terrain.WATER -> "Dark cold water."
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.EMPTY_DECO -> "There are old blood stains on the floor."
            Terrain.BOOKSHELF -> "This is probably a vestige of a prison library. Might it burn?"
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }

    private class Torch(private val pos: Int) : Emitter() {
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
            }
        }

        init {
            val p: PointF = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 1, p.y + 3, 2, 0)
            pour(FlameParticle.FACTORY, 0.15f)
            add(Halo(16, 0xFFFFCC, 0.2f).point(p.x, p.y))
        }
    }

    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map.get(i) === Terrain.WALL_DECO) {
                    scene.add(Torch(i))
                }
            }
        }
    }

    init {
        color1 = 0x6a723d
        color2 = 0x88924c
    }
}