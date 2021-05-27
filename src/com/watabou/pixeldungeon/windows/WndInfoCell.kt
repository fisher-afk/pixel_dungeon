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

class WndInfoCell(cell: Int) : Window() {
    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
        private const val TXT_NOTHING = "There is nothing here."
    }

    init {
        var tile: Int = Dungeon.level.map.get(cell)
        if (Level.water.get(cell)) {
            tile = Terrain.WATER
        } else if (Level.pit.get(cell)) {
            tile = Terrain.CHASM
        }
        val titlebar = IconTitle()
        if (tile == Terrain.WATER) {
            val water = Image(Dungeon.level.waterTex())
            water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE)
            titlebar.icon(water)
        } else {
            titlebar.icon(DungeonTilemap.tile(tile))
        }
        titlebar.label(Dungeon.level.tileName(tile))
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        val info: BitmapTextMultiline = PixelScene.createMultiline(6)
        add(info)
        val desc: StringBuilder = StringBuilder(Dungeon.level.tileDesc(tile))
        val newLine = '\n'
        for (blob in Dungeon.level.blobs.values()) {
            if (blob.cur.get(cell) > 0 && blob.tileDesc() != null) {
                if (desc.length > 0) {
                    desc.append(newLine)
                }
                desc.append(blob.tileDesc())
            }
        }
        info.text(if (desc.length > 0) desc.toString() else TXT_NOTHING)
        info.maxWidth = WIDTH
        info.measure()
        info.x = titlebar.left()
        info.y = titlebar.bottom() + GAP
        resize(WIDTH, (info.y + info.height()) as Int)
    }
}