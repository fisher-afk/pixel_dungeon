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

class ScrollOfChallenge : Scroll() {
    protected override fun doRead() {
        for (mob in Dungeon.level.mobs.toArray(arrayOfNulls<Mob>(0))) {
            mob.beckon(curUser.pos)
            if (Dungeon.visible.get(mob.pos)) {
                Buff.affect(mob, Rage::class.java, Level.distance(curUser.pos, mob.pos))
            }
        }
        for (heap in Dungeon.level.heaps.values()) {
            if (heap.type === Heap.Type.MIMIC) {
                val m: Mimic = Mimic.spawnAt(heap.pos, heap.items)
                if (m != null) {
                    m.beckon(curUser.pos)
                    heap.destroy()
                }
            }
        }
        GLog.w("The scroll emits a challenging roar that echoes throughout the dungeon!")
        setKnown()
        curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3)
        Sample.INSTANCE.play(Assets.SND_CHALLENGE)
        Invisibility.dispel()
        readAnimation()
    }

    fun desc(): String {
        return "When read aloud, this scroll will unleash a challenging roar " +
                "that will awaken all monsters and alert them to the reader's location."
    }

    init {
        name = "Scroll of Challenge"
    }
}