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

import com.watabou.noosa.Camera

class Entanglement : Glyph() {
    fun proc(armor: Armor, attacker: Char?, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(4) === 0) {
            Buff.prolong(defender, Roots::class.java, 5 - level / 5)
            Buff.affect(defender, Earthroot.Armor::class.java).level(5 * (level + 1))
            CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8)
            Camera.main.shake(1, 0.4f)
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_ENTANGLEMENT, weaponName)
    }

    fun glowing(): Glowing {
        return GREEN
    }

    companion object {
        private const val TXT_ENTANGLEMENT = "%s of entanglement"
        private val GREEN: ItemSprite.Glowing = Glowing(0x448822)
    }
}