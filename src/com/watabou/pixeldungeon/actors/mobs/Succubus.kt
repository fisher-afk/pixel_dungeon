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

import com.watabou.noosa.audio.Sample

class Succubus : Mob() {
    private var delay = 0
    fun damageRoll(): Int {
        return Random.NormalIntRange(15, 25)
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(3) === 0) {
            Buff.affect(enemy, Charm::class.java, Charm.durationFactor(enemy) * Random.IntRange(3, 7)).`object` = id()
            enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5)
            Sample.INSTANCE.play(Assets.SND_CHARMS)
        }
        return damage
    }

    protected override fun getCloser(target: Int): Boolean {
        return if (Level.fieldOfView.get(target) && Level.distance(pos, target) > 2 && delay <= 0) {
            blink(target)
            spend(-1 / speed())
            true
        } else {
            delay--
            super.getCloser(target)
        }
    }

    private fun blink(target: Int) {
        var cell: Int = Ballistica.cast(pos, target, true, true)
        if (Actor.findChar(cell) != null && Ballistica.distance > 1) {
            cell = Ballistica.trace.get(Ballistica.distance - 2)
        }
        WandOfBlink.appear(this, cell)
        delay = BLINK_DELAY
    }

    fun attackSkill(target: Char?): Int {
        return 40
    }

    fun dr(): Int {
        return 10
    }

    override fun description(): String {
        return "The succubi are demons that look like seductive (in a slightly gothic way) girls. Using its magic, the succubus " +
                "can charm a hero, who will become unable to attack anything until the charm wears off."
    }

    companion object {
        private const val BLINK_DELAY = 5
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(Leech::class.java)
        }

        init {
            IMMUNITIES.add(Sleep::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "succubus"
        spriteClass = SuccubusSprite::class.java
        HT = 80
        HP = HT
        defenseSkill = 25
        viewDistance = Light.DISTANCE
        EXP = 12
        maxLvl = 25
        loot = ScrollOfLullaby()
        lootChance = 0.05f
    }
}