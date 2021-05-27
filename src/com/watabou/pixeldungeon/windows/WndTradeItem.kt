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

class WndTradeItem : Window {
    private var owner: WndBag? = null

    constructor(item: Item, owner: WndBag?) : super() {
        this.owner = owner
        var pos = createDescription(item, false)
        pos = if (item.quantity() === 1) {
            val btnSell: RedButton = object :
                RedButton(Utils.format(TXT_SELL, item.price())) {
                protected fun onClick() {
                    sell(item)
                    hide()
                }
            }
            btnSell.setRect(
                0,
                pos + GAP,
                WIDTH,
                BTN_HEIGHT
            )
            add(btnSell)
            btnSell.bottom()
        } else {
            val priceAll: Int = item.price()
            val btnSell1: RedButton = object : RedButton(
                Utils.format(
                    TXT_SELL_1,
                    priceAll / item.quantity()
                )
            ) {
                protected fun onClick() {
                    sellOne(item)
                    hide()
                }
            }
            btnSell1.setRect(
                0,
                pos + GAP,
                WIDTH,
                BTN_HEIGHT
            )
            add(btnSell1)
            val btnSellAll: RedButton = object :
                RedButton(Utils.format(TXT_SELL_ALL, priceAll)) {
                protected fun onClick() {
                    sell(item)
                    hide()
                }
            }
            btnSellAll.setRect(
                0,
                btnSell1.bottom() + GAP,
                WIDTH,
                BTN_HEIGHT
            )
            add(btnSellAll)
            btnSellAll.bottom()
        }
        val btnCancel: RedButton = object : RedButton(TXT_CANCEL) {
            protected fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT)
        add(btnCancel)
        resize(WIDTH, btnCancel.bottom() as Int)
    }

    constructor(heap: Heap, canBuy: Boolean) : super() {
        val item: Item = heap.peek()
        val pos = createDescription(item, true)
        val price = price(item)
        if (canBuy) {
            val btnBuy: RedButton = object : RedButton(Utils.format(TXT_BUY, price)) {
                protected fun onClick() {
                    hide()
                    buy(heap)
                }
            }
            btnBuy.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT)
            btnBuy.enable(price <= Dungeon.gold)
            add(btnBuy)
            val btnCancel: RedButton = object : RedButton(TXT_CANCEL) {
                protected fun onClick() {
                    hide()
                }
            }
            btnCancel.setRect(0, btnBuy.bottom() + GAP, WIDTH, BTN_HEIGHT)
            add(btnCancel)
            resize(WIDTH, btnCancel.bottom() as Int)
        } else {
            resize(WIDTH, pos.toInt())
        }
    }

    fun hide() {
        super.hide()
        if (owner != null) {
            owner.hide()
            Shopkeeper.sell()
        }
    }

    private fun createDescription(item: Item, forSale: Boolean): Float {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(item.image(), item.glowing()))
        titlebar.label(
            if (forSale) Utils.format(
                TXT_SALE,
                item.toString(),
                price(item)
            ) else Utils.capitalize(item.toString())
        )
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        if (item.levelKnown) {
            if (item.level() < 0) {
                titlebar.color(ItemSlot.DEGRADED)
            } else if (item.level() > 0) {
                titlebar.color(if (item.isBroken()) ItemSlot.WARNING else ItemSlot.UPGRADED)
            }
        }
        val info: BitmapTextMultiline = PixelScene.createMultiline(item.info(), 6)
        info.maxWidth = WIDTH
        info.measure()
        info.x = titlebar.left()
        info.y = titlebar.bottom() + GAP
        add(info)
        return info.y + info.height()
    }

    private fun sell(item: Item) {
        val hero: Hero = Dungeon.hero
        if (item.isEquipped(hero) && !(item as EquipableItem).doUnequip(hero, false)) {
            return
        }
        item.detachAll(hero.belongings.backpack)
        val price: Int = item.price()
        Gold(price).doPickUp(hero)
        GLog.i(TXT_SOLD, item.name(), price)
    }

    private fun sellOne(item: Item) {
        var item: Item = item
        if (item.quantity() <= 1) {
            sell(item)
        } else {
            val hero: Hero = Dungeon.hero
            item = item.detach(hero.belongings.backpack)
            val price: Int = item.price()
            Gold(price).doPickUp(hero)
            GLog.i(TXT_SOLD, item.name(), price)
        }
    }

    private fun price(item: Item): Int {
        var price: Int = item.price() * 5 * (Dungeon.depth / 5 + 1)
        if (Dungeon.hero.buff(RingOfHaggler.Haggling::class.java) != null && price >= 2) {
            price /= 2
        }
        return price
    }

    private fun buy(heap: Heap) {
        val hero: Hero = Dungeon.hero
        val item: Item = heap.pickUp()
        val price = price(item)
        Dungeon.gold -= price
        GLog.i(TXT_BOUGHT, item.name(), price)
        if (!item.doPickUp(hero)) {
            Dungeon.level.drop(item, heap.pos).sprite.drop()
        }
    }

    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 16
        private const val TXT_SALE = "FOR SALE: %s - %dg"
        private const val TXT_BUY = "Buy for %dg"
        private const val TXT_SELL = "Sell for %dg"
        private const val TXT_SELL_1 = "Sell 1 for %dg"
        private const val TXT_SELL_ALL = "Sell all for %dg"
        private const val TXT_CANCEL = "Never mind"
        private const val TXT_SOLD = "You've sold your %s for %dg"
        private const val TXT_BOUGHT = "You've bought %s for %dg"
    }
}