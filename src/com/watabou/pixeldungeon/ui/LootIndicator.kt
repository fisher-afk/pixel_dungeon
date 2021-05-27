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
package com.watabou.pixeldungeon.ui

import com.watabou.pixeldungeon.Dungeon

class LootIndicator : Tag(0x1F75CC) {
    private var slot: ItemSlot? = null
    private var lastItem: Item? = null
    private var lastQuantity = 0
    protected override fun createChildren() {
        super.createChildren()
        slot = object : ItemSlot() {
            protected fun onClick() {
                Dungeon.hero.handle(Dungeon.hero.pos)
            }
        }
        slot.showParams(false)
        add(slot)
    }

    protected override fun layout() {
        super.layout()
        slot.setRect(x + 2, y + 3, width - 2, height - 6)
    }

    override fun update() {
        if (Dungeon.hero.ready) {
            val heap: Heap = Dungeon.level.heaps.get(Dungeon.hero.pos)
            if (heap != null && heap.type !== Heap.Type.HIDDEN) {
                val item: Item =
                    if (heap.type === Heap.Type.CHEST || heap.type === Heap.Type.MIMIC) ItemSlot.CHEST else if (heap.type === Heap.Type.LOCKED_CHEST) ItemSlot.LOCKED_CHEST else if (heap.type === Heap.Type.TOMB) ItemSlot.TOMB else if (heap.type === Heap.Type.SKELETON) ItemSlot.SKELETON else heap.peek()
                if (item !== lastItem || item.quantity() !== lastQuantity) {
                    lastItem = item
                    lastQuantity = item.quantity()
                    slot!!.item(item)
                    flash()
                }
                visible = true
            } else {
                lastItem = null
                visible = false
            }
        }
        slot!!.enable(visible && Dungeon.hero.ready)
        super.update()
    }

    init {
        setSize(24, 22)
        visible = false
    }
}