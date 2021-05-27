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

import com.watabou.noosa.BitmapTextMultiline

class AmuletScene : PixelScene() {
    private var amulet: Image? = null
    override fun create() {
        super.create()
        var text: BitmapTextMultiline? = null
        if (!noText) {
            text = createMultiline(TXT, 8)
            text.maxWidth = WIDTH
            text.measure()
            add(text)
        }
        amulet = Image(Assets.AMULET)
        add(amulet)
        val btnExit: RedButton = object : RedButton(TXT_EXIT) {
            protected fun onClick() {
                Dungeon.win(ResultDescriptions.WIN)
                Dungeon.deleteGame(Dungeon.hero.heroClass, true)
                Game.switchScene(if (noText) TitleScene::class.java else RankingsScene::class.java)
            }
        }
        btnExit.setSize(WIDTH, BTN_HEIGHT)
        add(btnExit)
        val btnStay: RedButton = object : RedButton(TXT_STAY) {
            protected fun onClick() {
                onBackPressed()
            }
        }
        btnStay.setSize(WIDTH, BTN_HEIGHT)
        add(btnStay)
        val height: Float
        if (noText) {
            height = amulet.height + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height()
            amulet.x = align((Camera.main.width - amulet.width) / 2)
            amulet.y = align((Camera.main.height - height) / 2)
            btnExit.setPos((Camera.main.width - btnExit.width()) / 2, amulet.y + amulet.height + LARGE_GAP)
            btnStay.setPos(btnExit.left(), btnExit.bottom() + SMALL_GAP)
        } else {
            height =
                amulet.height + LARGE_GAP + text.height() + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height()
            amulet.x = align((Camera.main.width - amulet.width) / 2)
            amulet.y = align((Camera.main.height - height) / 2)
            text.x = align((Camera.main.width - text.width()) / 2)
            text.y = amulet.y + amulet.height + LARGE_GAP
            btnExit.setPos((Camera.main.width - btnExit.width()) / 2, text.y + text.height() + LARGE_GAP)
            btnStay.setPos(btnExit.left(), btnExit.bottom() + SMALL_GAP)
        }
        Flare(8, 48).color(0xFFDDBB, true).show(amulet, 0).angularSpeed = +30
        fadeIn()
    }

    protected fun onBackPressed() {
        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
        Game.switchScene(InterlevelScene::class.java)
    }

    private var timer = 0f
    fun update() {
        super.update()
        if (Game.elapsed.let { timer -= it; timer } < 0) {
            timer = Random.Float(0.5f, 5f)
            val star: Speck = recycle(Speck::class.java) as Speck
            star.reset(0, amulet.x + 10.5f, amulet.y + 5.5f, Speck.DISCOVER)
            add(star)
        }
    }

    companion object {
        private const val TXT_EXIT = "Let's call it a day"
        private const val TXT_STAY = "I'm not done yet"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 18
        private const val SMALL_GAP = 2f
        private const val LARGE_GAP = 8f
        private const val TXT = "You finally hold it in your hands, the Amulet of Yendor. Using its power " +
                "you can take over the world or bring peace and prosperity to people or whatever. " +
                "Anyway, your life will change forever and this game will end here. " +
                "Or you can stay a mere mortal a little longer."
        var noText = false
    }
}