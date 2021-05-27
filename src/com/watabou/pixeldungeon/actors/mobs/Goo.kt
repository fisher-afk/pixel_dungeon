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

import com.watabou.pixeldungeon.Badges

class Goo : Mob() {
    private var pumpedUp = false
    private var jumped = false
    fun damageRoll(): Int {
        return if (pumpedUp) {
            Random.NormalIntRange(5, 30)
        } else {
            Random.NormalIntRange(2, 12)
        }
    }

    fun attackSkill(target: Char?): Int {
        return if (pumpedUp && !jumped) 30 else 15
    }

    fun dr(): Int {
        return 2
    }

    override fun act(): Boolean {
        if (Level.water.get(pos) && HP < HT) {
            sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
            HP++
        }
        return super.act()
    }

    protected override fun canAttack(enemy: Char?): Boolean {
        return if (pumpedUp) distance(enemy) <= 2 else super.canAttack(enemy)
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(3) === 0) {
            Buff.affect(enemy, Ooze::class.java)
            enemy.sprite.burst(0x000000, 5)
        }
        return damage
    }

    protected fun doAttack(enemy: Char): Boolean {
        return if (pumpedUp) {
            if (Level.adjacent(pos, enemy.pos)) {

                // Pumped up attack WITHOUT accuracy penalty
                jumped = false
                super.doAttack(enemy)
            } else {

                // Pumped up attack WITH accuracy penalty
                jumped = true
                if (Ballistica.cast(pos, enemy.pos, false, true) === enemy.pos) {
                    val dest: Int = Ballistica.trace.get(Ballistica.distance - 2)
                    val afterJump: Callback = object : Callback() {
                        fun call() {
                            move(dest)
                            Dungeon.level.mobPress(this@Goo)
                            super@Goo.doAttack(enemy)
                        }
                    }
                    if (Dungeon.visible.get(pos) || Dungeon.visible.get(dest)) {
                        sprite.jump(pos, dest, afterJump)
                        false
                    } else {
                        afterJump.call()
                        true
                    }
                } else {
                    sprite.idle()
                    pumpedUp = false
                    true
                }
            }
        } else if (Random.Int(3) > 0) {

            // Normal attack
            super.doAttack(enemy)
        } else {

            // Pumping up
            pumpedUp = true
            spend(PUMP_UP_DELAY)
            (sprite as GooSprite).pumpUp()
            if (Dungeon.visible.get(pos)) {
                sprite.showStatus(CharSprite.NEGATIVE, "!!!")
                GLog.n("Goo is pumping itself up!")
            }
            true
        }
    }

    fun attack(enemy: Char?): Boolean {
        val result: Boolean = super.attack(enemy)
        pumpedUp = false
        return result
    }

    protected override fun getCloser(target: Int): Boolean {
        pumpedUp = false
        return super.getCloser(target)
    }

    override fun move(step: Int) {
        (Dungeon.level as SewerBossLevel).seal()
        super.move(step)
    }

    override fun die(cause: Any?) {
        super.die(cause)
        (Dungeon.level as SewerBossLevel).unseal()
        GameScene.bossSlain()
        Dungeon.level.drop(SkeletonKey(), pos).sprite.drop()
        Badges.validateBossSlain()
        yell("glurp... glurp...")
    }

    override fun notice() {
        super.notice()
        yell("GLURP-GLURP!")
    }

    override fun description(): String {
        return "Little known about The Goo. It's quite possible that it is not even a creature, but rather a " +
                "conglomerate of substances from the sewers that gained rudiments of free will."
    }

    companion object {
        private const val PUMP_UP_DELAY = 2f
        private val RESISTANCES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(ToxicGas::class.java)
            RESISTANCES.add(Death::class.java)
            RESISTANCES.add(ScrollOfPsionicBlast::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    init {
        name = if (Dungeon.depth === Statistics.deepestFloor) "Goo" else "spawn of Goo"
        HT = 80
        HP = HT
        EXP = 10
        defenseSkill = 12
        spriteClass = GooSprite::class.java
        loot = LloydsBeacon()
        lootChance = 0.333f
    }
}