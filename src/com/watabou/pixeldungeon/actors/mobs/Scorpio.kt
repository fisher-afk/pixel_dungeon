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

import com.watabou.pixeldungeon.Dungeon

class Scorpio : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(20, 32)
    }

    fun attackSkill(target: Char?): Int {
        return 36
    }

    fun dr(): Int {
        return 16
    }

    protected fun canAttack(enemy: Char): Boolean {
        return !Level.adjacent(pos, enemy.pos) && Ballistica.cast(pos, enemy.pos, false, true) === enemy.pos
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        if (Random.Int(2) === 0) {
            Buff.prolong(enemy, Cripple::class.java, Cripple.DURATION)
        }
        return damage
    }

    protected override fun getCloser(target: Int): Boolean {
        return if (state === HUNTING) {
            enemySeen && getFurther(target)
        } else {
            super.getCloser(target)
        }
    }

    protected override fun dropLoot() {
        if (Random.Int(8) === 0) {
            Dungeon.level.drop(PotionOfHealing(), pos).sprite.drop()
        } else if (Random.Int(6) === 0) {
            Dungeon.level.drop(MysteryMeat(), pos).sprite.drop()
        }
    }

    override fun description(): String {
        return "These huge arachnid-like demonic creatures avoid close combat by all means, " +
                "firing crippling serrated spikes from long distances."
    }

    companion object {
        private val RESISTANCES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(Leech::class.java)
            RESISTANCES.add(Poison::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    init {
        name = "scorpio"
        spriteClass = ScorpioSprite::class.java
        HT = 95
        HP = HT
        defenseSkill = 24
        viewDistance = Light.DISTANCE
        EXP = 14
        maxLvl = 25
        loot = PotionOfHealing()
        lootChance = 0.125f
    }
}