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
package com.watabou.pixeldungeon.items.keys

import com.watabou.pixeldungeon.Dungeon

class IronKey : Key() {
    fun collect(bag: Bag?): Boolean {
        val result: Boolean = super.collect(bag)
        if (result && depth === Dungeon.depth && Dungeon.hero != null) {
            Dungeon.hero.belongings.countIronKeys()
        }
        return result
    }

    fun onDetach() {
        if (depth === Dungeon.depth) {
            Dungeon.hero.belongings.countIronKeys()
        }
    }

    override fun toString(): String {
        return Utils.format(TXT_FROM_DEPTH, depth)
    }

    fun info(): String {
        return "The notches on this ancient iron key are well worn; its leather lanyard " +
                "is battered by age. What door might it open?"
    }

    companion object {
        private const val TXT_FROM_DEPTH = "iron key from depth %d"
        var curDepthQuantity = 0
    }

    init {
        name = "iron key"
        image = ItemSpriteSheet.IRON_KEY
    }
}