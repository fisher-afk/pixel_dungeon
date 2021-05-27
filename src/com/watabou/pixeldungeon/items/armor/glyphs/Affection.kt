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

class Affection : Glyph() {
    fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = GameMath.gate(0, armor.effectiveLevel(), 6) as Int
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level / 2 + 5) >= 4) {
            var duration: Int = Random.IntRange(3, 7)
            Buff.affect(attacker, Charm::class.java, Charm.durationFactor(attacker) * duration).`object` = defender.id()
            attacker.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5)
            duration *= Random.Float(0.5f, 1)
            Buff.affect(defender, Charm::class.java, Charm.durationFactor(defender) * duration).`object` = attacker.id()
            defender.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5)
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_AFFECTION, weaponName)
    }

    fun glowing(): Glowing {
        return PINK
    }

    companion object {
        private const val TXT_AFFECTION = "%s of affection"
        private val PINK: ItemSprite.Glowing = Glowing(0xFF4488)
    }
}