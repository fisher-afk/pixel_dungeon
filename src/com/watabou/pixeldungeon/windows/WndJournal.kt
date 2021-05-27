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

class WndJournal : Window() {
    private val txtTitle: BitmapText
    private val list: ScrollPane

    private class ListItem(f: Journal.Feature, d: Int) : Component() {
        private var feature: BitmapText? = null
        private var depth: BitmapText? = null
        private var icon: Image? = null
        protected fun createChildren() {
            feature = PixelScene.createText(9)
            add(feature)
            depth = BitmapText(PixelScene.font1x)
            add(depth)
            icon = Icons.get(Icons.DEPTH)
            add(icon)
        }

        protected fun layout() {
            icon.x = width - icon.width
            depth.x = icon.x - 1 - depth.width()
            depth.y = PixelScene.align(y + (height - depth.height()) / 2)
            icon.y = depth.y - 1
            feature.y = PixelScene.align(depth.y + depth.baseLine() - feature.baseLine())
        }

        init {
            feature.text(f.desc)
            feature.measure()
            depth.text(Integer.toString(d))
            depth.measure()
            if (d == Dungeon.depth) {
                feature.hardlight(TITLE_COLOR)
                depth.hardlight(TITLE_COLOR)
            }
        }
    }

    companion object {
        private const val WIDTH = 112
        private const val HEIGHT_P = 160
        private const val HEIGHT_L = 144
        private const val ITEM_HEIGHT = 18
        private const val TXT_TITLE = "Journal"
    }

    init {
        resize(WIDTH, if (PixelDungeon.landscape()) HEIGHT_L else HEIGHT_P)
        txtTitle = PixelScene.createText(TXT_TITLE, 9)
        txtTitle.hardlight(Window.TITLE_COLOR)
        txtTitle.measure()
        txtTitle.x = PixelScene.align(PixelScene.uiCamera, (WIDTH - txtTitle.width()) / 2)
        add(txtTitle)
        val content = Component()
        Collections.sort(Journal.records)
        var pos = 0f
        for (rec in Journal.records) {
            val item = ListItem(rec.feature, rec.depth)
            item.setRect(0, pos, WIDTH, ITEM_HEIGHT)
            content.add(item)
            pos += item.height()
        }
        content.setSize(WIDTH, pos)
        list = ScrollPane(content)
        add(list)
        list.setRect(0, txtTitle.height(), WIDTH, height - txtTitle.height())
    }
}