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
package com.watabou.pixeldungeon.actors.buffs

class Weakness : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.WEAKNESS
    }

    override fun toString(): String {
        return "Weakened"
    }

    fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            val hero: Hero = target as Hero
            hero.weakened = true
            hero.belongings.discharge()
            true
        } else {
            false
        }
    }

    override fun detach() {
        super.detach()
        (target as Hero).weakened = false
    }

    companion object {
        private const val DURATION = 40f
        fun duration(ch: Char): Float {
            val r: Resistance = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
    }
}