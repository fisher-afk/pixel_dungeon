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

abstract class NPC : Mob() {
    protected fun throwItem() {
        val heap: Heap = Dungeon.level.heaps.get(pos)
        if (heap != null) {
            var n: Int
            do {
                n = pos + Level.NEIGHBOURS8.get(Random.Int(8))
            } while (!Level.passable.get(n) && !Level.avoid.get(n))
            Dungeon.level.drop(heap.pickUp(), n).sprite.drop(pos)
        }
    }

    fun beckon(cell: Int) {}
    abstract fun interact()

    init {
        HT = 1
        HP = HT
        EXP = 0
        hostile = false
        state = PASSIVE
    }
}