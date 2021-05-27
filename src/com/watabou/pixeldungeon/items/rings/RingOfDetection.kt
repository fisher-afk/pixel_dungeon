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
package com.watabou.pixeldungeon.items.rings

import com.watabou.pixeldungeon.Dungeon

class RingOfDetection : Ring() {
    override fun doEquip(hero: Hero): Boolean {
        return if (super.doEquip(hero)) {
            Dungeon.hero.search(false)
            true
        } else {
            false
        }
    }

    protected override fun buff(): RingBuff {
        return Detection()
    }

    override fun desc(): String {
        return if (isKnown()) "Wearing this ring will allow the wearer to notice hidden secrets - " +
                "traps and secret doors - without taking time to search. Degraded rings of detection " +
                "will dull your senses, making it harder to notice secrets even when actively searching for them." else super.desc()
    }

    inner class Detection : RingBuff()

    init {
        name = "Ring of Detection"
    }
}