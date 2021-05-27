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

class ConfusionGas : Blob() {
    protected override fun evolve() {
        super.evolve()
        var ch: Char?
        for (i in 0 until LENGTH) {
            if (cur.get(i) > 0 && Actor.findChar(i).also { ch = it } != null) {
                Buff.prolong(ch, Vertigo::class.java, Vertigo.duration(ch))
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.CONFUSION, true), 0.6f)
    }

    override fun tileDesc(): String {
        return "A cloud of confusion gas is swirling here."
    }
}