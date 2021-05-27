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

import com.watabou.noosa.Camera

class DM300 : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(18, 24)
    }

    fun attackSkill(target: Char?): Int {
        return 28
    }

    fun dr(): Int {
        return 10
    }

    override fun act(): Boolean {
        GameScene.add(Blob.seed(pos, 30, ToxicGas::class.java))
        return super.act()
    }

    override fun move(step: Int) {
        super.move(step)
        if (Dungeon.level.map.get(step) === Terrain.INACTIVE_TRAP && HP < HT) {
            HP += Random.Int(1, HT - HP)
            sprite.emitter().burst(ElmoParticle.FACTORY, 5)
            if (Dungeon.visible.get(step) && Dungeon.hero.isAlive()) {
                GLog.n("DM-300 repairs itself!")
            }
        }
        val cells = intArrayOf(
            step - 1, step + 1, step - Level.WIDTH, step + Level.WIDTH,
            step - 1 - Level.WIDTH,
            step - 1 + Level.WIDTH,
            step + 1 - Level.WIDTH,
            step + 1 + Level.WIDTH
        )
        val cell = cells[Random.Int(cells.size)]
        if (Dungeon.visible.get(cell)) {
            CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            Camera.main.shake(3, 0.7f)
            Sample.INSTANCE.play(Assets.SND_ROCKS)
            if (Level.water.get(cell)) {
                GameScene.ripple(cell)
            } else if (Dungeon.level.map.get(cell) === Terrain.EMPTY) {
                Level.set(cell, Terrain.EMPTY_DECO)
                GameScene.updateMap(cell)
            }
        }
        val ch: Char = Actor.findChar(cell)
        if (ch != null && ch !== this) {
            Buff.prolong(ch, Paralysis::class.java, 2)
        }
    }

    override fun die(cause: Any?) {
        super.die(cause)
        GameScene.bossSlain()
        Dungeon.level.drop(SkeletonKey(), pos).sprite.drop()
        Badges.validateBossSlain()
        yell("Mission failed. Shutting down.")
    }

    override fun notice() {
        super.notice()
        yell("Unauthorised personnel detected.")
    }

    override fun description(): String {
        return "This machine was created by the Dwarves several centuries ago. Later, Dwarves started to replace machines with " +
                "golems, elementals and even demons. Eventually it led their civilization to the decline. The DM-300 and similar " +
                "machines were typically used for construction and mining, and in some cases, for city defense."
    }

    companion object {
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(Death::class.java)
            RESISTANCES.add(ScrollOfPsionicBlast::class.java)
        }

        init {
            IMMUNITIES.add(ToxicGas::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = if (Dungeon.depth === Statistics.deepestFloor) "DM-300" else "DM-350"
        spriteClass = DM300Sprite::class.java
        HT = 200
        HP = HT
        EXP = 30
        defenseSkill = 18
        loot = RingOfThorns().random()
        lootChance = 0.333f
    }
}