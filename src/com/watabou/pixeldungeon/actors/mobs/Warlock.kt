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

class Warlock : Mob(), Callback {
    fun damageRoll(): Int {
        return Random.NormalIntRange(12, 20)
    }

    fun attackSkill(target: Char?): Int {
        return 25
    }

    fun dr(): Int {
        return 8
    }

    protected fun canAttack(enemy: Char): Boolean {
        return Ballistica.cast(pos, enemy.pos, false, true) === enemy.pos
    }

    protected fun doAttack(enemy: Char): Boolean {
        return if (Level.adjacent(pos, enemy.pos)) {
            super.doAttack(enemy)
        } else {
            val visible = Level.fieldOfView.get(pos) || Level.fieldOfView.get(enemy.pos)
            if (visible) {
                (sprite as WarlockSprite).zap(enemy.pos)
            } else {
                zap()
            }
            !visible
        }
    }

    private fun zap() {
        spend(TIME_TO_ZAP)
        if (hit(this, enemy, true)) {
            if (enemy === Dungeon.hero && Random.Int(2) === 0) {
                Buff.prolong(enemy, Weakness::class.java, Weakness.duration(enemy))
            }
            val dmg: Int = Random.Int(12, 18)
            enemy.damage(dmg, this)
            if (!enemy.isAlive() && enemy === Dungeon.hero) {
                Dungeon.fail(
                    Utils.format(
                        ResultDescriptions.MOB,
                        Utils.indefinite(name), Dungeon.depth
                    )
                )
                GLog.n(TXT_SHADOWBOLT_KILLED, name)
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
        }
    }

    fun onZapComplete() {
        zap()
        next()
    }

    fun call() {
        next()
    }

    override fun description(): String {
        return "When dwarves' interests have shifted from engineering to arcane arts, " +
                "warlocks have come to power in the city. They started with elemental magic, " +
                "but soon switched to demonology and necromancy."
    }

    companion object {
        private const val TIME_TO_ZAP = 1f
        private const val TXT_SHADOWBOLT_KILLED = "%s's shadow bolt killed you..."
        private val RESISTANCES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(Death::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    init {
        name = "dwarf warlock"
        spriteClass = WarlockSprite::class.java
        HT = 70
        HP = HT
        defenseSkill = 18
        EXP = 11
        maxLvl = 21
        loot = Generator.Category.POTION
        lootChance = 0.83f
    }
}