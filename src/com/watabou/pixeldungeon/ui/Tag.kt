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
package com.watabou.pixeldungeon.ui

import com.watabou.noosa.Game

class Tag(color: Int) : Button() {
    private val r: Float
    private val g: Float
    private val b: Float
    protected var bg: NinePatch? = null
    protected var lightness = 0f
    protected fun createChildren() {
        super.createChildren()
        bg = Chrome.get(Chrome.Type.TAG)
        add(bg)
    }

    protected fun layout() {
        super.layout()
        bg.x = x
        bg.y = y
        bg.size(width, height)
    }

    fun flash() {
        lightness = 1f
    }

    fun update() {
        super.update()
        if (visible && lightness > 0.5) {
            if (Game.elapsed.let { lightness -= it; lightness } > 0.5) {
                bg.ba = 2 * lightness - 1
                bg.ga = bg.ba
                bg.ra = bg.ga
                bg.rm = 2 * r * (1 - lightness)
                bg.gm = 2 * g * (1 - lightness)
                bg.bm = 2 * b * (1 - lightness)
            } else {
                bg.hardlight(r, g, b)
            }
        }
    }

    init {
        r = (color shr 16) / 255f
        g = (color shr 8 and 0xFF) / 255f
        b = (color and 0xFF) / 255f
    }
}