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

class CavesLevel : RegularLevel() {
    override fun tilesTex(): String {
        return Assets.TILES_CAVES
    }

    override fun waterTex(): String {
        return Assets.WATER_CAVES
    }

    protected override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.WATER) 0.60f else 0.45f, 6)
    }

    protected override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Feeling.GRASS) 0.55f else 0.35f, 3)
    }

    protected override fun assignRoomType() {
        super.assignRoomType()
        Blacksmith.Quest.spawn(rooms)
    }

    protected override fun decorate() {
        for (room in rooms) {
            if (room.type !== Room.Type.STANDARD) {
                continue
            }
            if (room.width() <= 3 || room.height() <= 3) {
                continue
            }
            val s: Int = room.square()
            if (Random.Int(s) > 8) {
                val corner: Int = room.left + 1 + (room.top + 1) * WIDTH
                if (map.get(corner - 1) === Terrain.WALL && map.get(corner - WIDTH) === Terrain.WALL) {
                    map.get(corner) = Terrain.WALL
                }
            }
            if (Random.Int(s) > 8) {
                val corner: Int = room.right - 1 + (room.top + 1) * WIDTH
                if (map.get(corner + 1) === Terrain.WALL && map.get(corner - WIDTH) === Terrain.WALL) {
                    map.get(corner) = Terrain.WALL
                }
            }
            if (Random.Int(s) > 8) {
                val corner: Int = room.left + 1 + (room.bottom - 1) * WIDTH
                if (map.get(corner - 1) === Terrain.WALL && map.get(corner + WIDTH) === Terrain.WALL) {
                    map.get(corner) = Terrain.WALL
                }
            }
            if (Random.Int(s) > 8) {
                val corner: Int = room.right - 1 + (room.bottom - 1) * WIDTH
                if (map.get(corner + 1) === Terrain.WALL && map.get(corner + WIDTH) === Terrain.WALL) {
                    map.get(corner) = Terrain.WALL
                }
            }
            for (n in room.connected.keySet()) {
                if ((n.type === Room.Type.STANDARD || n.type === Room.Type.TUNNEL) && Random.Int(3) === 0) {
                    Painter.set(this, room.connected.get(n), Terrain.EMPTY_DECO)
                }
            }
        }
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
                if (Random.Int(6) <= n) {
                    map.get(i) = Terrain.EMPTY_DECO
                }
            }
        }
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.WALL && Random.Int(12) === 0) {
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
        if (Dungeon.bossLevel(Dungeon.depth + 1)) {
            return
        }
        for (r in rooms) {
            if (r.type === Type.STANDARD) {
                for (n in r.neigbours) {
                    if (n.type === Type.STANDARD && !r.connected.containsKey(n)) {
                        val w: Rect = r.intersect(n)
                        if (w.left === w.right && w.bottom - w.top >= 5) {
                            w.top += 2
                            w.bottom -= 1
                            w.right++
                            Painter.fill(this, w.left, w.top, 1, w.height(), Terrain.CHASM)
                        } else if (w.top === w.bottom && w.right - w.left >= 5) {
                            w.left += 2
                            w.right -= 1
                            w.bottom++
                            Painter.fill(this, w.left, w.top, w.width(), 1, Terrain.CHASM)
                        }
                    }
                }
            }
        }
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
            Terrain.BOOKSHELF -> "Who would need a bookshelf in a cave?"
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }

    private class Vein(private val pos: Int) : Group() {
        private var delay: Float
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
                if (Game.elapsed.let { delay -= it; delay } <= 0) {
                    delay = Random.Float()
                    val p: PointF = DungeonTilemap.tileToWorld(pos)
                    (recycle(Sparkle::class.java) as Sparkle).reset(
                        p.x + Random.Float(DungeonTilemap.SIZE),
                        p.y + Random.Float(DungeonTilemap.SIZE)
                    )
                }
            }
        }

        init {
            delay = Random.Float(2)
        }
    }

    class Sparkle : PixelParticle() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            lifespan = 0.5f
            left = lifespan
        }

        fun update() {
            super.update()
            val p: Float = left / lifespan
            size((if (p < 0.5f) p * 2 else (1 - p) * 2.also { am = it }) * 2)
        }
    }

    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map.get(i) === Terrain.WALL_DECO) {
                    scene.add(Vein(i))
                }
            }
        }
    }

    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
        viewDistance = 6
    }
}