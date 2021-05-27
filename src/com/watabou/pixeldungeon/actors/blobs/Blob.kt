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

class Blob protected constructor() : Actor() {
    var volume = 0
    var cur: IntArray
    protected var off: IntArray
    var emitter: BlobEmitter? = null
    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        if (volume > 0) {
            var start: Int
            start = 0
            while (start < LENGTH) {
                if (cur[start] > 0) {
                    break
                }
                start++
            }
            var end: Int
            end = LENGTH - 1
            while (end > start) {
                if (cur[end] > 0) {
                    break
                }
                end--
            }
            bundle.put(START, start)
            bundle.put(CUR, trim(start, end + 1))
        }
    }

    private fun trim(start: Int, end: Int): IntArray {
        val len = end - start
        val copy = IntArray(len)
        System.arraycopy(cur, start, copy, 0, len)
        return copy
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        val data: IntArray = bundle.getIntArray(CUR)
        if (data != null) {
            val start: Int = bundle.getInt(START)
            for (i in data.indices) {
                cur[i + start] = data[i]
                volume += data[i]
            }
        }
        if (Level.resizingNeeded) {
            val cur = IntArray(Level.LENGTH)
            Arrays.fill(cur, 0)
            val loadedMapSize: Int = Level.loadedMapSize
            for (i in 0 until loadedMapSize) {
                System.arraycopy(this.cur, i * loadedMapSize, cur, i * Level.WIDTH, loadedMapSize)
            }
            this.cur = cur
        }
    }

    fun act(): Boolean {
        spend(TICK)
        if (volume > 0) {
            volume = 0
            evolve()
            val tmp = off
            off = cur
            cur = tmp
        }
        return true
    }

    fun use(emitter: BlobEmitter?) {
        this.emitter = emitter
    }

    protected fun evolve() {
        val notBlocking: BooleanArray = BArray.not(Level.solid, null)
        for (i in 1 until HEIGHT - 1) {
            val from = i * WIDTH + 1
            val to = from + WIDTH - 2
            for (pos in from until to) {
                if (notBlocking[pos]) {
                    var count = 1
                    var sum = cur[pos]
                    if (notBlocking[pos - 1]) {
                        sum += cur[pos - 1]
                        count++
                    }
                    if (notBlocking[pos + 1]) {
                        sum += cur[pos + 1]
                        count++
                    }
                    if (notBlocking[pos - WIDTH]) {
                        sum += cur[pos - WIDTH]
                        count++
                    }
                    if (notBlocking[pos + WIDTH]) {
                        sum += cur[pos + WIDTH]
                        count++
                    }
                    val value = if (sum >= count) sum / count - 1 else 0
                    off[pos] = value
                    volume += value
                } else {
                    off[pos] = 0
                }
            }
        }
    }

    fun seed(cell: Int, amount: Int) {
        cur[cell] += amount
        volume += amount
    }

    fun clear(cell: Int) {
        volume -= cur[cell]
        cur[cell] = 0
    }

    fun tileDesc(): String? {
        return null
    }

    companion object {
        val WIDTH: Int = Level.WIDTH
        val HEIGHT: Int = Level.HEIGHT
        val LENGTH: Int = Level.LENGTH
        private const val CUR = "cur"
        private const val START = "start"
        fun <T : Blob?> seed(cell: Int, amount: Int, type: Class<T>): T? {
            return try {
                var gas: T? = Dungeon.level.blobs.get(type)
                if (gas == null) {
                    gas = type.newInstance()
                    Dungeon.level.blobs.put(type, gas)
                }
                gas!!.seed(cell, amount)
                gas
            } catch (e: Exception) {
                PixelDungeon.reportException(e)
                null
            }
        }
    }

    init {
        cur = IntArray(LENGTH)
        off = IntArray(LENGTH)
        volume = 0
    }
}