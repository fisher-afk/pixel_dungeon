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

class Regrowth : Blob() {
    protected override fun evolve() {
        super.evolve()
        if (volume > 0) {
            var mapUpdated = false
            for (i in 0 until LENGTH) {
                if (off.get(i) > 0) {
                    val c: Int = Dungeon.level.map.get(i)
                    var c1 = c
                    if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
                        c1 = if (cur.get(i) > 9) Terrain.HIGH_GRASS else Terrain.GRASS
                    } else if (c == Terrain.GRASS && cur.get(i) > 9) {
                        c1 = Terrain.HIGH_GRASS
                    }
                    if (c1 != c) {
                        Level.set(i, Terrain.HIGH_GRASS)
                        mapUpdated = true
                        GameScene.updateMap(i)
                        if (Dungeon.visible.get(i)) {
                            GameScene.discoverTile(i, c)
                        }
                    }
                    val ch: Char = Actor.findChar(i)
                    if (ch != null) {
                        Buff.prolong(ch, Roots::class.java, TICK)
                    }
                }
            }
            if (mapUpdated) {
                Dungeon.observe()
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(LeafParticle.LEVEL_SPECIFIC, 0.2f, 0)
    }
}