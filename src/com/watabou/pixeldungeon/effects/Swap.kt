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

class Swap(private val ch1: Char, private val ch2: Char) : Actor() {
    private var eff1: Effect?
    private var eff2: Effect?
    private val delay: Float
    protected fun act(): Boolean {
        return false
    }

    private fun finish(eff: Effect) {
        if (eff === eff1) {
            eff1 = null
        }
        if (eff === eff2) {
            eff2 = null
        }
        if (eff1 == null && eff2 == null) {
            Actor.remove(this)
            next()
            val pos: Int = ch1.pos
            ch1.pos = ch2.pos
            ch2.pos = pos
            if (!ch1.flying) {
                if (ch1 is Mob) {
                    Dungeon.level.mobPress(ch1 as Mob)
                } else {
                    Dungeon.level.press(ch1.pos, ch1)
                }
            }
            if (!ch2.flying) {
                if (ch2 is Mob) {
                    Dungeon.level.mobPress(ch2 as Mob)
                } else {
                    Dungeon.level.press(ch2.pos, ch2)
                }
            }
            if (ch1 === Dungeon.hero || ch2 === Dungeon.hero) {
                Dungeon.observe()
            }
        }
    }

    private inner class Effect(sprite: CharSprite, from: Int, to: Int) : Visual(0, 0, 0, 0) {
        private val sprite: CharSprite
        private val end: PointF
        private var passed: Float
        fun update() {
            super.update()
            if (Game.elapsed.let { passed += it; passed } < delay) {
                sprite.x = x
                sprite.y = y
            } else {
                sprite.point(end)
                killAndErase()
                finish(this)
            }
        }

        init {
            this.sprite = sprite
            point(sprite.worldToCamera(from))
            end = sprite.worldToCamera(to)
            speed.set(2 * (end.x - x) / delay, 2 * (end.y - y) / delay)
            acc.set(-speed.x / delay, -speed.y / delay)
            passed = 0f
            sprite.parent.add(this)
        }
    }

    init {
        delay = Level.distance(ch1.pos, ch2.pos) * 0.1f
        eff1 = Effect(ch1.sprite, ch1.pos, ch2.pos)
        eff2 = Effect(ch2.sprite, ch2.pos, ch1.pos)
        Sample.INSTANCE.play(Assets.SND_TELEPORT)
    }
}