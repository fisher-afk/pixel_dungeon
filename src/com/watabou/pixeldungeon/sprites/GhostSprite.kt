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
class GhostSprite : MobSprite() {
    fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun die() {
        super.die()
        emitter().start(ShaftParticle.FACTORY, 0.3f, 4)
        emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3)
    }

    override fun blood(): Int {
        return 0xFFFFFF
    }

    init {
        texture(Assets.GHOST)
        val frames = TextureFilm(texture, 14, 15)
        idle = Animation(5, true)
        idle.frames(frames, 0, 1)
        run = Animation(10, true)
        run.frames(frames, 0, 1)
        die = Animation(20, false)
        die.frames(frames, 0)
        play(idle)
    }
}