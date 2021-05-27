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

abstract class KindOfWeapon : EquipableItem() {
    override fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }

    override fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.weapon === this
    }

    override fun doEquip(hero: Hero): Boolean {
        detachAll(hero.belongings.backpack)
        return if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip(hero, true)) {
            hero.belongings.weapon = this
            activate(hero)
            QuickSlot.refresh()
            cursedKnown = true
            if (cursed) {
                equipCursed(hero)
                GLog.n(TXT_EQUIP_CURSED, name())
            }
            hero.spendAndNext(TIME_TO_EQUIP)
            true
        } else {
            collect(hero.belongings.backpack)
            false
        }
    }

    override fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        return if (super.doUnequip(hero, collect, single)) {
            hero.belongings.weapon = null
            true
        } else {
            false
        }
    }

    fun activate(hero: Hero?) {}
    abstract fun min(): Int
    abstract fun max(): Int
    fun damageRoll(owner: Hero?): Int {
        return Random.NormalIntRange(min(), max())
    }

    fun acuracyFactor(hero: Hero?): Float {
        return 1f
    }

    fun speedFactor(hero: Hero?): Float {
        return 1f
    }

    fun proc(attacker: Char?, defender: Char?, damage: Int) {}

    companion object {
        private const val TXT_EQUIP_CURSED = "you wince as your grip involuntarily tightens around your %s"
        protected const val TIME_TO_EQUIP = 1f
    }
}