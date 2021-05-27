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

import com.watabou.pixeldungeon.actors.Actor

class Web : Blob() {
    protected override fun evolve() {
        for (i in 0 until LENGTH) {
            val offv = if (cur.get(i) > 0) cur.get(i) - 1 else 0
            off.get(i) = offv
            if (offv > 0) {
                volume += offv
                val ch: Char = Actor.findChar(i)
                if (ch != null) {
                    Buff.prolong(ch, Roots::class.java, TICK)
                }
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(WebParticle.FACTORY, 0.4f)
    }

    override fun seed(cell: Int, amount: Int) {
        val diff: Int = amount - cur.get(cell)
        if (diff > 0) {
            cur.get(cell) = amount
            volume += diff
        }
    }

    override fun tileDesc(): String {
        return "Everything is covered with a thick web here."
    }
}