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
package com.watabou.pixeldungeon.levels.traps

import com.watabou.noosa.Camera

object LightningTrap {
    private const val name = "lightning trap"

    // 00x66CCEE
    fun trigger(pos: Int, ch: Char?) {
        if (ch != null) {
            ch.damage(Math.max(1, Random.Int(ch.HP / 3, 2 * ch.HP / 3)), LIGHTNING)
            if (ch === Dungeon.hero) {
                Camera.main.shake(2, 0.3f)
                if (!ch.isAlive()) {
                    Dungeon.fail(Utils.format(ResultDescriptions.TRAP, name, Dungeon.depth))
                    GLog.n("You were killed by a discharge of a lightning trap...")
                } else {
                    (ch as Hero).belongings.charge(false)
                }
            }
            val points = IntArray(2)
            points[0] = pos - Level.WIDTH
            points[1] = pos + Level.WIDTH
            ch.sprite.parent.add(Lightning(points, 2, null))
            points[0] = pos - 1
            points[1] = pos + 1
            ch.sprite.parent.add(Lightning(points, 2, null))
        }
        CellEmitter.center(pos).burst(SparkParticle.FACTORY, Random.IntRange(3, 4))
    }

    val LIGHTNING = Electricity()

    class Electricity
}