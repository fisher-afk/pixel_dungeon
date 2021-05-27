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
package com.watabou.pixeldungeon.items

import com.watabou.noosa.Game

class Amulet : Item() {
    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_END)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === AC_END) {
            showAmuletScene(false)
        } else {
            super.execute(hero, action)
        }
    }

    override fun doPickUp(hero: Hero): Boolean {
        return if (super.doPickUp(hero)) {
            if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true
                Badges.validateVictory()
                showAmuletScene(true)
            }
            true
        } else {
            false
        }
    }

    private fun showAmuletScene(showText: Boolean) {
        try {
            Dungeon.saveAll()
            AmuletScene.noText = !showText
            Game.switchScene(AmuletScene::class.java)
        } catch (e: IOException) {
        }
    }

    override val isIdentified: Boolean
        get() = true
    override val isUpgradable: Boolean
        get() = false

    override fun info(): String {
        return "The Amulet of Yendor is the most powerful known artifact of unknown origin. It is said that the amulet " +
                "is able to fulfil any wish if its owner's will-power is strong enough to \"persuade\" it to do it."
    }

    companion object {
        private const val AC_END = "END THE GAME"
    }

    init {
        name = "Amulet of Yendor"
        image = ItemSpriteSheet.AMULET
        unique = true
    }
}