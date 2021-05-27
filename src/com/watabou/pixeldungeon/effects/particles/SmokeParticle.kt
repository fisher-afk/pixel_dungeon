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

class SmokeParticle : PixelParticle() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        lifespan = Random.Float(0.6f, 1f)
        left = lifespan
        speed.set(Random.Float(-4, +4), Random.Float(-8, +8))
    }

    fun update() {
        super.update()
        val p: Float = left / lifespan
        am = if (p > 0.8f) 2 - 2 * p else p * 0.5f
        size(16 - p * 8)
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(SmokeParticle::class.java) as SmokeParticle).reset(x, y)
            }
        }
    }

    init {
        color(0x222222)
        acc.set(0, -40)
    }
}