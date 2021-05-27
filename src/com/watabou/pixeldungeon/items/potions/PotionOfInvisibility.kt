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
package com.watabou.pixeldungeon.items.potions

import com.watabou.noosa.audio.Sample

class PotionOfInvisibility : Potion() {
    protected override fun apply(hero: Hero?) {
        setKnown()
        Buff.affect(hero, Invisibility::class.java, Invisibility.DURATION)
        GLog.i("You see your hands turn invisible!")
        Sample.INSTANCE.play(Assets.SND_MELD)
    }

    fun desc(): String {
        return "Drinking this potion will render you temporarily invisible. While invisible, " +
                "enemies will be unable to see you. Attacking an enemy, as well as using a wand or a scroll " +
                "before enemy's eyes, will dispel the effect."
    }

    override fun price(): Int {
        return if (isKnown()) 40 * quantity else super.price()
    }

    companion object {
        private const val ALPHA = 0.4f
        fun melt(ch: Char) {
            if (ch.sprite.parent != null) {
                ch.sprite.parent.add(AlphaTweener(ch.sprite, ALPHA, 0.4f))
            } else {
                ch.sprite.alpha(ALPHA)
            }
        }
    }

    init {
        name = "Potion of Invisibility"
    }
}