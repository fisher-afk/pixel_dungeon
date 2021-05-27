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

import com.watabou.pixeldungeon.Badges

class RingOfThorns : Ring() {
    protected override fun buff(): RingBuff {
        return Thorns()
    }

    override fun random(): Item {
        level(+1)
        return this
    }

    fun doPickUp(hero: Hero?): Boolean {
        identify()
        Badges.validateRingOfThorns()
        Badges.validateItemLevelAquired(this)
        return super.doPickUp(hero)
    }

    val isUpgradable: Boolean
        get() = false

    fun use() {
        // Do nothing (it can't degrade)
    }

    override fun desc(): String {
        return if (isKnown()) "Though this ring doesn't provide real thorns, an enemy that attacks you " +
                "will itself be wounded by a fraction of the damage that it inflicts. " +
                "Upgrading this ring won't give any additional bonuses." else super.desc()
    }

    inner class Thorns : RingBuff()

    init {
        name = "Ring of Thorns"
    }
}