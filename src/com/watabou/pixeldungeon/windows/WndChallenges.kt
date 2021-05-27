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

class WndChallenges(checked: Int, private val editable: Boolean) : Window() {
    private val boxes: ArrayList<CheckBox>
    fun onBackPressed() {
        if (editable) {
            var value = 0
            for (i in boxes.indices) {
                if (boxes[i].checked()) {
                    value = value or Challenges.MASKS.get(i)
                }
            }
            PixelDungeon.challenges(value)
        }
        super.onBackPressed()
    }

    companion object {
        private const val WIDTH = 108
        private const val TTL_HEIGHT = 12
        private const val BTN_HEIGHT = 18
        private const val GAP = 1
        private const val TITLE = "Challenges"
    }

    init {
        val title: BitmapText = PixelScene.createText(TITLE, 9)
        title.hardlight(TITLE_COLOR)
        title.measure()
        title.x = PixelScene.align(camera, (WIDTH - title.width()) / 2)
        title.y = PixelScene.align(camera, (TTL_HEIGHT - title.height()) / 2)
        add(title)
        boxes = ArrayList<CheckBox>()
        var pos = TTL_HEIGHT.toFloat()
        for (i in 0 until Challenges.NAMES.length) {
            val cb = CheckBox(Challenges.NAMES.get(i))
            cb.checked(checked and Challenges.MASKS.get(i) !== 0)
            cb.active = editable
            if (i > 0) {
                pos += GAP.toFloat()
            }
            cb.setRect(0, pos, WIDTH, BTN_HEIGHT)
            pos = cb.bottom()
            add(cb)
            boxes.add(cb)
        }
        resize(WIDTH, pos.toInt())
    }
}