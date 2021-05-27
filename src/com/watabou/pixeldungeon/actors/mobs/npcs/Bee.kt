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
package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon

class Bee : NPC() {
    private var level = 0
    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        spawn(bundle.getInt(LEVEL))
    }

    fun spawn(level: Int) {
        this.level = level
        HT = (3 + level) * 5
        defenseSkill = 9 + level
    }

    fun attackSkill(target: Char?): Int {
        return defenseSkill
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(HT / 10, HT / 4)
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        if (enemy is Mob) {
            (enemy as Mob).aggro(this)
        }
        return damage
    }

    protected fun act(): Boolean {
        HP--
        return if (HP <= 0) {
            die(null)
            true
        } else {
            super.act()
        }
    }

    protected fun chooseEnemy(): Char? {
        return if (enemy == null || !enemy.isAlive()) {
            val enemies: HashSet<Mob> = HashSet<Mob>()
            for (mob in Dungeon.level.mobs) {
                if (mob.hostile && Level.fieldOfView.get(mob.pos)) {
                    enemies.add(mob)
                }
            }
            if (enemies.size > 0) Random.element(enemies) else null
        } else {
            enemy
        }
    }

    fun description(): String {
        return "Despite their small size, golden bees tend " +
                "to protect their master fiercely. They don't live long though."
    }

    override fun interact() {
        val curPos: Int = pos
        moveSprite(pos, Dungeon.hero.pos)
        move(Dungeon.hero.pos)
        Dungeon.hero.sprite.move(Dungeon.hero.pos, curPos)
        Dungeon.hero.move(curPos)
        Dungeon.hero.spend(1 / Dungeon.hero.speed())
        Dungeon.hero.busy()
    }

    companion object {
        private const val LEVEL = "level"
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Poison::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    private inner class Wandering : AiState {
        fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            if (enemyInFOV) {
                enemySeen = true
                notice()
                state = HUNTING
                target = enemy.pos
            } else {
                enemySeen = false
                val oldPos: Int = pos
                if (getCloser(Dungeon.hero.pos)) {
                    spend(1 / speed())
                    return moveSprite(oldPos, pos)
                } else {
                    spend(TICK)
                }
            }
            return true
        }

        fun status(): String {
            return Utils.format("This %s is wandering", name)
        }
    }

    init {
        name = "golden bee"
        spriteClass = BeeSprite::class.java
        viewDistance = 4
        WANDERING = Wandering()
        flying = true
        state = WANDERING
    }
}