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

class AntiEntropy : Glyph() {
    fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level + 6) >= 5) {
            Buff.prolong(attacker, Frost::class.java, Frost.duration(attacker) * Random.Float(1f, 1.5f))
            CellEmitter.get(attacker.pos).start(SnowParticle.FACTORY, 0.2f, 6)
            Buff.affect(defender, Burning::class.java).reignite(defender)
            defender.sprite.emitter().burst(FlameParticle.FACTORY, 5)
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_ANTI_ENTROPY, weaponName)
    }

    fun glowing(): Glowing {
        return BLUE
    }

    companion object {
        private const val TXT_ANTI_ENTROPY = "%s of anti-entropy"
        private val BLUE: ItemSprite.Glowing = Glowing(0x0000FF)
    }
}