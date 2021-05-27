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

import com.watabou.noosa.Image

class WndTitledMessage(titlebar: Component, message: String?) : Window() {
    constructor(icon: Image?, title: String?, message: String?) : this(IconTitle(icon, title), message) {}

    companion object {
        private const val WIDTH_P = 120
        private const val WIDTH_L = 144
        private const val GAP = 2
    }

    init {
        val width = if (PixelDungeon.landscape()) WIDTH_L else WIDTH_P
        titlebar.setRect(0, 0, width, 0)
        add(titlebar)
        val text = HighlightedText(6)
        text.text(message, width)
        text.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(text)
        resize(width, text.bottom() as Int)
    }
}