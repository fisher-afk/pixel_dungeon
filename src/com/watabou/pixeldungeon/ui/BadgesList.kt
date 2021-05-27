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

class BadgesList(global: Boolean) : ScrollPane(Component()) {
    private val items = ArrayList<ListItem>()
    protected override fun layout() {
        var pos = 0f
        val size = items.size
        for (i in 0 until size) {
            items[i].setRect(0, pos, width, ListItem.Companion.HEIGHT)
            pos += ListItem.Companion.HEIGHT
        }
        content.setSize(width, pos)
        super.layout()
    }

    override fun onClick(x: Float, y: Float) {
        val size = items.size
        for (i in 0 until size) {
            if (items[i].onClick(x, y)) {
                break
            }
        }
    }

    private inner class ListItem(badge: Badges.Badge) : Component() {
        private val badge: Badges.Badge
        private var icon: Image? = null
        private var label: BitmapText? = null
        protected fun createChildren() {
            icon = Image()
            add(icon)
            label = PixelScene.createText(6)
            add(label)
        }

        protected fun layout() {
            icon.x = x
            icon.y = PixelScene.align(y + (height - icon.height) / 2)
            label.x = icon.x + icon.width + 2
            label.y = PixelScene.align(y + (height - label.baseLine()) / 2)
        }

        fun onClick(x: Float, y: Float): Boolean {
            return if (inside(x, y)) {
                Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
                Game.scene().add(WndBadge(badge))
                true
            } else {
                false
            }
        }

        companion object {
            const val HEIGHT = 20f
        }

        init {
            this.badge = badge
            icon.copy(BadgeBanner.image(badge.image))
            label.text(badge.description)
        }
    }

    init {
        for (badge in Badges.filtered(global)) {
            if (badge.image === -1) {
                continue
            }
            val item = ListItem(badge)
            content.add(item)
            items.add(item)
        }
    }
}