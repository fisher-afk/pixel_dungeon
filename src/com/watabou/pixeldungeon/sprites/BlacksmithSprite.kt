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

class BlacksmithSprite : MobSprite() {
    private var emitter: Emitter? = null
    fun link(ch: Char?) {
        super.link(ch)
        emitter = Emitter()
        emitter.autoKill = false
        emitter.pos(x + 7, y + 12)
        parent.add(emitter)
    }

    override fun update() {
        super.update()
        if (emitter != null) {
            emitter.visible = visible
        }
    }

    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (visible && emitter != null && anim === idle) {
            emitter.burst(Speck.factory(Speck.FORGE), 3)
            val volume: Float = 0.2f / Level.distance(ch.pos, Dungeon.hero.pos)
            Sample.INSTANCE.play(Assets.SND_EVOKE, volume, volume, 0.8f)
        }
    }

    init {
        texture(Assets.TROLL)
        val frames = TextureFilm(texture, 13, 16)
        idle = Animation(15, true)
        idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3)
        run = Animation(20, true)
        run.frames(frames, 0)
        die = Animation(20, false)
        die.frames(frames, 0)
        play(idle)
    }
}