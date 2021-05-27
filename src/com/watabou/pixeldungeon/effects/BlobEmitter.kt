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
package com.watabou.pixeldungeon.effects

import com.watabou.noosa.particles.Emitter

class BlobEmitter(blob: Blob) : Emitter() {
    private val blob: Blob
    protected fun emit(index: Int) {
        if (blob.volume <= 0) {
            return
        }
        val map: IntArray = blob.cur
        val size: Float = DungeonTilemap.SIZE
        for (i in 0 until LENGTH) {
            if (map[i] > 0 && Dungeon.visible.get(i)) {
                val x: Float = (i % WIDTH + Random.Float()) * size
                val y: Float = (i / WIDTH + Random.Float()) * size
                factory.emit(this, index, x, y)
            }
        }
    }

    companion object {
        private val WIDTH: Int = Blob.WIDTH
        private val LENGTH: Int = Blob.LENGTH
    }

    init {
        this.blob = blob
        blob.use(this)
    }
}