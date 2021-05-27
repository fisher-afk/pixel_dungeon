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
package com.watabou.pixeldungeon.actors.buffs

import com.watabou.pixeldungeon.Badges

class Hunger : Buff(), Hero.Doom {
    private var level = 0f
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getFloat(LEVEL)
    }

    override fun act(): Boolean {
        if (target.isAlive()) {
            val hero: Hero = target as Hero
            if (isStarving) {
                if (Random.Float() < 0.3f && (target.HP > 1 || !target.paralysed)) {
                    GLog.n(TXT_STARVING)
                    hero.damage(1, this)
                    hero.interrupt()
                }
            } else {
                var bonus = 0
                for (buff in target.buffs(RingOfSatiety.Satiety::class.java)) {
                    bonus += (buff as RingOfSatiety.Satiety).level
                }
                val newLevel = level + STEP - bonus
                var statusUpdated = false
                if (newLevel >= STARVING) {
                    GLog.n(TXT_STARVING)
                    statusUpdated = true
                    hero.interrupt()
                } else if (newLevel >= HUNGRY && level < HUNGRY) {
                    GLog.w(TXT_HUNGRY)
                    statusUpdated = true
                }
                level = newLevel
                if (statusUpdated) {
                    BuffIndicator.refreshHero()
                }
            }
            val step = if ((target as Hero).heroClass === HeroClass.ROGUE) STEP * 1.2f else STEP
            spend(if (target.buff(Shadows::class.java) == null) step else step * 1.5f)
        } else {
            diactivate()
        }
        return true
    }

    fun satisfy(energy: Float) {
        level -= energy
        if (level < 0) {
            level = 0f
        } else if (level > STARVING) {
            level = STARVING
        }
        BuffIndicator.refreshHero()
    }

    val isStarving: Boolean
        get() = level >= STARVING

    override fun icon(): Int {
        return if (level < HUNGRY) {
            BuffIndicator.NONE
        } else if (level < STARVING) {
            BuffIndicator.HUNGER
        } else {
            BuffIndicator.STARVATION
        }
    }

    override fun toString(): String {
        return if (level < STARVING) {
            "Hungry"
        } else {
            "Starving"
        }
    }

    fun onDeath() {
        Badges.validateDeathFromHunger()
        Dungeon.fail(Utils.format(ResultDescriptions.HUNGER, Dungeon.depth))
        GLog.n(TXT_DEATH)
    }

    companion object {
        private const val STEP = 10f
        const val HUNGRY = 260f
        const val STARVING = 360f
        private const val TXT_HUNGRY = "You are hungry."
        private const val TXT_STARVING = "You are starving!"
        private const val TXT_DEATH = "You starved to death..."
        private const val LEVEL = "level"
    }
}