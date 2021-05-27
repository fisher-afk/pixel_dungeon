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
package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.Dungeon

class MirrorImage : NPC() {
    var tier = 0
    private var attack = 0
    private var damage = 0
    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TIER, tier)
        bundle.put(ATTACK, attack)
        bundle.put(DAMAGE, damage)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        tier = bundle.getInt(TIER)
        attack = bundle.getInt(ATTACK)
        damage = bundle.getInt(DAMAGE)
    }

    fun duplicate(hero: Hero) {
        tier = hero.tier()
        attack = hero.attackSkill(hero)
        damage = hero.damageRoll()
    }

    fun attackSkill(target: Char?): Int {
        return attack
    }

    fun damageRoll(): Int {
        return damage
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        val dmg: Int = super.attackProc(enemy, damage)
        destroy()
        sprite.die()
        return dmg
    }

    protected fun chooseEnemy(): Char? {
        if (enemy == null || !enemy.isAlive()) {
            val enemies: HashSet<Mob> = HashSet<Mob>()
            for (mob in Dungeon.level.mobs) {
                if (mob.hostile && Level.fieldOfView.get(mob.pos)) {
                    enemies.add(mob)
                }
            }
            return if (enemies.size > 0) Random.element(enemies) else null
        }
        return enemy
    }

    fun description(): String {
        return "This illusion bears a close resemblance to you, " +
                "but it's paler and twitches a little."
    }

    fun sprite(): CharSprite {
        val s: CharSprite = super.sprite()
        (s as MirrorSprite).updateArmor(tier)
        return s
    }

    override fun interact() {
        val curPos: Int = pos
        moveSprite(pos, Dungeon.hero.pos)
        move(Dungeon.hero.pos)
        Dungeon.hero.sprite.move(Dungeon.hero.pos, curPos)
        Dungeon.hero.move(curPos)
        Dungeon.hero.spend(1 / Dungeon.hero.speed())
        Dungeon.hero.busy()
    }

    companion object {
        private const val TIER = "tier"
        private const val ATTACK = "attack"
        private const val DAMAGE = "damage"
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(ToxicGas::class.java)
            IMMUNITIES.add(Burning::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "mirror image"
        spriteClass = MirrorSprite::class.java
        state = HUNTING
    }
}