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
package com.watabou.pixeldungeon

import com.watabou.noosa.NinePatch

object Chrome {
    operator fun get(type: Type?): NinePatch? {
        return when (type) {
            Type.WINDOW -> NinePatch(Assets.CHROME, 0, 0, 22, 22, 7)
            Type.TOAST -> NinePatch(Assets.CHROME, 22, 0, 18, 18, 5)
            Type.TOAST_TR -> NinePatch(Assets.CHROME, 40, 0, 18, 18, 5)
            Type.BUTTON -> NinePatch(Assets.CHROME, 58, 0, 6, 6, 2)
            Type.TAG -> NinePatch(Assets.CHROME, 22, 18, 16, 14, 3)
            Type.SCROLL -> NinePatch(Assets.CHROME, 32, 32, 32, 32, 5, 11, 5, 11)
            Type.TAB_SET -> NinePatch(Assets.CHROME, 64, 0, 22, 22, 7, 7, 7, 7)
            Type.TAB_SELECTED -> NinePatch(Assets.CHROME, 64, 22, 10, 14, 4, 7, 4, 6)
            Type.TAB_UNSELECTED -> NinePatch(Assets.CHROME, 74, 22, 10, 14, 4, 7, 4, 6)
            else -> null
        }
    }

    enum class Type {
        TOAST, TOAST_TR, WINDOW, BUTTON, TAG, SCROLL, TAB_SET, TAB_SELECTED, TAB_UNSELECTED
    }
}