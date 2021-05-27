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

class IceBlock(target: CharSprite) : Gizmo() {
    private var phase: Float
    private val target: CharSprite
    fun update() {
        super.update()
        if (Game.elapsed * 2.let { phase += it; phase } < 1) {
            target.tint(0.83f, 1.17f, 1.33f, phase * 0.6f)
        } else {
            target.tint(0.83f, 1.17f, 1.33f, 0.6f)
        }
    }

    fun melt() {
        target.resetColor()
        killAndErase()
        if (visible) {
            Splash.at(target.center(), -0x4d2901, 5)
            Sample.INSTANCE.play(Assets.SND_SHATTER)
        }
    }

    companion object {
        fun freeze(sprite: CharSprite): IceBlock {
            val iceBlock = IceBlock(sprite)
            sprite.parent.add(iceBlock)
            return iceBlock
        }
    }

    init {
        this.target = target
        phase = 0f
    }
}