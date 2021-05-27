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

class Leech : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char, defender: Char?, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())

        // lvl 0 - 33%
        // lvl 1 - 43%
        // lvl 2 - 50%
        val maxValue = damage * (level + 2) / (level + 6)
        val effValue: Int = Math.min(Random.IntRange(0, maxValue), attacker.HT - attacker.HP)
        return if (effValue > 0) {
            attacker.HP += effValue
            attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1)
            attacker.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(effValue))
            true
        } else {
            false
        }
    }

    fun glowing(): Glowing {
        return RED
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_VAMPIRIC, weaponName)
    }

    companion object {
        private const val TXT_VAMPIRIC = "vampiric %s"
        private val RED: ItemSprite.Glowing = Glowing(0x660022)
    }
}