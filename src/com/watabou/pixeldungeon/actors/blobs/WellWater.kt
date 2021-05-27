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
package com.watabou.pixeldungeon.actors.blobs

import com.watabou.pixeldungeon.Dungeon

class WellWater : Blob() {
    protected var pos = 0
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until LENGTH) {
            if (cur.get(i) > 0) {
                pos = i
                break
            }
        }
    }

    protected override fun evolve() {
        off.get(pos) = cur.get(pos)
        volume = off.get(pos)
        if (Dungeon.visible.get(pos)) {
            if (this is WaterOfAwareness) {
                Journal.add(Feature.WELL_OF_AWARENESS)
            } else if (this is WaterOfHealth) {
                Journal.add(Feature.WELL_OF_HEALTH)
            } else if (this is WaterOfTransmutation) {
                Journal.add(Feature.WELL_OF_TRANSMUTATION)
            }
        }
    }

    protected fun affect(): Boolean {
        var heap: Heap
        return if (pos == Dungeon.hero.pos && affectHero(Dungeon.hero)) {
            cur.get(pos) = 0
            off.get(pos) = cur.get(pos)
            volume = off.get(pos)
            true
        } else if (Dungeon.level.heaps.get(pos).also { heap = it } != null) {
            val oldItem: Item = heap.peek()
            val newItem: Item? = affectItem(oldItem)
            if (newItem != null) {
                if (newItem === oldItem) {
                } else if (oldItem.quantity() > 1) {
                    oldItem.quantity(oldItem.quantity() - 1)
                    heap.drop(newItem)
                } else {
                    heap.replace(oldItem, newItem)
                }
                heap.sprite.link()
                cur.get(pos) = 0
                off.get(pos) = cur.get(pos)
                volume = off.get(pos)
                true
            } else {
                var newPlace: Int
                do {
                    newPlace = pos + Level.NEIGHBOURS8.get(Random.Int(8))
                } while (!Level.passable.get(newPlace) && !Level.avoid.get(newPlace))
                Dungeon.level.drop(heap.pickUp(), newPlace).sprite.drop(pos)
                false
            }
        } else {
            false
        }
    }

    protected fun affectHero(hero: Hero?): Boolean {
        return false
    }

    protected fun affectItem(item: Item?): Item? {
        return null
    }

    override fun seed(cell: Int, amount: Int) {
        cur.get(pos) = 0
        pos = cell
        cur.get(pos) = amount
        volume = cur.get(pos)
    }

    companion object {
        fun affectCell(cell: Int) {
            val waters = arrayOf<Class<*>>(
                WaterOfHealth::class.java,
                WaterOfAwareness::class.java,
                WaterOfTransmutation::class.java
            )
            for (waterClass in waters) {
                val water = Dungeon.level.blobs.get(waterClass) as WellWater
                if (water != null && water.volume > 0 && water.pos == cell &&
                    water.affect()
                ) {
                    Level.set(cell, Terrain.EMPTY_WELL)
                    GameScene.updateMap(cell)
                    return
                }
            }
        }
    }
}