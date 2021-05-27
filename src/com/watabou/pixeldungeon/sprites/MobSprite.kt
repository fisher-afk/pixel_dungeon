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

import com.watabou.noosa.tweeners.AlphaTweener

class MobSprite : CharSprite() {
    override fun update() {
        sleeping = ch != null && (ch as Mob).state === (ch as Mob).SLEEPEING
        super.update()
    }

    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === die) {
            parent.add(object : AlphaTweener(this, 0, FADE_TIME) {
                protected fun onComplete() {
                    this@MobSprite.killAndErase()
                    parent.erase(this)
                }
            })
        }
    }

    fun fall() {
        origin.set(width / 2, height - DungeonTilemap.SIZE / 2)
        angularSpeed = if (Random.Int(2) === 0) -720 else 720
        parent.add(object : ScaleTweener(this, PointF(0, 0), FALL_TIME) {
            protected fun onComplete() {
                this@MobSprite.killAndErase()
                parent.erase(this)
            }

            protected fun updateValues(progress: Float) {
                super.updateValues(progress)
                am = 1 - progress
            }
        })
    }

    companion object {
        private const val FADE_TIME = 3f
        private const val FALL_TIME = 1f
    }
}