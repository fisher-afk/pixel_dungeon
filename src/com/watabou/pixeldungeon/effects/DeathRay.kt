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

import com.watabou.noosa.Game

class DeathRay(s: PointF, e: PointF) : Image(Effects.get(Effects.Type.RAY)) {
    private var timeLeft: Float
    fun update() {
        super.update()
        val p = timeLeft / DURATION
        alpha(p)
        scale.set(scale.x, p)
        if (Game.elapsed.let { timeLeft -= it; timeLeft } <= 0) {
            killAndErase()
        }
    }

    fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    companion object {
        private const val A = 180 / Math.PI
        private const val DURATION = 0.5f
    }

    init {
        origin.set(0, height / 2)
        x = s.x - origin.x
        y = s.y - origin.y
        val dx: Float = e.x - s.x
        val dy: Float = e.y - s.y
        angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
        scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / width
        Sample.INSTANCE.play(Assets.SND_RAY)
        timeLeft = DURATION
    }
}