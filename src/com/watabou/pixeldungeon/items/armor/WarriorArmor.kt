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

import com.watabou.noosa.Camera

class WarriorArmor : ClassArmor() {
    override fun special(): String {
        return AC_SPECIAL
    }

    override fun doSpecial() {
        GameScene.selectCell(leaper)
    }

    override fun doEquip(hero: Hero): Boolean {
        return if (hero.heroClass === HeroClass.WARRIOR) {
            super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_WARRIOR)
            false
        }
    }

    override fun desc(): String {
        return "While this armor looks heavy, it allows a warrior to perform heroic leap towards " +
                "a targeted location, slamming down to stun all neighbouring enemies."
    }

    companion object {
        private const val LEAP_TIME = 1
        private const val SHOCK_TIME = 3
        private const val AC_SPECIAL = "HEROIC LEAP"
        private const val TXT_NOT_WARRIOR = "Only warriors can use this armor!"
        protected var leaper: CellSelector.Listener = object : Listener() {
            fun onSelect(target: Int?) {
                if (target != null && target !== curUser.pos) {
                    var cell: Int = Ballistica.cast(curUser.pos, target, false, true)
                    if (Actor.findChar(cell) != null && cell != curUser.pos) {
                        cell = Ballistica.trace.get(Ballistica.distance - 2)
                    }
                    curUser.HP -= curUser.HP / 3
                    if (curUser.subClass === HeroSubClass.BERSERKER && curUser.HP <= curUser.HT * Fury.LEVEL) {
                        Buff.affect(curUser, Fury::class.java)
                    }
                    Invisibility.dispel()
                    val dest = cell
                    curUser.busy()
                    curUser.sprite.jump(curUser.pos, cell, object : Callback() {
                        fun call() {
                            curUser.move(dest)
                            Dungeon.level.press(dest, curUser)
                            Dungeon.observe()
                            for (i in 0 until Level.NEIGHBOURS8.length) {
                                val mob: Char = Actor.findChar(curUser.pos + Level.NEIGHBOURS8.get(i))
                                if (mob != null && mob !== curUser) {
                                    Buff.prolong(mob, Paralysis::class.java, SHOCK_TIME)
                                }
                            }
                            CellEmitter.center(dest).burst(Speck.factory(Speck.DUST), 10)
                            Camera.main.shake(2, 0.5f)
                            curUser.spendAndNext(LEAP_TIME)
                        }
                    })
                }
            }

            fun prompt(): String {
                return "Choose direction to leap"
            }
        }
    }

    init {
        name = "warrior suit of armor"
        image = ItemSpriteSheet.ARMOR_WARRIOR
    }
}