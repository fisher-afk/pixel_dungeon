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
package com.watabou.pixeldungeon.items.wands

import com.watabou.noosa.audio.Sample

class WandOfReach : Wand() {
    protected override fun onZap(cell: Int) {
        val reach: Int = Math.min(Ballistica.distance, power() + 4)
        var mapUpdated = false
        for (i in 1 until reach) {
            val c: Int = Ballistica.trace.get(i)
            val before: Int = Dungeon.level.map.get(c)
            val ch: Char = Actor.findChar(c)
            if (ch != null) {
                Actor.addDelayed(Swap(curUser, ch), -1)
                break
            }
            val heap: Heap = Dungeon.level.heaps.get(c)
            if (heap != null) {
                when (heap.type) {
                    HEAP -> transport(heap)
                    CHEST, MIMIC, TOMB, SKELETON -> heap.open(curUser)
                    else -> {
                    }
                }
                break
            }
            Dungeon.level.press(c, null)
            if (before == Terrain.OPEN_DOOR) {
                Level.set(c, Terrain.DOOR)
                GameScene.updateMap(c)
            } else if (Level.water.get(c)) {
                GameScene.ripple(c)
            }
            mapUpdated = mapUpdated || Dungeon.level.map.get(c) !== before
        }
        if (mapUpdated) {
            Dungeon.observe()
        }
    }

    private fun transport(heap: Heap?) {
        val item: Item = heap.pickUp()
        if (item.doPickUp(curUser)) {
            if (item is Dewdrop) {
                // Do nothing
            } else {
                if ((item is ScrollOfUpgrade || item is ScrollOfEnchantment) && (item as Scroll).isKnown() ||
                    (item is PotionOfStrength || item is PotionOfMight) && (item as Potion).isKnown()
                ) {
                    GLog.p(TXT_YOU_NOW_HAVE, item.name())
                } else {
                    GLog.i(TXT_YOU_NOW_HAVE, item.name())
                }
            }
        } else {
            Dungeon.level.drop(item, curUser.pos).sprite.drop()
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.force(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "This utility wand can be used to grab objects from a distance and to switch places with enemies. " +
                "Waves of magic force radiated from it will affect all cells on their way triggering traps, " +
                "trampling high vegetation, opening closed doors and closing open ones."
    }

    companion object {
        private const val TXT_YOU_NOW_HAVE = "You have magically transported %s into your backpack"
    }

    init {
        name = "Wand of Reach"
        hitChars = false
    }
}