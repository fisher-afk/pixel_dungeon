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

class FetidRat : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(2, 6)
    }

    fun attackSkill(target: Char?): Int {
        return 12
    }

    fun dr(): Int {
        return 2
    }

    fun defenseVerb(): String {
        return "evaded"
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {
        GameScene.add(Blob.seed(pos, 20, ParalyticGas::class.java))
        return super.defenseProc(enemy, damage)
    }

    override fun die(cause: Any?) {
        super.die(cause)
        Dungeon.level.drop(RatSkull(), pos).sprite.drop()
    }

    override fun description(): String {
        return "This marsupial rat is much larger than a regular one. It is surrounded by a foul cloud."
    }

    companion object {
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Paralysis::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "fetid rat"
        spriteClass = FetidRatSprite::class.java
        HT = 15
        HP = HT
        defenseSkill = 5
        EXP = 3
        maxLvl = 5
        state = WANDERING
    }
}