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

class LeafParticle : PixelParticle.Shrinking() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        speed.set(Random.Float(-8, +8), -20)
        left = lifespan
        size = Random.Float(2, 3)
    }

    companion object {
        var color1 = 0
        var color2 = 0
        val GENERAL: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = emitter.recycle(LeafParticle::class.java) as LeafParticle
                p.color(ColorMath.random(0x004400, 0x88CC44))
                p.reset(x, y)
            }
        }
        val LEVEL_SPECIFIC: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = emitter.recycle(LeafParticle::class.java) as LeafParticle
                p.color(ColorMath.random(Dungeon.level.color1, Dungeon.level.color2))
                p.reset(x, y)
            }
        }
    }

    init {
        lifespan = 1.2f
        acc.set(0, 25)
    }
}