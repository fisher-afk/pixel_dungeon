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

class Bounce : Glyph() {
    fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level + 5) >= 4) {
            for (i in 0 until Level.NEIGHBOURS8.length) {
                val ofs: Int = Level.NEIGHBOURS8.get(i)
                if (attacker.pos - defender.pos === ofs) {
                    val newPos: Int = attacker.pos + ofs
                    if ((Level.passable.get(newPos) || Level.avoid.get(newPos)) && Actor.findChar(newPos) == null) {
                        Actor.addDelayed(Pushing(attacker, attacker.pos, newPos), -1)
                        attacker.pos = newPos
                        // FIXME
                        if (attacker is Mob) {
                            Dungeon.level.mobPress(attacker as Mob)
                        } else {
                            Dungeon.level.press(newPos, attacker)
                        }
                    }
                    break
                }
            }
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_BOUNCE, weaponName)
    }

    companion object {
        private const val TXT_BOUNCE = "%s of bounce"
    }
}