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

class ScrollOfPsionicBlast : Scroll() {
    protected override fun doRead() {
        GameScene.flash(0xFFFFFF)
        Sample.INSTANCE.play(Assets.SND_BLAST)
        Invisibility.dispel()
        for (mob in Dungeon.level.mobs.toArray(arrayOfNulls<Mob>(0))) {
            if (Level.fieldOfView.get(mob.pos)) {
                Buff.prolong(mob, Blindness::class.java, Random.Int(3, 6))
                mob.damage(Random.IntRange(1, mob.HT * 2 / 3), this)
            }
        }
        Buff.prolong(curUser, Blindness::class.java, Random.Int(3, 6))
        Dungeon.observe()
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "This scroll contains destructive energy, that can be psionically channeled to inflict a " +
                "massive damage to all creatures within a field of view. An accompanying flash of light will " +
                "temporarily blind everybody in the area of effect including the reader of the scroll."
    }

    override fun price(): Int {
        return if (isKnown()) 80 * quantity else super.price()
    }

    init {
        name = "Scroll of Psionic Blast"
    }
}