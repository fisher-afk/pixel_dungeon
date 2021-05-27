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

object Statistics {
    var goldCollected = 0
    var deepestFloor = 0
    var enemiesSlain = 0
    var foodEaten = 0
    var potionsCooked = 0
    var piranhasKilled = 0
    var nightHunt = 0
    var ankhsUsed = 0
    var duration = 0f
    var qualifiedForNoKilling = false
    var completedWithNoKilling = false
    var amuletObtained = false
    fun reset() {
        goldCollected = 0
        deepestFloor = 0
        enemiesSlain = 0
        foodEaten = 0
        potionsCooked = 0
        piranhasKilled = 0
        nightHunt = 0
        ankhsUsed = 0
        duration = 0f
        qualifiedForNoKilling = false
        amuletObtained = false
    }

    private const val GOLD = "score"
    private const val DEEPEST = "maxDepth"
    private const val SLAIN = "enemiesSlain"
    private const val FOOD = "foodEaten"
    private const val ALCHEMY = "potionsCooked"
    private const val PIRANHAS = "priranhas"
    private const val NIGHT = "nightHunt"
    private const val ANKHS = "ankhsUsed"
    private const val DURATION = "duration"
    private const val AMULET = "amuletObtained"
    fun storeInBundle(bundle: Bundle) {
        bundle.put(GOLD, goldCollected)
        bundle.put(DEEPEST, deepestFloor)
        bundle.put(SLAIN, enemiesSlain)
        bundle.put(FOOD, foodEaten)
        bundle.put(ALCHEMY, potionsCooked)
        bundle.put(PIRANHAS, piranhasKilled)
        bundle.put(NIGHT, nightHunt)
        bundle.put(ANKHS, ankhsUsed)
        bundle.put(DURATION, duration)
        bundle.put(AMULET, amuletObtained)
    }

    fun restoreFromBundle(bundle: Bundle) {
        goldCollected = bundle.getInt(GOLD)
        deepestFloor = bundle.getInt(DEEPEST)
        enemiesSlain = bundle.getInt(SLAIN)
        foodEaten = bundle.getInt(FOOD)
        potionsCooked = bundle.getInt(ALCHEMY)
        piranhasKilled = bundle.getInt(PIRANHAS)
        nightHunt = bundle.getInt(NIGHT)
        ankhsUsed = bundle.getInt(ANKHS)
        duration = bundle.getFloat(DURATION)
        amuletObtained = bundle.getBoolean(AMULET)
    }
}