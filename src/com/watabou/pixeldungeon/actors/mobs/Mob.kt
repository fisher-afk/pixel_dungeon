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

abstract class Mob : Char() {
    var SLEEPEING: AiState = Sleeping()
    var HUNTING: AiState = Hunting()
    var WANDERING: AiState = Wandering()
    var FLEEING: AiState = Fleeing()
    var PASSIVE: AiState = Passive()
    var state = SLEEPEING
    var spriteClass: Class<out CharSprite?>? = null
    protected var target = -1
    protected var defenseSkill = 0
    protected var EXP = 1
    protected var maxLvl = 30
    protected var enemy: Char? = null
    protected var enemySeen = false
    protected var alerted = false
    var hostile = true
    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        if (state === SLEEPEING) {
            bundle.put(STATE, Sleeping.Companion.TAG)
        } else if (state === WANDERING) {
            bundle.put(STATE, Wandering.Companion.TAG)
        } else if (state === HUNTING) {
            bundle.put(STATE, Hunting.Companion.TAG)
        } else if (state === FLEEING) {
            bundle.put(STATE, Fleeing.Companion.TAG)
        } else if (state === PASSIVE) {
            bundle.put(STATE, Passive.Companion.TAG)
        }
        bundle.put(TARGET, target)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        val state: String = bundle.getString(STATE)
        if (state == Sleeping.Companion.TAG) {
            this.state = SLEEPEING
        } else if (state == Wandering.Companion.TAG) {
            this.state = WANDERING
        } else if (state == Hunting.Companion.TAG) {
            this.state = HUNTING
        } else if (state == Fleeing.Companion.TAG) {
            this.state = FLEEING
        } else if (state == Passive.Companion.TAG) {
            this.state = PASSIVE
        }
        target = bundle.getInt(TARGET)
    }

    fun sprite(): CharSprite? {
        var sprite: CharSprite? = null
        try {
            sprite = spriteClass!!.newInstance()
        } catch (e: Exception) {
        }
        return sprite
    }

    protected fun act(): Boolean {
        super.act()
        val justAlerted = alerted
        alerted = false
        sprite.hideAlert()
        if (paralysed) {
            enemySeen = false
            spend(TICK)
            return true
        }
        enemy = chooseEnemy()
        val enemyInFOV = enemy != null && enemy.isAlive() &&
                Level.fieldOfView.get(enemy.pos) && enemy.invisible <= 0
        return state.act(enemyInFOV, justAlerted)
    }

    protected fun chooseEnemy(): Char {
        if (buff(Amok::class.java) != null) {
            if (enemy === Dungeon.hero || enemy == null) {
                val enemies = HashSet<Mob>()
                for (mob in Dungeon.level.mobs) {
                    if (mob !== this && Level.fieldOfView.get(mob.pos)) {
                        enemies.add(mob)
                    }
                }
                if (enemies.size > 0) {
                    return Random.element(enemies)
                }
            }
        }
        val terror: Terror? = buff(Terror::class.java) as Terror?
        if (terror != null) {
            val source = Actor.findById(terror.`object`) as Char
            if (source != null) {
                return source
            }
        }
        return if (enemy != null && enemy.isAlive()) enemy!! else Dungeon.hero
    }

    protected fun moveSprite(from: Int, to: Int): Boolean {
        return if (sprite.isVisible() && (Dungeon.visible.get(from) || Dungeon.visible.get(to))) {
            sprite.move(from, to)
            true
        } else {
            sprite.place(to)
            true
        }
    }

    fun add(buff: Buff?) {
        super.add(buff)
        if (buff is Amok) {
            if (sprite != null) {
                sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE)
            }
            state = HUNTING
        } else if (buff is Terror) {
            state = FLEEING
        } else if (buff is Sleep) {
            if (sprite != null) {
                Flare(4, 32).color(0x44ffff, true).show(sprite, 2f)
            }
            state = SLEEPEING
            postpone(Sleep.SWS)
        }
    }

    fun remove(buff: Buff?) {
        super.remove(buff)
        if (buff is Terror) {
            sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE)
            state = HUNTING
        }
    }

    protected fun canAttack(enemy: Char?): Boolean {
        return Level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy)
    }

    protected fun getCloser(target: Int): Boolean {
        if (rooted) {
            return false
        }
        val step: Int = Dungeon.findPath(
            this, pos, target,
            Level.passable,
            Level.fieldOfView
        )
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }

    protected fun getFurther(target: Int): Boolean {
        val step: Int = Dungeon.flee(
            this, pos, target,
            Level.passable,
            Level.fieldOfView
        )
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }

    fun move(step: Int) {
        super.move(step)
        if (!flying) {
            Dungeon.level.mobPress(this)
        }
    }

    protected fun attackDelay(): Float {
        return 1f
    }

    protected fun doAttack(enemy: Char?): Boolean {
        val visible: Boolean = Dungeon.visible.get(pos)
        if (visible) {
            sprite.attack(enemy.pos)
        } else {
            attack(enemy)
        }
        spend(attackDelay())
        return !visible
    }

    fun onAttackComplete() {
        attack(enemy)
        super.onAttackComplete()
    }

    fun defenseSkill(enemy: Char?): Int {
        return if (enemySeen && !paralysed) defenseSkill else 0
    }

    fun defenseProc(enemy: Char, damage: Int): Int {
        var damage = damage
        if (!enemySeen && enemy === Dungeon.hero && (enemy as Hero).subClass === HeroSubClass.ASSASSIN) {
            damage += Random.Int(1, damage)
            Wound.hit(this)
        }
        return damage
    }

    fun aggro(ch: Char?) {
        enemy = ch
    }

    fun damage(dmg: Int, src: Any?) {
        Terror.recover(this)
        if (state === SLEEPEING) {
            state = WANDERING
        }
        alerted = true
        super.damage(dmg, src)
    }

    fun destroy() {
        super.destroy()
        Dungeon.level.mobs.remove(this)
        if (Dungeon.hero.isAlive()) {
            if (hostile) {
                Statistics.enemiesSlain++
                Badges.validateMonstersSlain()
                Statistics.qualifiedForNoKilling = false
                if (Dungeon.nightMode) {
                    Statistics.nightHunt++
                } else {
                    Statistics.nightHunt = 0
                }
                Badges.validateNightHunter()
            }
            val exp = exp()
            if (exp > 0) {
                Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, TXT_EXP, exp)
                Dungeon.hero.earnExp(exp)
            }
        }
    }

    fun exp(): Int {
        return if (Dungeon.hero.lvl <= maxLvl) EXP else 0
    }

    fun die(cause: Any?) {
        super.die(cause)
        if (Dungeon.hero.lvl <= maxLvl + 2) {
            dropLoot()
        }
        if (Dungeon.hero.isAlive() && !Dungeon.visible.get(pos)) {
            GLog.i(TXT_DIED)
        }
    }

    protected var loot: Any? = null
    protected var lootChance = 0f
    protected fun dropLoot() {
        if (loot != null && Random.Float() < lootChance) {
            var item: Item? = null
            item = if (loot is Generator.Category) {
                Generator.random(loot as Generator.Category?)
            } else if (loot is Class<*>) {
                Generator.random(loot as Class<out Item?>?)
            } else {
                loot as Item?
            }
            Dungeon.level.drop(item, pos).sprite.drop()
        }
    }

    fun reset(): Boolean {
        return false
    }

    fun beckon(cell: Int) {
        notice()
        if (state !== HUNTING) {
            state = WANDERING
        }
        target = cell
    }

    fun description(): String {
        return "Real description is coming soon!"
    }

    fun notice() {
        sprite.showAlert()
    }

    fun yell(str: String?) {
        GLog.n("%s: \"%s\" ", name, str)
    }

    interface AiState {
        fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean
        fun status(): String?
    }

    private inner class Sleeping : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            if (enemyInFOV && Random.Int(distance(enemy) + enemy.stealth() + if (enemy.flying) 2 else 0) === 0) {
                enemySeen = true
                notice()
                state = HUNTING
                target = enemy.pos
                if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                    for (mob in Dungeon.level.mobs) {
                        if (mob !== this@Mob) {
                            mob.beckon(target)
                        }
                    }
                }
                spend(TIME_TO_WAKE_UP)
            } else {
                enemySeen = false
                spend(TICK)
            }
            return true
        }

        override fun status(): String {
            return Utils.format("This %s is sleeping", name)
        }

        companion object {
            const val TAG = "SLEEPING"
        }
    }

    private inner class Wandering : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            if (enemyInFOV && (justAlerted || Random.Int(distance(enemy) / 2 + enemy.stealth()) === 0)) {
                enemySeen = true
                notice()
                state = HUNTING
                target = enemy.pos
            } else {
                enemySeen = false
                val oldPos: Int = pos
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed())
                    return moveSprite(oldPos, pos)
                } else {
                    target = Dungeon.level.randomDestination()
                    spend(TICK)
                }
            }
            return true
        }

        override fun status(): String {
            return Utils.format("This %s is wandering", name)
        }

        companion object {
            const val TAG = "WANDERING"
        }
    }

    private inner class Hunting : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            return if (enemyInFOV && canAttack(enemy)) {
                doAttack(enemy)
            } else {
                if (enemyInFOV) {
                    target = enemy.pos
                }
                val oldPos: Int = pos
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed())
                    moveSprite(oldPos, pos)
                } else {
                    spend(TICK)
                    state = WANDERING
                    target = Dungeon.level.randomDestination()
                    true
                }
            }
        }

        override fun status(): String {
            return Utils.format("This %s is hunting", name)
        }

        companion object {
            const val TAG = "HUNTING"
        }
    }

    protected inner class Fleeing : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            if (enemyInFOV) {
                target = enemy.pos
            }
            val oldPos: Int = pos
            return if (target != -1 && getFurther(target)) {
                spend(1 / speed())
                moveSprite(oldPos, pos)
            } else {
                spend(TICK)
                nowhereToRun()
                true
            }
        }

        protected fun nowhereToRun() {}
        override fun status(): String {
            return Utils.format("This %s is fleeing", name)
        }

        companion object {
            const val TAG = "FLEEING"
        }
    }

    private inner class Passive : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = false
            spend(TICK)
            return true
        }

        override fun status(): String {
            return Utils.format("This %s is passive", name)
        }

        companion object {
            const val TAG = "PASSIVE"
        }
    }

    companion object {
        private const val TXT_DIED = "You hear something died in the distance"
        protected const val TXT_ECHO = "echo of "
        protected const val TXT_NOTICE1 = "?!"
        protected const val TXT_RAGE = "#$%^"
        protected const val TXT_EXP = "%+dEXP"
        protected const val TIME_TO_WAKE_UP = 1f
        private const val STATE = "state"
        private const val TARGET = "target"
    }
}