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

import com.watabou.pixeldungeon.Dungeon

class WandOfDisintegration : Wand() {
    protected override fun onZap(cell: Int) {
        var terrainAffected = false
        val level: Int = power()
        val maxDistance = distance()
        Ballistica.distance = Math.min(Ballistica.distance, maxDistance)
        val chars = ArrayList<Char>()
        for (i in 1 until Ballistica.distance) {
            val c: Int = Ballistica.trace.get(i)
            var ch: Char
            if (Actor.findChar(c).also { ch = it } != null) {
                chars.add(ch)
            }
            val terr: Int = Dungeon.level.map.get(c)
            if (terr == Terrain.DOOR || terr == Terrain.SIGN) {
                Dungeon.level.destroy(c)
                GameScene.updateMap(c)
                terrainAffected = true
            } else if (terr == Terrain.HIGH_GRASS) {
                Level.set(c, Terrain.GRASS)
                GameScene.updateMap(c)
                terrainAffected = true
            }
            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2))
        }
        if (terrainAffected) {
            Dungeon.observe()
        }
        val lvl = level + chars.size
        val dmgMax = 8 + lvl * lvl / 3
        for (ch in chars) {
            ch.damage(Random.NormalIntRange(lvl, dmgMax), this)
            ch.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2))
            ch.sprite.flash()
        }
    }

    private fun distance(): Int {
        return level() + 4
    }

    protected override fun fx(cell: Int, callback: Callback) {
        var cell = cell
        cell = Ballistica.trace.get(Math.min(Ballistica.distance, distance()) - 1)
        curUser.sprite.parent.add(DeathRay(curUser.sprite.center(), DungeonTilemap.tileCenterToWorld(cell)))
        callback.call()
    }

    fun desc(): String {
        return "This wand emits a beam of destructive energy, which pierces all creatures in its way. " +
                "The more targets it hits, the more damage it inflicts to each of them."
    }

    init {
        name = "Wand of Disintegration"
        hitChars = false
    }
}