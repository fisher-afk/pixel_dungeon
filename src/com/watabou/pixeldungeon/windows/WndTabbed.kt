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

class WndTabbed : Window(0, 0, Chrome.get(Chrome.Type.TAB_SET)) {
    protected var tabs = ArrayList<Tab?>()
    protected var selected: Tab? = null
    protected fun add(tab: Tab?): Tab? {
        tab.setPos(if (tabs.size == 0) -chrome.marginLeft() + 1 else tabs[tabs.size - 1].right(), height)
        tab!!.select(false)
        super.add(tab)
        tabs.add(tab)
        return tab
    }

    fun select(index: Int) {
        select(tabs[index])
    }

    fun select(tab: Tab?) {
        if (tab !== selected) {
            for (t in tabs) {
                if (t === selected) {
                    t!!.select(false)
                } else if (t === tab) {
                    t!!.select(true)
                }
            }
            selected = tab
        }
    }

    fun resize(w: Int, h: Int) {
        // -> super.resize(...)
        this.width = w
        this.height = h
        chrome.size(
            width + chrome.marginHor(),
            height + chrome.marginVer()
        )
        camera.resize(chrome.width as Int, (chrome.marginTop() + height + tabHeight()) as Int)
        camera.x = (Game.width - camera.screenWidth()) as Int / 2
        camera.y = (Game.height - camera.screenHeight()) as Int / 2
        shadow.boxRect(
            camera.x / camera.zoom,
            camera.y / camera.zoom,
            chrome.width(), chrome.height
        )
        // <- super.resize(...)
        for (tab in tabs) {
            remove(tab)
        }
        val tabs = ArrayList(tabs)
        this.tabs.clear()
        for (tab in tabs) {
            add(tab)
        }
    }

    protected fun tabHeight(): Int {
        return 25
    }

    protected fun onClick(tab: Tab?) {
        select(tab)
    }

    protected inner class Tab : Button() {
        protected val CUT = 5
        protected var selected = false
        protected var bg: NinePatch? = null
        protected fun layout() {
            super.layout()
            if (bg != null) {
                bg.x = x
                bg.y = y
                bg.size(width, height)
            }
        }

        fun select(value: Boolean) {
            active = !value.also { selected = it }
            if (bg != null) {
                remove(bg)
            }
            bg = Chrome.get(if (selected) Chrome.Type.TAB_SELECTED else Chrome.Type.TAB_UNSELECTED)
            addToBack(bg)
            layout()
        }

        protected fun onClick() {
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
            this@WndTabbed.onClick(this)
        }
    }

    protected inner class LabeledTab(label: String?) : Tab() {
        private var btLabel: BitmapText? = null
        protected fun createChildren() {
            super.createChildren()
            btLabel = PixelScene.createText(9)
            add(btLabel)
        }

        override fun layout() {
            super.layout()
            btLabel.x = PixelScene.align(x + (width - btLabel.width()) / 2)
            btLabel.y = PixelScene.align(y + (height - btLabel.baseLine()) / 2) - 1
            if (!selected) {
                btLabel.y -= 2
            }
        }

        protected override fun select(value: Boolean) {
            super.select(value)
            btLabel.am = if (selected) 1.0f else 0.6f
        }

        init {
            btLabel.text(label)
            btLabel.measure()
        }
    }
}