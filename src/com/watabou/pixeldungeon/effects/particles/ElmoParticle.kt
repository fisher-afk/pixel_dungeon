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

class ElmoParticle : PixelParticle.Shrinking() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        left = lifespan
        size = 4
        speed.set(0)
    }

    fun update() {
        super.update()
        val p: Float = left / lifespan
        am = if (p > 0.8f) (1 - p) * 5 else 1
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ElmoParticle::class.java) as ElmoParticle).reset(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
    }

    init {
        color(0x22EE66)
        lifespan = 0.6f
        acc.set(0, -80)
    }
}