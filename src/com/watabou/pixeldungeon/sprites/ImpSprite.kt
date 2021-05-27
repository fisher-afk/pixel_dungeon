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

class ImpSprite : MobSprite() {
    fun link(ch: Char?) {
        super.link(ch)
        if (ch is Imp) {
            alpha(0.4f)
        }
    }

    override fun onComplete(anim: Animation) {
        if (anim === die) {
            emitter().burst(Speck.factory(Speck.WOOL), 15)
            killAndErase()
        } else {
            super.onComplete(anim)
        }
    }

    init {
        texture(Assets.IMP)
        val frames = TextureFilm(texture, 12, 14)
        idle = Animation(10, true)
        idle.frames(
            frames,
            0, 1, 2, 3, 0, 1, 2, 3, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 3, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4
        )
        run = Animation(20, true)
        run.frames(frames, 0)
        die = Animation(10, false)
        die.frames(frames, 0, 3, 2, 1, 0, 3, 2, 1, 0)
        play(idle)
    }
}