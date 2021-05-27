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

import com.watabou.pixeldungeon.actors.buffs.Barkskin

class FrozenCarpaccio : Food() {
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            when (Random.Int(5)) {
                0 -> {
                    GLog.i("You see your hands turn invisible!")
                    Buff.affect(hero, Invisibility::class.java, Invisibility.DURATION)
                }
                1 -> {
                    GLog.i("You feel your skin hardens!")
                    Buff.affect(hero, Barkskin::class.java).level(hero.HT / 4)
                }
                2 -> {
                    GLog.i("Refreshing!")
                    Buff.detach(hero, Poison::class.java)
                    Buff.detach(hero, Cripple::class.java)
                    Buff.detach(hero, Weakness::class.java)
                    Buff.detach(hero, Bleeding::class.java)
                }
                3 -> {
                    GLog.i("You feel better!")
                    if (hero.HP < hero.HT) {
                        hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT)
                        hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
                    }
                }
            }
        }
    }

    override fun info(): String {
        return "It's a piece of frozen raw meat. The only way to eat it is " +
                "by cutting thin slices of it. And this way it's suprisingly good."
    }

    override fun price(): Int {
        return 10 * quantity
    }

    companion object {
        fun cook(ingredient: MysteryMeat): Food {
            val result = FrozenCarpaccio()
            result.quantity = ingredient.quantity()
            return result
        }
    }

    init {
        name = "frozen carpaccio"
        image = ItemSpriteSheet.CARPACCIO
        energy = Hunger.STARVING - Hunger.HUNGRY
    }
}