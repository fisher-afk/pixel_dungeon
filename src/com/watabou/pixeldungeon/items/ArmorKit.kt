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

import com.watabou.noosa.audio.Sample

class ArmorKit : Item() {
    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_APPLY)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === AC_APPLY) {
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR, TXT_SELECT_ARMOR)
        } else {
            super.execute(hero, action)
        }
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    private override fun upgrade(armor: Armor) {
        detach(curUser.belongings.backpack)
        curUser.sprite.centerEmitter().start(Speck.factory(Speck.KIT), 0.05f, 10)
        curUser.spend(TIME_TO_UPGRADE)
        curUser.busy()
        GLog.w(TXT_UPGRADED, armor.name())
        val classArmor: ClassArmor = ClassArmor.upgrade(curUser, armor)
        if (curUser.belongings.armor === armor) {
            curUser.belongings.armor = classArmor
            (curUser.sprite as HeroSprite).updateArmor()
        } else {
            armor.detach(curUser.belongings.backpack)
            classArmor.collect(curUser.belongings.backpack)
        }
        curUser.sprite.operate(curUser.pos)
        Sample.INSTANCE.play(Assets.SND_EVOKE)
    }

    override fun info(): String {
        return "Using this kit of small tools and materials anybody can transform any armor into an \"epic armor\", " +
                "which will keep all properties of the original armor, but will also provide its wearer a special ability " +
                "depending on his class. No skills in tailoring, leatherworking or blacksmithing are required."
    }

    private val itemSelector: WndBag.Listener = object : Listener() {
        fun onSelect(item: Item?) {
            if (item != null) {
                upgrade(item as Armor)
            }
        }
    }

    companion object {
        private const val TXT_SELECT_ARMOR = "Select an armor to upgrade"
        private const val TXT_UPGRADED = "you applied the armor kit to upgrade your %s"
        private const val TIME_TO_UPGRADE = 2f
        private const val AC_APPLY = "APPLY"
    }

    init {
        name = "armor kit"
        image = ItemSpriteSheet.KIT
        unique = true
    }
}