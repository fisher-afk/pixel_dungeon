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

class BadgesScene : PixelScene() {
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
        val pw = Math.min(w, (if (PixelDungeon.landscape()) MIN_WIDTH_L else MIN_WIDTH_P) * 3) as Int - 16
        val ph = Math.min(h, (if (PixelDungeon.landscape()) MIN_HEIGHT_L else MIN_HEIGHT_P) * 3) as Int - 32
        var size = Math.sqrt((pw * ph / 27f).toDouble()).toFloat()
        val nCols = Math.ceil((pw / size).toDouble()).toInt()
        val nRows = Math.ceil((ph / size).toDouble()).toInt()
        size = Math.min(pw / nCols, ph / nRows).toFloat()
        val left = (w - size * nCols) / 2
        val top = (h - size * nRows) / 2
        val title: BitmapText = PixelScene.createText(TXT_TITLE, 9)
        title.hardlight(Window.TITLE_COLOR)
        title.measure()
        title.x = align((w - title.width()) / 2)
        title.y = align((top - title.baseLine()) / 2)
        add(title)
        Badges.loadGlobal()
        val badges: List<Badges.Badge> = Badges.filtered(true)
        for (i in 0 until nRows) {
            for (j in 0 until nCols) {
                val index = i * nCols + j
                val b: Badges.Badge? = if (index < badges.size) badges[index] else null
                val button = BadgeButton(b)
                button.setPos(
                    left + j * size + (size - button.width()) / 2,
                    top + i * size + (size - button.height()) / 2
                )
                add(button)
            }
        }
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0)
        add(btnExit)
        fadeIn()
        Badges.loadingListener = object : Callback() {
            fun call() {
                if (Game.scene() === this@BadgesScene) {
                    PixelDungeon.switchNoFade(BadgesScene::class.java)
                }
            }
        }
    }

    override fun destroy() {
        Badges.saveGlobal()
        Badges.loadingListener = null
        super.destroy()
    }

    protected fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }

    private class BadgeButton(badge: Badges.Badge?) : Button() {
        private val badge: Badges.Badge?
        private val icon: Image
        protected fun layout() {
            super.layout()
            icon.x = align(x + (width - icon.width()) / 2)
            icon.y = align(y + (height - icon.height()) / 2)
        }

        fun update() {
            super.update()
            if (Random.Float() < Game.elapsed * 0.1) {
                BadgeBanner.highlight(icon, badge.image)
            }
        }

        protected fun onClick() {
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
            Game.scene().add(WndBadge(badge))
        }

        init {
            this.badge = badge
            active = badge != null
            icon = if (active) BadgeBanner.image(badge.image) else Image(Assets.LOCKED)
            add(icon)
            setSize(icon.width(), icon.height())
        }
    }

    companion object {
        private const val TXT_TITLE = "Your Badges"
    }
}