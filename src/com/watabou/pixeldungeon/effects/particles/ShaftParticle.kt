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

class ShaftParticle : PixelParticle() {
    private var offs = 0f
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        offs = -Random.Float(lifespan)
        left = lifespan - offs
    }

    fun update() {
        super.update()
        val p: Float = left / lifespan
        am = if (p < 0.5f) p else 1 - p
        scale.x = (1 - p) * 4
        scale.y = 16 + (1 - p) * 16
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShaftParticle::class.java) as ShaftParticle).reset(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
    }

    init {
        lifespan = 1.2f
        speed.set(0, -6)
    }
}