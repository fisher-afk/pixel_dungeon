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

import com.watabou.pixeldungeon.Dungeon

class Ooze : Buff() {
    var damage = 1
    override fun icon(): Int {
        return BuffIndicator.OOZE
    }

    override fun toString(): String {
        return "Caustic ooze"
    }

    override fun act(): Boolean {
        if (target.isAlive()) {
            target.damage(damage, this)
            if (!target.isAlive() && target === Dungeon.hero) {
                Dungeon.fail(Utils.format(ResultDescriptions.OOZE, Dungeon.depth))
                GLog.n(TXT_HERO_KILLED, toString())
            }
            spend(TICK)
        }
        if (Level.water.get(target.pos)) {
            detach()
        }
        return true
    }

    companion object {
        private const val TXT_HERO_KILLED = "%s killed you..."
    }
}