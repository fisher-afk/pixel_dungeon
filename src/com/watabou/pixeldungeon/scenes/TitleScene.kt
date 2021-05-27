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

import com.watabou.noosa.BitmapText

class TitleScene : PixelScene() {
    override fun create() {
        super.create()
        Music.INSTANCE.play(Assets.THEME, true)
        Music.INSTANCE.volume(1f)
        uiCamera.visible = false
        val w: Int = Camera.main.width
        val h: Int = Camera.main.height
        val archs = Archs()
        archs.setSize(w, h)
        add(archs)
        val title: Image = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON)
        add(title)
        val height: Float = title.height +
                if (PixelDungeon.landscape()) DashboardItem.SIZE else DashboardItem.SIZE * 2
        title.x = (w - title.width()) / 2
        title.y = (h - height) / 2
        placeTorch(title.x + 18, title.y + 20)
        placeTorch(title.x + title.width - 18, title.y + 20)
        val signs: Image = object : Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)) {
            private var time = 0f
            fun update() {
                super.update()
                am = Math.sin(-Game.elapsed.let { time += it; time }.toDouble()).toFloat()
            }

            fun draw() {
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
                super.draw()
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            }
        }
        signs.x = title.x
        signs.y = title.y
        add(signs)
        val btnBadges: DashboardItem = object : DashboardItem(TXT_BADGES, 3) {
            protected fun onClick() {
                PixelDungeon.switchNoFade(BadgesScene::class.java)
            }
        }
        add(btnBadges)
        val btnAbout: DashboardItem = object : DashboardItem(TXT_ABOUT, 1) {
            protected fun onClick() {
                PixelDungeon.switchNoFade(AboutScene::class.java)
            }
        }
        add(btnAbout)
        val btnPlay: DashboardItem = object : DashboardItem(TXT_PLAY, 0) {
            protected fun onClick() {
                PixelDungeon.switchNoFade(StartScene::class.java)
            }
        }
        add(btnPlay)
        val btnHighscores: DashboardItem = object : DashboardItem(TXT_HIGHSCORES, 2) {
            protected fun onClick() {
                PixelDungeon.switchNoFade(RankingsScene::class.java)
            }
        }
        add(btnHighscores)
        if (PixelDungeon.landscape()) {
            val y = (h + height) / 2 - DashboardItem.SIZE
            btnHighscores.setPos(w / 2 - btnHighscores.width(), y)
            btnBadges.setPos(w / 2, y)
            btnPlay.setPos(btnHighscores.left() - btnPlay.width(), y)
            btnAbout.setPos(btnBadges.right(), y)
        } else {
            btnBadges.setPos(w / 2 - btnBadges.width(), (h + height) / 2 - DashboardItem.SIZE)
            btnAbout.setPos(w / 2, (h + height) / 2 - DashboardItem.SIZE)
            btnPlay.setPos(w / 2 - btnPlay.width(), btnAbout.top() - DashboardItem.SIZE)
            btnHighscores.setPos(w / 2, btnPlay.top())
        }
        val version = BitmapText("v " + Game.version, font1x)
        version.measure()
        version.hardlight(0x888888)
        version.x = w - version.width()
        version.y = h - version.height()
        add(version)
        val btnPrefs = PrefsButton()
        btnPrefs.setPos(0, 0)
        add(btnPrefs)
        val btnExit = ExitButton()
        btnExit.setPos(w - btnExit.width(), 0)
        add(btnExit)
        fadeIn()
    }

    private fun placeTorch(x: Float, y: Float) {
        val fb = Fireball()
        fb.setPos(x, y)
        add(fb)
    }

    private class DashboardItem(text: String?, index: Int) : Button() {
        private var image: Image? = null
        private var label: BitmapText? = null
        protected fun createChildren() {
            super.createChildren()
            image = Image(Assets.DASHBOARD)
            add(image)
            label = createText(9)
            add(label)
        }

        protected fun layout() {
            super.layout()
            image.x = align(x + (width - image.width()) / 2)
            image.y = align(y)
            label.x = align(x + (width - label.width()) / 2)
            label.y = align(image.y + image.height() + 2)
        }

        protected fun onTouchDown() {
            image.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 1, 1, 0.8f)
        }

        protected fun onTouchUp() {
            image.resetColor()
        }

        companion object {
            const val SIZE = 48f
            private const val IMAGE_SIZE = 32
        }

        init {
            image.frame(image.texture.uvRect(index * IMAGE_SIZE, 0, (index + 1) * IMAGE_SIZE, IMAGE_SIZE))
            label.text(text)
            label.measure()
            setSize(SIZE, SIZE)
        }
    }

    companion object {
        private const val TXT_PLAY = "Play"
        private const val TXT_HIGHSCORES = "Rankings"
        private const val TXT_BADGES = "Badges"
        private const val TXT_ABOUT = "About"
    }
}