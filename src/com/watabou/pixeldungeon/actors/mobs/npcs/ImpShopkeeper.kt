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

class ImpShopkeeper : Shopkeeper() {
    private var seenBefore = false
    protected override fun act(): Boolean {
        if (!seenBefore && Dungeon.visible.get(pos)) {
            yell(Utils.format(TXT_GREETINGS))
            seenBefore = true
        }
        return super.act()
    }

    protected override fun flee() {
        for (heap in Dungeon.level.heaps.values()) {
            if (heap.type === Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4)
                heap.destroy()
            }
        }
        destroy()
        sprite.emitter().burst(Speck.factory(Speck.WOOL), 15)
        sprite.killAndErase()
    }

    override fun description(): String {
        return "Imps are lesser demons. They are notable for neither their strength nor their magic talent. " +
                "But they are quite smart and sociable, and many of imps prefer to live and do business among non-demons."
    }

    companion object {
        private const val TXT_GREETINGS = "Hello, friend!"
    }

    init {
        name = "ambitious imp"
        spriteClass = ImpSprite::class.java
    }
}