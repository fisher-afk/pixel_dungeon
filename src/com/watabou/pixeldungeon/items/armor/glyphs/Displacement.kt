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

import com.watabou.pixeldungeon.Dungeon

class Displacement : Glyph() {
    fun proc(armor: Armor, attacker: Char?, defender: Char?, damage: Int): Int {
        if (Dungeon.bossLevel()) {
            return damage
        }
        val level: Int = armor.effectiveLevel()
        val nTries = (if (level < 0) 1 else level + 1) * 5
        for (i in 0 until nTries) {
            val pos: Int = Random.Int(Level.LENGTH)
            if (Dungeon.visible.get(pos) && Level.passable.get(pos) && Actor.findChar(pos) == null) {
                WandOfBlink.appear(defender, pos)
                Dungeon.level.press(pos, defender)
                Dungeon.observe()
                break
            }
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_DISPLACEMENT, weaponName)
    }

    fun glowing(): Glowing {
        return BLUE
    }

    companion object {
        private const val TXT_DISPLACEMENT = "%s of displacement"
        private val BLUE: ItemSprite.Glowing = Glowing(0x66AAFF)
    }
}