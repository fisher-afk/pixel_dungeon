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

import com.watabou.noosa.Game

class PlantSprite() : Image(Assets.PLANTS) {
    private enum class State {
        GROWING, NORMAL, WITHERING
    }

    private var state = State.NORMAL
    private var time = 0f
    private var pos = -1

    constructor(image: Int) : this() {
        reset(image)
    }

    fun reset(plant: Plant) {
        revive()
        reset(plant.image)
        alpha(1f)
        pos = plant.pos
        x = pos % Level.WIDTH * DungeonTilemap.SIZE
        y = pos / Level.WIDTH * DungeonTilemap.SIZE
        state = State.GROWING
        time = DELAY
    }

    fun reset(image: Int) {
        frame(frames.get(image))
    }

    fun update() {
        super.update()
        visible = pos == -1 || Dungeon.visible.get(pos)
        when (state) {
            State.GROWING -> if (Game.elapsed.let { time -= it; time } <= 0) {
                state = State.NORMAL
                scale.set(1)
            } else {
                scale.set(1 - time / DELAY)
            }
            State.WITHERING -> if (Game.elapsed.let { time -= it; time } <= 0) {
                super.kill()
            } else {
                alpha(time / DELAY)
            }
            else -> {
            }
        }
    }

    fun kill() {
        state = State.WITHERING
        time = DELAY
    }

    companion object {
        private const val DELAY = 0.2f
        private var frames: TextureFilm? = null
    }

    init {
        if (frames == null) {
            frames = TextureFilm(texture, 16, 16)
        }
        origin.set(8, 12)
    }
}