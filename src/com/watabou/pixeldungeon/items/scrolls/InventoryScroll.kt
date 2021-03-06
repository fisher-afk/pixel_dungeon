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
package com.watabou.pixeldungeon.items.scrolls

import com.watabou.noosa.audio.Sample

abstract class InventoryScroll : Scroll() {
    protected var inventoryTitle = "Select an item"
    protected var mode: WndBag.Mode = WndBag.Mode.ALL
    protected override fun doRead() {
        if (!isKnown()) {
            setKnown()
            identifiedByUse = true
        } else {
            identifiedByUse = false
        }
        GameScene.selectItem(itemSelector, mode, inventoryTitle)
    }

    private fun confirmCancelation() {
        GameScene.show(object : WndOptions(name(), TXT_WARNING, TXT_YES, TXT_NO) {
            protected fun onSelect(index: Int) {
                when (index) {
                    0 -> {
                        curUser.spendAndNext(TIME_TO_READ)
                        identifiedByUse = false
                    }
                    1 -> GameScene.selectItem(itemSelector, mode, inventoryTitle)
                }
            }

            fun onBackPressed() {}
        })
    }

    protected abstract fun onItemSelected(item: Item?)

    companion object {
        private const val TXT_WARNING = "Do you really want to cancel this scroll usage? It will be consumed anyway."
        private const val TXT_YES = "Yes, I'm positive"
        private const val TXT_NO = "No, I changed my mind"
        protected var identifiedByUse = false
        protected var itemSelector: WndBag.Listener = object : Listener() {
            fun onSelect(item: Item?) {
                if (item != null) {
                    (curItem as InventoryScroll).onItemSelected(item)
                    (curItem as InventoryScroll).readAnimation()
                    Sample.INSTANCE.play(Assets.SND_READ)
                    Invisibility.dispel()
                } else if (identifiedByUse) {
                    (curItem as InventoryScroll).confirmCancelation()
                } else {
                    curItem.collect(curUser.belongings.backpack)
                }
            }
        }
    }
}