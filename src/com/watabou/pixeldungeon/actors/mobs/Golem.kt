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

class Golem : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(20, 40)
    }

    fun attackSkill(target: Char?): Int {
        return 28
    }

    protected override fun attackDelay(): Float {
        return 1.5f
    }

    fun dr(): Int {
        return 12
    }

    fun defenseVerb(): String {
        return "blocked"
    }

    override fun die(cause: Any?) {
        Imp.Quest.process(this)
        super.die(cause)
    }

    override fun description(): String {
        return "The Dwarves tried to combine their knowledge of mechanisms with their newfound power of elemental binding. " +
                "They used spirits of earth as the \"soul\" for the mechanical bodies of golems, which were believed to be " +
                "most controllable of all. Despite this, the tiniest mistake in the ritual could cause an outbreak."
    }

    companion object {
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(ScrollOfPsionicBlast::class.java)
        }

        init {
            IMMUNITIES.add(Amok::class.java)
            IMMUNITIES.add(Terror::class.java)
            IMMUNITIES.add(Sleep::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "golem"
        spriteClass = GolemSprite::class.java
        HT = 85
        HP = HT
        defenseSkill = 18
        EXP = 12
        maxLvl = 22
    }
}