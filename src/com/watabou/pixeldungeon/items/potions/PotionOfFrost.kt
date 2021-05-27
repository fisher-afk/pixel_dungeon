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

class PotionOfFrost : Potion() {
    override fun shatter(cell: Int) {
        PathFinder.buildDistanceMap(cell, BArray.not(Level.losBlocking, null), DISTANCE)
        val fire: Fire = Dungeon.level.blobs.get(Fire::class.java) as Fire
        var visible = false
        for (i in 0 until Level.LENGTH) {
            if (PathFinder.distance.get(i) < Int.MAX_VALUE) {
                visible = Freezing.affect(i, fire) || visible
            }
        }
        if (visible) {
            splash(cell)
            Sample.INSTANCE.play(Assets.SND_SHATTER)
            setKnown()
        }
    }

    fun desc(): String {
        return "Upon exposure to open air, this chemical will evaporate into a freezing cloud, causing " +
                "any creature that contacts it to be frozen in place, unable to act and move."
    }

    override fun price(): Int {
        return if (isKnown()) 50 * quantity else super.price()
    }

    companion object {
        private const val DISTANCE = 2
    }

    init {
        name = "Potion of Frost"
    }
}