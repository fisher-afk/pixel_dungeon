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

import com.watabou.gltextures.SmartTexture

class WndHero : WndTabbed() {
    private val stats: StatsTab
    private val buffs: BuffsTab
    private val icons: SmartTexture
    private val film: TextureFilm

    private inner class StatsTab : Group() {
        private var pos: Float
        private fun statSlot(label: String, value: String) {
            var txt: BitmapText = PixelScene.createText(label, 8)
            txt.y = pos
            add(txt)
            txt = PixelScene.createText(value, 8)
            txt.measure()
            txt.x = PixelScene.align(WIDTH * 0.65f)
            txt.y = pos
            add(txt)
            pos += Companion.GAP + txt.baseLine()
        }

        private fun statSlot(label: String, value: Int) {
            statSlot(label, Integer.toString(value))
        }

        fun height(): Float {
            return pos
        }

        companion object {
            private const val TXT_TITLE = "Level %d %s"
            private const val TXT_CATALOGUS = "Catalogus"
            private const val TXT_JOURNAL = "Journal"
            private const val GAP = 5
        }

        init {
            val hero: Hero = Dungeon.hero
            val title: BitmapText = PixelScene.createText(
                Utils.format(Companion.TXT_TITLE, hero.lvl, hero.className()).toUpperCase(Locale.ENGLISH), 9
            )
            title.hardlight(TITLE_COLOR)
            title.measure()
            add(title)
            val btnCatalogus: RedButton = object : RedButton(Companion.TXT_CATALOGUS) {
                protected fun onClick() {
                    hide()
                    GameScene.show(WndCatalogus())
                }
            }
            btnCatalogus.setRect(0, title.y + title.height(), btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2)
            add(btnCatalogus)
            val btnJournal: RedButton = object : RedButton(Companion.TXT_JOURNAL) {
                protected fun onClick() {
                    hide()
                    GameScene.show(WndJournal())
                }
            }
            btnJournal.setRect(
                btnCatalogus.right() + 1, btnCatalogus.top(),
                btnJournal.reqWidth() + 2, btnJournal.reqHeight() + 2
            )
            add(btnJournal)
            pos = btnCatalogus.bottom() + Companion.GAP
            statSlot(TXT_STR, hero.STR())
            statSlot(TXT_HEALTH, hero.HP.toString() + "/" + hero.HT)
            statSlot(TXT_EXP, hero.exp.toString() + "/" + hero.maxExp())
            pos += Companion.GAP.toFloat()
            statSlot(TXT_GOLD, Statistics.goldCollected)
            statSlot(TXT_DEPTH, Statistics.deepestFloor)
            pos += Companion.GAP.toFloat()
        }
    }

    private inner class BuffsTab : Group() {
        private var pos = 0f
        private fun buffSlot(buff: Buff) {
            val index: Int = buff.icon()
            if (index != BuffIndicator.NONE) {
                val icon = Image(icons)
                icon.frame(film.get(index))
                icon.y = pos
                add(icon)
                val txt: BitmapText = PixelScene.createText(buff.toString(), 8)
                txt.x = icon.width + Companion.GAP
                txt.y = pos + (icon.height - txt.baseLine()) as Int / 2
                add(txt)
                pos += Companion.GAP + icon.height
            }
        }

        fun height(): Float {
            return pos
        }

        companion object {
            private const val GAP = 2
        }

        init {
            for (buff in Dungeon.hero.buffs()) {
                buffSlot(buff)
            }
        }
    }

    companion object {
        private const val TXT_STATS = "Stats"
        private const val TXT_BUFFS = "Buffs"
        private const val TXT_EXP = "Experience"
        private const val TXT_STR = "Strength"
        private const val TXT_HEALTH = "Health"
        private const val TXT_GOLD = "Gold Collected"
        private const val TXT_DEPTH = "Maximum Depth"
        private const val WIDTH = 100
        private const val TAB_WIDTH = 40
    }

    init {
        icons = TextureCache.get(Assets.BUFFS_LARGE)
        film = TextureFilm(icons, 16, 16)
        stats = StatsTab()
        add(stats)
        buffs = BuffsTab()
        add(buffs)
        add(object : LabeledTab(TXT_STATS) {
            protected override fun select(value: Boolean) {
                super.select(value)
                stats.active = selected
                stats.visible = stats.active
            }
        })
        add(object : LabeledTab(TXT_BUFFS) {
            protected override fun select(value: Boolean) {
                super.select(value)
                buffs.active = selected
                buffs.visible = buffs.active
            }
        })
        for (tab in tabs) {
            tab.setSize(TAB_WIDTH, tabHeight())
        }
        resize(
            WIDTH, Math.max(stats.height(), buffs.height())
                .toInt()
        )
        select(0)
    }
}