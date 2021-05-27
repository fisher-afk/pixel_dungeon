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

class Fadeleaf : Plant() {
    fun activate(ch: Char) {
        super.activate(ch)
        if (ch is Hero) {
            ScrollOfTeleportation.teleportHero(ch as Hero)
            (ch as Hero).curAction = null
        } else if (ch is Mob) {
            var count = 10
            var newPos: Int
            do {
                newPos = Dungeon.level.randomRespawnCell()
                if (count-- <= 0) {
                    break
                }
            } while (newPos == -1)
            if (newPos != -1) {
                ch.pos = newPos
                ch.sprite.place(ch.pos)
                ch.sprite.visible = Dungeon.visible.get(pos)
            }
        }
        if (Dungeon.visible.get(pos)) {
            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
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
            plantName = "Fadeleaf"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_FADELEAF
            plantClass = Fadeleaf::class.java
            alchemyClass = PotionOfMindVision::class.java
        }
    }

    companion object {
        private const val TXT_DESC = "Touching a Fadeleaf will teleport any creature " +
                "to a random place on the current level."
    }

    init {
        image = 6
        plantName = "Fadeleaf"
    }
}