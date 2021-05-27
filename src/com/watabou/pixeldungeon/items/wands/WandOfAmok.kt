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

class WandOfAmok : Wand() {
    protected override fun onZap(cell: Int) {
        val ch: Char = Actor.findChar(cell)
        if (ch != null) {
            if (ch === Dungeon.hero) {
                Buff.affect(ch, Vertigo::class.java, Vertigo.duration(ch))
            } else {
                Buff.affect(ch, Amok::class.java, 3f + power())
            }
        } else {
            GLog.i("nothing happened")
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.purpleLight(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    fun desc(): String {
        return "The purple light from this wand will make the target run amok " +
                "attacking random creatures in its vicinity."
    }

    init {
        name = "Wand of Amok"
    }
}