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

class RankingsScene : PixelScene() {
    private var archs: Archs? = null
    override fun create() {
        super.create()
        Music.INSTANCE.play(Assets.THEME, true)
        Music.INSTANCE.volume(1f)
        uiCamera.visible = false
        val w: Int = Camera.main.width
        val h: Int = Camera.main.height
        archs = Archs()
        archs.setSize(w, h)
        add(archs)
        Rankings.INSTANCE.load()
        if (Rankings.INSTANCE.records.size() > 0) {
            val rowHeight = if (PixelDungeon.landscape()) ROW_HEIGHT_L else ROW_HEIGHT_P
            val left = (w - Math.min(MAX_ROW_WIDTH, w.toFloat())) / 2 + GAP
            val top: Float = align((h - rowHeight * Rankings.INSTANCE.records.size()) / 2)
            val title: BitmapText = PixelScene.createText(TXT_TITLE, 9)
            title.hardlight(Window.TITLE_COLOR)
            title.measure()
            title.x = align((w - title.width()) / 2)
            title.y = align(top - title.height() - GAP)
            add(title)
            var pos = 0
            for (rec in Rankings.INSTANCE.records) {
                val row = Record(pos, pos == Rankings.INSTANCE.lastRecord, rec)
                row.setRect(left, top + pos * rowHeight, w - left * 2, rowHeight)
                add(row)
                pos++
            }
            if (Rankings.INSTANCE.totalNumber >= Rankings.TABLE_SIZE) {
                val label: BitmapText = PixelScene.createText(TXT_TOTAL, 8)
                label.hardlight(DEFAULT_COLOR)
                label.measure()
                add(label)
                val won: BitmapText = PixelScene.createText(Integer.toString(Rankings.INSTANCE.wonNumber), 8)
                won.hardlight(Window.TITLE_COLOR)
                won.measure()
                add(won)
                val total: BitmapText = PixelScene.createText("/" + Rankings.INSTANCE.totalNumber, 8)
                total.hardlight(DEFAULT_COLOR)
                total.measure()
                total.x = align((w - total.width()) / 2)
                total.y = align(top + pos * rowHeight + GAP)
                add(total)
                val tw: Float = label.width() + won.width() + total.width()
                label.x = align((w - tw) / 2)
                won.x = label.x + label.width()
                total.x = won.x + won.width()
                total.y = align(top + pos * rowHeight + GAP)
                won.y = total.y
                label.y = won.y
            }
        } else {
            val title: BitmapText = PixelScene.createText(TXT_NO_GAMES, 8)
            title.hardlight(DEFAULT_COLOR)
            title.measure()
            title.x = align((w - title.width()) / 2)
            title.y = align((h - title.height()) / 2)
            add(title)
        }
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0)
        add(btnExit)
        fadeIn()
    }

    protected fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }

    class Record(pos: Int, latest: Boolean, rec: Rankings.Record) : Button() {
        private val rec: Rankings.Record
        private var shield: ItemSprite? = null
        private var flare: Flare? = null
        private var position: BitmapText? = null
        private var desc: BitmapTextMultiline? = null
        private var classIcon: Image? = null
        protected fun createChildren() {
            super.createChildren()
            shield = ItemSprite(ItemSpriteSheet.TOMB, null)
            add(shield)
            position = BitmapText(PixelScene.font1x)
            add(position)
            desc = createMultiline(9)
            add(desc)
            classIcon = Image()
            add(classIcon)
        }

        protected fun layout() {
            super.layout()
            shield.x = x
            shield.y = y + (height - shield.height) / 2
            position.x = align(shield.x + (shield.width - position.width()) / 2)
            position.y = align(shield.y + (shield.height - position.height()) / 2 + 1)
            if (flare != null) {
                flare.point(shield.center())
            }
            classIcon.x = align(x + width - classIcon.width)
            classIcon.y = shield.y
            desc.x = shield.x + shield.width + GAP
            desc.maxWidth = (classIcon.x - desc.x)
            desc.measure()
            desc.y = position.y + position.baseLine() - desc.baseLine()
        }

        protected fun onClick() {
            if (rec.gameFile.length() > 0) {
                parent.add(WndRanking(rec.gameFile))
            } else {
                parent.add(WndError(TXT_NO_INFO))
            }
        }

        companion object {
            private const val GAP = 4f
            private const val TEXT_WIN = 0xFFFF88
            private const val TEXT_LOSE = 0xCCCCCC
            private const val FLARE_WIN = 0x888866
            private const val FLARE_LOSE = 0x666666
        }

        init {
            this.rec = rec
            if (latest) {
                flare = Flare(6, 24)
                flare.angularSpeed = 90
                flare.color(if (rec.win) FLARE_WIN else FLARE_LOSE)
                addToBack(flare)
            }
            position.text(Integer.toString(pos + 1))
            position.measure()
            desc.text(rec.info)
            desc.measure()
            if (rec.win) {
                shield.view(ItemSpriteSheet.AMULET, null)
                position.hardlight(TEXT_WIN)
                desc.hardlight(TEXT_WIN)
            } else {
                position.hardlight(TEXT_LOSE)
                desc.hardlight(TEXT_LOSE)
            }
            classIcon.copy(Icons.get(rec.heroClass))
        }
    }

    companion object {
        private const val DEFAULT_COLOR = 0xCCCCCC
        private const val TXT_TITLE = "Top Rankings"
        private const val TXT_TOTAL = "Games played: "
        private const val TXT_NO_GAMES = "No games have been played yet."
        private const val TXT_NO_INFO = "No additional information"
        private const val ROW_HEIGHT_L = 22f
        private const val ROW_HEIGHT_P = 28f
        private const val MAX_ROW_WIDTH = 180f
        private const val GAP = 4f
    }
}