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

class Enchanting(item: Item) : ItemSprite(item.image(), null) {
    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    private val color: Int
    private var target: Char? = null
    private var phase: Phase
    private var duration: Float
    private var passed: Float
    fun update() {
        super.update()
        x = target.sprite.center().x - SIZE / 2
        y = target.sprite.y - SIZE
        when (phase) {
            Phase.FADE_IN -> {
                alpha(passed / duration * ALPHA)
                scale.set(passed / duration)
            }
            Phase.STATIC -> tint(color, passed / duration * 0.8f)
            Phase.FADE_OUT -> {
                alpha((1 - passed / duration) * ALPHA)
                scale.set(1 + passed / duration)
            }
        }
        if (Game.elapsed.let { passed += it; passed } > duration) {
            when (phase) {
                Phase.FADE_IN -> {
                    phase = Phase.STATIC
                    duration = STATIC_TIME
                }
                Phase.STATIC -> {
                    phase = Phase.FADE_OUT
                    duration = FADE_OUT_TIME
                }
                Phase.FADE_OUT -> kill()
            }
            passed = 0f
        }
    }

    companion object {
        private const val SIZE = 16
        private const val FADE_IN_TIME = 0.2f
        private const val STATIC_TIME = 1.0f
        private const val FADE_OUT_TIME = 0.4f
        private const val ALPHA = 0.6f
        fun show(ch: Char, item: Item) {
            if (!ch.sprite.visible) {
                return
            }
            val sprite = Enchanting(item)
            sprite.target = ch
            ch.sprite.parent.add(sprite)
        }
    }

    init {
        originToCenter()
        color = item.glowing().color
        phase = Phase.FADE_IN
        duration = FADE_IN_TIME
        passed = 0f
    }
}