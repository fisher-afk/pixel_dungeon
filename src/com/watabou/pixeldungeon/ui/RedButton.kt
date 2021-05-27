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

class RedButton(label: String?) : Button() {
    protected var bg: NinePatch? = null
    protected var text: BitmapText? = null
    protected var icon: Image? = null
    protected fun createChildren() {
        super.createChildren()
        bg = Chrome.get(Chrome.Type.BUTTON)
        add(bg)
        text = PixelScene.createText(9)
        add(text)
    }

    protected fun layout() {
        super.layout()
        bg.x = x
        bg.y = y
        bg.size(width, height)
        text.x = x + (width - text.width()) as Int / 2
        text.y = y + (height - text.baseLine()) as Int / 2
        if (icon != null) {
            icon.x = x + text.x - icon.width() - 2
            icon.y = y + (height - icon.height()) / 2
        }
    }

    protected fun onTouchDown() {
        bg.brightness(1.2f)
        Sample.INSTANCE.play(Assets.SND_CLICK)
    }

    protected fun onTouchUp() {
        bg.resetColor()
    }

    fun enable(value: Boolean) {
        active = value
        text.alpha(if (value) 1.0f else 0.3f)
    }

    fun text(value: String?) {
        text.text(value)
        text.measure()
        layout()
    }

    fun textColor(value: Int) {
        text.hardlight(value)
    }

    fun icon(icon: Image?) {
        if (this.icon != null) {
            remove(this.icon)
        }
        this.icon = icon
        if (this.icon != null) {
            add(this.icon)
            layout()
        }
    }

    fun reqWidth(): Float {
        return text.width() + 4
    }

    fun reqHeight(): Float {
        return text.baseLine() + 4
    }

    init {
        text.text(label)
        text.measure()
    }
}