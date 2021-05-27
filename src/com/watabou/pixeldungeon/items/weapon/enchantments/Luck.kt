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

import com.watabou.pixeldungeon.actors.Char

class Luck : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())
        var dmg = damage
        for (i in 1..level + 1) {
            dmg = Math.max(dmg, attacker.damageRoll() - i)
        }
        return if (dmg > damage) {
            defender.damage(dmg - damage, this)
            true
        } else {
            false
        }
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_LUCKY, weaponName)
    }

    fun glowing(): Glowing {
        return GREEN
    }

    companion object {
        private const val TXT_LUCKY = "lucky %s"
        private val GREEN: ItemSprite.Glowing = Glowing(0x00FF00)
    }
}