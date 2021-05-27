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

class Monk : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(12, 16)
    }

    fun attackSkill(target: Char?): Int {
        return 30
    }

    protected override fun attackDelay(): Float {
        return 0.5f
    }

    fun dr(): Int {
        return 2
    }

    fun defenseVerb(): String {
        return "parried"
    }

    override fun die(cause: Any?) {
        Imp.Quest.process(this)
        super.die(cause)
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(6) === 0 && enemy === Dungeon.hero) {
            val hero: Hero = Dungeon.hero
            val weapon: KindOfWeapon = hero.belongings.weapon
            if (weapon != null && weapon !is Knuckles && !weapon.cursed) {
                hero.belongings.weapon = null
                Dungeon.level.drop(weapon, hero.pos).sprite.drop()
                GLog.w(TXT_DISARM, name, weapon.name())
            }
        }
        return damage
    }

    override fun description(): String {
        return "These monks are fanatics, who devoted themselves to protecting their city's secrets from all aliens. " +
                "They don't use any armor or weapons, relying solely on the art of hand-to-hand combat."
    }

    companion object {
        const val TXT_DISARM = "%s has knocked the %s from your hands!"
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Amok::class.java)
            IMMUNITIES.add(Terror::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "dwarf monk"
        spriteClass = MonkSprite::class.java
        HT = 70
        HP = HT
        defenseSkill = 30
        EXP = 11
        maxLvl = 21
        loot = Food()
        lootChance = 0.083f
    }
}