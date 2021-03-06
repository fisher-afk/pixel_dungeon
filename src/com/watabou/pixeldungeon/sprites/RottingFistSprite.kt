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

import com.watabou.noosa.Camera

class RottingFistSprite : MobSprite() {
    override fun attack(cell: Int) {
        super.attack(cell)
        speed.set(0, -FALL_SPEED)
        acc.set(0, FALL_SPEED * 4)
    }

    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === attack) {
            speed.set(0)
            acc.set(0)
            place(ch.pos)
            Camera.main.shake(4, 0.2f)
        }
    }

    companion object {
        private const val FALL_SPEED = 64f
    }

    init {
        texture(Assets.ROTTING)
        val frames = TextureFilm(texture, 24, 17)
        idle = Animation(2, true)
        idle.frames(frames, 0, 0, 1)
        run = Animation(3, true)
        run.frames(frames, 0, 1)
        attack = Animation(2, false)
        attack.frames(frames, 0)
        die = Animation(10, false)
        die.frames(frames, 0, 2, 3, 4)
        play(idle)
    }
}