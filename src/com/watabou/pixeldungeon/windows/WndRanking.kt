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

class WndRanking(gameFile: String?) : WndTabbed() {
    private var thread: Thread?
    private var error: String? = null
    private val busy: Image
    fun update() {
        super.update()
        if (thread != null && !thread!!.isAlive) {
            thread = null
            if (error == null) {
                remove(busy)
                createControls()
            } else {
                hide()
                Game.scene().add(WndError(TXT_ERROR))
            }
        }
    }

    private fun createControls() {
        val labels = arrayOf(TXT_STATS, TXT_ITEMS, TXT_BADGES)
        val pages: Array<Group> = arrayOf<Group>(StatsTab(), ItemsTab(), BadgesTab())
        for (i in pages.indices) {
            add(pages[i])
            val tab: Tab = RankingTab(labels[i], pages[i])
            tab.setSize(TAB_WIDTH, tabHeight())
            add(tab)
        }
        select(0)
    }

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

    private inner class StatsTab : Group() {
        private fun statSlot(parent: Group, label: String, value: String, pos: Float): Float {
            var txt: BitmapText = PixelScene.createText(label, 7)
            txt.y = pos
            parent.add(txt)
            txt = PixelScene.createText(value, 7)
            txt.measure()
            txt.x = PixelScene.align(WIDTH * 0.65f)
            txt.y = pos
            parent.add(txt)
            return pos + Companion.GAP + txt.baseLine()
        }

        companion object {
            private const val GAP = 4
            private const val TXT_TITLE = "Level %d %s"
            private const val TXT_CHALLENGES = "Challenges"
            private const val TXT_HEALTH = "Health"
            private const val TXT_STR = "Strength"
            private const val TXT_DURATION = "Game Duration"
            private const val TXT_DEPTH = "Maximum Depth"
            private const val TXT_ENEMIES = "Mobs Killed"
            private const val TXT_GOLD = "Gold Collected"
            private const val TXT_FOOD = "Food Eaten"
            private const val TXT_ALCHEMY = "Potions Cooked"
            private const val TXT_ANKHS = "Ankhs Used"
        }

        init {
            val heroClass: String = Dungeon.hero.className()
            val title = IconTitle()
            title.icon(HeroSprite.avatar(Dungeon.hero.heroClass, Dungeon.hero.tier()))
            title.label(Utils.format(Companion.TXT_TITLE, Dungeon.hero.lvl, heroClass).toUpperCase(Locale.ENGLISH))
            title.setRect(0, 0, WIDTH, 0)
            add(title)
            var pos: Float = title.bottom()
            if (Dungeon.challenges > 0) {
                val btnCatalogus: RedButton = object : RedButton(Companion.TXT_CHALLENGES) {
                    protected fun onClick() {
                        Game.scene().add(WndChallenges(Dungeon.challenges, false))
                    }
                }
                btnCatalogus.setRect(0, pos + Companion.GAP, btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2)
                add(btnCatalogus)
                pos = btnCatalogus.bottom()
            }
            pos += (Companion.GAP + Companion.GAP).toFloat()
            pos = statSlot(this, Companion.TXT_STR, Integer.toString(Dungeon.hero.STR), pos)
            pos = statSlot(this, Companion.TXT_HEALTH, Integer.toString(Dungeon.hero.HT), pos)
            pos += Companion.GAP.toFloat()
            pos = statSlot(this, Companion.TXT_DURATION, Integer.toString(Statistics.duration as Int), pos)
            pos += Companion.GAP.toFloat()
            pos = statSlot(this, Companion.TXT_DEPTH, Integer.toString(Statistics.deepestFloor), pos)
            pos = statSlot(this, Companion.TXT_ENEMIES, Integer.toString(Statistics.enemiesSlain), pos)
            pos = statSlot(this, Companion.TXT_GOLD, Integer.toString(Statistics.goldCollected), pos)
            pos += Companion.GAP.toFloat()
            pos = statSlot(this, Companion.TXT_FOOD, Integer.toString(Statistics.foodEaten), pos)
            pos = statSlot(this, Companion.TXT_ALCHEMY, Integer.toString(Statistics.potionsCooked), pos)
            pos = statSlot(this, Companion.TXT_ANKHS, Integer.toString(Statistics.ankhsUsed), pos)
        }
    }

