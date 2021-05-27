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

import com.watabou.noosa.particles.Emitter

object Splash {
    fun at(cell: Int, color: Int, n: Int) {
        at(DungeonTilemap.tileCenterToWorld(cell), color, n)
    }

    fun at(p: PointF?, color: Int, n: Int) {
        if (n <= 0) {
            return
        }
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(p)
        FACTORY.color = color
        FACTORY.dir = -3.1415926f / 2
        FACTORY.cone = 3.1415926f
        emitter.burst(FACTORY, n)
    }

    fun at(p: PointF?, dir: Float, cone: Float, color: Int, n: Int) {
        if (n <= 0) {
            return
        }
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(p)
        FACTORY.color = color
        FACTORY.dir = dir
        FACTORY.cone = cone
        emitter.burst(FACTORY, n)
    }

    private val FACTORY = SplashFactory()

    private class SplashFactory : Emitter.Factory() {
        var color = 0
        var dir = 0f
        var cone = 0f
        fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
            val p: PixelParticle = emitter.recycle(PixelParticle.Shrinking::class.java) as PixelParticle
            p.reset(x, y, color, 4, Random.Float(0.5f, 1.0f))
            p.speed.polar(Random.Float(dir - cone / 2, dir + cone / 2), Random.Float(40, 80))
            p.acc.set(0, +100)
        }
    }
}