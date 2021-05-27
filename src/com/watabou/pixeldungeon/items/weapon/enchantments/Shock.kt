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

import com.watabou.pixeldungeon.actors.Actor

class Shock : Weapon.Enchantment() {
    fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        val level = Math.max(0, weapon.effectiveLevel())
        return if (Random.Int(level + 4) >= 3) {
            points[0] = attacker.pos
            nPoints = 1
            affected.clear()
            affected.add(attacker)
            hit(defender, Random.Int(1, damage / 2))
            attacker.sprite.parent.add(Lightning(points, nPoints, null))
            true
        } else {
            false
        }
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_SHOCKING, weaponName)
    }

    private val affected = ArrayList<Char>()
    private val points = IntArray(20)
    private var nPoints = 0
    private fun hit(ch: Char, damage: Int) {
        if (damage < 1) {
            return
        }
        affected.add(ch)
        ch.damage(if (Level.water.get(ch.pos) && !ch.flying) (damage * 2) else damage, LightningTrap.LIGHTNING)
        ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3)
        ch.sprite.flash()
        points[nPoints++] = ch.pos
        val ns = HashSet<Char>()
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val n: Char = Actor.findChar(ch.pos + Level.NEIGHBOURS8.get(i))
            if (n != null && !affected.contains(n)) {
                ns.add(n)
            }
        }
        if (ns.size > 0) {
            hit(Random.element(ns), Random.Int(damage / 2, damage))
        }
    }

    companion object {
        private const val TXT_SHOCKING = "shocking %s"
    }
}