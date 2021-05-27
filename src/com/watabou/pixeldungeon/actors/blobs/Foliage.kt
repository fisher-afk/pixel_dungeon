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

class Foliage : Blob() {
    protected override fun evolve() {
        val from: Int = WIDTH + 1
        val to: Int = Level.LENGTH - WIDTH - 1
        val map: IntArray = Dungeon.level.map
        var regrowth = false
        var visible = false
        for (pos in from until to) {
            if (cur.get(pos) > 0) {
                off.get(pos) = cur.get(pos)
                volume += off.get(pos)
                if (map[pos] == Terrain.EMBERS) {
                    map[pos] = Terrain.GRASS
                    regrowth = true
                }
                visible = visible || Dungeon.visible.get(pos)
            } else {
                off.get(pos) = 0
            }
        }
        val hero: Hero = Dungeon.hero
        if (hero.isAlive() && hero.visibleEnemies() === 0 && cur.get(hero.pos) > 0) {
            Buff.affect(hero, Shadows::class.java).prolong()
        }
        if (regrowth) {
            GameScene.updateMap()
        }
        if (visible) {
            Journal.add(Journal.Feature.GARDEN)
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(ShaftParticle.FACTORY, 0.9f, 0)
    }

    override fun tileDesc(): String {
        return "Shafts of light pierce the gloom of the underground garden."
    }
}