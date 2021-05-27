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
package com.watabou.pixeldungeon.items.quest

import com.watabou.pixeldungeon.items.Item

class DwarfToken : Item() {
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun info(): String {
        return "Many dwarves and some of their larger creations carry these small pieces of metal of unknown purpose. " +
                "Maybe they are jewelry or maybe some kind of ID. Dwarves are strange folk."
    }

    override fun price(): Int {
        return quantity * 100
    }

    init {
        name = "dwarf token"
        image = ItemSpriteSheet.TOKEN
        stackable = true
        unique = true
    }
}