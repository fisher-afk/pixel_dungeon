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

class ShadowParticle : PixelParticle.Shrinking() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        speed.set(Random.Float(-5, +5), Random.Float(-5, +5))
        size = 6
        lifespan = 0.5f
        left = lifespan
    }

    fun resetCurse(x: Float, y: Float) {
        revive()
        size = 8
        lifespan = 0.5f
        left = lifespan
        speed.polar(Random.Float(PointF.PI2), Random.Float(16, 32))
        x = x - speed.x * lifespan
        y = y - speed.y * lifespan
    }

    fun resetUp(x: Float, y: Float) {
        revive()
        speed.set(Random.Float(-8, +8), Random.Float(-32, -48))
        x = x
        y = y
        size = 6
        lifespan = 1f
        left = lifespan
    }

    fun update() {
        super.update()
        val p: Float = left / lifespan
        // alpha: 0 -> 1 -> 0; size: 6 -> 0; color: 0x660044 -> 0x000000
        color(ColorMath.interpolate(0x000000, 0x440044, p))
        am = if (p < 0.5f) p * p * 4 else (1 - p) * 2
    }

    companion object {
        val MISSILE: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).reset(x, y)
            }
        }
        val CURSE: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).resetCurse(x, y)
            }
        }
        val UP: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).resetUp(x, y)
            }
        }
    }
}