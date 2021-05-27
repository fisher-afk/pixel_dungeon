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

class Dreamweed : Plant() {
    fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            GameScene.add(Blob.seed(pos, 400, ConfusionGas::class.java))
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
            plantName = "Dreamweed"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_DREAMWEED
            plantClass = Dreamweed::class.java
            alchemyClass = PotionOfInvisibility::class.java
        }
    }

    companion object {
        private const val TXT_DESC = "Upon touching a Dreamweed it secretes a glittering cloud of confusing gas."
    }

    init {
        image = 3
        plantName = "Dreamweed"
    }
}