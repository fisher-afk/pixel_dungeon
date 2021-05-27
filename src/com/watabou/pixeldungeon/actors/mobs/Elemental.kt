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

class Elemental : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(16, 20)
    }

    fun attackSkill(target: Char?): Int {
        return 25
    }

    fun dr(): Int {
        return 5
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        if (Random.Int(2) === 0) {
            Buff.affect(enemy, Burning::class.java).reignite(enemy)
        }
        return damage
    }

    override fun add(buff: Buff?) {
        if (buff is Burning) {
            if (HP < HT) {
                HP++
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
            }
        } else {
            if (buff is Frost) {
                damage(Random.NormalIntRange(1, HT * 2 / 3), buff)
            }
            super.add(buff)
        }
    }

    override fun description(): String {
        return "Wandering fire elementals are a byproduct of summoning greater entities. " +
                "They are too chaotic in their nature to be controlled by even the most powerful demonologist."
    }

    companion object {
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Burning::class.java)
            IMMUNITIES.add(Fire::class.java)
            IMMUNITIES.add(WandOfFirebolt::class.java)
            IMMUNITIES.add(ScrollOfPsionicBlast::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "fire elemental"
        spriteClass = ElementalSprite::class.java
        HT = 65
        HP = HT
        defenseSkill = 20
        EXP = 10
        maxLvl = 20
        flying = true
        loot = PotionOfLiquidFlame()
        lootChance = 0.1f
    }
}