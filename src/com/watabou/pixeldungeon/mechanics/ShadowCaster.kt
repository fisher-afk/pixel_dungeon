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
package com.watabou.pixeldungeon.mechanics

import com.watabou.pixeldungeon.levels.Level

object ShadowCaster {
    private const val MAX_DISTANCE = 8
    private val WIDTH: Int = Level.WIDTH
    private val HEIGHT: Int = Level.HEIGHT
    private var distance = 0
    private var limits: IntArray?
    private var losBlocking: BooleanArray
    private var fieldOfView: BooleanArray
    private var rounding: Array<IntArray?>
    private val obs = Obstacles()
    fun castShadow(x: Int, y: Int, fieldOfView: BooleanArray, distance: Int) {
        losBlocking = Level.losBlocking
        ShadowCaster.distance = distance
        limits = rounding[distance]
        ShadowCaster.fieldOfView = fieldOfView
        Arrays.fill(fieldOfView, false)
        fieldOfView[y * WIDTH + x] = true
        scanSector(x, y, +1, +1, 0, 0)
        scanSector(x, y, -1, +1, 0, 0)
        scanSector(x, y, +1, -1, 0, 0)
        scanSector(x, y, -1, -1, 0, 0)
        scanSector(x, y, 0, 0, +1, +1)
        scanSector(x, y, 0, 0, -1, +1)
        scanSector(x, y, 0, 0, +1, -1)
        scanSector(x, y, 0, 0, -1, -1)
    }

    private fun scanSector(cx: Int, cy: Int, m1: Int, m2: Int, m3: Int, m4: Int) {
        obs.reset()
        for (p in 1..distance) {
            val dq2 = 0.5f / p
            val pp = limits!![p]
            for (q in 0..pp) {
                val x = cx + q * m1 + p * m3
                val y = cy + p * m2 + q * m4
                if (y >= 0 && y < HEIGHT && x >= 0 && x < WIDTH) {
                    val a0 = q.toFloat() / p
                    val a1 = a0 - dq2
                    val a2 = a0 + dq2
                    val pos = y * WIDTH + x
                    if (obs.isBlocked(a0) && obs.isBlocked(a1) && obs.isBlocked(a2)) {
                        // Do nothing					
                    } else {
                        fieldOfView[pos] = true
                    }
                    if (losBlocking[pos]) {
                        obs.add(a1, a2)
                    }
                }
            }
            obs.nextRow()
        }
    }

    private class Obstacles {
        private var length = 0
        private var limit = 0
        fun reset() {
            length = 0
            limit = 0
        }

        fun add(o1: Float, o2: Float) {
            if (length > limit && o1 <= a2[length - 1]) {

                // Merging several blocking cells
                a2[length - 1] = o2
            } else {
                a1[length] = o1
                a2[length++] = o2
            }
        }

        fun isBlocked(a: Float): Boolean {
            for (i in 0 until limit) {
                if (a >= a1[i] && a <= a2[i]) {
                    return true
                }
            }
            return false
        }

        fun nextRow() {
            limit = length
        }

        companion object {
            private const val SIZE = (MAX_DISTANCE + 1) * (MAX_DISTANCE + 1) / 2
            private val a1 = FloatArray(SIZE)
            private val a2 = FloatArray(SIZE)
        }
    }

    init {
        rounding = arrayOfNulls(MAX_DISTANCE + 1)
        for (i in 1..MAX_DISTANCE) {
            rounding[i] = IntArray(i + 1)
            for (j in 1..i) {
                rounding[i]!![j] = Math.min(j.toLong(), Math.round(i * Math.cos(Math.asin(j / (i + 0.5)))))
                    .toInt()
            }
        }
    }
}