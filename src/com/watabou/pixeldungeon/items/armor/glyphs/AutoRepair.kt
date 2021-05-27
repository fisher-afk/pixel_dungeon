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
package com.watabou.pixeldungeon.items.armor.glyphs

import com.watabou.pixeldungeon.Dungeon

class AutoRepair : Glyph() {
    fun proc(armor: Armor, attacker: Char?, defender: Char?, damage: Int): Int {
        if (defender is Hero && Dungeon.gold >= armor.tier) {
            Dungeon.gold -= armor.tier
            armor.polish()
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_AUTO_REPAIR, weaponName)
    }

    fun glowing(): Glowing {
        return GRAY
    }

    companion object {
        private const val TXT_AUTO_REPAIR = "%s of auto-repair"
        private val GRAY: Glowing = Glowing(0xCC8888)
    }
}