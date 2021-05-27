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

class ScrollOfLullaby : Scroll() {
    protected override fun doRead() {
        curUser.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5)
        Sample.INSTANCE.play(Assets.SND_LULLABY)
        Invisibility.dispel()
        var count = 0
        var affected: Mob? = null
        for (mob in Dungeon.level.mobs.toArray(arrayOfNulls<Mob>(0))) {
            if (Level.fieldOfView.get(mob.pos)) {
                Buff.affect(mob, Sleep::class.java)
                if (mob.buff(Sleep::class.java) != null) {
                    affected = mob
                    count++
                }
            }
        }
        when (count) {
            0 -> GLog.i("The scroll utters a soothing melody.")
            1 -> GLog.i("The scroll utters a soothing melody and the " + affected.name.toString() + " falls asleep!")
            else -> GLog.i("The scroll utters a soothing melody and the monsters fall asleep!")
        }
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "A soothing melody will put all creatures in your field of view into a deep sleep, " +
                "giving you a chance to flee or make a surprise attack on them."
    }

    override fun price(): Int {
        return if (isKnown()) 50 * quantity else super.price()
    }

    init {
        name = "Scroll of Lullaby"
    }
}