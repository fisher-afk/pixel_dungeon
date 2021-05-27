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
class AlbinoSprite : MobSprite() {
    init {
        texture(Assets.RAT)
        val frames = TextureFilm(texture, 16, 15)
        idle = Animation(2, true)
        idle.frames(frames, 16, 16, 16, 17)
        run = Animation(10, true)
        run.frames(frames, 22, 23, 24, 25, 26)
        attack = Animation(15, false)
        attack.frames(frames, 18, 19, 20, 21)
        die = Animation(10, false)
        die.frames(frames, 27, 28, 29, 30)
        play(idle)
    }
}