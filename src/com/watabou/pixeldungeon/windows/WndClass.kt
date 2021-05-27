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

import com.watabou.noosa.BitmapText

class WndClass(cl: HeroClass) : WndTabbed() {
    private val cl: HeroClass
    private val tabPerks: PerksTab
    private var tabMastery: MasteryTab? = null

    private inner class RankingTab(label: String?, page: Group?) : LabeledTab(label) {
        private val page: Group?
        protected override fun select(value: Boolean) {
            super.select(value)
            if (page != null) {
                page.active = selected
                page.visible = page.active
            }
        }

        init {
            this.page = page
        }
    }

    private inner class PerksTab : Group() {
        var height: Float
        var width: Float

        companion object {
            private const val MARGIN = 4
            private const val GAP = 4
            private const val DOT = "\u007F"
        }

        init {
            var dotWidth = 0f
            val items: Array<String> = cl.perks()
            var pos = Companion.MARGIN.toFloat()
            for (i in items.indices) {
                if (i > 0) {
                    pos += Companion.GAP.toFloat()
                }
                val dot: BitmapText = PixelScene.createText(Companion.DOT, 6)
                dot.x = Companion.MARGIN
                dot.y = pos
                if (dotWidth == 0f) {
                    dot.measure()
                    dotWidth = dot.width()
                }
                add(dot)
                val item: BitmapTextMultiline = PixelScene.createMultiline(items[i], 6)
                item.x = dot.x + dotWidth
                item.y = pos
                item.maxWidth = (WIDTH - Companion.MARGIN * 2 - dotWidth).toInt()
                item.measure()
                add(item)
                pos += item.height()
                val w: Float = item.width()
                if (w > width) {
                    width = w
                }
            }
            width += Companion.MARGIN + dotWidth
            height = pos + Companion.MARGIN
        }
    }

    private inner class MasteryTab : Group() {
        var height: Float
        var width: Float

        companion object {
            private const val MARGIN = 4
        }

        init {
            var message: String? = null
            when (cl) {
                HeroClass.WARRIOR -> message =
                    HeroSubClass.GLADIATOR.desc().toString() + "\n\n" + HeroSubClass.BERSERKER.desc()
                HeroClass.MAGE -> message =
                    HeroSubClass.BATTLEMAGE.desc().toString() + "\n\n" + HeroSubClass.WARLOCK.desc()
                HeroClass.ROGUE -> message =
                    HeroSubClass.FREERUNNER.desc().toString() + "\n\n" + HeroSubClass.ASSASSIN.desc()
                HeroClass.HUNTRESS -> message =
                    HeroSubClass.SNIPER.desc().toString() + "\n\n" + HeroSubClass.WARDEN.desc()
            }
            val text = HighlightedText(6)
            text.text(message, WIDTH - Companion.MARGIN * 2)
            text.setPos(Companion.MARGIN, Companion.MARGIN)
            add(text)
            height = text.bottom() + Companion.MARGIN
            width = text.right() + Companion.MARGIN
        }
    }

    companion object {
        private const val TXT_MASTERY = "Mastery"
        private const val WIDTH = 110
        private const val TAB_WIDTH = 50
    }

    init {
        this.cl = cl
        tabPerks = PerksTab()
        add(tabPerks)
        var tab: Tab = RankingTab(Utils.capitalize(cl.title()), tabPerks)
        tab.setSize(TAB_WIDTH, tabHeight())
        add(tab)
        if (Badges.isUnlocked(cl.masteryBadge())) {
            tabMastery = MasteryTab()
            add(tabMastery)
            tab = RankingTab(TXT_MASTERY, tabMastery)
            tab.setSize(TAB_WIDTH, tabHeight())
            add(tab)
            resize(
                Math.max(tabPerks.width, tabMastery!!.width).toInt(),
                Math.max(tabPerks.height, tabMastery!!.height).toInt()
            )
        } else {
            resize(tabPerks.width.toInt(), tabPerks.height.toInt())
        }
        select(0)
    }
}