    private inner class ItemsTab : Group() {
        private var count = 0
        private var pos = 0f
        private fun addItem(item: Item) {
            val slot: LabelledItemButton = LabelledItemButton(item)
            slot.setRect(0, pos, width, SIZE)
            add(slot)
            pos += slot.height() + 1
            count++
        }

        private fun getQuickslot(value: Any): Item? {
            if (value is Item && Dungeon.hero.belongings.backpack.contains(value as Item)) {
                return value as Item
            } else if (value is Class<*>) {
                val item: Item = Dungeon.hero.belongings.getItem(value as Class<out Item?>)
                if (item != null) {
                    return item
                }
            }
            return null
        }

        init {
            val stuff: Belongings = Dungeon.hero.belongings
            if (stuff.weapon != null) {
                addItem(stuff.weapon)
            }
            if (stuff.armor != null) {
                addItem(stuff.armor)
            }
            if (stuff.ring1 != null) {
                addItem(stuff.ring1)
            }
            if (stuff.ring2 != null) {
                addItem(stuff.ring2)
            }
            val primary: Item? = getQuickslot(QuickSlot.primaryValue)
            val secondary: Item? = getQuickslot(QuickSlot.secondaryValue)
            if (count >= 4 && primary != null && secondary != null) {
                val size = ItemButton.Companion.SIZE.toFloat()
                var slot: ItemButton = ItemButton(primary)
                slot.setRect(0, pos, size, size)
                add(slot)
                slot = ItemButton(secondary)
                slot.setRect(size + 1, pos, size, size)
                add(slot)
            } else {
                if (primary != null) {
                    addItem(primary)
                }
                if (secondary != null) {
                    addItem(secondary)
                }
            }
        }
    }

    private inner class BadgesTab : Group() {
        init {
            camera = this@WndRanking.camera
            val list: ScrollPane = BadgesList(false)
            add(list)
            list.setSize(WIDTH, HEIGHT)
        }
    }

    private inner class ItemButton(item: Item) : Button() {
        protected var item: Item
        protected var slot: ItemSlot? = null
        private var bg: ColorBlock? = null
        protected fun createChildren() {
            bg = ColorBlock(Companion.SIZE, Companion.SIZE, -0xb5b2bc)
            add(bg)
            slot = ItemSlot()
            add(slot)
            super.createChildren()
        }

        protected fun layout() {
            bg.x = x
            bg.y = y
            slot.setRect(x, y, Companion.SIZE, Companion.SIZE)
            super.layout()
        }

        protected fun onTouchDown() {
            bg.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        protected fun onTouchUp() {
            bg.brightness(1.0f)
        }

        protected fun onClick() {
            Game.scene().add(WndItem(null, item))
        }

        companion object {
            const val SIZE = 26
        }

        init {
            this.item = item
            slot.item(item)
            if (item.cursed && item.cursedKnown) {
                bg.ra = +0.2f
                bg.ga = -0.1f
            } else if (!item.isIdentified()) {
                bg.ra = 0.1f
                bg.ba = 0.1f
            }
        }
    }

    private inner class LabelledItemButton(item: Item) : ItemButton(item) {
        private var name: BitmapText? = null
        override fun createChildren() {
            super.createChildren()
            name = PixelScene.createText("?", 7)
            add(name)
        }

        override fun layout() {
            super.layout()
            name.x = slot.right() + 2
            name.y = y + (height - name.baseLine()) / 2
            var str: String = Utils.capitalize(item.name())
            name.text(str)
            name.measure()
            if (name.width() > width - name.x) {
                do {
                    str = str.substring(0, str.length - 1)
                    name.text("$str...")
                    name.measure()
                } while (name.width() > width - name.x)
            }
        }
    }

    companion object {
        private const val TXT_ERROR = "Unable to load additional information"
        private const val TXT_STATS = "Stats"
        private const val TXT_ITEMS = "Items"
        private const val TXT_BADGES = "Badges"
        private const val WIDTH = 112
        private const val HEIGHT = 134
        private const val TAB_WIDTH = 40
    }

    init {
        resize(WIDTH, HEIGHT)
        thread = object : Thread() {
            override fun run() {
                try {
                    Badges.loadGlobal()
                    Dungeon.loadGame(gameFile)
                } catch (e: Exception) {
                    error = TXT_ERROR
                }
            }
        }
        thread.start()
        busy = Icons.BUSY.get()
        busy.origin.set(busy.width / 2, busy.height / 2)
        busy.angularSpeed = 720
        busy.x = (WIDTH - busy.width) / 2
        busy.y = (HEIGHT - busy.height) / 2
        add(busy)
    }
}