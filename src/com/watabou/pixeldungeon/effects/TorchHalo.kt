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

class TorchHalo(sprite: CharSprite) : Halo(24, 0xFFDDCC, 0.15f) {
    private val target: CharSprite
    private var phase = 0f
    fun update() {
        super.update()
        if (phase < 0) {
            if (Game.elapsed.let { phase += it; phase } >= 0) {
                killAndErase()
            } else {
                scale.set((2 + phase) * radius / RADIUS)
                am = -phase * brightness
            }
        } else if (phase < 1) {
            if (Game.elapsed.let { phase += it; phase } >= 1) {
                phase = 1f
            }
            scale.set(phase * radius / RADIUS)
            am = phase * brightness
        }
        point(target.x + target.width / 2, target.y + target.height / 2)
    }

    fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    fun putOut() {
        phase = -1f
    }

    init {
        target = sprite
        am = 0
    }
}