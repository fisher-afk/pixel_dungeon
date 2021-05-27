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
package com.watabou.pixeldungeon.items.weapon.melee

import com.watabou.noosa.audio.Sample

class ShortSword : MeleeWeapon(1, 1f, 1f) {
    private var equipped = false
    protected override fun max0(): Int {
        return 12
    }

    fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        if (level() > 0) {
            actions.add(AC_REFORGE)
        }
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === AC_REFORGE) {
            if (hero.belongings.weapon === this) {
                equipped = true
                hero.belongings.weapon = null
            } else {
                equipped = false
                detach(hero.belongings.backpack)
            }
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON)
        } else {
            super.execute(hero, action)
        }
    }

    fun desc(): String {
        return "It is indeed quite short, just a few inches longer, than a dagger."
    }

    private val itemSelector: WndBag.Listener = object : Listener() {
        fun onSelect(item: Item?) {
            if (item != null && item !is Boomerang) {
                Sample.INSTANCE.play(Assets.SND_EVOKE)
                ScrollOfUpgrade.upgrade(curUser)
                evoke(curUser)
                GLog.w(TXT_REFORGED, item.name())
                (item as MeleeWeapon).safeUpgrade()
                curUser.spendAndNext(TIME_TO_REFORGE)
                Badges.validateItemLevelAquired(item)
            } else {
                if (item is Boomerang) {
                    GLog.w(TXT_NOT_BOOMERANG)
                }
                if (equipped) {
                    curUser.belongings.weapon = this@ShortSword
                } else {
                    collect(curUser.belongings.backpack)
                }
            }
        }
    }

    companion object {
        const val AC_REFORGE = "REFORGE"
        private const val TXT_SELECT_WEAPON = "Select a weapon to upgrade"
        private const val TXT_REFORGED = "you reforged the short sword to upgrade your %s"
        private const val TXT_NOT_BOOMERANG = "you can't upgrade a boomerang this way"
        private const val TIME_TO_REFORGE = 2f
    }

    init {
        name = "short sword"
        image = ItemSpriteSheet.SHORT_SWORD
    }

    init {
        STR = 11
    }
}