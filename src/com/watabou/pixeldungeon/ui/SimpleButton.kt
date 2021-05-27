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

import com.watabou.input.Touchscreen.Touch

class SimpleButton(image: Image) : Component() {
    private var image: Image? = null
    protected fun createChildren() {
        image = Image()
        add(image)
        add(object : TouchArea(image) {
            protected fun onTouchDown(touch: Touch?) {
                image.brightness(1.2f)
            }

            protected fun onTouchUp(touch: Touch?) {
                image.brightness(1.0f)
            }

            protected fun onClick(touch: Touch?) {
                this@SimpleButton.onClick()
            }
        })
    }

    protected fun layout() {
        image.x = x
        image.y = y
    }

    protected fun onClick() {}

    init {
        this.image.copy(image)
        width = image.width
        height = image.height
    }
}