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

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class SkeletonKey : Key() {
    fun info(): String {
        return "This key looks serious: its head is shaped like a skull. " +
                "Probably it can open some serious door."
    }

    init {
        name = "skeleton key"
        image = ItemSpriteSheet.SKELETON_KEY
    }
}