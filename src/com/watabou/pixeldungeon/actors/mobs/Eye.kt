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

class Eye : Mob() {
    fun dr(): Int {
        return 10
    }

    private var hitCell = 0
    protected fun canAttack(enemy: Char): Boolean {
        hitCell = Ballistica.cast(pos, enemy.pos, true, false)
        for (i in 1 until Ballistica.distance) {
            if (Ballistica.trace.get(i) === enemy.pos) {
                return true
            }
        }
        return false
    }

    fun attackSkill(target: Char?): Int {
        return 30
    }

    protected override fun attackDelay(): Float {
        return 1.6f
    }

    protected override fun doAttack(enemy: Char?): Boolean {
        spend(attackDelay())
        var rayVisible = false
        for (i in 0 until Ballistica.distance) {
            if (Dungeon.visible.get(Ballistica.trace.get(i))) {
                rayVisible = true
            }
        }
        return if (rayVisible) {
            sprite.attack(hitCell)
            false
        } else {
            attack(enemy)
            true
        }
    }

    fun attack(enemy: Char?): Boolean {
        for (i in 1 until Ballistica.distance) {
            val pos: Int = Ballistica.trace.get(i)
            val ch: Char = Actor.findChar(pos) ?: continue
            if (hit(this, ch, true)) {
                ch.damage(Random.NormalIntRange(14, 20), this)
                if (Dungeon.visible.get(pos)) {
                    ch.sprite.flash()
                    CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2))
                }
                if (!ch.isAlive() && ch === Dungeon.hero) {
                    Dungeon.fail(Utils.format(ResultDescriptions.MOB, Utils.indefinite(name), Dungeon.depth))
                    GLog.n(TXT_DEATHGAZE_KILLED, name)
                }
            } else {
                ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb())
            }
        }
        return true
    }

    override fun description(): String {
        return "One of this demon's other names is \"orb of hatred\", because when it sees an enemy, " +
                "it uses its deathgaze recklessly, often ignoring its allies and wounding them."
    }

    companion object {
        private const val TXT_DEATHGAZE_KILLED = "%s's deathgaze killed you..."
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(WandOfDisintegration::class.java)
            RESISTANCES.add(Death::class.java)
            RESISTANCES.add(Leech::class.java)
        }

        init {
            IMMUNITIES.add(Terror::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "evil eye"
        spriteClass = EyeSprite::class.java
        HT = 100
        HP = HT
        defenseSkill = 20
        viewDistance = Light.DISTANCE
        EXP = 13
        maxLvl = 25
        flying = true
        loot = Dewdrop()
        lootChance = 0.5f
    }
}