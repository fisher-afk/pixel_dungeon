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

class WandOfBlink : Wand() {
    protected override fun onZap(cell: Int) {
        var cell = cell
        val level: Int = power()
        if (Ballistica.distance > level + 4) {
            cell = Ballistica.trace.get(level + 3)
        } else if (Actor.findChar(cell) != null && Ballistica.distance > 1) {
            cell = Ballistica.trace.get(Ballistica.distance - 2)
        }
        curUser.sprite.visible = true
        appear(Dungeon.hero, cell)
        Dungeon.observe()
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        MagicMissile.whiteLight(curUser.sprite.parent, curUser.pos, cell, callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
        curUser.sprite.visible = false
    }

    fun desc(): String {
        return "This wand will allow you to teleport in the chosen direction. " +
                "Creatures and inanimate obstructions will block the teleportation."
    }

    companion object {
        fun appear(ch: Char, pos: Int) {
            ch.sprite.interruptMotion()
            ch.move(pos)
            ch.sprite.place(pos)
            if (ch.invisible === 0) {
                ch.sprite.alpha(0)
                ch.sprite.parent.add(AlphaTweener(ch.sprite, 1, 0.4f))
            }
            ch.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3)
            Sample.INSTANCE.play(Assets.SND_TELEPORT)
        }
    }

    init {
        name = "Wand of Blink"
    }
}