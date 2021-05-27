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

class Skeleton : Mob() {
    fun damageRoll(): Int {
        return Random.NormalIntRange(3, 8)
    }

    override fun die(cause: Any?) {
        super.die(cause)
        var heroKilled = false
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val ch: Char = findChar(pos + Level.NEIGHBOURS8.get(i))
            if (ch != null && ch.isAlive()) {
                val damage = Math.max(0, damageRoll() - Random.IntRange(0, ch.dr() / 2))
                ch.damage(damage, this)
                if (ch === Dungeon.hero && !ch.isAlive()) {
                    heroKilled = true
                }
            }
        }
        if (Dungeon.visible.get(pos)) {
            Sample.INSTANCE.play(Assets.SND_BONES)
        }
        if (heroKilled) {
            Dungeon.fail(Utils.format(ResultDescriptions.MOB, Utils.indefinite(name), Dungeon.depth))
            GLog.n(TXT_HERO_KILLED)
        }
    }

    protected override fun dropLoot() {
        if (Random.Int(5) === 0) {
            var loot: Item = Generator.random(Generator.Category.WEAPON)
            for (i in 0..1) {
                val l: Item = Generator.random(Generator.Category.WEAPON)
                if (l.level() < loot.level()) {
                    loot = l
                }
            }
            Dungeon.level.drop(loot, pos).sprite.drop()
        }
    }

    fun attackSkill(target: Char?): Int {
        return 12
    }

    fun dr(): Int {
        return 5
    }

    fun defenseVerb(): String {
        return "blocked"
    }

    override fun description(): String {
        return "Skeletons are composed of corpses bones from unlucky adventurers and inhabitants of the dungeon, " +
                "animated by emanations of evil magic from the depths below. After they have been " +
                "damaged enough, they disintegrate in an explosion of bones."
    }

    companion object {
        private const val TXT_HERO_KILLED = "You were killed by the explosion of bones..."
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Death::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "skeleton"
        spriteClass = SkeletonSprite::class.java
        HT = 25
        HP = HT
        defenseSkill = 9
        EXP = 5
        maxLvl = 10
    }
}