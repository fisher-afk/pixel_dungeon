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

class Tengu : Mob() {
    private var timeToJump = JUMP_DELAY
    fun damageRoll(): Int {
        return Random.NormalIntRange(8, 15)
    }

    fun attackSkill(target: Char?): Int {
        return 20
    }

    fun dr(): Int {
        return 5
    }

    override fun die(cause: Any?) {
        var badgeToCheck: Badges.Badge? = null
        when (Dungeon.hero.heroClass) {
            WARRIOR -> badgeToCheck = Badge.MASTERY_WARRIOR
            MAGE -> badgeToCheck = Badge.MASTERY_MAGE
            ROGUE -> badgeToCheck = Badge.MASTERY_ROGUE
            HUNTRESS -> badgeToCheck = Badge.MASTERY_HUNTRESS
        }
        if (!Badges.isUnlocked(badgeToCheck) || Dungeon.hero.subClass !== HeroSubClass.NONE) {
            Dungeon.level.drop(TomeOfMastery(), pos).sprite.drop()
        }
        GameScene.bossSlain()
        Dungeon.level.drop(SkeletonKey(), pos).sprite.drop()
        super.die(cause)
        Badges.validateBossSlain()
        yell("Free at last...")
    }

    protected override fun getCloser(target: Int): Boolean {
        return if (Level.fieldOfView.get(target)) {
            jump()
            true
        } else {
            super.getCloser(target)
        }
    }

    protected fun canAttack(enemy: Char): Boolean {
        return Ballistica.cast(pos, enemy.pos, false, true) === enemy.pos
    }

    protected fun doAttack(enemy: Char): Boolean {
        timeToJump--
        return if (timeToJump <= 0 && Level.adjacent(pos, enemy.pos)) {
            jump()
            true
        } else {
            super.doAttack(enemy)
        }
    }

    private fun jump() {
        timeToJump = JUMP_DELAY
        for (i in 0..3) {
            var trapPos: Int
            do {
                trapPos = Random.Int(Level.LENGTH)
            } while (!Level.fieldOfView.get(trapPos) || !Level.passable.get(trapPos))
            if (Dungeon.level.map.get(trapPos) === Terrain.INACTIVE_TRAP) {
                Level.set(trapPos, Terrain.POISON_TRAP)
                GameScene.updateMap(trapPos)
                ScrollOfMagicMapping.discover(trapPos)
            }
        }
        var newPos: Int
        do {
            newPos = Random.Int(Level.LENGTH)
        } while (!Level.fieldOfView.get(newPos) ||
            !Level.passable.get(newPos) ||
            enemy != null && Level.adjacent(newPos, enemy.pos) || Actor.findChar(newPos) != null
        )
        sprite.move(pos, newPos)
        move(newPos)
        if (Dungeon.visible.get(newPos)) {
            CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6)
            Sample.INSTANCE.play(Assets.SND_PUFF)
        }
        spend(1 / speed())
    }

    override fun notice() {
        super.notice()
        yell("Gotcha, " + Dungeon.hero.heroClass.title().toString() + "!")
    }

    override fun description(): String {
        return "Tengu are members of the ancient assassins clan, which is also called Tengu. " +
                "These assassins are noted for extensive use of shuriken and traps."
    }

    companion object {
        private const val JUMP_DELAY = 5
        private val RESISTANCES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(ToxicGas::class.java)
            RESISTANCES.add(Poison::class.java)
            RESISTANCES.add(Death::class.java)
            RESISTANCES.add(ScrollOfPsionicBlast::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    init {
        name = if (Dungeon.depth === Statistics.deepestFloor) "Tengu" else "memory of Tengu"
        spriteClass = TenguSprite::class.java
        HT = 120
        HP = HT
        EXP = 20
        defenseSkill = 20
    }
}