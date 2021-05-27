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
package com.watabou.pixeldungeon.actors.buffs

import com.watabou.pixeldungeon.Badges

class Burning : Buff(), Hero.Doom {
    private var left = 0f
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }

    override fun act(): Boolean {
        if (target.isAlive()) {
            if (target is Hero) {
                Buff.prolong(target, Light::class.java, TICK * 1.01f)
            }
            target.damage(Random.Int(1, 5), this)
            if (target is Hero) {
                var item: Item = (target as Hero).belongings.randomUnequipped()
                if (item is Scroll) {
                    item = item.detach((target as Hero).belongings.backpack)
                    GLog.w(TXT_BURNS_UP, item.toString())
                    Heap.burnFX(target.pos)
                } else if (item is MysteryMeat) {
                    item = item.detach((target as Hero).belongings.backpack)
                    val steak = ChargrilledMeat()
                    if (!steak.collect((target as Hero).belongings.backpack)) {
                        Dungeon.level.drop(steak, target.pos).sprite.drop()
                    }
                    GLog.w(TXT_BURNS_UP, item.toString())
                    Heap.burnFX(target.pos)
                }
            } else if (target is Thief && (target as Thief).item is Scroll) {
                (target as Thief).item = null
                target.sprite.emitter().burst(ElmoParticle.FACTORY, 6)
            }
        } else {
            detach()
        }
        if (Level.flamable.get(target.pos)) {
            GameScene.add(Blob.seed(target.pos, 4, Fire::class.java))
        }
        spend(TICK)
        left -= TICK
        if (left <= 0 || Random.Float() > (2 + target.HP as Float / target.HT) / 3 ||
            Level.water.get(target.pos) && !target.flying
        ) {
            detach()
        }
        return true
    }

    fun reignite(ch: Char) {
        left = duration(ch)
    }

    override fun icon(): Int {
        return BuffIndicator.FIRE
    }

    override fun toString(): String {
        return "Burning"
    }

    fun onDeath() {
        Badges.validateDeathFromFire()
        Dungeon.fail(Utils.format(ResultDescriptions.BURNING, Dungeon.depth))
        GLog.n(TXT_BURNED_TO_DEATH)
    }

    companion object {
        private const val TXT_BURNS_UP = "%s burns up!"
        private const val TXT_BURNED_TO_DEATH = "You burned to death..."
        private const val DURATION = 8f
        private const val LEFT = "left"
        fun duration(ch: Char): Float {
            val r: Resistance = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
    }
}