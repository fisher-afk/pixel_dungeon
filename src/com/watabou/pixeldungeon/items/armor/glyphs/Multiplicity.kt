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

import com.watabou.pixeldungeon.actors.Actor

class Multiplicity : Glyph() {
    fun proc(armor: Armor, attacker: Char?, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(level / 2 + 6) >= 5) {
            val respawnPoints = ArrayList<Int>()
            for (i in 0 until Level.NEIGHBOURS8.length) {
                val p: Int = defender.pos + Level.NEIGHBOURS8.get(i)
                if (Actor.findChar(p) == null && (Level.passable.get(p) || Level.avoid.get(p))) {
                    respawnPoints.add(p)
                }
            }
            if (respawnPoints.size > 0) {
                val mob = MirrorImage()
                mob.duplicate(defender as Hero)
                GameScene.add(mob)
                WandOfBlink.appear(mob, Random.element(respawnPoints))
                defender.damage(Random.IntRange(1, defender.HT / 6), this)
                checkOwner(defender)
            }
        }
        return damage
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_MULTIPLICITY, weaponName)
    }

    fun glowing(): Glowing {
        return PINK
    }

    companion object {
        private const val TXT_MULTIPLICITY = "%s of multiplicity"
        private val PINK: ItemSprite.Glowing = Glowing(0xCCAA88)
    }
}