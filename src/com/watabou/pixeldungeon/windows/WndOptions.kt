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

class WndOptions(title: String?, message: String?, vararg options: String?) : Window() {
    protected fun onSelect(index: Int) {}

    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 2
        private const val BUTTON_HEIGHT = 20
    }

    init {
        val tfTitle: BitmapTextMultiline = PixelScene.createMultiline(title, 9)
        tfTitle.hardlight(TITLE_COLOR)
        tfTitle.y = MARGIN
        tfTitle.x = tfTitle.y
        tfTitle.maxWidth = WIDTH - MARGIN * 2
        tfTitle.measure()
        add(tfTitle)
        val tfMesage: BitmapTextMultiline = PixelScene.createMultiline(message, 8)
        tfMesage.maxWidth = WIDTH - MARGIN * 2
        tfMesage.measure()
        tfMesage.x = MARGIN
        tfMesage.y = tfTitle.y + tfTitle.height() + MARGIN
        add(tfMesage)
        var pos: Float = tfMesage.y + tfMesage.height() + MARGIN
        for (i in 0 until options.size) {
            val btn: RedButton = object : RedButton(options[i]) {
                protected fun onClick() {
                    hide()
                    onSelect(i)
                }
            }
            btn.setRect(MARGIN, pos, WIDTH - MARGIN * 2, BUTTON_HEIGHT)
            add(btn)
            pos += (BUTTON_HEIGHT + MARGIN).toFloat()
        }
        resize(WIDTH, pos.toInt())
    }
}