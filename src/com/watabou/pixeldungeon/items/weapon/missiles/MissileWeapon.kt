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
package com.watabou.pixeldungeon.items.weapon.missiles

import com.watabou.pixeldungeon.Dungeon

abstract class MissileWeapon : Weapon() {
    fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        if (hero.heroClass !== HeroClass.HUNTRESS && hero.heroClass !== HeroClass.ROGUE) {
            actions.remove(AC_EQUIP)
            actions.remove(AC_UNEQUIP)
        }
        return actions
    }

    protected fun onThrow(cell: Int) {
        val enemy: Char = Actor.findChar(cell)
        if (enemy == null || enemy === curUser) {
            super.onThrow(cell)
        } else {
            if (!curUser.shoot(enemy, this)) {
                miss(cell)
            }
        }
    }

    protected fun miss(cell: Int) {
        super.onThrow(cell)
    }

    fun proc(attacker: Char, defender: Char?, damage: Int) {
        super.proc(attacker, defender, damage)
        val hero: Hero = attacker as Hero
        if (hero.rangedWeapon == null && stackable) {
            if (quantity === 1) {
                doUnequip(hero, false, false)
            } else {
                detach(null)
            }
        }
    }

    fun doEquip(hero: Hero?): Boolean {
        GameScene.show(
            object : WndOptions(TXT_MISSILES, TXT_R_U_SURE, TXT_YES, TXT_NO) {
                protected fun onSelect(index: Int) {
                    if (index == 0) {
                        super@MissileWeapon.doEquip(hero)
                    }
                }
            }
        )
        return false
    }

    fun random(): Item {
        return this
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    fun info(): String {
        val info: StringBuilder = StringBuilder(desc())
        val min: Int = min()
        val max: Int = max()
        info.append(
            """

Average damage of this weapon equals to ${min + (max - min) / 2} points per hit. """
        )
        if (Dungeon.hero.belongings.backpack.items.contains(this)) {
            if (STR > Dungeon.hero.STR()) {
                info.append(
                    "Because of your inadequate strength the accuracy and speed " +
                            "of your attack with this " + name + " is decreased."
                )
            }
            if (STR < Dungeon.hero.STR()) {
                info.append(
                    "Because of your excess strength the damage " +
                            "of your attack with this " + name + " is increased."
                )
            }
        }
        if (isEquipped(Dungeon.hero)) {
            info.append(
                """

You hold the ${name.toString()} at the ready."""
            )
        }
        return info.toString()
    }

    companion object {
        private const val TXT_MISSILES = "Missile weapon"
        private const val TXT_YES = "Yes, I know what I'm doing"
        private const val TXT_NO = "No, I changed my mind"
        private const val TXT_R_U_SURE = "Do you really want to equip it as a melee weapon?"
    }

    init {
        stackable = true
        levelKnown = true
        defaultAction = AC_THROW
    }
}