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

class DM300Sprite : MobSprite() {
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === die) {
            emitter().burst(Speck.factory(Speck.WOOL), 15)
        }
    }

    override fun blood(): Int {
        return -0x78
    }

    init {
        texture(Assets.DM300)
        val frames = TextureFilm(texture, 22, 20)
        idle = Animation(10, true)
        idle.frames(frames, 0, 1)
        run = Animation(10, true)
        run.frames(frames, 2, 3)
        attack = Animation(15, false)
        attack.frames(frames, 4, 5, 6)
        die = Animation(20, false)
        die.frames(frames, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 8)
        play(idle)
    }
}