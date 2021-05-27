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

import com.watabou.noosa.Image

object Effects {
    operator fun get(type: Type?): Image {
        val icon = Image(Assets.EFFECTS)
        when (type) {
            Type.RIPPLE -> icon.frame(icon.texture.uvRect(0, 0, 16, 16))
            Type.LIGHTNING -> icon.frame(icon.texture.uvRect(16, 0, 32, 8))
            Type.WOUND -> icon.frame(icon.texture.uvRect(16, 8, 32, 16))
            Type.RAY -> icon.frame(icon.texture.uvRect(16, 16, 32, 24))
        }
        return icon
    }

    enum class Type {
        RIPPLE, LIGHTNING, WOUND, RAY
    }
}