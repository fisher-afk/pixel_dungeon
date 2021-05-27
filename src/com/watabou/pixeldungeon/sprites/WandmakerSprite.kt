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

import com.watabou.noosa.Game

class WandmakerSprite : MobSprite() {
    private var shield: Shield? = null
    fun link(ch: Char?) {
        super.link(ch)
        if (shield == null) {
            parent.add(Shield().also { shield = it })
        }
    }

    override fun die() {
        super.die()
        if (shield != null) {
            shield!!.putOut()
        }
        emitter().start(ElmoParticle.FACTORY, 0.03f, 60)
        if (visible) {
            Sample.INSTANCE.play(Assets.SND_BURNING)
        }
    }

    inner class Shield : Halo(14, 0xBBAACC, 1f) {
        private var phase: Float
        fun update() {
            super.update()
            if (phase < 1) {
                if (Game.elapsed.let { phase -= it; phase } <= 0) {
                    killAndErase()
                } else {
                    scale.set((2 - phase) * radius / RADIUS)
                    am = phase * -1
                    aa = phase * +1
                }
            }
            if (this@WandmakerSprite.visible.also { visible = it }) {
                val p: PointF = this@WandmakerSprite.center()
                point(p.x, p.y)
            }
        }

        fun draw() {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            super.draw()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        }

        fun putOut() {
            phase = 0.999f
        }

        init {
            am = -1
            aa = +1
            phase = 1f
        }
    }

    init {
        texture(Assets.MAKER)
        val frames = TextureFilm(texture, 12, 14)
        idle = Animation(10, true)
        idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1)
        run = Animation(20, true)
        run.frames(frames, 0)
        die = Animation(20, false)
        die.frames(frames, 0)
        play(idle)
    }
}