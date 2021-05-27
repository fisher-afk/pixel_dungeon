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
package com.watabou.pixeldungeon.items.armor

import com.watabou.pixeldungeon.actors.hero.Hero

abstract class ClassArmor : Armor(6) {
    private var DR = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ARMOR_STR, STR)
        bundle.put(ARMOR_DR, DR)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        STR = bundle.getInt(ARMOR_STR)
        DR = bundle.getInt(ARMOR_DR)
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        if (hero.HP >= 3 && isEquipped(hero)) {
            actions.add(special())
        }
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === special()) {
            if (hero.HP < 3) {
                GLog.w(TXT_LOW_HEALTH)
            } else if (!isEquipped(hero)) {
                GLog.w(TXT_NOT_EQUIPPED)
            } else {
                curUser = hero
                doSpecial()
            }
        } else {
            super.execute(hero, action)
        }
    }

    abstract fun special(): String
    abstract fun doSpecial()
    override fun DR(): Int {
        return DR
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    override fun price(): Int {
        return 0
    }

    fun desc(): String {
        return "The thing looks awesome!"
    }

    companion object {
        private const val TXT_LOW_HEALTH = "Your health is too low!"
        private const val TXT_NOT_EQUIPPED = "You need to be wearing this armor to use its special power!"
        fun upgrade(owner: Hero, armor: Armor): ClassArmor? {
            var classArmor: ClassArmor? = null
            when (owner.heroClass) {
                WARRIOR -> classArmor = WarriorArmor()
                ROGUE -> classArmor = RogueArmor()
                MAGE -> classArmor = MageArmor()
                HUNTRESS -> classArmor = HuntressArmor()
            }
            classArmor.STR = armor.STR
            classArmor!!.DR = armor.DR()
            classArmor.inscribe(armor.glyph)
            return classArmor
        }

        private const val ARMOR_STR = "STR"
        private const val ARMOR_DR = "DR"
    }

    init {
        levelKnown = true
        cursedKnown = true
        defaultAction = special()
    }
}