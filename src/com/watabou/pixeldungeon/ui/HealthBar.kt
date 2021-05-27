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

import com.watabou.noosa.ColorBlock

class HealthBar : Component() {
    private var hpBg: ColorBlock? = null
    private var hpLvl: ColorBlock? = null
    private var level = 0f
    protected fun createChildren() {
        hpBg = ColorBlock(1, 1, COLOR_BG)
        add(hpBg)
        hpLvl = ColorBlock(1, 1, COLOR_LVL)
        add(hpLvl)
        height = HEIGHT
    }

    protected fun layout() {
        hpLvl.x = x
        hpBg.x = hpLvl.x
        hpLvl.y = y
        hpBg.y = hpLvl.y
        hpBg.size(width, HEIGHT)
        hpLvl.size(width * level, HEIGHT)
        height = HEIGHT
    }

    fun level(value: Float) {
        level = value
        layout()
    }

    companion object {
        private const val COLOR_BG = -0x340000
        private const val COLOR_LVL = -0xff1200
        private const val HEIGHT = 2
    }
}