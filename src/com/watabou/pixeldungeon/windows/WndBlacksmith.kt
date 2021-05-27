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

class WndBlacksmith(troll: Blacksmith, hero: Hero?) : Window() {
    private var btnPressed: ItemButton? = null
    private val btnItem1: ItemButton
    private val btnItem2: ItemButton
    private val btnReforge: RedButton
    protected var itemSelector: WndBag.Listener = object : Listener() {
        fun onSelect(item: Item?) {
            if (item != null) {
                btnPressed!!.item(item)
                if (btnItem1.item != null && btnItem2.item != null) {
                    val result: String = Blacksmith.verify(btnItem1.item, btnItem2.item)
                    if (result != null) {
                        GameScene.show(WndMessage(result))
                        btnReforge.enable(false)
                    } else {
                        btnReforge.enable(true)
                    }
                }
            }
        }
    }

    class ItemButton : Component() {
        protected var bg: NinePatch? = null
        protected var slot: ItemSlot? = null
        var item: Item? = null
        protected fun createChildren() {
            super.createChildren()
            bg = Chrome.get(Chrome.Type.BUTTON)
            add(bg)
            slot = object : ItemSlot() {
                protected fun onTouchDown() {
                    bg.brightness(1.2f)
                    Sample.INSTANCE.play(Assets.SND_CLICK)
                }

                protected fun onTouchUp() {
                    bg.resetColor()
                }

                protected fun onClick() {
                    this@ItemButton.onClick()
                }
            }
            add(slot)
        }

        protected fun onClick() {}
        protected fun layout() {
            super.layout()
            bg.x = x
            bg.y = y
            bg.size(width, height)
            slot.setRect(x + 2, y + 2, width - 4, height - 4)
        }

        fun item(item: Item) {
            slot.item(item.also { this.item = it })
        }
    }

    companion object {
        private const val BTN_SIZE = 36
        private const val GAP = 2f
        private const val BTN_GAP = 10f
        private const val WIDTH = 116
        private const val TXT_PROMPT = "Ok, a deal is a deal, dat's what I can do for you: I can reforge " +
                "2 items and turn them into one of a better quality."
        private const val TXT_SELECT = "Select an item to reforge"
        private const val TXT_REFORGE = "Reforge them"
    }

    init {
        val titlebar = IconTitle()
        titlebar.icon(troll.sprite())
        titlebar.label(Utils.capitalize(troll.name))
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        val message: BitmapTextMultiline = PixelScene.createMultiline(TXT_PROMPT, 6)
        message.maxWidth = WIDTH
        message.measure()
        message.y = titlebar.bottom() + GAP
        add(message)
        btnItem1 = object : ItemButton() {
            override fun onClick() {
                btnPressed = btnItem1
                GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, TXT_SELECT)
            }
        }
        btnItem1.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.y + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE)
        add(btnItem1)
        btnItem2 = object : ItemButton() {
            override fun onClick() {
                btnPressed = btnItem2
                GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, TXT_SELECT)
            }
        }
        btnItem2.setRect(btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE)
        add(btnItem2)
        btnReforge = object : RedButton(TXT_REFORGE) {
            protected fun onClick() {
                Blacksmith.upgrade(btnItem1.item, btnItem2.item)
                hide()
            }
        }
        btnReforge.enable(false)
        btnReforge.setRect(0, btnItem1.bottom() + BTN_GAP, WIDTH, 20)
        add(btnReforge)
        resize(WIDTH, btnReforge.bottom() as Int)
    }
}