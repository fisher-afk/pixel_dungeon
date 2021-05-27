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
package com.watabou.pixeldungeon.actors.mobs

class Gnoll : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(2, 5)
    }

    fun attackSkill(target: Char?): Int {
        return 11
    }

    fun dr(): Int {
        return 2
    }

    override fun die(cause: Any?) {
        Ghost.Quest.processSewersKill(pos)
        super.die(cause)
    }

    override fun description(): String {
        return "Gnolls are hyena-like humanoids. They dwell in sewers and dungeons, venturing up to raid the surface from time to time. " +
                "Gnoll scouts are regular members of their pack, they are not as strong as brutes and not as intelligent as shamans."
    }

    init {
        name = "gnoll scout"
        spriteClass = GnollSprite::class.java
        HT = 12
        HP = HT
        defenseSkill = 4
        EXP = 2
        maxLvl = 8
        loot = Gold::class.java
        lootChance = 0.5f
    }
}