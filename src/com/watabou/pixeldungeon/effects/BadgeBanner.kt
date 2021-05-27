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

class BadgeBanner private constructor(index: Int) : Image(Assets.BADGES) {
    private enum class State {
        FADE_IN, STATIC, FADE_OUT
    }

    private var state: State
    private val index: Int
    private var time: Float
    fun update() {
        super.update()
        time -= Game.elapsed
        if (time >= 0) {
            when (state) {
                State.FADE_IN -> {
                    val p = time / FADE_IN_TIME
                    scale.set((1 + p) * DEFAULT_SCALE)
                    alpha(1 - p)
                }
                State.STATIC -> {
                }
                State.FADE_OUT -> alpha(time / FADE_OUT_TIME)
            }
        } else {
            when (state) {
                State.FADE_IN -> {
                    time = STATIC_TIME
                    state = State.STATIC
                    scale.set(DEFAULT_SCALE)
                    alpha(1)
                    highlight(this, index)
                }
                State.STATIC -> {
                    time = FADE_OUT_TIME
                    state = State.FADE_OUT
                }
                State.FADE_OUT -> killAndErase()
            }
        }
    }

    fun kill() {
        if (current === this) {
            current = null
        }
        super.kill()
    }

    companion object {
        private const val DEFAULT_SCALE = 3f
        private const val FADE_IN_TIME = 0.2f
        private const val STATIC_TIME = 1f
        private const val FADE_OUT_TIME = 1.0f
        private var atlas: TextureFilm? = null
        private var current: BadgeBanner? = null
        fun highlight(image: Image, index: Int) {
            val p = PointF()
            when (index) {
                0, 1, 2, 3 -> p.offset(7, 3)
                4, 5, 6, 7 -> p.offset(6, 5)
                8, 9, 10, 11 -> p.offset(6, 3)
                12, 13, 14, 15 -> p.offset(7, 4)
                16 -> p.offset(6, 3)
                17 -> p.offset(5, 4)
                18 -> p.offset(7, 3)
                20 -> p.offset(7, 3)
                21 -> p.offset(7, 3)
                22 -> p.offset(6, 4)
                23 -> p.offset(4, 5)
                24 -> p.offset(6, 4)
                25 -> p.offset(6, 5)
                26 -> p.offset(5, 5)
                27 -> p.offset(6, 4)
                28 -> p.offset(3, 5)
                29 -> p.offset(5, 4)
                30 -> p.offset(5, 4)
                31 -> p.offset(5, 5)
                32, 33 -> p.offset(7, 4)
                34 -> p.offset(6, 4)
                35 -> p.offset(6, 4)
                36 -> p.offset(6, 5)
                37 -> p.offset(4, 4)
                38 -> p.offset(5, 5)
                39 -> p.offset(5, 4)
                40, 41, 42, 43 -> p.offset(5, 4)
                44, 45, 46, 47 -> p.offset(5, 5)
                48, 49, 50, 51 -> p.offset(7, 4)
                52, 53, 54, 55 -> p.offset(4, 4)
                56 -> p.offset(3, 7)
                57 -> p.offset(4, 5)
                58 -> p.offset(6, 4)
                59 -> p.offset(7, 4)
                60, 61, 62, 63 -> p.offset(4, 4)
            }
            p.x *= image.scale.x
            p.y *= image.scale.y
            p.offset(
                -image.origin.x * (image.scale.x - 1),
                -image.origin.y * (image.scale.y - 1)
            )
            p.offset(image.point())
            val star = Speck()
            star.reset(0, p.x, p.y, Speck.DISCOVER)
            star.camera = image.camera()
            image.parent.add(star)
        }

        fun show(image: Int): BadgeBanner {
            if (current != null) {
                current.killAndErase()
            }
            return BadgeBanner(image).also { current = it }
        }

        fun image(index: Int): Image {
            val image = Image(Assets.BADGES)
            if (atlas == null) {
                atlas = TextureFilm(image.texture, 16, 16)
            }
            image.frame(atlas.get(index))
            return image
        }
    }

    init {
        if (atlas == null) {
            atlas = TextureFilm(texture, 16, 16)
        }
        this.index = index
        frame(atlas.get(index))
        origin.set(width / 2, height / 2)
        alpha(0)
        scale.set(2 * DEFAULT_SCALE)
        state = State.FADE_IN
        time = FADE_IN_TIME
        Sample.INSTANCE.play(Assets.SND_BADGE)
    }
}