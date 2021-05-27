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
package com.watabou.pixeldungeon.actors.blobs

import com.watabou.pixeldungeon.Dungeon

class Alchemy : Blob() {
    protected var pos = 0
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until LENGTH) {
            if (cur.get(i) > 0) {
                pos = i
                break
            }
        }
    }

    protected override fun evolve() {
        off.get(pos) = cur.get(pos)
        volume = off.get(pos)
        if (Dungeon.visible.get(pos)) {
            Journal.add(Journal.Feature.ALCHEMY)
        }
    }

    override fun seed(cell: Int, amount: Int) {
        cur.get(pos) = 0
        pos = cell
        cur.get(pos) = amount
        volume = cur.get(pos)
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.BUBBLE), 0.4f, 0)
    }

    companion object {
        fun transmute(cell: Int) {
            val heap: Heap = Dungeon.level.heaps.get(cell)
            if (heap != null) {
                val result: Item = heap.transmute()
                if (result != null) {
                    Dungeon.level.drop(result, cell).sprite.drop(cell)
                }
            }
        }
    }
}