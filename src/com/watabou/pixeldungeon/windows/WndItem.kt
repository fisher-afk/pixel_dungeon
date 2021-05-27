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

class WndItem(owner: WndBag?, item: Item) : Window() {
    companion object {
        private const val BUTTON_WIDTH = 36f
        private const val BUTTON_HEIGHT = 16f
        private const val GAP = 2f
        private const val WIDTH = 120
    }

    init {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(item.image(), item.glowing()))
        titlebar.label(Utils.capitalize(item.toString()))
        if (item.isUpgradable() && item.levelKnown) {
            titlebar.health(item.durability() as Float / item.maxDurability())
        }
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        if (item.levelKnown) {
            if (item.level() < 0) {
                titlebar.color(ItemSlot.DEGRADED)
            } else if (item.level() > 0) {
                titlebar.color(if (item.isBroken()) ItemSlot.WARNING else ItemSlot.UPGRADED)
            }
        }
        val info: BitmapTextMultiline = PixelScene.createMultiline(item.info(), 6)
        info.maxWidth = WIDTH
        info.measure()
        info.x = titlebar.left()
        info.y = titlebar.bottom() + GAP
        add(info)
        var y: Float = info.y + info.height() + GAP
        var x = 0f
        if (Dungeon.hero.isAlive() && owner != null) {
            for (action in item.actions(Dungeon.hero)) {
                val btn: RedButton = object : RedButton(action) {
                    protected fun onClick() {
                        item.execute(Dungeon.hero, action)
                        hide()
                        owner.hide()
                    }
                }
                btn.setSize(Math.max(BUTTON_WIDTH, btn.reqWidth()), BUTTON_HEIGHT)
                if (x + btn.width() > WIDTH) {
                    x = 0f
                    y += BUTTON_HEIGHT + GAP
                }
                btn.setPos(x, y)
                add(btn)
                if (action === item.defaultAction) {
                    btn.textColor(TITLE_COLOR)
                }
                x += btn.width() + GAP
            }
        }
        resize(WIDTH, (y + if (x > 0) BUTTON_HEIGHT else 0) as Int)
    }
}