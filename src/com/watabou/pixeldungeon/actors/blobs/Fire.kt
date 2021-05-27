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

class Fire : Blob() {
    protected override fun evolve() {
        val flamable: BooleanArray = Level.flamable
        val from: Int = WIDTH + 1
        val to: Int = Level.LENGTH - WIDTH - 1
        var observe = false
        for (pos in from until to) {
            var fire: Int
            if (cur.get(pos) > 0) {
                burn(pos)
                fire = cur.get(pos) - 1
                if (fire <= 0 && flamable[pos]) {
                    val oldTile: Int = Dungeon.level.map.get(pos)
                    Dungeon.level.destroy(pos)
                    observe = true
                    GameScene.updateMap(pos)
                    if (Dungeon.visible.get(pos)) {
                        GameScene.discoverTile(pos, oldTile)
                    }
                }
            } else {
                if (flamable[pos] && (cur.get(pos - 1) > 0 || cur.get(pos + 1) > 0 || cur.get(pos - WIDTH) > 0 || cur.get(
                        pos + WIDTH
                    ) > 0)
                ) {
                    fire = 4
                    burn(pos)
                } else {
                    fire = 0
                }
            }
            volume += fire.also { off.get(pos) = it }
        }
        if (observe) {
            Dungeon.observe()
        }
    }

    private fun burn(pos: Int) {
        val ch: Char = Actor.findChar(pos)
        if (ch != null) {
            Buff.affect(ch, Burning::class.java).reignite(ch)
        }
        val heap: Heap = Dungeon.level.heaps.get(pos)
        if (heap != null) {
            heap.burn()
        }
    }

    override fun seed(cell: Int, amount: Int) {
        if (cur.get(cell) === 0) {
            volume += amount
            cur.get(cell) = amount
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(FlameParticle.FACTORY, 0.03f, 0)
    }

    override fun tileDesc(): String {
        return "A fire is raging here."
    }
}