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

import com.watabou.noosa.audio.Sample

class Food : Item() {
    var energy: Float = Hunger.HUNGRY
    var message = "That food tasted delicious!"
    fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_EAT)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_EAT) {
            detach(hero.belongings.backpack)
            (hero.buff(Hunger::class.java) as Hunger).satisfy(energy)
            GLog.i(message)
            when (hero.heroClass) {
                WARRIOR -> if (hero.HP < hero.HT) {
                    hero.HP = Math.min(hero.HP + 5, hero.HT)
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
                }
                MAGE -> {
                    hero.belongings.charge(false)
                    ScrollOfRecharging.charge(hero)
                }
                ROGUE, HUNTRESS -> {
                }
            }
            hero.sprite.operate(hero.pos)
            hero.busy()
            SpellSprite.show(hero, SpellSprite.FOOD)
            Sample.INSTANCE.play(Assets.SND_EAT)
            hero.spend(TIME_TO_EAT)
            Statistics.foodEaten++
            Badges.validateFoodEaten()
        } else {
            super.execute(hero, action)
        }
    }

    fun info(): String {
        return "Nothing fancy here: dried meat, " +
                "some biscuits - things like that."
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    fun price(): Int {
        return 10 * quantity
    }

    companion object {
        private const val TIME_TO_EAT = 3f
        const val AC_EAT = "EAT"
    }

    init {
        stackable = true
        name = "ration of food"
        image = ItemSpriteSheet.RATION
    }
}