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
package com.watabou.pixeldungeon.plants

import com.watabou.pixeldungeon.Dungeon

class Icecap : Plant() {
    override fun activate(ch: Char?) {
        super.activate(ch)
        PathFinder.buildDistanceMap(pos, BArray.not(Level.losBlocking, null), 1)
        val fire: Fire = Dungeon.level.blobs.get(Fire::class.java) as Fire
        for (i in 0 until Level.LENGTH) {
            if (PathFinder.distance.get(i) < Int.MAX_VALUE) {
                Freezing.affect(i, fire)
            }
        }
    }

    override fun desc(): String {
        return TXT_DESC
    }

    class Seed : Plant.Seed() {
        fun desc(): String {
            return TXT_DESC
        }

        init {
            plantName = "Icecap"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_ICECAP
            plantClass = Icecap::class.java
            alchemyClass = PotionOfFrost::class.java
        }
    }

    companion object {
        private const val TXT_DESC =
            "Upon touching an Icecap excretes a pollen, which freezes everything in its vicinity."
    }

    init {
        image = 1
        plantName = "Icecap"
    }
}