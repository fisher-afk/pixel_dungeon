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

import com.watabou.gltextures.SmartTexture

class Halo() : Image() {
    protected var radius = RADIUS.toFloat()
    protected var brightness = 1f

    constructor(radius: Float, color: Int, brightness: Float) : this() {
        hardlight(color)
        alpha(brightness.also { this.brightness = it })
        radius(radius)
    }

    fun point(x: Float, y: Float): Halo {
        x = x - RADIUS
        y = y - RADIUS
        return this
    }

    fun radius(value: Float) {
        scale.set(value.also { radius = it } / RADIUS)
    }

    companion object {
        private val CACHE_KEY: Any = Halo::class.java
        protected const val RADIUS = 64
    }

    init {
        if (!TextureCache.contains(CACHE_KEY)) {
            val bmp: Bitmap = Bitmap.createBitmap(RADIUS * 2, RADIUS * 2, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.setColor(-0x1)
            canvas.drawCircle(RADIUS.toFloat(), RADIUS.toFloat(), RADIUS * 0.75f, paint)
            paint.setColor(-0x77000001)
            canvas.drawCircle(RADIUS.toFloat(), RADIUS.toFloat(), RADIUS.toFloat(), paint)
            TextureCache.add(CACHE_KEY, SmartTexture(bmp))
        }
        texture(CACHE_KEY)
        origin.set(RADIUS)
    }
}