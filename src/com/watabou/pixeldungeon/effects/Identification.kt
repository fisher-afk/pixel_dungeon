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

import com.watabou.noosa.Group

class Identification(p: PointF) : Group() {
    fun update() {
        super.update()
        if (countLiving() === 0) {
            killAndErase()
        }
    }

    fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    class Speck(x0: Float, y0: Float, mx: Int, my: Int) : PixelParticle() {
        fun update() {
            super.update()
            am = 1 - Math.abs(left / lifespan - 0.5f) * 2
            am *= am
            size(am * SIZE)
        }

        companion object {
            private const val COLOR = 0x4488CC
            private const val SIZE = 3
        }

        init {
            var x0 = x0
            var y0 = y0
            color(COLOR)
            val x1 = x0 + mx * SIZE
            val y1 = y0 + my * SIZE
            val p: PointF = PointF().polar(Random.Float(2 * PointF.PI), 8)
            x0 += p.x
            y0 += p.y
            val dx = x1 - x0
            val dy = y1 - y0
            x = x0
            y = y0
            speed.set(dx, dy)
            acc.set(-dx / 4, -dy / 4)
            lifespan = 2f
            left = lifespan
        }
    }

    companion object {
        private val DOTS = intArrayOf(
            -1, -3,
            0, -3,
            +1, -3,
            -1, -2,
            +1, -2,
            +1, -1,
            0, 0,
            +1, 0,
            0, +1,
            0, +3
        )
    }

    init {
        var i = 0
        while (i < DOTS.size) {
            add(Speck(p.x, p.y, DOTS[i], DOTS[i + 1]))
            add(Speck(p.x, p.y, DOTS[i], DOTS[i + 1]))
            i += 2
        }
    }
}