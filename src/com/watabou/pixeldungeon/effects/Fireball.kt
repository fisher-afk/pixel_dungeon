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

import com.watabou.glwrap.Texture

class Fireball : Component() {
    private var bLight: Image? = null
    private var fLight: Image? = null
    private var emitter: Emitter? = null
    private var sparks: Group? = null
    protected fun createChildren() {
        sparks = Group()
        add(sparks)
        bLight = Image(Assets.FIREBALL)
        bLight.frame(BLIGHT)
        bLight.origin.set(bLight.width / 2)
        bLight.angularSpeed = -90
        add(bLight)
        emitter = Emitter()
        emitter.pour(object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = emitter.recycle(Flame::class.java) as Flame
                p.reset()
                p.x = x - p.width / 2
                p.y = y - p.height / 2
            }
        }, 0.1f)
        add(emitter)
        fLight = Image(Assets.FIREBALL)
        fLight.frame(FLIGHT)
        fLight.origin.set(fLight.width / 2)
        fLight.angularSpeed = 360
        add(fLight)
        bLight.texture.filter(Texture.LINEAR, Texture.LINEAR)
    }

    protected fun layout() {
        bLight.x = x - bLight.width / 2
        bLight.y = y - bLight.height / 2
        emitter.pos(
            x - bLight.width / 4,
            y - bLight.height / 4,
            bLight.width / 2,
            bLight.height / 2
        )
        fLight.x = x - fLight.width / 2
        fLight.y = y - fLight.height / 2
    }

    fun update() {
        super.update()
        if (Random.Float() < Game.elapsed) {
            val spark: PixelParticle = sparks.recycle(PixelParticle.Shrinking::class.java) as PixelParticle
            spark.reset(x, y, ColorMath.random(COLOR, 0x66FF66), 2, Random.Float(0.5f, 1.0f))
            spark.speed.set(
                Random.Float(-40, +40),
                Random.Float(-60, +20)
            )
            spark.acc.set(0, +80)
            sparks.add(spark)
        }
    }

    fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    class Flame : Image(Assets.FIREBALL) {
        private var timeLeft = 0f
        fun reset() {
            revive()
            timeLeft = LIFESPAN
            speed.set(0, SPEED)
        }

        fun update() {
            super.update()
            if (Game.elapsed.let { timeLeft -= it; timeLeft } <= 0) {
                kill()
            } else {
                val p = timeLeft / LIFESPAN
                scale.set(p)
                alpha(if (p > 0.8f) (1 - p) * 5f else p * 1.25f)
            }
        }

        companion object {
            private const val LIFESPAN = 1f
            private const val SPEED = -40f
            private const val ACC = -20f
        }

        init {
            frame(if (Random.Int(2) === 0) FLAME1 else FLAME2)
            origin.set(width / 2, height / 2)
            acc.set(0, ACC)
        }
    }

    companion object {
        private val BLIGHT: RectF = RectF(0, 0, 0.25f, 1)
        private val FLIGHT: RectF = RectF(0.25f, 0, 0.5f, 1)
        private val FLAME1: RectF = RectF(0.50f, 0, 0.75f, 1)
        private val FLAME2: RectF = RectF(0.75f, 0, 1.00f, 1)
        private const val COLOR = 0xFF66FF
    }
}