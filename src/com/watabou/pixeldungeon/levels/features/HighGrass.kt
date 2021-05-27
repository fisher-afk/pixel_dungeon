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
package com.watabou.pixeldungeon.levels.features

import com.watabou.pixeldungeon.Challenges

object HighGrass {
    fun trample(level: Level, pos: Int, ch: Char?) {
        Level.set(pos, Terrain.GRASS)
        GameScene.updateMap(pos)
        if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
            var herbalismLevel = 0
            if (ch != null) {
                val herbalism: Herbalism = ch.buff(Herbalism::class.java)
                if (herbalism != null) {
                    herbalismLevel = herbalism.level
                }
            }
            // Seed
            if (herbalismLevel >= 0 && Random.Int(18) <= Random.Int(herbalismLevel + 1)) {
                level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop()
            }

            // Dew
            if (herbalismLevel >= 0 && Random.Int(6) <= Random.Int(herbalismLevel + 1)) {
                level.drop(Dewdrop(), pos).sprite.drop()
            }
        }
        var leaves = 4

        // Warlock's barkskin
        if (ch is Hero && (ch as Hero?).subClass === HeroSubClass.WARDEN) {
            Buff.affect(ch, Barkskin::class.java).level(ch.HT / 3)
            leaves = 8
        }
        CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, leaves)
        Dungeon.observe()
    }
}