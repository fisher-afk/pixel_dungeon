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

class CursePersonification : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(3, 5)
    }

    fun attackSkill(target: Char?): Int {
        return 10 + Dungeon.depth
    }

    fun dr(): Int {
        return 1
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val ofs: Int = Level.NEIGHBOURS8.get(i)
            if (enemy.pos - pos === ofs) {
                val newPos: Int = enemy.pos + ofs
                if ((Level.passable.get(newPos) || Level.avoid.get(newPos)) && Actor.findChar(newPos) == null) {
                    Actor.addDelayed(Pushing(enemy, enemy.pos, newPos), -1)
                    enemy.pos = newPos
                    // FIXME
                    if (enemy is Mob) {
                        Dungeon.level.mobPress(enemy as Mob)
                    } else {
                        Dungeon.level.press(newPos, enemy)
                    }
                }
                break
            }
        }
        return super.attackProc(enemy, damage)
    }

    protected override fun act(): Boolean {
        if (HP > 0 && HP < HT) {
            HP++
        }
        return super.act()
    }

    override fun die(cause: Any?) {
        val ghost = Ghost()
        ghost.state = ghost.PASSIVE
        Ghost.replace(this, ghost)
    }

    override fun description(): String {
        return "This creature resembles the sad ghost, but it swirls with darkness. " +
                "Its face bears an expression of despair."
    }

    companion object {
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Death::class.java)
            IMMUNITIES.add(Terror::class.java)
            IMMUNITIES.add(Paralysis::class.java)
            IMMUNITIES.add(Roots::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "curse personification"
        spriteClass = CursePersonificationSprite::class.java
        HT = 10 + Dungeon.depth * 3
        HP = HT
        defenseSkill = 10 + Dungeon.depth
        EXP = 3
        maxLvl = 5
        state = HUNTING
        baseSpeed = 0.5f
        flying = true
    }
}