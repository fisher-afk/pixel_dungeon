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

class WndResurrect(ankh: Ankh, causeOfDeath: Any?) : Window() {
    fun destroy() {
        super.destroy()
        instance = null
    }

    fun onBackPressed() {}

    companion object {
        private const val TXT_MESSAGE =
            "You died, but you were given another chance to win this dungeon. Will you take it?"
        private const val TXT_YES = "Yes, I will fight!"
        private const val TXT_NO = "No, I give up"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 20
        private const val GAP = 2f
        var instance: WndResurrect?
        var causeOfDeath: Any? = null
    }

    init {
        instance = this
        Companion.causeOfDeath = causeOfDeath
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(ankh.image(), null))
        titlebar.label(ankh.name())
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        val message: BitmapTextMultiline = PixelScene.createMultiline(TXT_MESSAGE, 6)
        message.maxWidth = WIDTH
        message.measure()
        message.y = titlebar.bottom() + GAP
        add(message)
        val btnYes: RedButton = object : RedButton(TXT_YES) {
            protected fun onClick() {
                hide()
                Statistics.ankhsUsed++
                InterlevelScene.mode = InterlevelScene.Mode.RESURRECT
                Game.switchScene(InterlevelScene::class.java)
            }
        }
        btnYes.setRect(0, message.y + message.height() + GAP, WIDTH, BTN_HEIGHT)
        add(btnYes)
        val btnNo: RedButton = object : RedButton(TXT_NO) {
            protected fun onClick() {
                hide()
                Rankings.INSTANCE.submit(false)
                Hero.reallyDie(Companion.causeOfDeath)
            }
        }
        btnNo.setRect(0, btnYes.bottom() + GAP, WIDTH, BTN_HEIGHT)
        add(btnNo)
        resize(WIDTH, btnNo.bottom() as Int)
    }
}