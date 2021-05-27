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

class Firebloom : Plant() {
    override fun activate(ch: Char?) {
        super.activate(ch)
        GameScene.add(Blob.seed(pos, 2, Fire::class.java))
        if (Dungeon.visible.get(pos)) {
            CellEmitter.get(pos).burst(FlameParticle.FACTORY, 5)
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
            plantName = "Firebloom"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_FIREBLOOM
            plantClass = Firebloom::class.java
            alchemyClass = PotionOfLiquidFlame::class.java
        }
    }

    companion object {
        private const val TXT_DESC = "When something touches a Firebloom, it bursts into flames."
    }

    init {
        image = 0
        plantName = "Firebloom"
    }
}