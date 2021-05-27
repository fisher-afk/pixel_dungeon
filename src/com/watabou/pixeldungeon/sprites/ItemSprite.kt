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
package com.watabou.pixeldungeon.sprites

import com.watabou.gltextures.TextureCache

class ItemSprite @JvmOverloads constructor(image: Int = ItemSpriteSheet.SMTH, glowing: Glowing? = null) :
    MovieClip(Assets.ITEMS) {
    var heap: Heap? = null
    private var glowing: Glowing? = null
    private var phase = 0f
    private var glowUp = false
    private var dropInterval = 0f

    constructor(item: Item) : this(item.image(), item.glowing()) {}

    fun originToCenter() {
        origin.set(SIZE / 2)
    }

    @JvmOverloads
    fun link(heap: Heap? = this.heap) {
        this.heap = heap
        view(heap.image(), heap.glowing())
        place(heap.pos)
    }

    fun revive() {
        super.revive()
        speed.set(0)
        acc.set(0)
        dropInterval = 0f
        heap = null
    }

    fun worldToCamera(cell: Int): PointF {
        val csize: Int = DungeonTilemap.SIZE
        return PointF(
            cell % Level.WIDTH * csize + (csize - SIZE) * 0.5f,
            cell / Level.WIDTH * csize + (csize - SIZE) * 0.5f
        )
    }

    fun place(p: Int) {
        point(worldToCamera(p))
    }

    fun drop() {
        if (heap.isEmpty()) {
            return
        }
        dropInterval = DROP_INTERVAL
        speed.set(0, -100)
        acc.set(0, -speed.y / DROP_INTERVAL * 2)
        if (visible && heap != null && heap.peek() is Gold) {
            CellEmitter.center(heap.pos).burst(Speck.factory(Speck.COIN), 5)
            Sample.INSTANCE.play(Assets.SND_GOLD, 1, 1, Random.Float(0.9f, 1.1f))
        }
    }

    fun drop(from: Int) {
        if (heap.pos === from) {
            drop()
        } else {
            val px: Float = x
            val py: Float = y
            drop()
            place(from)
            speed.offset((px - x) / DROP_INTERVAL, (py - y) / DROP_INTERVAL)
        }
    }

    fun view(image: Int, glowing: Glowing?): ItemSprite {
        frame(film.get(image))
        if (glowing.also { this.glowing = it } == null) {
            resetColor()
        }
        return this
    }

    fun update() {
        super.update()
        visible = heap == null || Dungeon.visible.get(heap.pos)
        if (dropInterval > 0 && Game.elapsed.let { dropInterval -= it; dropInterval } <= 0) {
            speed.set(0)
            acc.set(0)
            place(heap.pos)
            if (visible) {
                var water: Boolean = Level.water.get(heap.pos)
                if (water) {
                    GameScene.ripple(heap.pos)
                } else {
                    val cell: Int = Dungeon.level.map.get(heap.pos)
                    water = cell == Terrain.WELL || cell == Terrain.ALCHEMY
                }
                if (heap.peek() !is Gold) {
                    Sample.INSTANCE.play(if (water) Assets.SND_WATER else Assets.SND_STEP, 0.8f, 0.8f, 1.2f)
                }
            }
        }
        if (visible && glowing != null) {
            if (glowUp && Game.elapsed.let { phase += it; phase } > glowing!!.period) {
                glowUp = false
                phase = glowing!!.period
            } else if (!glowUp && Game.elapsed.let { phase -= it; phase } < 0) {
                glowUp = true
                phase = 0f
            }
            val value = phase / glowing!!.period * 0.6f
            bm = 1 - value
            gm = bm
            rm = gm
            ra = glowing!!.red * value
            ga = glowing!!.green * value
            ba = glowing!!.blue * value
        }
    }

    class Glowing @JvmOverloads constructor(var color: Int, period: Float = 1f) {
        var red: Float
        var green: Float
        var blue: Float
        var period: Float

        companion object {
            val WHITE = Glowing(0xFFFFFF, 0.6f)
        }

        init {
            red = (color shr 16) / 255f
            green = (color shr 8 and 0xFF) / 255f
            blue = (color and 0xFF) / 255f
            this.period = period
        }
    }

    companion object {
        const val SIZE = 16
        private const val DROP_INTERVAL = 0.4f
        protected var film: TextureFilm? = null
        fun pick(index: Int, x: Int, y: Int): Int {
            val bmp: Bitmap = TextureCache.get(Assets.ITEMS).bitmap
            val rows: Int = bmp.getWidth() / SIZE
            val row = index / rows
            val col = index % rows
            return bmp.getPixel(col * SIZE + x, row * SIZE + y)
        }
    }

    init {
        if (film == null) {
            film = TextureFilm(texture, SIZE, SIZE)
        }
        view(image, glowing)
    }
}