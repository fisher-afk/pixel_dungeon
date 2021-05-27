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

class WandOfFlock : Wand() {
    protected override fun onZap(cell: Int) {
        var cell = cell
        val level: Int = power()
        val n = level + 2
        if (Actor.findChar(cell) != null && Ballistica.distance > 2) {
            cell = Ballistica.trace.get(Ballistica.distance - 2)
        }
        val passable: BooleanArray = BArray.or(Level.passable, Level.avoid, null)
        for (actor in Actor.all()) {
            if (actor is Char) {
                passable[(actor as Char).pos] = false
            }
        }
        PathFinder.buildDistanceMap(cell, passable, n)
        var dist = 0
        if (Actor.findChar(cell) != null) {
            PathFinder.distance.get(cell) = Int.MAX_VALUE
            dist = 1
        }
        val lifespan = (level + 3).toFloat()
        sheepLabel@ for (i in 0 until n) {
            do {
                for (j in 0 until Level.LENGTH) {
                    if (PathFinder.distance.get(j) === dist) {
                        val sheep = Sheep()
                        sheep.lifespan = lifespan
                        sheep.pos = j
                        GameScene.add(sheep)
                        Dungeon.level.mobPress(sheep)
                        CellEmitter.get(j).burst(Speck.factory(Speck.WOOL), 4)
                        PathFinder.distance.get(j) = Int.MAX_VALUE
                        continue@sheepLabel
                    }
                }
                dist++
            } while (dist < n)
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.wool(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "A flick of this wand summons a flock of magic sheep, creating temporary impenetrable obstacle."
    }

    class Sheep : NPC() {
        var lifespan = 0f
        private var initialized = false
        protected fun act(): Boolean {
            if (initialized) {
                HP = 0
                destroy()
                sprite.die()
            } else {
                initialized = true
                spend(lifespan + Random.Float(2))
            }
            return true
        }

        fun damage(dmg: Int, src: Any?) {}
        fun description(): String {
            return "This is a magic sheep. What's so magical about it? You can't kill it. " +
                    "It will stand there until it magcially fades away, all the while chewing cud with a blank stare."
        }

        fun interact() {
            yell(Random.element(QUOTES))
        }

        companion object {
            private val QUOTES = arrayOf("Baa!", "Baa?", "Baa.", "Baa...")
        }

        init {
            name = "sheep"
            spriteClass = SheepSprite::class.java
        }
    }

    init {
        name = "Wand of Flock"
    }
}