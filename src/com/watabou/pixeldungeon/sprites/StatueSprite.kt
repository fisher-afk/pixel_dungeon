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
class StatueSprite : MobSprite() {
    override fun blood(): Int {
        return -0x323249
    }

    init {
        texture(Assets.STATUE)
        val frames = TextureFilm(texture, 12, 15)
        idle = Animation(2, true)
        idle.frames(frames, 0, 0, 0, 0, 0, 1, 1)
        run = Animation(15, true)
        run.frames(frames, 2, 3, 4, 5, 6, 7)
        attack = Animation(12, false)
        attack.frames(frames, 8, 9, 10)
        die = Animation(5, false)
        die.frames(frames, 11, 12, 13, 14, 15, 15)
        play(idle)
    }
}