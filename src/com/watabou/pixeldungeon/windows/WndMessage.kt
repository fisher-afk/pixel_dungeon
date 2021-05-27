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
package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapTextMultiline

class WndMessage(text: String?) : Window() {
    companion object {
        private const val WIDTH_P = 120
        private const val WIDTH_L = 144
        private const val MARGIN = 4
    }

    init {
        val info: BitmapTextMultiline = PixelScene.createMultiline(text, 6)
        info.maxWidth = (if (PixelDungeon.landscape()) WIDTH_L else WIDTH_P) - MARGIN * 2
        info.measure()
        info.y = MARGIN
        info.x = info.y
        add(info)
        resize(
            info.width() as Int + MARGIN * 2,
            info.height() as Int + MARGIN * 2
        )
    }
}