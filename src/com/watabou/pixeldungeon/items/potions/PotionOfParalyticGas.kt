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

class PotionOfParalyticGas : Potion() {
    override fun shatter(cell: Int) {
        if (Dungeon.visible.get(cell)) {
            setKnown()
            splash(cell)
            Sample.INSTANCE.play(Assets.SND_SHATTER)
        }
        GameScene.add(Blob.seed(cell, 1000, ParalyticGas::class.java))
    }

    fun desc(): String {
        return "Upon exposure to open air, the liquid in this flask will vaporize " +
                "into a numbing yellow haze. Anyone who inhales the cloud will be paralyzed " +
                "instantly, unable to move for some time after the cloud dissipates. This " +
                "item can be thrown at distant enemies to catch them within the effect of the gas."
    }

    override fun price(): Int {
        return if (isKnown()) 40 * quantity else super.price()
    }

    init {
        name = "Potion of Paralytic Gas"
    }
}