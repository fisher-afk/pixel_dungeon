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

class PoisonParticle : PixelParticle() {
    fun resetMissile(x: Float, y: Float) {
        revive()
        x = x
        y = y
        left = lifespan
        speed.polar(-Random.Float(3.1415926f), Random.Float(6))
    }

    fun resetSplash(x: Float, y: Float) {
        revive()
        x = x
        y = y
        left = lifespan
        speed.polar(Random.Float(3.1415926f), Random.Float(10, 20))
    }

    fun update() {
        super.update()
        // alpha: 1 -> 0; size: 1 -> 4
        size(4 - left / lifespan.also { am = it } * 3)
        // color: 0x8844FF -> 0x00FF00
        color(ColorMath.interpolate(0x00FF00, 0x8844FF, am))
    }

    companion object {
        val MISSILE: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PoisonParticle::class.java) as PoisonParticle).resetMissile(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
        val SPLASH: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PoisonParticle::class.java) as PoisonParticle).resetSplash(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
    }

    init {
        lifespan = 0.6f
        acc.set(0, +30)
    }
}