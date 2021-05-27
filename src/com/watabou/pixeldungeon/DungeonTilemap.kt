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
package com.watabou.pixeldungeon

import com.watabou.noosa.Image

class DungeonTilemap : Tilemap(
    Dungeon.level.tilesTex(),
    TextureFilm(Dungeon.level.tilesTex(), SIZE, SIZE)
) {
    fun screenToTile(x: Int, y: Int): Int {
        val p: Point = camera().screenToCamera(x, y).offset(this.point().negate()).invScale(SIZE).floor()
        return if (p.x >= 0 && p.x < Level.WIDTH && p.y >= 0 && p.y < Level.HEIGHT) p.x + p.y * Level.WIDTH else -1
    }

    fun overlapsPoint(x: Float, y: Float): Boolean {
        return true
    }

    fun discover(pos: Int, oldValue: Int) {
        val tile: Image = tile(oldValue)
        tile.point(tileToWorld(pos))

        // For bright mode
        tile.bm = rm
        tile.gm = tile.bm
        tile.rm = tile.gm
        tile.ba = ra
        tile.ga = tile.ba
        tile.ra = tile.ga
        parent.add(tile)
        parent.add(object : AlphaTweener(tile, 0, 0.6f) {
            protected fun onComplete() {
                tile.killAndErase()
                killAndErase()
            }
        })
    }

    fun overlapsScreenPoint(x: Int, y: Int): Boolean {
        return true
    }

    companion object {
        const val SIZE = 16
        private var instance: DungeonTilemap
        fun tileToWorld(pos: Int): PointF {
            return PointF(pos % Level.WIDTH, pos / Level.WIDTH).scale(SIZE)
        }

        fun tileCenterToWorld(pos: Int): PointF {
            return PointF(
                (pos % Level.WIDTH + 0.5f) * SIZE,
                (pos / Level.WIDTH + 0.5f) * SIZE
            )
        }

        fun tile(index: Int): Image {
            val img = Image(instance.texture)
            img.frame(instance.tileset.get(index))
            return img
        }
    }

    init {
        map(Dungeon.level.map, Level.WIDTH)
        instance = this
    }
}