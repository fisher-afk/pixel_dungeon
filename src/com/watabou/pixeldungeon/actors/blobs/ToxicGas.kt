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
package com.watabou.pixeldungeon.actors.blobs

import com.watabou.pixeldungeon.Badges

class ToxicGas : Blob(), Hero.Doom {
    protected override fun evolve() {
        super.evolve()
        val levelDamage: Int = 5 + Dungeon.depth * 5
        var ch: Char
        for (i in 0 until LENGTH) {
            if (cur.get(i) > 0 && Actor.findChar(i).also { ch = it } != null) {
                var damage: Int = (ch.HT + levelDamage) / 40
                if (Random.Int(40) < (ch.HT + levelDamage) % 40) {
                    damage++
                }
                ch.damage(damage, this)
            }
        }
        val blob: Blob = Dungeon.level.blobs.get(ParalyticGas::class.java)
        if (blob != null) {
            val par: IntArray = blob.cur
            for (i in 0 until LENGTH) {
                val t: Int = cur.get(i)
                val p = par[i]
                if (p >= t) {
                    volume -= t
                    cur.get(i) = 0
                } else {
                    blob.volume -= p
                    par[i] = 0
                }
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.TOXIC), 0.6f)
    }

    override fun tileDesc(): String {
        return "A greenish cloud of toxic gas is swirling here."
    }

    fun onDeath() {
        Badges.validateDeathFromGas()
        Dungeon.fail(Utils.format(ResultDescriptions.GAS, Dungeon.depth))
        GLog.n("You died from a toxic gas..")
    }
}