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

class Yog : Mob() {
    fun spawnFists() {
        val fist1 = RottingFist()
        val fist2 = BurningFist()
        do {
            fist1.pos = pos + Level.NEIGHBOURS8.get(Random.Int(8))
            fist2.pos = pos + Level.NEIGHBOURS8.get(Random.Int(8))
        } while (!Level.passable.get(fist1.pos) || !Level.passable.get(fist2.pos) || fist1.pos === fist2.pos)
        GameScene.add(fist1)
        GameScene.add(fist2)
    }

    override fun damage(dmg: Int, src: Any?) {
        var dmg = dmg
        if (fistsCount > 0) {
            for (mob in Dungeon.level.mobs) {
                if (mob is BurningFist || mob is RottingFist) {
                    mob.beckon(pos)
                }
            }
            dmg = dmg shr fistsCount
        }
        super.damage(dmg, src)
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {
        val spawnPoints = ArrayList<Int>()
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val p: Int = pos + Level.NEIGHBOURS8.get(i)
            if (Actor.findChar(p) == null && (Level.passable.get(p) || Level.avoid.get(p))) {
                spawnPoints.add(p)
            }
        }
        if (spawnPoints.size > 0) {
            val larva = Larva()
            larva.pos = Random.element(spawnPoints)
            GameScene.add(larva)
            Actor.addDelayed(Pushing(larva, pos, larva.pos), -1)
        }
        return super.defenseProc(enemy, damage)
    }

    override fun beckon(cell: Int) {}
    override fun die(cause: Any?) {
        for (mob in Dungeon.level.mobs.clone()) {
            if (mob is BurningFist || mob is RottingFist) {
                mob.die(cause)
            }
        }
        GameScene.bossSlain()
        Dungeon.level.drop(SkeletonKey(), pos).sprite.drop()
        super.die(cause)
        yell("...")
    }

    override fun notice() {
        super.notice()
        yell("Hope is an illusion...")
    }

    override fun description(): String {
        return TXT_DESC
    }

