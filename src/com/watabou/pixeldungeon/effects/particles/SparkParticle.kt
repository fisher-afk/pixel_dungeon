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

class SparkParticle : PixelParticle() {
    fun reset(x: Float, y: Float) {
        revive()
        x = x
        y = y
        lifespan = Random.Float(0.5f, 1.0f)
        left = lifespan
        speed.polar(-Random.Float(3.1415926f), Random.Float(20, 40))
    }

    fun update() {
        super.update()
        size(Random.Float(5 * left / lifespan))
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(SparkParticle::class.java) as SparkParticle).reset(x, y)
            }

            fun lightMode(): Boolean {
                return true
            }
        }
    }

    init {
        size(2)
        acc.set(0, +50)
    }
}