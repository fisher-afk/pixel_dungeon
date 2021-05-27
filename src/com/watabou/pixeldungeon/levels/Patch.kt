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
package com.watabou.pixeldungeon.levels

import com.watabou.utils.Random

object Patch {
    private var cur = BooleanArray(Level.LENGTH)
    private var off = BooleanArray(Level.LENGTH)
    fun generate(seed: Float, nGen: Int): BooleanArray {
        val w: Int = Level.WIDTH
        val h: Int = Level.HEIGHT
        for (i in 0 until Level.LENGTH) {
            off[i] = Random.Float() < seed
        }
        for (i in 0 until nGen) {
            for (y in 1 until h - 1) {
                for (x in 1 until w - 1) {
                    val pos = x + y * w
                    var count = 0
                    if (off[pos - w - 1]) {
                        count++
                    }
                    if (off[pos - w]) {
                        count++
                    }
                    if (off[pos - w + 1]) {
                        count++
                    }
                    if (off[pos - 1]) {
                        count++
                    }
                    if (off[pos + 1]) {
                        count++
                    }
                    if (off[pos + w - 1]) {
                        count++
                    }
                    if (off[pos + w]) {
                        count++
                    }
                    if (off[pos + w + 1]) {
                        count++
                    }
                    if (!off[pos] && count >= 5) {
                        cur[pos] = true
                    } else if (off[pos] && count >= 4) {
                        cur[pos] = true
                    } else {
                        cur[pos] = false
                    }
                }
            }
            val tmp = cur
            cur = off
            off = tmp
        }
        return off
    }
}