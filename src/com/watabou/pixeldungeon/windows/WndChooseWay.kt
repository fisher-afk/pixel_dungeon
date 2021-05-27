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

import com.watabou.pixeldungeon.actors.hero.HeroSubClass

class WndChooseWay : Window {
    constructor(tome: TomeOfMastery, way1: HeroSubClass, way2: HeroSubClass) : super() {
        val TXT_MASTERY = "Which way will you follow?"
        val TXT_CANCEL = "I'll decide later"
        val bottom = createCommonStuff(tome, way1.desc().toString() + "\n\n" + way2.desc() + "\n\n" + TXT_MASTERY)
        val btnWay1: RedButton = object : RedButton(Utils.capitalize(way1.title())) {
            protected fun onClick() {
                hide()
                tome.choose(way1)
            }
        }
        btnWay1.setRect(0, bottom + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT)
        add(btnWay1)
        val btnWay2: RedButton = object : RedButton(Utils.capitalize(way2.title())) {
            protected fun onClick() {
                hide()
                tome.choose(way2)
            }
        }
        btnWay2.setRect(btnWay1.right() + GAP, btnWay1.top(), btnWay1.width(), BTN_HEIGHT)
        add(btnWay2)
        val btnCancel: RedButton = object : RedButton(TXT_CANCEL) {
            protected fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0, btnWay2.bottom() + GAP, WIDTH, BTN_HEIGHT)
        add(btnCancel)
        resize(WIDTH, btnCancel.bottom() as Int)
    }

    constructor(tome: TomeOfMastery, way: HeroSubClass) : super() {
        val TXT_REMASTERY = "Do you want to respec into %s?"
        val TXT_OK = "Yes, I want to respec"
        val TXT_CANCEL = "Maybe later"
        val bottom = createCommonStuff(
            tome,
            way.desc().toString() + "\n\n" + Utils.format(TXT_REMASTERY, Utils.indefinite(way.title()))
        )
        val btnWay: RedButton = object : RedButton(TXT_OK) {
            protected fun onClick() {
                hide()
                tome.choose(way)
            }
        }
        btnWay.setRect(0, bottom + GAP, WIDTH, BTN_HEIGHT)
        add(btnWay)
        val btnCancel: RedButton = object : RedButton(TXT_CANCEL) {
            protected fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0, btnWay.bottom() + GAP, WIDTH, BTN_HEIGHT)
        add(btnCancel)
        resize(WIDTH, btnCancel.bottom() as Int)
    }

    private fun createCommonStuff(tome: TomeOfMastery, text: String): Float {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(tome.image(), null))
        titlebar.label(tome.name())
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        val hl = HighlightedText(6)
        hl.text(text, WIDTH)
        hl.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(hl)
        return hl.bottom()
    }

    companion object {
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 18
        private const val GAP = 2f
    }
}