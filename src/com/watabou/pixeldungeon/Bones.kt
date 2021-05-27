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
package com.watabou.pixeldungeon

import com.watabou.noosa.Game

object Bones {
    private const val BONES_FILE = "bones.dat"
    private const val LEVEL = "level"
    private const val ITEM = "item"
    private var depth = -1
    private var item: Item? = null
    fun leave() {
        item = null
        when (Random.Int(4)) {
            0 -> item = Dungeon.hero.belongings.weapon
            1 -> item = Dungeon.hero.belongings.armor
            2 -> item = Dungeon.hero.belongings.ring1
            3 -> item = Dungeon.hero.belongings.ring2
        }
        if (item == null) {
            if (Dungeon.gold > 0) {
                item = Gold(Random.IntRange(1, Dungeon.gold))
            } else {
                item = Gold(1)
            }
        }
        depth = Dungeon.depth
        val bundle = Bundle()
        bundle.put(LEVEL, depth)
        bundle.put(ITEM, item)
        try {
            val output: OutputStream = Game.instance.openFileOutput(BONES_FILE, Game.MODE_PRIVATE)
            Bundle.write(bundle, output)
            output.close()
        } catch (e: IOException) {
        }
    }

    fun get(): Item? {
        return if (depth == -1) {
            try {
                val input: InputStream = Game.instance.openFileInput(BONES_FILE)
                val bundle: Bundle = Bundle.read(input)
                input.close()
                depth = bundle.getInt(LEVEL)
                item = bundle.get(ITEM) as Item
                get()
            } catch (e: IOException) {
                null
            }
        } else {
            if (depth == Dungeon.depth) {
                Game.instance.deleteFile(BONES_FILE)
                depth = 0
                if (!item.stackable) {
                    item.cursed = true
                    item.cursedKnown = true
                    if (item.isUpgradable()) {
                        val lvl: Int = (Dungeon.depth - 1) * 3 / 5 + 1
                        if (lvl < item.level()) {
                            item.degrade(item.level() - lvl)
                        }
                        item.levelKnown = false
                    }
                }
                if (item is Ring) {
                    (item as Ring?).syncGem()
                }
                item
            } else {
                null
            }
        }
    }
}