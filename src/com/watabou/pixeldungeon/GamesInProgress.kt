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

import com.watabou.utils.Bundle

object GamesInProgress {
    private val state: HashMap<HeroClass, Info?> = HashMap<HeroClass, Info?>()
    fun check(cl: HeroClass): Info? {
        return if (state.containsKey(cl)) {
            state[cl]
        } else {
            var info: Info?
            try {
                val bundle: Bundle = Dungeon.gameBundle(Dungeon.gameFile(cl))
                info = Info()
                Dungeon.preview(info, bundle)
            } catch (e: Exception) {
                info = null
            }
            state[cl] = info
            info
        }
    }

    operator fun set(cl: HeroClass, depth: Int, level: Int, challenges: Boolean) {
        val info = Info()
        info.depth = depth
        info.level = level
        info.challenges = challenges
        state[cl] = info
    }

    fun setUnknown(cl: HeroClass) {
        state.remove(cl)
    }

    fun delete(cl: HeroClass) {
        state[cl] = null
    }

    class Info {
        var depth = 0
        var level = 0
        var challenges = false
    }
}