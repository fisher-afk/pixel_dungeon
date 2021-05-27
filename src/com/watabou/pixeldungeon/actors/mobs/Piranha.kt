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

import com.watabou.pixeldungeon.Badges

class Piranha : Mob() {
    protected override fun act(): Boolean {
        return if (!Level.water.get(pos)) {
            die(null)
            true
        } else {
            super.act()
        }
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2)
    }

    fun attackSkill(target: Char?): Int {
        return 20 + Dungeon.depth * 2
    }

    fun dr(): Int {
        return Dungeon.depth
    }

    override fun die(cause: Any?) {
        Dungeon.level.drop(MysteryMeat(), pos).sprite.drop()
        super.die(cause)
        Statistics.piranhasKilled++
        Badges.validatePiranhasKilled()
    }

    override fun reset(): Boolean {
        return true
    }

    protected override fun getCloser(target: Int): Boolean {
        if (rooted) {
            return false
        }
        val step: Int = Dungeon.findPath(
            this, pos, target,
            Level.water,
            Level.fieldOfView
        )
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }

    protected override fun getFurther(target: Int): Boolean {
        val step: Int = Dungeon.flee(
            this, pos, target,
            Level.water,
            Level.fieldOfView
        )
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }

    override fun description(): String {
        return "These carnivorous fish are not natural inhabitants of underground pools. " +
                "They were bred specifically to protect flooded treasure vaults."
    }

    companion object {
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Burning::class.java)
            IMMUNITIES.add(Paralysis::class.java)
            IMMUNITIES.add(ToxicGas::class.java)
            IMMUNITIES.add(Roots::class.java)
            IMMUNITIES.add(Frost::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "giant piranha"
        spriteClass = PiranhaSprite::class.java
        baseSpeed = 2f
        EXP = 0
    }

    init {
        HT = 10 + Dungeon.depth * 5
        HP = HT
        defenseSkill = 10 + Dungeon.depth * 2
    }
}