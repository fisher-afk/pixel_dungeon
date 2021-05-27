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

class ScrollOfTerror : Scroll() {
    protected override fun doRead() {
        Flare(5, 32).color(0xFF0000, true).show(curUser.sprite, 2f)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        var count = 0
        var affected: Mob? = null
        for (mob in Dungeon.level.mobs.toArray(arrayOfNulls<Mob>(0))) {
            if (Level.fieldOfView.get(mob.pos)) {
                Buff.affect(mob, Terror::class.java, Terror.DURATION).`object` = curUser.id()
                count++
                affected = mob
            }
        }
        when (count) {
            0 -> GLog.i("The scroll emits a brilliant flash of red light")
            1 -> GLog.i("The scroll emits a brilliant flash of red light and the " + affected.name.toString() + " flees!")
            else -> GLog.i("The scroll emits a brilliant flash of red light and the monsters flee!")
        }
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "A flash of red light will overwhelm all creatures in your field of view with terror, " +
                "and they will turn and flee. Attacking a fleeing enemy will dispel the effect."
    }

    override fun price(): Int {
        return if (isKnown()) 50 * quantity else super.price()
    }

    init {
        name = "Scroll of Terror"
    }
}