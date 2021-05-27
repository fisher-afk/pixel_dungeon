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

import com.watabou.pixeldungeon.actors.Char

class Metabolism : Glyph() {
    fun proc(armor: Armor, attacker: Char?, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(level / 2 + 5) >= 4) {
            val healing: Int = Math.min(defender.HT - defender.HP, Random.Int(1, defender.HT / 5))
            if (healing > 0) {
                val hunger: Hunger = defender.buff(Hunger::class.java)
                if (hunger != null && !hunger.isStarving()) {
                    hunger.satisfy(-Hunger.STARVING / 10)
                    BuffIndicator.refreshHero()
                    defender.HP += healing
                    defender.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
                    defender.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healing))
                }
            }
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_METABOLISM, weaponName)
    }

    fun glowing(): Glowing {
        return RED
    }

    companion object {
        private const val TXT_METABOLISM = "%s of metabolism"
        private val RED: ItemSprite.Glowing = Glowing(0xCC0000)
    }
}