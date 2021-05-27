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

class TenguSprite : MobSprite() {
    private val cast: Animation
    override fun move(from: Int, to: Int) {
        place(to)
        play(run)
        turnTo(from, to)
        isMoving = true
        if (Level.water.get(to)) {
            GameScene.ripple(to)
        }
        ch.onMotionComplete()
    }

    override fun attack(cell: Int) {
        if (!Level.adjacent(cell, ch.pos)) {
            (parent.recycle(MissileSprite::class.java) as MissileSprite).reset(
                ch.pos,
                cell,
                Shuriken(),
                object : Callback() {
                    fun call() {
                        ch.onAttackComplete()
                    }
                })
            play(cast)
            turnTo(ch.pos, cell)
        } else {
            super.attack(cell)
        }
    }

    override fun onComplete(anim: Animation) {
        if (anim === run) {
            isMoving = false
            idle()
        } else {
            super.onComplete(anim)
        }
    }

    init {
        texture(Assets.TENGU)
        val frames = TextureFilm(texture, 14, 16)
        idle = Animation(2, true)
        idle.frames(frames, 0, 0, 0, 1)
        run = Animation(15, false)
        run.frames(frames, 2, 3, 4, 5, 0)
        attack = Animation(15, false)
        attack.frames(frames, 6, 7, 7, 0)
        cast = attack.clone()
        die = Animation(8, false)
        die.frames(frames, 8, 9, 10, 10, 10, 10, 10, 10)
        play(run.clone())
    }
}