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

import com.watabou.gltextures.SmartTexture

class ShadowBox : NinePatch(Assets.SHADOW, 1) {
    fun size(width: Float, height: Float) {
        super.size(width / SIZE, height / SIZE)
    }

    fun boxRect(x: Float, y: Float, width: Float, height: Float) {
        x = x - SIZE
        y = y - SIZE
        size(width + SIZE * 2, height + SIZE * 2)
    }

    companion object {
        const val SIZE = 16f
    }

    init {
        texture.filter(SmartTexture.LINEAR, SmartTexture.LINEAR)
        scale.set(SIZE, SIZE)
    }
}