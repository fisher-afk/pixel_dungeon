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

class WandOfRegrowth : Wand() {
    protected override fun onZap(cell: Int) {
        for (i in 1 until Ballistica.distance - 1) {
            val p: Int = Ballistica.trace.get(i)
            val c: Int = Dungeon.level.map.get(p)
            if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
                Level.set(p, Terrain.GRASS)
                GameScene.updateMap(p)
                if (Dungeon.visible.get(p)) {
                    GameScene.discoverTile(p, c)
                }
            }
        }
        val c: Int = Dungeon.level.map.get(cell)
        if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO || c == Terrain.GRASS || c == Terrain.HIGH_GRASS) {
            GameScene.add(Blob.seed(cell, (power() + 2) * 20, Regrowth::class.java))
        } else {
            GLog.i("nothing happened")
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.foliage(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "\"When life ceases new life always begins to grow... The eternal cycle always remains!\""
    }

    init {
        name = "Wand of Regrowth"
    }
}