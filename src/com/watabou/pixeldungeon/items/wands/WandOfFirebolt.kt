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

class WandOfFirebolt : Wand() {
    protected override fun onZap(cell: Int) {
        val level: Int = power()
        for (i in 1 until Ballistica.distance - 1) {
            val c: Int = Ballistica.trace.get(i)
            if (Level.flamable.get(c)) {
                GameScene.add(Blob.seed(c, 1, Fire::class.java))
            }
        }
        GameScene.add(Blob.seed(cell, 1, Fire::class.java))
        val ch: Char = Actor.findChar(cell)
        if (ch != null) {
            ch.damage(Random.Int(1, 8 + level * level), this)
            Buff.affect(ch, Burning::class.java).reignite(ch)
            ch.sprite.emitter().burst(FlameParticle.FACTORY, 5)
            if (ch === curUser && !ch.isAlive()) {
                Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
                GLog.n("You killed yourself with your own Wand of Firebolt...")
            }
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.fire(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "This wand unleashes bursts of magical fire. It will ignite " +
                "flammable terrain, and will damage and burn a creature it hits."
    }

    init {
        name = "Wand of Firebolt"
    }
}