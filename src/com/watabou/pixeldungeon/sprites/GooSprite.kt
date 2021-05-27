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
package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.TextureFilm

class GooSprite : MobSprite() {
    private val pump: Animation
    private val jump: Animation
    private var spray: Emitter? = null
    fun pumpUp() {
        play(pump)
    }

    fun play(anim: Animation, force: Boolean) {
        super.play(anim, force)
        if (anim === pump) {
            spray = centerEmitter()
            spray.pour(GooParticle.FACTORY, 0.04f)
        } else if (spray != null) {
            spray.on = false
            spray = null
        }
    }

    override fun blood(): Int {
        return -0x1000000
    }

    class GooParticle : PixelParticle.Shrinking() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            left = lifespan
            size = 4
            speed.polar(-Random.Float(PointF.PI), Random.Float(32, 48))
        }

        fun update() {
            super.update()
            val p: Float = left / lifespan
            am = if (p > 0.5f) (1 - p) * 2f else 1
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(GooParticle::class.java) as GooParticle).reset(x, y)
                }
            }
        }

        init {
            color(0x000000)
            lifespan = 0.3f
            acc.set(0, +50)
        }
    }

    init {
        texture(Assets.GOO)
        val frames = TextureFilm(texture, 20, 14)
        idle = Animation(10, true)
        idle.frames(frames, 0, 1)
        run = Animation(10, true)
        run.frames(frames, 0, 1)
        pump = Animation(20, true)
        pump.frames(frames, 0, 1)
        jump = Animation(1, true)
        jump.frames(frames, 6)
        attack = Animation(10, false)
        attack.frames(frames, 5, 0, 6)
        die = Animation(10, false)
        die.frames(frames, 2, 3, 4)
        play(idle)
    }
}