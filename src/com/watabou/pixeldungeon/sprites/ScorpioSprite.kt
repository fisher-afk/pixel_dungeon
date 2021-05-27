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

class ScorpioSprite : MobSprite() {
    private var cellToAttack = 0
    override fun blood(): Int {
        return -0xbb00de
    }

    override fun attack(cell: Int) {
        if (!Level.adjacent(cell, ch.pos)) {
            cellToAttack = cell
            turnTo(ch.pos, cell)
            play(zap)
        } else {
            super.attack(cell)
        }
    }

    override fun onComplete(anim: Animation) {
        if (anim === zap) {
            idle()
            (parent.recycle(MissileSprite::class.java) as MissileSprite).reset(
                ch.pos,
                cellToAttack,
                Dart(),
                object : Callback() {
                    fun call() {
                        ch.onAttackComplete()
                    }
                })
        } else {
            super.onComplete(anim)
        }
    }

    init {
        texture(Assets.SCORPIO)
        val frames = TextureFilm(texture, 18, 17)
        idle = Animation(12, true)
        idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 2)
        run = Animation(8, true)
        run.frames(frames, 5, 5, 6, 6)
        attack = Animation(15, false)
        attack.frames(frames, 0, 3, 4)
        zap = attack.clone()
        die = Animation(12, false)
        die.frames(frames, 0, 7, 8, 9, 10)
        play(idle)
    }
}