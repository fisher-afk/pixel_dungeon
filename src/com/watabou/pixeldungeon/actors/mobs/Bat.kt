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
package com.watabou.pixeldungeon.actors.mobs

import com.watabou.pixeldungeon.actors.Char

class Bat : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(6, 12)
    }

    fun attackSkill(target: Char?): Int {
        return 16
    }

    fun dr(): Int {
        return 4
    }

    fun defenseVerb(): String {
        return "evaded"
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        val reg = Math.min(damage, HT - HP)
        if (reg > 0) {
            HP += reg
            sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
        }
        return damage
    }

    override fun description(): String {
        return "These brisk and tenacious inhabitants of cave domes may defeat much larger opponents by " +
                "replenishing their health with each successful attack."
    }

    companion object {
        private val RESISTANCES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(Leech::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    init {
        name = "vampire bat"
        spriteClass = BatSprite::class.java
        HT = 30
        HP = HT
        defenseSkill = 15
        baseSpeed = 2f
        EXP = 7
        maxLvl = 15
        flying = true
        loot = PotionOfHealing()
        lootChance = 0.125f
    }
}