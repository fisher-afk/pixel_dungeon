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

class WandOfSlowness : Wand() {
    protected override fun onZap(cell: Int) {
        val ch: Char = Actor.findChar(cell)
        if (ch != null) {
            Buff.affect(ch, Slow::class.java, Slow.duration(ch) / 3 + power())
        } else {
            GLog.i("nothing happened")
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.slowness(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "This wand will cause a creature to move and attack " +
                "at half its ordinary speed until the effect ends"
    }

    init {
        name = "Wand of Slowness"
    }
}