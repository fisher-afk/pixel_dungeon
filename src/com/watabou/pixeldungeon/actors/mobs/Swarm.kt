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

class Swarm : Mob() {
    var generation = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(GENERATION, generation)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        generation = bundle.getInt(GENERATION)
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(1, 4)
    }

    fun defenseProc(enemy: Char?, damage: Int): Int {
        if (HP >= damage + 2) {
            val candidates = ArrayList<Int>()
            val passable: BooleanArray = Level.passable
            val neighbours = intArrayOf(pos + 1, pos - 1, pos + Level.WIDTH, pos - Level.WIDTH)
            for (n in neighbours) {
                if (passable[n] && Actor.findChar(n) == null) {
                    candidates.add(n)
                }
            }
            if (candidates.size > 0) {
                val clone = split()
                clone.HP = (HP - damage) / 2
                clone.pos = Random.element(candidates)
                clone.state = clone.HUNTING
                if (Dungeon.level.map.get(clone.pos) === Terrain.DOOR) {
                    Door.enter(clone.pos)
                }
                GameScene.add(clone, SPLIT_DELAY)
                Actor.addDelayed(Pushing(clone, pos, clone.pos), -1)
                HP -= clone.HP
            }
        }
        return damage
    }

    fun attackSkill(target: Char?): Int {
        return 12
    }

    fun defenseVerb(): String {
        return "evaded"
    }

    private fun split(): Swarm {
        val clone = Swarm()
        clone.generation = generation + 1
        if (buff(Burning::class.java) != null) {
            Buff.affect(clone, Burning::class.java).reignite(clone)
        }
        if (buff(Poison::class.java) != null) {
            Buff.affect(clone, Poison::class.java).set(2)
        }
        return clone
    }

    protected override fun dropLoot() {
        if (Random.Int(5 * (generation + 1)) === 0) {
            Dungeon.level.drop(PotionOfHealing(), pos).sprite.drop()
        }
    }

    override fun description(): String {
        return "The deadly swarm of flies buzzes angrily. Every non-magical attack " +
                "will split it into two smaller but equally dangerous swarms."
    }

    companion object {
        private const val SPLIT_DELAY = 1f
        private const val GENERATION = "generation"
    }

    init {
        name = "swarm of flies"
        spriteClass = SwarmSprite::class.java
        HT = 80
        HP = HT
        defenseSkill = 5
        maxLvl = 10
        flying = true
    }
}