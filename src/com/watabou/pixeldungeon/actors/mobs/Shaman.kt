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

import com.watabou.noosa.Camera

class Shaman : Mob(), Callback {
    fun damageRoll(): Int {
        return Random.NormalIntRange(2, 6)
    }

    fun attackSkill(target: Char?): Int {
        return 11
    }

    fun dr(): Int {
        return 4
    }

    protected fun canAttack(enemy: Char): Boolean {
        return Ballistica.cast(pos, enemy.pos, false, true) === enemy.pos
    }

    protected fun doAttack(enemy: Char): Boolean {
        return if (Level.distance(pos, enemy.pos) <= 1) {
            super.doAttack(enemy)
        } else {
            val visible = Level.fieldOfView.get(pos) || Level.fieldOfView.get(enemy.pos)
            if (visible) {
                (sprite as ShamanSprite).zap(enemy.pos)
            }
            spend(TIME_TO_ZAP)
            if (hit(this, enemy, true)) {
                var dmg: Int = Random.Int(2, 12)
                if (Level.water.get(enemy.pos) && !enemy.flying) {
                    (dmg *= 1.5f).toInt()
                }
                enemy.damage(dmg, LightningTrap.LIGHTNING)
                enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3)
                enemy.sprite.flash()
                if (enemy === Dungeon.hero) {
                    Camera.main.shake(2, 0.3f)
                    if (!enemy.isAlive()) {
                        Dungeon.fail(
                            Utils.format(
                                ResultDescriptions.MOB,
                                Utils.indefinite(name), Dungeon.depth
                            )
                        )
                        GLog.n(TXT_LIGHTNING_KILLED, name)
                    }
                }
            } else {
                enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
            }
            !visible
        }
    }

    fun call() {
        next()
    }

    override fun description(): String {
        return "The most intelligent gnolls can master shamanistic magic. Gnoll shamans prefer " +
                "battle spells to compensate for lack of might, not hesitating to use them " +
                "on those who question their status in a tribe."
    }

    companion object {
        private const val TIME_TO_ZAP = 2f
        private const val TXT_LIGHTNING_KILLED = "%s's lightning bolt killed you..."
        private val RESISTANCES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(LightningTrap.Electricity::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    init {
        name = "gnoll shaman"
        spriteClass = ShamanSprite::class.java
        HT = 18
        HP = HT
        defenseSkill = 8
        EXP = 6
        maxLvl = 14
        loot = Generator.Category.SCROLL
        lootChance = 0.33f
    }
}