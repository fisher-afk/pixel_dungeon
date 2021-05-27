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

class Sorrowmoss : Plant() {
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buff.affect(ch, Poison::class.java).set(Poison.durationFactor(ch) * (4 + Dungeon.depth / 2))
        }
        if (Dungeon.visible.get(pos)) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3)
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
            plantName = "Sorrowmoss"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_SORROWMOSS
            plantClass = Sorrowmoss::class.java
            alchemyClass = PotionOfToxicGas::class.java
        }
    }

    companion object {
        private const val TXT_DESC =
            "A Sorrowmoss is a flower (not a moss) with razor-sharp petals, coated with a deadly venom."
    }

    init {
        image = 2
        plantName = "Sorrowmoss"
    }
}