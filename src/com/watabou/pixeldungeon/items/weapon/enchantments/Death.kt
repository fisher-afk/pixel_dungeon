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

import com.watabou.pixeldungeon.Badges

class Death : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char?, defender: Char, damage: Int): Boolean {
        // lvl 0 - 8%
        // lvl 1 ~ 9%
        // lvl 2 ~ 10%
        val level = Math.max(0, weapon.effectiveLevel())
        return if (Random.Int(level + 100) >= 92) {
            defender.damage(defender.HP, this)
            defender.sprite.emitter().burst(ShadowParticle.UP, 5)
            if (!defender.isAlive() && attacker is Hero) {
                Badges.validateGrimWeapon()
            }
            true
        } else {
            false
        }
    }

    fun glowing(): Glowing {
        return BLACK
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_GRIM, weaponName)
    }

    companion object {
        private const val TXT_GRIM = "grim %s"
        private val BLACK: ItemSprite.Glowing = Glowing(0x000000)
    }
}