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

class PotionOfLiquidFlame : Potion() {
    override fun shatter(cell: Int) {
        if (Dungeon.visible.get(cell)) {
            setKnown()
            splash(cell)
            Sample.INSTANCE.play(Assets.SND_SHATTER)
        }
        GameScene.add(Blob.seed(cell, 2, Fire::class.java))
    }

    fun desc(): String {
        return "This flask contains an unstable compound which will burst " +
                "violently into flame upon exposure to open air."
    }

    override fun price(): Int {
        return if (isKnown()) 40 * quantity else super.price()
    }

    init {
        name = "Potion of Liquid Flame"
    }
}