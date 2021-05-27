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

import com.watabou.gltextures.TextureCache

class WndBag(bag: Bag?, private val listener: Listener?, private val mode: Mode, private val title: String?) :
    WndTabbed() {
    enum class Mode {
        ALL, UNIDENTIFED, UPGRADEABLE, QUICKSLOT, FOR_SALE, WEAPON, ARMOR, ENCHANTABLE, WAND, SEED
    }

    private val nCols: Int
    private val nRows: Int
    protected var count = 0
    protected var col = 0
    protected var row = 0
    protected fun placeItems(container: Bag) {

        // Equipped items
        val stuff: Belongings = Dungeon.hero.belongings
        placeItem(if (stuff.weapon != null) stuff.weapon else Placeholder(ItemSpriteSheet.WEAPON))
        placeItem(if (stuff.armor != null) stuff.armor else Placeholder(ItemSpriteSheet.ARMOR))
        placeItem(if (stuff.ring1 != null) stuff.ring1 else Placeholder(ItemSpriteSheet.RING))
        placeItem(if (stuff.ring2 != null) stuff.ring2 else Placeholder(ItemSpriteSheet.RING))
        val backpack = container === Dungeon.hero.belongings.backpack
        if (!backpack) {
            count = nCols
            col = 0
            row = 1
        }

        // Items in the bag
        for (item in container.items) {
            placeItem(item)
        }

        // Free space
        while (count - (if (backpack) 4 else nCols) < container.size) {
            placeItem(null)
        }

        // Gold in the backpack
        if (container === Dungeon.hero.belongings.backpack) {
            row = nRows - 1
            col = nCols - 1
            placeItem(Gold(Dungeon.gold))
        }
    }

    protected fun placeItem(item: Item?) {
        val x = col * (SLOT_SIZE + SLOT_MARGIN)
        val y = TITLE_HEIGHT + row * (SLOT_SIZE + SLOT_MARGIN)
        add(ItemButton(item).setPos(x, y))
        if (++col >= nCols) {
            col = 0
            row++
        }
        count++
    }

    fun onMenuPressed() {
        if (listener == null) {
            hide()
        }
    }

    fun onBackPressed() {
        listener?.onSelect(null)
        super.onBackPressed()
    }

    protected fun onClick(tab: Tab) {
        hide()
        GameScene.show(WndBag((tab as BagTab).bag, listener, mode, title))
    }

    protected override fun tabHeight(): Int {
        return 20
    }

    private inner class BagTab(bag: Bag) : Tab() {
        private val icon: Image
        val bag: Bag
        override fun select(value: Boolean) {
            super.select(value)
            icon.am = if (selected) 1.0f else 0.6f
        }

        protected override fun layout() {
            super.layout()
            icon.copy(icon())
            icon.x = x + (width - icon.width) / 2
            icon.y = y + (height - icon.height) / 2 - 2 - if (selected) 0 else 1
            if (!selected && icon.y < y + CUT) {
                val frame: RectF = icon.frame()
                frame.top += (y + CUT - icon.y) / icon.texture.height
                icon.frame(frame)
                icon.y = y + CUT
            }
        }

        private fun icon(): Image {
            return if (bag is SeedPouch) {
                Icons.get(Icons.SEED_POUCH)
            } else if (bag is ScrollHolder) {
                Icons.get(Icons.SCROLL_HOLDER)
            } else if (bag is WandHolster) {
                Icons.get(Icons.WAND_HOLSTER)
            } else if (bag is Keyring) {
                Icons.get(Icons.KEYRING)
            } else {
                Icons.get(Icons.BACKPACK)
            }
        }

        init {
            this.bag = bag
            icon = icon()
            add(icon)
        }
    }

    private class Placeholder(image: Int) : Item() {
        val isIdentified: Boolean
            get() = true

        fun isEquipped(hero: Hero?): Boolean {
            return true
        }

        init {
            name = null
        }

        init {
            image = image
        }
    }

    private inner class ItemButton(item: Item) : ItemSlot(item) {
        private val item: Item
        private var bg: ColorBlock? = null
        private var durability: Array<ColorBlock?>?
        protected fun createChildren() {
            bg = ColorBlock(SLOT_SIZE, SLOT_SIZE, Companion.NORMAL)
            add(bg)
            super.createChildren()
        }

        protected fun layout() {
            bg.x = x
            bg.y = y
            if (durability != null) {
                for (i in 0 until Companion.NBARS) {
                    durability!![i].x = x + 1 + i * 3
                    durability!![i].y = y + height - 3
                }
            }
            super.layout()
        }

        fun item(item: Item?) {
            super.item(item)
            if (item != null) {
                bg.texture(TextureCache.createSolid(if (item.isEquipped(Dungeon.hero)) Companion.EQUIPPED else Companion.NORMAL))
                if (item.cursed && item.cursedKnown) {
                    bg.ra = +0.2f
                    bg.ga = -0.1f
                } else if (!item.isIdentified()) {
                    bg.ra = 0.1f
                    bg.ba = 0.1f
                }
                if (lastBag.owner.isAlive() && item.isUpgradable() && item.levelKnown) {
                    durability = arrayOfNulls<ColorBlock>(Companion.NBARS)
                    val nBars = GameMath.gate(
                        0,
                        Math.round(Companion.NBARS.toFloat() * item.durability() / item.maxDurability()),
                        Companion.NBARS
                    ) as Int
                    for (i in 0 until nBars) {
                        durability!![i] = ColorBlock(2, 2, -0xff1200)
                        add(durability!![i])
                    }
                    for (i in nBars until Companion.NBARS) {
                        durability!![i] = ColorBlock(2, 2, -0x340000)
                        add(durability!![i])
                    }
                }
                if (item.name() == null) {
                    enable(false)
                } else {
                    enable(
                        mode == Mode.QUICKSLOT && item.defaultAction != null || mode == Mode.FOR_SALE && item.price() > 0 && (!item.isEquipped(
                            Dungeon.hero
                        ) || !item.cursed) || mode == Mode.UPGRADEABLE && item.isUpgradable() || mode == Mode.UNIDENTIFED && !item.isIdentified() || mode == Mode.WEAPON && (item is MeleeWeapon || item is Boomerang) || mode == Mode.ARMOR && item is Armor || mode == Mode.ENCHANTABLE && (item is MeleeWeapon || item is Boomerang || item is Armor) || mode == Mode.WAND && item is Wand || mode == Mode.SEED && item is Seed || mode == Mode.ALL
                    )
                }
            } else {
                bg.color(Companion.NORMAL)
            }
        }

        protected fun onTouchDown() {
            bg.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        protected fun onTouchUp() {
            bg.brightness(1.0f)
        }

        protected fun onClick() {
            if (listener != null) {
                hide()
                listener.onSelect(item)
            } else {
                this@WndBag.add(WndItem(this@WndBag, item))
            }
        }

        protected fun onLongClick(): Boolean {
            return if (listener == null && item.defaultAction != null) {
                hide()
                QuickSlot.primaryValue = if (item.stackable) item.getClass() else item
                QuickSlot.refresh()
                true
            } else {
                false
            }
        }

        companion object {
            private const val NORMAL = -0xb5b2bc
            private const val EQUIPPED = -0x9c99a5
            private const val NBARS = 3
        }

        init {
            this.item = item
            if (item is Gold) {
                bg.visible = false
            }
            height = SLOT_SIZE
            width = height
        }
    }

    interface Listener {
        fun onSelect(item: Item?)
    }

    companion object {
        protected const val COLS_P = 4
        protected const val COLS_L = 6
        protected const val SLOT_SIZE = 28
        protected const val SLOT_MARGIN = 1
        protected const val TAB_WIDTH = 25
        protected const val TITLE_HEIGHT = 12
        private var lastMode: Mode
        private var lastBag: Bag?
        fun lastBag(listener: Listener?, mode: Mode, title: String?): WndBag {
            return if (mode == lastMode && lastBag != null &&
                Dungeon.hero.belongings.backpack.contains(lastBag)
            ) {
                WndBag(lastBag, listener, mode, title)
            } else {
                WndBag(Dungeon.hero.belongings.backpack, listener, mode, title)
            }
        }

        fun seedPouch(listener: Listener?, mode: Mode, title: String?): WndBag {
            val pouch: SeedPouch = Dungeon.hero.belongings.getItem(SeedPouch::class.java)
            return if (pouch != null) WndBag(pouch, listener, mode, title) else WndBag(
                Dungeon.hero.belongings.backpack,
                listener,
                mode,
                title
            )
        }
    }

    init {
        lastMode = mode
        lastBag = bag
        nCols = if (PixelDungeon.landscape()) COLS_L else COLS_P
        nRows =
            (Belongings.BACKPACK_SIZE + 4 + 1) / nCols + if ((Belongings.BACKPACK_SIZE + 4 + 1) % nCols > 0) 1 else 0
        val slotsWidth = SLOT_SIZE * nCols + SLOT_MARGIN * (nCols - 1)
        val slotsHeight = SLOT_SIZE * nRows + SLOT_MARGIN * (nRows - 1)
        val txtTitle: BitmapText = PixelScene.createText(title ?: Utils.capitalize(bag.name()), 9)
        txtTitle.hardlight(TITLE_COLOR)
        txtTitle.measure()
        txtTitle.x = (slotsWidth - txtTitle.width()) as Int / 2
        txtTitle.y = (TITLE_HEIGHT - txtTitle.height()) as Int / 2
        add(txtTitle)
        placeItems(bag)
        resize(slotsWidth, slotsHeight + TITLE_HEIGHT)
        val stuff: Belongings = Dungeon.hero.belongings
        val bags: Array<Bag> = arrayOf<Bag>(
            stuff.backpack,
            stuff.getItem(SeedPouch::class.java),
            stuff.getItem(ScrollHolder::class.java),
            stuff.getItem(WandHolster::class.java),
            stuff.getItem(Keyring::class.java)
        )
        for (b in bags) {
            if (b != null) {
                val tab: BagTab = BagTab(b)
                tab.setSize(TAB_WIDTH, tabHeight())
                add(tab)
                tab.select(b === bag)
            }
        }
    }
}