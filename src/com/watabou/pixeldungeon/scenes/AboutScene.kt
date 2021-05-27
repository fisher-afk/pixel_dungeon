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
package com.watabou.pixeldungeon.scenes

import com.watabou.input.Touchscreen.Touch

class AboutScene : PixelScene() {
    override fun create() {
        super.create()
        val text: BitmapTextMultiline = createMultiline(TXT, 8)
        text.maxWidth = Math.min(Camera.main.width, 120)
        text.measure()
        add(text)
        text.x = align((Camera.main.width - text.width()) / 2)
        text.y = align((Camera.main.height - text.height()) / 2)
        val link: BitmapTextMultiline = createMultiline(LNK, 8)
        link.maxWidth = Math.min(Camera.main.width, 120)
        link.measure()
        link.hardlight(Window.TITLE_COLOR)
        add(link)
        link.x = text.x
        link.y = text.y + text.height()
        val hotArea: TouchArea = object : TouchArea(link) {
            protected fun onClick(touch: Touch?) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://" + LNK))
                Game.instance.startActivity(intent)
            }
        }
        add(hotArea)
        val wata: Image = Icons.WATA.get()
        wata.x = align((Camera.main.width - wata.width) / 2)
        wata.y = text.y - wata.height - 8
        add(wata)
        Flare(7, 64).color(0x112233, true).show(wata, 0).angularSpeed = +20
        val archs = Archs()
        archs.setSize(Camera.main.width, Camera.main.height)
        addToBack(archs)
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0)
        add(btnExit)
        fadeIn()
    }

    protected fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }

    companion object {
        private const val TXT = "Code & graphics: Watabou\n" +
                "Music: Cube_Code\n\n" +
                "This game is inspired by Brian Walker's Brogue. " +
                "Try it on Windows, Mac OS or Linux - it's awesome! ;)\n\n" +
                "Please visit official website for additional info:"
        private const val LNK = "pixeldungeon.watabou.ru"
    }
}