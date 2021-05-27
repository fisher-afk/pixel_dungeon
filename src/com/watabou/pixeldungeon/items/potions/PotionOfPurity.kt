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
package com.watabou.pixeldungeon.items.potions

import com.watabou.noosa.audio.Sample

class PotionOfPurity : Potion() {
    override fun shatter(cell: Int) {
        PathFinder.buildDistanceMap(cell, BArray.not(Level.losBlocking, null), DISTANCE)
        var procd = false
        val blobs: Array<Blob> = arrayOf<Blob>(
            Dungeon.level.blobs.get(ToxicGas::class.java),
            Dungeon.level.blobs.get(ParalyticGas::class.java)
        )
        for (j in blobs.indices) {
            val blob: Blob = blobs[j] ?: continue
            for (i in 0 until Level.LENGTH) {
                if (PathFinder.distance.get(i) < Int.MAX_VALUE) {
                    val value: Int = blob.cur.get(i)
                    if (value > 0) {
                        blob.cur.get(i) = 0
                        blob.volume -= value
                        procd = true
                        if (Dungeon.visible.get(i)) {
                            CellEmitter.get(i).burst(Speck.factory(Speck.DISCOVER), 1)
                        }
                    }
                }
            }
        }
        val heroAffected: Boolean = PathFinder.distance.get(Dungeon.hero.pos) < Int.MAX_VALUE
        if (procd) {
            if (Dungeon.visible.get(cell)) {
                splash(cell)
                Sample.INSTANCE.play(Assets.SND_SHATTER)
            }
            setKnown()
            if (heroAffected) {
                GLog.p(TXT_FRESHNESS)
            }
        } else {
            super.shatter(cell)
            if (heroAffected) {
                GLog.i(TXT_FRESHNESS)
                setKnown()
            }
        }
    }

    protected override fun apply(hero: Hero?) {
        GLog.w(TXT_NO_SMELL)
        Buff.prolong(hero, GasesImmunity::class.java, GasesImmunity.DURATION)
        setKnown()
    }

    fun desc(): String {
        return "This reagent will quickly neutralize all harmful gases in the area of effect. " +
                "Drinking it will give you a temporary immunity to such gases."
    }

    override fun price(): Int {
        return if (isKnown()) 50 * quantity else super.price()
    }

    companion object {
        private const val TXT_FRESHNESS = "You feel uncommon freshness in the air."
        private const val TXT_NO_SMELL = "You've stopped sensing any smells!"
        private const val DISTANCE = 2
    }

    init {
        name = "Potion of Purification"
    }
}