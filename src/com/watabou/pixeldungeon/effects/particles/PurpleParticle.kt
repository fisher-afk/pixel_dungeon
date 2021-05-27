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
package com.watabou.pixeldungeon.effects.particles

import com.watabou.noosa.particles.Emitter

class PurpleParticle : PixelParticle() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        speed.set(Random.Float(-5, +5), Random.Float(-5, +5))
        left = lifespan
    }

    fun resetBurst(x: Float, y: Float) {
        revive()
        x = x
        y = y
        speed.polar(Random.Float(PointF.PI2), Random.Float(16, 32))
        left = lifespan
    }

    fun update() {
        super.update()
        // alpha: 1 -> 0; size: 1 -> 5
        size(5 - left / lifespan.also { am = it } * 4)
        // color: 0xFF0044 -> 0x220066
        color(ColorMath.interpolate(0x220066, 0xFF0044, am))
    }

    companion object {
        val MISSILE: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PurpleParticle::class.java) as PurpleParticle).reset(x, y)
            }
        }
        val BURST: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PurpleParticle::class.java) as PurpleParticle).resetBurst(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
    }

    init {
        lifespan = 0.5f
    }
}