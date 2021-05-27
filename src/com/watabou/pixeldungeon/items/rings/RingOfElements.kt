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
package com.watabou.pixeldungeon.items.rings

import com.watabou.pixeldungeon.actors.blobs.ToxicGas

class RingOfElements : Ring() {
    protected override fun buff(): RingBuff {
        return Resistance()
    }

    override fun desc(): String {
        return if (isKnown()) "This ring provides resistance to different elements, such as fire, " +
                "electricity, gases etc. Also it decreases duration of negative effects." else super.desc()
    }

    companion object {
        private val EMPTY = HashSet<Class<*>>()
        private val FULL: HashSet<Class<*>>? = null

        init {
            FULL = HashSet()
            FULL.add(Burning::class.java)
            FULL.add(ToxicGas::class.java)
            FULL.add(Poison::class.java)
            FULL.add(LightningTrap.Electricity::class.java)
            FULL.add(Warlock::class.java)
            FULL.add(Eye::class.java)
            FULL.add(Yog.BurningFist::class.java)
        }
    }

    inner class Resistance : RingBuff() {
        fun resistances(): HashSet<Class<*>>? {
            return if (Random.Int(level + 3) >= 3) {
                FULL
            } else {
                EMPTY
            }
        }

        fun durationFactor(): Float {
            return if (level < 0) 1 else (2 + 0.5f * level) / (2 + level)
        }
    }

    init {
        name = "Ring of Elements"
    }
}