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
package com.watabou.pixeldungeon.items.potions

import com.watabou.pixeldungeon.Dungeon

class PotionOfHealing : Potion() {
    protected override fun apply(hero: Hero?) {
        setKnown()
        heal(Dungeon.hero)
        GLog.p("Your wounds heal completely.")
    }

    fun desc(): String {
        return "An elixir that will instantly return you to full health and cure poison."
    }

    override fun price(): Int {
        return if (isKnown()) 30 * quantity else super.price()
    }

    companion object {
        fun heal(hero: Hero) {
            hero.HP = hero.HT
            Buff.detach(hero, Poison::class.java)
            Buff.detach(hero, Cripple::class.java)
            Buff.detach(hero, Weakness::class.java)
            Buff.detach(hero, Bleeding::class.java)
            hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4)
        }
    }

    init {
        name = "Potion of Healing"
    }
}