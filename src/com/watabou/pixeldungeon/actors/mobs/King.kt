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

class King : Mob() {
    private var nextPedestal = true
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(PEDESTAL, nextPedestal)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        nextPedestal = bundle.getBoolean(PEDESTAL)
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(20, 38)
    }

    fun attackSkill(target: Char?): Int {
        return 32
    }

    fun dr(): Int {
        return 14
    }

    fun defenseVerb(): String {
        return "parried"
    }

    protected override fun getCloser(target: Int): Boolean {
        return if (canTryToSummon()) super.getCloser(CityBossLevel.pedestal(nextPedestal)) else super.getCloser(target)
    }

    protected fun canAttack(enemy: Char): Boolean {
        return if (canTryToSummon()) pos === CityBossLevel.pedestal(nextPedestal) else Level.adjacent(pos, enemy.pos)
    }

    private fun canTryToSummon(): Boolean {
        return if (Undead.count < maxArmySize()) {
            val ch: Char = Actor.findChar(CityBossLevel.pedestal(nextPedestal))
            ch === this || ch == null
        } else {
            false
        }
    }

    fun attack(enemy: Char): Boolean {
        return if (canTryToSummon() && pos === CityBossLevel.pedestal(nextPedestal)) {
            summon()
            true
        } else {
            if (Actor.findChar(CityBossLevel.pedestal(nextPedestal)) === enemy) {
                nextPedestal = !nextPedestal
            }
            super.attack(enemy)
        }
    }

    override fun die(cause: Any?) {
        GameScene.bossSlain()
        Dungeon.level.drop(ArmorKit(), pos).sprite.drop()
        Dungeon.level.drop(SkeletonKey(), pos).sprite.drop()
        super.die(cause)
        Badges.validateBossSlain()
        yell("You cannot kill me, " + Dungeon.hero.heroClass.title().toString() + "... I am... immortal...")
    }

    private fun maxArmySize(): Int {
        return 1 + MAX_ARMY_SIZE * (HT - HP) / HT
    }

    private fun summon() {
        nextPedestal = !nextPedestal
        sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2)
        Sample.INSTANCE.play(Assets.SND_CHALLENGE)
        val passable: BooleanArray = Level.passable.clone()
        for (actor in Actor.all()) {
            if (actor is Char) {
                passable[(actor as Char).pos] = false
            }
        }
        val undeadsToSummon = maxArmySize() - Undead.count
        PathFinder.buildDistanceMap(pos, passable, undeadsToSummon)
        PathFinder.distance.get(pos) = Int.MAX_VALUE
        var dist = 1
        undeadLabel@ for (i in 0 until undeadsToSummon) {
            do {
                for (j in 0 until Level.LENGTH) {
                    if (PathFinder.distance.get(j) === dist) {
                        val undead = Undead()
                        undead.pos = j
                        GameScene.add(undead)
                        WandOfBlink.appear(undead, j)
                        Flare(3, 32).color(0x000000, false).show(undead.sprite, 2f)
                        PathFinder.distance.get(j) = Int.MAX_VALUE
                        continue@undeadLabel
                    }
                }
                dist++
            } while (dist < undeadsToSummon)
        }
        yell("Arise, slaves!")
    }

    override fun notice() {
        super.notice()
        yell("How dare you!")
    }

    override fun description(): String {
        return "The last king of dwarves was known for his deep understanding of processes of life and death. " +
                "He has persuaded members of his court to participate in a ritual, that should have granted them " +
                "eternal youthfulness. In the end he was the only one, who got it - and an army of undead " +
                "as a bonus."
    }

    companion object {
        private const val MAX_ARMY_SIZE = 5
        private const val PEDESTAL = "pedestal"
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(ToxicGas::class.java)
            RESISTANCES.add(Death::class.java)
            RESISTANCES.add(ScrollOfPsionicBlast::class.java)
            RESISTANCES.add(WandOfDisintegration::class.java)
        }

        init {
            IMMUNITIES.add(Paralysis::class.java)
            IMMUNITIES.add(Vertigo::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    class Undead : Mob() {
        protected fun onAdd() {
            count++
            super.onAdd()
        }

        protected fun onRemove() {
            count--
            super.onRemove()
        }

        fun damageRoll(): Int {
            return Random.NormalIntRange(12, 16)
        }

        fun attackSkill(target: Char?): Int {
            return 16
        }

        fun attackProc(enemy: Char?, damage: Int): Int {
            if (Random.Int(MAX_ARMY_SIZE) === 0) {
                Buff.prolong(enemy, Paralysis::class.java, 1)
            }
            return damage
        }

        fun damage(dmg: Int, src: Any) {
            super.damage(dmg, src)
            if (src is ToxicGas) {
                (src as ToxicGas).clear(pos)
            }
        }

        override fun die(cause: Any?) {
            super.die(cause)
            if (Dungeon.visible.get(pos)) {
                Sample.INSTANCE.play(Assets.SND_BONES)
            }
        }

        fun dr(): Int {
            return 5
        }

        fun defenseVerb(): String {
            return "blocked"
        }

        override fun description(): String {
            return "These undead dwarves, risen by the will of the King of Dwarves, were members of his court. " +
                    "They appear as skeletons with a stunning amount of facial hair."
        }

        companion object {
            var count = 0
            private val IMMUNITIES = HashSet<Class<*>>()

            init {
                IMMUNITIES.add(Death::class.java)
                IMMUNITIES.add(Paralysis::class.java)
            }
        }

        fun immunities(): HashSet<Class<*>> {
            return IMMUNITIES
        }

        init {
            name = "undead dwarf"
            spriteClass = UndeadSprite::class.java
            HT = 28
            HP = HT
            defenseSkill = 15
            EXP = 0
            state = WANDERING
        }
    }

    init {
        name = if (Dungeon.depth === Statistics.deepestFloor) "King of Dwarves" else "undead King of Dwarves"
        spriteClass = KingSprite::class.java
        HT = 300
        HP = HT
        EXP = 40
        defenseSkill = 25
        Undead.count = 0
    }
}