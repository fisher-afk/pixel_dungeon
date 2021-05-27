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

import com.watabou.noosa.Camera

class WandOfAvalanche : Wand() {
    protected override fun onZap(cell: Int) {
        Sample.INSTANCE.play(Assets.SND_ROCKS)
        val level: Int = power()
        Ballistica.distance = Math.min(Ballistica.distance, 8 + level)
        val size = 1 + level / 3
        PathFinder.buildDistanceMap(cell, BArray.not(Level.solid, null), size)
        var shake = 0
        for (i in 0 until Level.LENGTH) {
            val d: Int = PathFinder.distance.get(i)
            if (d < Int.MAX_VALUE) {
                val ch: Char = Actor.findChar(i)
                if (ch != null) {
                    ch.sprite.flash()
                    ch.damage(Random.Int(2, 6 + (size - d) * 2), this)
                    if (ch.isAlive() && Random.Int(2 + d) === 0) {
                        Buff.prolong(ch, Paralysis::class.java, Random.IntRange(2, 6))
                    }
                }
                if (ch != null && ch.isAlive()) {
                    if (ch is Mob) {
                        Dungeon.level.mobPress(ch as Mob)
                    } else {
                        Dungeon.level.press(i, ch)
                    }
                } else {
                    Dungeon.level.press(i, null)
                }
                if (Dungeon.visible.get(i)) {
                    CellEmitter.get(i).start(Speck.factory(Speck.ROCK), 0.07f, 3 + (size - d))
                    if (Level.water.get(i)) {
                        GameScene.ripple(i)
                    }
                    if (shake < size - d) {
                        shake = size - d
                    }
                }
            }
            Camera.main.shake(3, 0.07f * (3 + shake))
        }
        if (!curUser.isAlive()) {
            Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
            GLog.n("You killed yourself with your own Wand of Avalanche...")
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.earth(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "When a discharge of this wand hits a wall (or any other solid obstacle) it causes " +
                "an avalanche of stones, damaging and stunning all creatures in the affected area."
    }

    init {
        name = "Wand of Avalanche"
        hitChars = false
    }
}