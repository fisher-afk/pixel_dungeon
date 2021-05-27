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

class Wound : Image(Effects.get(Effects.Type.WOUND)) {
    private var time = 0f
    fun reset(p: Int) {
        revive()
        x = p % Level.WIDTH * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2
        y = p / Level.WIDTH * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2
        time = TIME_TO_FADE
    }

    fun update() {
        super.update()
        if (Game.elapsed.let { time -= it; time } <= 0) {
            kill()
        } else {
            val p = time / TIME_TO_FADE
            alpha(p)
            scale.x = 1 + p
        }
    }

    companion object {
        private const val TIME_TO_FADE = 0.8f
        fun hit(ch: Char) {
            hit(ch, 0f)
        }

        fun hit(ch: Char, angle: Float) {
            val w = ch.sprite.parent.recycle(Wound::class.java) as Wound
            ch.sprite.parent.bringToFront(w)
            w.reset(ch.pos)
            w.angle = angle
        }

        @JvmOverloads
        fun hit(pos: Int, angle: Float = 0f) {
            val parent: Group = Dungeon.hero.sprite.parent
            val w = parent.recycle(Wound::class.java) as Wound
            parent.bringToFront(w)
            w.reset(pos)
            w.angle = angle
        }
    }

    init {
        origin.set(width / 2, height / 2)
    }
}