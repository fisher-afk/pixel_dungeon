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

class SpellSprite : Image(Assets.SPELL_ICONS) {
    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    private var target: Char? = null
    private var phase: Phase? = null
    private var duration = 0f
    private var passed = 0f
    fun reset(index: Int) {
        frame(film.get(index))
        origin.set(width / 2, height / 2)
        phase = Phase.FADE_IN
        duration = FADE_IN_TIME
        passed = 0f
    }

    fun update() {
        super.update()
        x = target.sprite.center().x - SIZE / 2
        y = target.sprite.y - SIZE
        when (phase) {
            Phase.FADE_IN -> {
                alpha(passed / duration)
                scale.set(passed / duration)
            }
            Phase.STATIC -> {
            }
            Phase.FADE_OUT -> alpha(1 - passed / duration)
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

    fun kill() {
        super.kill()
        all.remove(target)
    }

    companion object {
        const val FOOD = 0
        const val MAP = 1
        const val CHARGE = 2
        const val MASTERY = 3
        private const val SIZE = 16
        private const val FADE_IN_TIME = 0.2f
        private const val STATIC_TIME = 0.8f
        private const val FADE_OUT_TIME = 0.4f
        private var film: TextureFilm? = null
        private val all = HashMap<Char?, SpellSprite>()
        fun show(ch: Char, index: Int) {
            if (!ch.sprite.visible) {
                return
            }
            val old = all[ch]
            old?.kill()
            val sprite: SpellSprite = GameScene.spellSprite()
            sprite.revive()
            sprite.reset(index)
            sprite.target = ch
            all[ch] = sprite
        }
    }

    init {
        if (film == null) {
            film = TextureFilm(texture, SIZE)
        }
    }
}