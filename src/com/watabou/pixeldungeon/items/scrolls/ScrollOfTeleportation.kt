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

class ScrollOfTeleportation : Scroll() {
    protected override fun doRead() {
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        teleportHero(curUser)
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "The spell on this parchment instantly transports the reader " +
                "to a random location on the dungeon level. It can be used " +
                "to escape a dangerous situation, but the unlucky reader might " +
                "find himself in an even more dangerous place."
    }

    override fun price(): Int {
        return if (isKnown()) 40 * quantity else super.price()
    }

    companion object {
        const val TXT_TELEPORTED = "In a blink of an eye you were teleported to another location of the level."
        const val TXT_NO_TELEPORT = "Strong magic aura of this place prevents you from teleporting!"
        fun teleportHero(hero: Hero?) {
            var count = 10
            var pos: Int
            do {
                pos = Dungeon.level.randomRespawnCell()
                if (count-- <= 0) {
                    break
                }
            } while (pos == -1)
            if (pos == -1) {
                GLog.w(TXT_NO_TELEPORT)
            } else {
                WandOfBlink.appear(hero, pos)
                Dungeon.level.press(pos, hero)
                Dungeon.observe()
                GLog.i(TXT_TELEPORTED)
            }
        }
    }

    init {
        name = "Scroll of Teleportation"
    }
}