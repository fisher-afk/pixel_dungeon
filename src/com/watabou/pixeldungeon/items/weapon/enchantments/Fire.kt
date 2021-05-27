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

class Fire : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char?, defender: Char, damage: Int): Boolean {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        val level = Math.max(0, weapon.effectiveLevel())
        return if (Random.Int(level + 3) >= 2) {
            if (Random.Int(2) === 0) {
                Buff.affect(defender, Burning::class.java).reignite(defender)
            }
            defender.damage(Random.Int(1, level + 2), this)
            defender.sprite.emitter().burst(FlameParticle.FACTORY, level + 1)
            true
        } else {
            false
        }
    }

    fun glowing(): Glowing {
        return ORANGE
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_BLAZING, weaponName)
    }

    companion object {
        private const val TXT_BLAZING = "blazing %s"
        private val ORANGE: ItemSprite.Glowing = Glowing(0xFF4400)
    }
}