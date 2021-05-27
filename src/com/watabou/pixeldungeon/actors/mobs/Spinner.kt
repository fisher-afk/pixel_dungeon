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

class Spinner : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(12, 16)
    }

    fun attackSkill(target: Char?): Int {
        return 20
    }

    fun dr(): Int {
        return 6
    }

    protected override fun act(): Boolean {
        val result: Boolean = super.act()
        if (state === FLEEING && buff(Terror::class.java) == null) {
            if (enemy != null && enemySeen && enemy.buff(Poison::class.java) == null) {
                state = HUNTING
            }
        }
        return result
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        if (Random.Int(2) === 0) {
            Buff.affect(enemy, Poison::class.java).set(Random.Int(7, 9) * Poison.durationFactor(enemy))
            state = FLEEING
        }
        return damage
    }

    override fun move(step: Int) {
        if (state === FLEEING) {
            GameScene.add(Blob.seed(pos, Random.Int(5, 7), Web::class.java))
        }
        super.move(step)
    }

    override fun description(): String {
        return "These greenish furry cave spiders try to avoid direct combat, preferring to wait in the distance " +
                "while their victim, entangled in the spinner's excreted cobweb, slowly dies from their poisonous bite."
    }

    companion object {
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(Poison::class.java)
        }

        init {
            IMMUNITIES.add(Roots::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    private inner class Fleeing : Mob.Fleeing() {
        protected override fun nowhereToRun() {
            if (buff(Terror::class.java) == null) {
                state = HUNTING
            } else {
                super.nowhereToRun()
            }
        }
    }

    init {
        name = "cave spinner"
        spriteClass = SpinnerSprite::class.java
        HT = 50
        HP = HT
        defenseSkill = 14
        EXP = 9
        maxLvl = 16
        loot = MysteryMeat()
        lootChance = 0.125f
        FLEEING = Fleeing()
    }
}