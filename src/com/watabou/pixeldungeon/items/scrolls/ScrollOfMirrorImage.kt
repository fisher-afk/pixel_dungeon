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
package com.watabou.pixeldungeon.items.scrolls

import com.watabou.noosa.audio.Sample

class ScrollOfMirrorImage : Scroll() {
    protected override fun doRead() {
        val respawnPoints = ArrayList<Int>()
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val p: Int = curUser.pos + Level.NEIGHBOURS8.get(i)
            if (Actor.findChar(p) == null && (Level.passable.get(p) || Level.avoid.get(p))) {
                respawnPoints.add(p)
            }
        }
        var nImages = NIMAGES
        while (nImages > 0 && respawnPoints.size > 0) {
            val index: Int = Random.index(respawnPoints)
            val mob = MirrorImage()
            mob.duplicate(curUser)
            GameScene.add(mob)
            WandOfBlink.appear(mob, respawnPoints[index])
            respawnPoints.removeAt(index)
            nImages--
        }
        if (nImages < NIMAGES) {
            setKnown()
        }
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        readAnimation()
    }

    fun desc(): String {
        return "The incantation on this scroll will create illusionary twins of the reader, which will chase his enemies."
    }

    companion object {
        private const val NIMAGES = 3
    }

    init {
        name = "Scroll of Mirror Image"
    }
}