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
package com.watabou.pixeldungeon.items.food

import com.watabou.pixeldungeon.actors.buffs.Buff

class MysteryMeat : Food() {
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            when (Random.Int(5)) {
                0 -> {
                    GLog.w("Oh it's hot!")
                    Buff.affect(hero, Burning::class.java).reignite(hero)
                }
                1 -> {
                    GLog.w("You can't feel your legs!")
                    Buff.prolong(hero, Roots::class.java, Paralysis.duration(hero))
                }
                2 -> {
                    GLog.w("You are not feeling well.")
                    Buff.affect(hero, Poison::class.java).set(Poison.durationFactor(hero) * hero.HT / 5)
                }
                3 -> {
                    GLog.w("You are stuffed.")
                    Buff.prolong(hero, Slow::class.java, Slow.duration(hero))
                }
            }
        }
    }

    override fun info(): String {
        return "Eat at your own risk!"
    }

    override fun price(): Int {
        return 5 * quantity
    }

    init {
        name = "mystery meat"
        image = ItemSpriteSheet.MEAT
        energy = Hunger.STARVING - Hunger.HUNGRY
        message = "That food tasted... strange."
    }
}