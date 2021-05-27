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

class DiscardedItemSprite : ItemSprite() {
    override fun drop() {
        scale.set(1)
        am = 1
    }

    override fun update() {
        super.update()
        scale.set(scale.x * 0.9f)
        if (Game.elapsed.let { am -= it; am } <= 0) {
            remove()
        }
    }

    init {
        originToCenter()
        angularSpeed = 720
    }
}