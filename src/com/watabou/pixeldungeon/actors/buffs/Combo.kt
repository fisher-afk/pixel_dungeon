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

class Combo : Buff() {
    var count = 0
    override fun icon(): Int {
        return BuffIndicator.COMBO
    }

    override fun toString(): String {
        return "Combo"
    }

    fun hit(enemy: Char?, damage: Int): Int {
        count++
        return if (count >= 3) {
            Badges.validateMasteryCombo(count)
            GLog.p(TXT_COMBO, count)
            postpone(1.41f - count / 10f)
            (damage * (count - 2) / 5f).toInt()
        } else {
            postpone(1.1f)
            0
        }
    }

    override fun act(): Boolean {
        detach()
        return true
    }

    companion object {
        private const val TXT_COMBO = "%d hit combo!"
    }
}