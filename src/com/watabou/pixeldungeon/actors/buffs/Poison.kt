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

import com.watabou.pixeldungeon.Badges

class Poison : Buff(), Hero.Doom {
    protected var left = 0f
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }

    fun set(duration: Float) {
        left = duration
    }

    override fun icon(): Int {
        return BuffIndicator.POISON
    }

    override fun toString(): String {
        return "Poisoned"
    }

    override fun act(): Boolean {
        if (target.isAlive()) {
            target.damage((left / 3).toInt() + 1, this)
            spend(TICK)
            if (TICK.let { left -= it; left } <= 0) {
                detach()
            }
        } else {
            detach()
        }
        return true
    }

    fun onDeath() {
        Badges.validateDeathFromPoison()
        Dungeon.fail(Utils.format(ResultDescriptions.POISON, Dungeon.depth))
        GLog.n("You died from poison...")
    }

    companion object {
        private const val LEFT = "left"
        fun durationFactor(ch: Char): Float {
            val r: Resistance = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() else 1
        }
    }
}