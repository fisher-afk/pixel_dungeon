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

class ScrollOfRecharging : Scroll() {
    protected override fun doRead() {
        val count: Int = curUser.belongings.charge(true)
        charge(curUser)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        if (count > 0) {
            GLog.i("a surge of energy courses through your pack, recharging your wand" + if (count > 1) "s" else "")
            SpellSprite.show(curUser, SpellSprite.CHARGE)
        } else {
            GLog.i("a surge of energy courses through your pack, but nothing happens")
        }
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "The raw magical power bound up in this parchment will, when released, " +
                "recharge all of the reader's wands to full power."
    }

    override fun price(): Int {
        return if (isKnown()) 40 * quantity else super.price()
    }

    companion object {
        fun charge(hero: Hero) {
            hero.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 15)
        }
    }

    init {
        name = "Scroll of Recharging"
    }
}