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
package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.Dungeon

class Horror : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 20%
        // lvl 1 - 33%
        // lvl 2 - 43%
        val level = Math.max(0, weapon.effectiveLevel())
        return if (Random.Int(level + 5) >= 4) {
            if (defender === Dungeon.hero) {
                Buff.affect(defender, Vertigo::class.java, Vertigo.duration(defender))
            } else {
                Buff.affect(defender, Terror::class.java, Terror.DURATION).`object` = attacker.id()
            }
            true
        } else {
            false
        }
    }

    fun glowing(): Glowing {
        return GREY
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_ELDRITCH, weaponName)
    }

    companion object {
        private const val TXT_ELDRITCH = "eldritch %s"
        private val GREY: ItemSprite.Glowing = Glowing(0x222222)
    }
}