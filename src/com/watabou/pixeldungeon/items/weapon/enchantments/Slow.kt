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

class Slow : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char?, defender: Char?, damage: Int): Boolean {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        val level = Math.max(0, weapon.effectiveLevel())
        return if (Random.Int(level + 4) >= 3) {
            Buff.prolong(
                defender, com.watabou.pixeldungeon.actors.buffs.Slow::class.java,
                Random.Float(1, 1.5f + level)
            )
            true
        } else {
            false
        }
    }

    fun glowing(): Glowing {
        return BLUE
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_CHILLING, weaponName)
    }

    companion object {
        private const val TXT_CHILLING = "chilling %s"
        private val BLUE: ItemSprite.Glowing = Glowing(0x0044FF)
    }
}