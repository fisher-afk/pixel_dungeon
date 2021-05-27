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

import com.watabou.utils.Bundlable

object Journal {
    var records: ArrayList<Record>? = null
    fun reset() {
        records = ArrayList()
    }

    private const val JOURNAL = "journal"
    fun storeInBundle(bundle: Bundle) {
        bundle.put(JOURNAL, records)
    }

    fun restoreFromBundle(bundle: Bundle) {
        records = ArrayList()
        for (rec in bundle.getCollection(JOURNAL)) {
            records!!.add(rec as Record)
        }
    }

    fun add(feature: Feature) {
        val size = records!!.size
        for (i in 0 until size) {
            val rec = records!![i]
            if (rec.feature == feature && rec.depth == Dungeon.depth) {
                return
            }
        }
        records!!.add(Record(feature, Dungeon.depth))
    }

    fun remove(feature: Feature) {
        val size = records!!.size
        for (i in 0 until size) {
            val rec = records!![i]
            if (rec.feature == feature && rec.depth == Dungeon.depth) {
                records!!.removeAt(i)
                return
            }
        }
    }

    enum class Feature(var desc: String) {
        WELL_OF_HEALTH("Well of Health"), WELL_OF_AWARENESS("Well of Awareness"), WELL_OF_TRANSMUTATION("Well of Transmutation"), SACRIFICIAL_FIRE(
            "Sacrificial chamber"
        ),
        ALCHEMY("Alchemy pot"), GARDEN("Garden"), STATUE("Animated statue"), GHOST("Sad ghost"), WANDMAKER("Old wandmaker"), TROLL(
            "Troll blacksmith"
        ),
        IMP("Ambitious imp");
    }

    class Record : Comparable<Record?>, Bundlable {
        var feature: Feature? = null
        var depth = 0

        constructor() {}
        constructor(feature: Feature?, depth: Int) {
            this.feature = feature
            this.depth = depth
        }

        override operator fun compareTo(another: Record): Int {
            return another.depth - depth
        }

        fun restoreFromBundle(bundle: Bundle) {
            feature = Feature.valueOf(bundle.getString(FEATURE))
            depth = bundle.getInt(DEPTH)
        }

        fun storeInBundle(bundle: Bundle) {
            bundle.put(FEATURE, feature.toString())
            bundle.put(DEPTH, depth)
        }

        companion object {
            private const val FEATURE = "feature"
            private const val DEPTH = "depth"
        }
    }
}