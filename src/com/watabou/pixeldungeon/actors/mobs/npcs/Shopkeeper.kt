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
package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon

class Shopkeeper : NPC() {
    protected fun act(): Boolean {
        throwItem()
        sprite.turnTo(pos, Dungeon.hero.pos)
        spend(TICK)
        return true
    }

    fun damage(dmg: Int, src: Any?) {
        flee()
    }

    fun add(buff: Buff?) {
        flee()
    }

    protected fun flee() {
        for (heap in Dungeon.level.heaps.values()) {
            if (heap.type === Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4)
                heap.destroy()
            }
        }
        destroy()
        sprite.killAndErase()
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
    }

    fun reset(): Boolean {
        return true
    }

    fun description(): String {
        return "This stout guy looks more appropriate for a trade district in some large city " +
                "than for a dungeon. His prices explain why he prefers to do business here."
    }

    override fun interact() {
        sell()
    }

    companion object {
        fun sell(): WndBag {
            return GameScene.selectItem(itemSelector, WndBag.Mode.FOR_SALE, "Select an item to sell")
        }

        private val itemSelector: WndBag.Listener = object : Listener() {
            fun onSelect(item: Item?) {
                if (item != null) {
                    val parentWnd: WndBag = sell()
                    GameScene.show(WndTradeItem(item, parentWnd))
                }
            }
        }
    }

    init {
        name = "shopkeeper"
        spriteClass = ShopkeeperSprite::class.java
    }
}