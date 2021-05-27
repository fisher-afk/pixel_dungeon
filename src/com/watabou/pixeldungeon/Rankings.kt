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

import com.watabou.noosa.Game

enum class Rankings {
    INSTANCE;

    var records: ArrayList<Record>? = null
    var lastRecord = 0
    var totalNumber = 0
    var wonNumber = 0
    fun submit(win: Boolean) {
        load()
        val rec = Record()
        rec.info = Dungeon.resultDescription
        rec.win = win
        rec.heroClass = Dungeon.hero.heroClass
        rec.armorTier = Dungeon.hero.tier()
        rec.score = score(win)
        val gameFile: String = Utils.format(DETAILS_FILE, SystemTime.now)
        try {
            Dungeon.saveGame(gameFile)
            rec.gameFile = gameFile
        } catch (e: IOException) {
            rec.gameFile = ""
        }
        records!!.add(rec)
        Collections.sort(records, scoreComparator)
        lastRecord = records!!.indexOf(rec)
        val size = records!!.size
        if (size > TABLE_SIZE) {
            val removedGame: Record
            if (lastRecord == size - 1) {
                removedGame = records!!.removeAt(size - 2)
                lastRecord--
            } else {
                removedGame = records!!.removeAt(size - 1)
            }
            if (removedGame.gameFile!!.length > 0) {
                Game.instance.deleteFile(removedGame.gameFile)
            }
        }
        totalNumber++
        if (win) {
            wonNumber++
        }
        Badges.validateGamesPlayed()
        save()
    }

    private fun score(win: Boolean): Int {
        return (Statistics.goldCollected + Dungeon.hero.lvl * Statistics.deepestFloor * 100) * if (win) 2 else 1
    }

    fun save() {
        val bundle = Bundle()
        bundle.put(RECORDS, records)
        bundle.put(LATEST, lastRecord)
        bundle.put(TOTAL, totalNumber)
        bundle.put(WON, wonNumber)
        try {
            val output: OutputStream = Game.instance.openFileOutput(RANKINGS_FILE, Game.MODE_PRIVATE)
            Bundle.write(bundle, output)
            output.close()
        } catch (e: Exception) {
        }
    }

    fun load() {
        if (records != null) {
            return
        }
        records = ArrayList()
        try {
            val input: InputStream = Game.instance.openFileInput(RANKINGS_FILE)
            val bundle: Bundle = Bundle.read(input)
            input.close()
            for (record in bundle.getCollection(RECORDS)) {
                records!!.add(record as Record)
            }
            lastRecord = bundle.getInt(LATEST)
            totalNumber = bundle.getInt(TOTAL)
            if (totalNumber == 0) {
                totalNumber = records!!.size
            }
            wonNumber = bundle.getInt(WON)
            if (wonNumber == 0) {
                for (rec in records!!) {
                    if (rec.win) {
                        wonNumber++
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    class Record : Bundlable {
        var info: String? = null
        var win = false
        var heroClass: HeroClass? = null
        var armorTier = 0
        var score = 0
        var gameFile: String? = null
        fun restoreFromBundle(bundle: Bundle) {
            info = bundle.getString(REASON)
            win = bundle.getBoolean(WIN)
            score = bundle.getInt(SCORE)
            heroClass = HeroClass.restoreInBundle(bundle)
            armorTier = bundle.getInt(TIER)
            gameFile = bundle.getString(GAME)
        }

        fun storeInBundle(bundle: Bundle) {
            bundle.put(REASON, info)
            bundle.put(WIN, win)
            bundle.put(SCORE, score)
            heroClass.storeInBundle(bundle)
            bundle.put(TIER, armorTier)
            bundle.put(GAME, gameFile)
        }

        companion object {
            private const val REASON = "reason"
            private const val WIN = "win"
            private const val SCORE = "score"
            private const val TIER = "tier"
            private const val GAME = "gameFile"
        }
    }

    companion object {
        const val TABLE_SIZE = 6
        const val RANKINGS_FILE = "rankings.dat"
        const val DETAILS_FILE = "game_%d.dat"
        private const val RECORDS = "records"
        private const val LATEST = "latest"
        private const val TOTAL = "total"
        private const val WON = "won"
        private val scoreComparator =
            Comparator<Record> { lhs, rhs -> Math.signum((rhs.score - lhs.score).toFloat()).toInt() }
    }
}