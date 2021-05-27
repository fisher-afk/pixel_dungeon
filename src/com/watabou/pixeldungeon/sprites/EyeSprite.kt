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

class EyeSprite : MobSprite() {
    private var attackPos = 0
    override fun attack(pos: Int) {
        attackPos = pos
        super.attack(pos)
    }

    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === attack) {
            if (Dungeon.visible.get(ch.pos) || Dungeon.visible.get(attackPos)) {
                parent.add(DeathRay(center(), DungeonTilemap.tileCenterToWorld(attackPos)))
            }
        }
    }

    init {
        texture(Assets.EYE)
        val frames = TextureFilm(texture, 16, 18)
        idle = Animation(8, true)
        idle.frames(frames, 0, 1, 2)
        run = Animation(12, true)
        run.frames(frames, 5, 6)
        attack = Animation(8, false)
        attack.frames(frames, 4, 3)
        die = Animation(8, false)
        die.frames(frames, 7, 8, 9)
        play(idle)
    }
}