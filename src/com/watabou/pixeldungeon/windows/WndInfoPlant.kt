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

class WndInfoPlant(plant: Plant) : Window() {
    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
    }

    init {
        val titlebar = IconTitle()
        titlebar.icon(PlantSprite(plant.image))
        titlebar.label(plant.plantName)
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        val info: BitmapTextMultiline = PixelScene.createMultiline(6)
        add(info)
        info.text(plant.desc())
        info.maxWidth = WIDTH
        info.measure()
        info.x = titlebar.left()
        info.y = titlebar.bottom() + GAP
        resize(WIDTH, (info.y + info.height()) as Int)
    }
}