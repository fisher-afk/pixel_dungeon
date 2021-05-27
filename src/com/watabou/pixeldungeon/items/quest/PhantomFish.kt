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
package com.watabou.pixeldungeon.items.quest

import com.watabou.noosa.audio.Sample

class PhantomFish : Item() {
    fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_EAT)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_EAT) {
            detach(hero.belongings.backpack)
            hero.sprite.operate(hero.pos)
            hero.busy()
            Sample.INSTANCE.play(Assets.SND_EAT)
            Sample.INSTANCE.play(Assets.SND_MELD)
            GLog.i("You see your hands turn invisible!")
            Buff.affect(hero, Invisibility::class.java, Invisibility.DURATION)
            hero.spend(TIME_TO_EAT)
        } else {
            super.execute(hero, action)
        }
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    fun info(): String {
        return "You can barely see this tiny translucent fish in the air. " +
                "In the water it becomes effectively invisible."
    }

    companion object {
        private const val AC_EAT = "EAT"
        private const val TIME_TO_EAT = 2f
    }

    init {
        name = "phantom fish"
        image = ItemSpriteSheet.PHANTOM
        unique = true
    }
}