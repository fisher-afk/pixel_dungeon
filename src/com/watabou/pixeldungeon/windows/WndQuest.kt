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

import com.watabou.pixeldungeon.PixelDungeon

class WndQuest(questgiver: NPC, text: String?, vararg options: String?) : Window() {
    protected fun onSelect(index: Int) {}

    companion object {
        private const val WIDTH_P = 120
        private const val WIDTH_L = 144
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }

    init {
        val width = if (PixelDungeon.landscape()) WIDTH_L else WIDTH_P
        val titlebar = IconTitle(questgiver.sprite(), Utils.capitalize(questgiver.name))
        titlebar.setRect(0, 0, width, 0)
        add(titlebar)
        val hl = HighlightedText(6)
        hl.text(text, width)
        hl.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(hl)
        if (options.size > 0) {
            var pos: Float = hl.bottom()
            for (i in 0 until options.size) {
                pos += GAP.toFloat()
                val btn: RedButton = object : RedButton(options[i]) {
                    protected fun onClick() {
                        hide()
                        onSelect(i)
                    }
                }
                btn.setRect(0, pos, width, BTN_HEIGHT)
                add(btn)
                pos += BTN_HEIGHT.toFloat()
            }
            resize(width, pos.toInt())
        } else {
            resize(width, hl.bottom() as Int)
        }
    }
}