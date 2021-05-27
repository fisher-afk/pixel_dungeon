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

import com.watabou.pixeldungeon.Dungeon

class Bleeding : Buff() {
    protected var level = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getInt(LEVEL)
    }

    fun set(level: Int) {
        this.level = level
    }

    override fun icon(): Int {
        return BuffIndicator.BLEEDING
    }

    override fun toString(): String {
        return "Bleeding"
    }

    override fun act(): Boolean {
        if (target.isAlive()) {
            if (Random.Int(level / 2, level).also { level = it } > 0) {
                target.damage(level, this)
                if (target.sprite.visible) {
                    Splash.at(
                        target.sprite.center(), -PointF.PI / 2, PointF.PI / 6,
                        target.sprite.blood(), Math.min(10 * level / target.HT, 10)
                    )
                }
                if (target === Dungeon.hero && !target.isAlive()) {
                    Dungeon.fail(Utils.format(ResultDescriptions.BLEEDING, Dungeon.depth))
                    GLog.n("You bled to death...")
                }
                spend(TICK)
            } else {
                detach()
            }
        } else {
            detach()
        }
        return true
    }

    companion object {
        private const val LEVEL = "level"
    }
}