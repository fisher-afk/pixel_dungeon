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
package com.watabou.pixeldungeon.levels.traps

import com.watabou.pixeldungeon.Dungeon

object SummoningTrap {
    private const val DELAY = 2f
    private val DUMMY: Mob = object : Mob() {}

    // 0x770088
    fun trigger(pos: Int, c: Char?) {
        if (Dungeon.bossLevel()) {
            return
        }
        if (c != null) {
            Actor.occupyCell(c)
        }
        var nMobs = 1
        if (Random.Int(2) === 0) {
            nMobs++
            if (Random.Int(2) === 0) {
                nMobs++
            }
        }
        val candidates = ArrayList<Int>()
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val p: Int = pos + Level.NEIGHBOURS8.get(i)
            if (Actor.findChar(p) == null && (Level.passable.get(p) || Level.avoid.get(p))) {
                candidates.add(p)
            }
        }
        val respawnPoints = ArrayList<Int>()
        while (nMobs > 0 && candidates.size > 0) {
            val index: Int = Random.index(candidates)
            DUMMY.pos = candidates[index]
            Actor.occupyCell(DUMMY)
            respawnPoints.add(candidates.removeAt(index))
            nMobs--
        }
        for (point in respawnPoints) {
            val mob: Mob = Bestiary.mob(Dungeon.depth)
            mob.state = mob.WANDERING
            GameScene.add(mob, DELAY)
            WandOfBlink.appear(mob, point)
        }
    }
}