    companion object {
        private const val TXT_DESC =
            "Yog-Dzewa is an Old God, a powerful entity from the realms of chaos. A century ago, the ancient dwarves " +
                    "barely won the war against its army of demons, but were unable to kill the god itself. Instead, they then " +
                    "imprisoned it in the halls below their city, believing it to be too weak to rise ever again."
        private var fistsCount = 0
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(Death::class.java)
            IMMUNITIES.add(Terror::class.java)
            IMMUNITIES.add(Amok::class.java)
            IMMUNITIES.add(Charm::class.java)
            IMMUNITIES.add(Sleep::class.java)
            IMMUNITIES.add(Burning::class.java)
            IMMUNITIES.add(ToxicGas::class.java)
            IMMUNITIES.add(ScrollOfPsionicBlast::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    class RottingFist : Mob() {
        override fun die(cause: Any?) {
            super.die(cause)
            fistsCount--
        }

        fun attackSkill(target: Char?): Int {
            return 36
        }

        fun damageRoll(): Int {
            return Random.NormalIntRange(24, 36)
        }

        fun dr(): Int {
            return 15
        }

        fun attackProc(enemy: Char, damage: Int): Int {
            if (Random.Int(3) === 0) {
                Buff.affect(enemy, Ooze::class.java)
                enemy.sprite.burst(-0x1000000, 5)
            }
            return damage
        }

        override fun act(): Boolean {
            if (Level.water.get(pos) && HP < HT) {
                sprite.emitter().burst(ShadowParticle.UP, 2)
                HP += REGENERATION
            }
            return super.act()
        }

        override fun description(): String {
            return TXT_DESC
        }

        companion object {
            private const val REGENERATION = 4
            private val RESISTANCES = HashSet<Class<*>>()
            private val IMMUNITIES = HashSet<Class<*>>()

            init {
                RESISTANCES.add(ToxicGas::class.java)
                RESISTANCES.add(Death::class.java)
                RESISTANCES.add(ScrollOfPsionicBlast::class.java)
            }

            init {
                IMMUNITIES.add(Amok::class.java)
                IMMUNITIES.add(Sleep::class.java)
                IMMUNITIES.add(Terror::class.java)
                IMMUNITIES.add(Poison::class.java)
                IMMUNITIES.add(Vertigo::class.java)
            }
        }

        fun resistances(): HashSet<Class<*>> {
            return RESISTANCES
        }

        fun immunities(): HashSet<Class<*>> {
            return IMMUNITIES
        }

        init {
            name = "rotting fist"
            spriteClass = RottingFistSprite::class.java
            HT = 300
            HP = HT
            defenseSkill = 25
            EXP = 0
            state = WANDERING
        }

        init {
            fistsCount++
        }
    }

    class BurningFist : Mob() {
        override fun die(cause: Any?) {
            super.die(cause)
            fistsCount--
        }

        fun attackSkill(target: Char?): Int {
            return 36
        }

        fun damageRoll(): Int {
            return Random.NormalIntRange(20, 32)
        }

        fun dr(): Int {
            return 15
        }

        protected fun canAttack(enemy: Char): Boolean {
            return Ballistica.cast(pos, enemy.pos, false, true) === enemy.pos
        }

        fun attack(enemy: Char): Boolean {
            return if (!Level.adjacent(pos, enemy.pos)) {
                spend(attackDelay())
                if (hit(this, enemy, true)) {
                    val dmg = damageRoll()
                    enemy.damage(dmg, this)
                    enemy.sprite.bloodBurstA(sprite.center(), dmg)
                    enemy.sprite.flash()
                    if (!enemy.isAlive() && enemy === Dungeon.hero) {
                        Dungeon.fail(Utils.format(ResultDescriptions.BOSS, name, Dungeon.depth))
                        GLog.n(TXT_KILL, name)
                    }
                    true
                } else {
                    enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
                    false
                }
            } else {
                super.attack(enemy)
            }
        }

        override fun act(): Boolean {
            for (i in 0 until Level.NEIGHBOURS9.length) {
                GameScene.add(Blob.seed(pos + Level.NEIGHBOURS9.get(i), 2, Fire::class.java))
            }
            return super.act()
        }

        override fun description(): String {
            return TXT_DESC
        }

        companion object {
            private val RESISTANCES = HashSet<Class<*>>()
            private val IMMUNITIES = HashSet<Class<*>>()

            init {
                RESISTANCES.add(ToxicGas::class.java)
                RESISTANCES.add(Death::class.java)
                RESISTANCES.add(ScrollOfPsionicBlast::class.java)
            }

            init {
                IMMUNITIES.add(Amok::class.java)
                IMMUNITIES.add(Sleep::class.java)
                IMMUNITIES.add(Terror::class.java)
                IMMUNITIES.add(Burning::class.java)
            }
        }

        fun resistances(): HashSet<Class<*>> {
            return RESISTANCES
        }

        fun immunities(): HashSet<Class<*>> {
            return IMMUNITIES
        }

        init {
            name = "burning fist"
            spriteClass = BurningFistSprite::class.java
            HT = 200
            HP = HT
            defenseSkill = 25
            EXP = 0
            state = WANDERING
        }

        init {
            fistsCount++
        }
    }

    class Larva : Mob() {
        fun attackSkill(target: Char?): Int {
            return 30
        }

        fun damageRoll(): Int {
            return Random.NormalIntRange(15, 20)
        }

        fun dr(): Int {
            return 8
        }

        override fun description(): String {
            return TXT_DESC
        }

        init {
            name = "god's larva"
            spriteClass = LarvaSprite::class.java
            HT = 25
            HP = HT
            defenseSkill = 20
            EXP = 0
            state = HUNTING
        }
    }

    init {
        name = if (Dungeon.depth === Statistics.deepestFloor) "Yog-Dzewa" else "echo of Yog-Dzewa"
        spriteClass = YogSprite::class.java
        HT = 300
        HP = HT
        EXP = 50
        state = PASSIVE
    }
}