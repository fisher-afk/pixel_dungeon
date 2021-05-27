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
package com.watabou.pixeldungeon.ui

import com.watabou.noosa.BitmapText

class Toast(text: String?) : Component() {
    protected var bg: NinePatch? = null
    protected var close: SimpleButton? = null
    protected var text: BitmapText? = null
    protected fun createChildren() {
        super.createChildren()
        bg = Chrome.get(Chrome.Type.TOAST_TR)
        add(bg)
        close = object : SimpleButton(Icons.get(Icons.CLOSE)) {
            protected override fun onClick() {
                onClose()
            }
        }
        add(close)
        text = PixelScene.createText(8)
        add(text)
    }

    protected fun layout() {
        super.layout()
        bg.x = x
        bg.y = y
        bg.size(width, height)
        close.setPos(
            bg.x + bg.width() - bg.marginHor() / 2 - MARGIN_HOR - close.width(),
            y + (height - close.height()) / 2
        )
        text.x = close.left() - MARGIN_HOR - text.width()
        text.y = y + (height - text.height()) / 2
        PixelScene.align(text)
    }

    fun text(txt: String?) {
        text.text(txt)
        text.measure()
    }

    protected fun onClose() {}

    companion object {
        private const val MARGIN_HOR = 2f
        private const val MARGIN_VER = 2f
    }

    init {
        text(text)
        width = this.text.width() + close.width() + bg.marginHor() + MARGIN_HOR * 3
        height = Math.max(this.text.height(), close.height()) + bg.marginVer() + MARGIN_VER * 2
    }
}