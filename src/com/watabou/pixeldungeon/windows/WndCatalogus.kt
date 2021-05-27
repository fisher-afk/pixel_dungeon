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

class WndCatalogus : WndTabbed() {
    private val txtTitle: BitmapText
    private val list: ScrollPane
    private val items = ArrayList<ListItem>()
    private fun updateList() {
        txtTitle.text(Utils.format(TXT_TITLE, if (showPotions) TXT_POTIONS else TXT_SCROLLS))
        txtTitle.measure()
        txtTitle.x = PixelScene.align(PixelScene.uiCamera, (width - txtTitle.width()) / 2)
        items.clear()
        val content: Component = list.content()
        content.clear()
        list.scrollTo(0, 0)
        var pos = 0f
        for (itemClass in if (showPotions) Potion.getKnown() else Scroll.getKnown()) {
            val item = ListItem(itemClass)
            item.setRect(0, pos, width, ITEM_HEIGHT)
            content.add(item)
            items.add(item)
            pos += item.height()
        }
        for (itemClass in if (showPotions) Potion.getUnknown() else Scroll.getUnknown()) {
            val item = ListItem(itemClass)
            item.setRect(0, pos, width, ITEM_HEIGHT)
            content.add(item)
            items.add(item)
            pos += item.height()
        }
        content.setSize(width, pos)
        list.setSize(list.width(), list.height())
    }

    private class ListItem(cl: Class<out Item?>) : Component() {
        private var item: Item? = null
        private var identified = false
        private var sprite: ItemSprite? = null
        private var label: BitmapText? = null
        protected fun createChildren() {
            sprite = ItemSprite()
            add(sprite)
            label = PixelScene.createText(8)
            add(label)
        }

        protected fun layout() {
            sprite.y = PixelScene.align(y + (height - sprite.height) / 2)
            label.x = sprite.x + sprite.width
            label.y = PixelScene.align(y + (height - label.baseLine()) / 2)
        }

        fun onClick(x: Float, y: Float): Boolean {
            return if (identified && inside(x, y)) {
                GameScene.show(WndInfoItem(item))
                true
            } else {
                false
            }
        }

        init {
            try {
                item = cl.newInstance()
                if (item.isIdentified().also({ identified = it })) {
                    sprite.view(item.image(), null)
                    label.text(item.name())
                } else {
                    sprite.view(127, null)
                    label.text(item.trueName())
                    label.hardlight(0xCCCCCC)
                }
            } catch (e: Exception) {
                // Do nothing
            }
        }
    }

    companion object {
        private const val WIDTH_P = 112
        private const val HEIGHT_P = 160
        private const val WIDTH_L = 128
        private const val HEIGHT_L = 128
        private const val ITEM_HEIGHT = 18
        private const val TAB_WIDTH = 50
        private const val TXT_POTIONS = "Potions"
        private const val TXT_SCROLLS = "Scrolls"
        private const val TXT_TITLE = "Catalogus"
        private var showPotions = true
    }

    init {
        if (PixelDungeon.landscape()) {
            resize(WIDTH_L, HEIGHT_L)
        } else {
            resize(WIDTH_P, HEIGHT_P)
        }
        txtTitle = PixelScene.createText(TXT_TITLE, 9)
        txtTitle.hardlight(Window.TITLE_COLOR)
        txtTitle.measure()
        add(txtTitle)
        list = object : ScrollPane(Component()) {
            fun onClick(x: Float, y: Float) {
                val size = items.size
                for (i in 0 until size) {
                    if (items[i].onClick(x, y)) {
                        break
                    }
                }
            }
        }
        add(list)
        list.setRect(0, txtTitle.height(), width, height - txtTitle.height())
        val showPotions = showPotions
        val tabs: Array<Tab> = arrayOf(
            object : LabeledTab(TXT_POTIONS) {
                protected override fun select(value: Boolean) {
                    super.select(value)
                    Companion.showPotions = value
                    updateList()
                }
            },
            object : LabeledTab(TXT_SCROLLS) {
                protected override fun select(value: Boolean) {
                    super.select(value)
                    Companion.showPotions = !value
                    updateList()
                }
            }
        )
        for (tab in tabs) {
            tab.setSize(TAB_WIDTH, tabHeight())
            add(tab)
        }
        select(if (showPotions) 0 else 1)
    }
}