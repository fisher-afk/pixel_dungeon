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

import com.watabou.noosa.tweeners.AlphaTweener

class Wraith : Mob() {
    private var level = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getInt(LEVEL)
        adjustStats(level)
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(1, 3 + level)
    }

    fun attackSkill(target: Char?): Int {
        return 10 + level
    }

    fun adjustStats(level: Int) {
        this.level = level
        defenseSkill = attackSkill(null) * 5
        enemySeen = true
    }

    fun defenseVerb(): String {
        return "evaded"
    }

    override fun reset(): Boolean {
        state = WANDERING
        return true
    }

    override fun description(): String {
        return "A wraith is a vengeful spirit of a sinner, whose grave or tomb was disturbed. " +
                "Being an ethereal entity, it is very hard to hit with a regular weapon."
    }

    companion object {
        private const val SPAWN_DELAY = 2f
        private const val LEVEL = "level"
        fun spawnAround(pos: Int) {
            for (n in Level.NEIGHBOURS4) {
                val cell = pos + n
                if (Level.passable.get(cell) && Actor.findChar(cell) == null) {
                    spawnAt(cell)
                }
            }
        }

        fun spawnAt(pos: Int): Wraith? {
            return if (Level.passable.get(pos) && Actor.findChar(pos) == null) {
                val w = Wraith()
                w.adjustStats(Dungeon.depth)
                w.pos = pos
                w.state = w.HUNTING
                GameScene.add(w, SPAWN_DELAY)
                w.sprite.alpha(0)
                w.sprite.parent.add(AlphaTweener(w.sprite, 1, 0.5f))
                w.sprite.emitter().burst(ShadowParticle.CURSE, 5)
                w
            } else {
                null
            }
        }

        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Death::class.java)
            IMMUNITIES.add(Terror::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "wraith"
        spriteClass = WraithSprite::class.java
        HT = 1
        HP = HT
        EXP = 0
        flying = true
    }
}