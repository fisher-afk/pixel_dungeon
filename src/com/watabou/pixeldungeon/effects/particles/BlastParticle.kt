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

class BlastParticle : PixelParticle.Shrinking() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        lifespan = Random.Float()
        left = lifespan
        size = 8
        speed.polar(-Random.Float(3.1415926f), Random.Float(32, 64))
    }

    fun update() {
        super.update()
        am = if (left > 0.8f) (1 - left) * 5 else 1
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(BlastParticle::class.java) as BlastParticle).reset(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
    }

    init {
        color(0xEE7722)
        acc.set(0, +50)
    }
}