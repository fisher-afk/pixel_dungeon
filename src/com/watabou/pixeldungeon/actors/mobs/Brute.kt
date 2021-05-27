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

import com.watabou.pixeldungeon.Dungeon

class Brute : Mob() {
    private var enraged = false
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        enraged = HP < HT / 4
    }

    fun damageRoll(): Int {
        return if (enraged) Random.NormalIntRange(10, 40) else Random.NormalIntRange(8, 18)
    }

    fun attackSkill(target: Char?): Int {
        return 20
    }

    fun dr(): Int {
        return 8
    }

    override fun damage(dmg: Int, src: Any?) {
        super.damage(dmg, src)
        if (isAlive() && !enraged && HP < HT / 4) {
            enraged = true
            spend(TICK)
            if (Dungeon.visible.get(pos)) {
                GLog.w(TXT_ENRAGED, name)
                sprite.showStatus(CharSprite.NEGATIVE, "enraged")
            }
        }
    }

    override fun description(): String {
        return "Brutes are the largest, strongest and toughest of all gnolls. When severely wounded, " +
                "they go berserk, inflicting even more damage to their enemies."
    }

    companion object {
        private const val TXT_ENRAGED = "%s becomes enraged!"
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Terror::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "gnoll brute"
        spriteClass = BruteSprite::class.java
        HT = 40
        HP = HT
        defenseSkill = 15
        EXP = 8
        maxLvl = 15
        loot = Gold::class.java
        lootChance = 0.5f
    }
}