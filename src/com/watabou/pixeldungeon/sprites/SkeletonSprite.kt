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
class SkeletonSprite : MobSprite() {
    override fun die() {
        super.die()
        if (Dungeon.visible.get(ch.pos)) {
            emitter().burst(Speck.factory(Speck.BONE), 6)
        }
    }

    override fun blood(): Int {
        return -0x333334
    }

    init {
        texture(Assets.SKELETON)
        val frames = TextureFilm(texture, 12, 15)
        idle = Animation(12, true)
        idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3)
        run = Animation(15, true)
        run.frames(frames, 4, 5, 6, 7, 8, 9)
        attack = Animation(15, false)
        attack.frames(frames, 14, 15, 16)
        die = Animation(12, false)
        die.frames(frames, 10, 11, 12, 13)
        play(idle)
    }
}