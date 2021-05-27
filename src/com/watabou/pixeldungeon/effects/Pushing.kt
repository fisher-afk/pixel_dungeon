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
package com.watabou.pixeldungeon.effects

import com.watabou.noosa.Game

class Pushing(ch: Char, from: Int, to: Int) : Actor() {
    private val sprite: CharSprite?
    private val from: Int
    private val to: Int
    private val effect: Effect? = null
    protected fun act(): Boolean {
        return if (sprite != null) {
            if (effect == null) {
                Effect()
            }
            false
        } else {
            Actor.remove(this@Pushing)
            true
        }
    }

    inner class Effect : Visual(0, 0, 0, 0) {
        private val end: PointF
        private var delay: Float
        fun update() {
            super.update()
            if (Game.elapsed.let { delay += it; delay } < Companion.DELAY) {
                sprite.x = x
                sprite.y = y
            } else {
                sprite.point(end)
                killAndErase()
                Actor.remove(this@Pushing)
                next()
            }
        }

        companion object {
            private const val DELAY = 0.15f
        }

        init {
            point(sprite.worldToCamera(from))
            end = sprite.worldToCamera(to)
            speed.set(2 * (end.x - x) / Companion.DELAY, 2 * (end.y - y) / Companion.DELAY)
            acc.set(-speed.x / Companion.DELAY, -speed.y / Companion.DELAY)
            delay = 0f
            sprite.parent.add(this)
        }
    }

    init {
        sprite = ch.sprite
        this.from = from
        this.to = to
    }
}