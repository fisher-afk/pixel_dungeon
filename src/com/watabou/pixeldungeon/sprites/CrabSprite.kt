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
class CrabSprite : MobSprite() {
    override fun blood(): Int {
        return -0x1580
    }

    init {
        texture(Assets.CRAB)
        val frames = TextureFilm(texture, 16)
        idle = Animation(5, true)
        idle.frames(frames, 0, 1, 0, 2)
        run = Animation(15, true)
        run.frames(frames, 3, 4, 5, 6)
        attack = Animation(12, false)
        attack.frames(frames, 7, 8, 9)
        die = Animation(12, false)
        die.frames(frames, 10, 11, 12, 13)
        play(idle)
    }
}