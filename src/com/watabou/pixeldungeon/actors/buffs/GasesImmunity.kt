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

import com.watabou.pixeldungeon.actors.blobs.ToxicGas

class GasesImmunity : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.IMMUNITY
    }

    override fun toString(): String {
        return "Immune to gases"
    }

    companion object {
        const val DURATION = 5f
        val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Paralysis::class.java)
            IMMUNITIES.add(ToxicGas::class.java)
            IMMUNITIES.add(Vertigo::class.java)
        }
    }
}