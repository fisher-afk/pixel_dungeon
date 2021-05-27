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
package com.watabou.pixeldungeon.items.scrolls

import com.watabou.pixeldungeon.Badges

class ScrollOfIdentify : InventoryScroll() {
    protected override fun onItemSelected(item: Item) {
        curUser.sprite.parent.add(Identification(curUser.sprite.center().offset(0, -16)))
        item.identify()
        GLog.i("It is $item")
        Badges.validateItemLevelAquired(item)
    }

    fun desc(): String {
        return "Permanently reveals all of the secrets of a single item."
    }

    override fun price(): Int {
        return if (isKnown()) 30 * quantity else super.price()
    }

    init {
        name = "Scroll of Identify"
        inventoryTitle = "Select an item to identify"
        mode = WndBag.Mode.UNIDENTIFED
    }
}