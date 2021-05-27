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

import com.watabou.gltextures.SmartTexture

class FogOfWar(mapWidth: Int, mapHeight: Int) : Image() {
    private var pixels: IntArray?
    private val pWidth: Int
    private val pHeight: Int
    private var width2: Int
    private var height2: Int
    fun updateVisibility(visible: BooleanArray, visited: BooleanArray, mapped: BooleanArray) {
        if (pixels == null) {
            pixels = IntArray(width2 * height2)
            Arrays.fill(pixels, INVISIBLE)
        }
        for (i in 1 until pHeight - 1) {
            var pos = (pWidth - 1) * i
            for (j in 1 until pWidth - 1) {
                pos++
                var c = INVISIBLE
                if (visible[pos] && visible[pos - (pWidth - 1)] &&
                    visible[pos - 1] && visible[pos - (pWidth - 1) - 1]
                ) {
                    c = VISIBLE
                } else if (visited[pos] && visited[pos - (pWidth - 1)] &&
                    visited[pos - 1] && visited[pos - (pWidth - 1) - 1]
                ) {
                    c = VISITED
                } else if (mapped[pos] && mapped[pos - (pWidth - 1)] &&
                    mapped[pos - 1] && mapped[pos - (pWidth - 1) - 1]
                ) {
                    c = MAPPED
                }
                pixels!![i * width2 + j] = c
            }
        }
        texture.pixels(width2, height2, pixels)
    }

    private inner class FogTexture : SmartTexture(Bitmap.createBitmap(width2, height2, Bitmap.Config.ARGB_8888)) {
        fun reload() {
            super.reload()
            GameScene.afterObserve()
        }

        init {
            filter(Texture.LINEAR, Texture.LINEAR)
            TextureCache.add(FogOfWar::class.java, this)
        }
    }

    companion object {
        private const val VISIBLE = 0x00000000
        private const val VISITED = -0x33eeeeef
        private const val MAPPED = -0x33bbddef
        private const val INVISIBLE = -0x1000000
    }

    init {
        pWidth = mapWidth + 1
        pHeight = mapHeight + 1
        width2 = 1
        while (width2 < pWidth) {
            width2 = width2 shl 1
        }
        height2 = 1
        while (height2 < pHeight) {
            height2 = height2 shl 1
        }
        val size: Float = com.watabou.pixeldungeon.DungeonTilemap.Companion.SIZE.toFloat()
        width = width2 * size
        height = height2 * size
        texture(FogTexture())
        scale.set(
            com.watabou.pixeldungeon.DungeonTilemap.Companion.SIZE,
            com.watabou.pixeldungeon.DungeonTilemap.Companion.SIZE
        )
        y = -size / 2
        x = y
    }
}