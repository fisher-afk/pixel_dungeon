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

import com.watabou.noosa.BitmapText

class WndBadge(badge: Badges.Badge) : Window() {
    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 4
    }

    init {
        val icon: Image = BadgeBanner.image(badge.image)
        icon.scale.set(2)
        add(icon)
        val info: BitmapTextMultiline = PixelScene.createMultiline(badge.description, 8)
        info.maxWidth = WIDTH - MARGIN * 2
        info.measure()
        val w: Float = Math.max(icon.width(), info.width()) + MARGIN * 2
        icon.x = (w - icon.width()) / 2
        icon.y = MARGIN
        var pos: Float = icon.y + icon.height() + MARGIN
        for (line in info.LineSplitter().split()) {
            line.measure()
            line.x = PixelScene.align((w - line.width()) / 2)
            line.y = PixelScene.align(pos)
            add(line)
            pos += line.height()
        }
        resize(w.toInt(), (pos + MARGIN).toInt())
        BadgeBanner.highlight(icon, badge.image)
    }
}