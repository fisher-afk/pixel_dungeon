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

class ScrollOfRemoveCurse : Scroll() {
    protected override fun doRead() {
        Flare(6, 32).show(curUser.sprite, 2f)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        var procced = uncurse(curUser, curUser.belongings.backpack.items.toArray(arrayOfNulls<Item>(0)))
        procced = uncurse(
            curUser,
            curUser.belongings.weapon,
            curUser.belongings.armor,
            curUser.belongings.ring1,
            curUser.belongings.ring2
        ) || procced
        Weakness.detach(curUser, Weakness::class.java)
        if (procced) {
            GLog.p(TXT_PROCCED)
        } else {
            GLog.i(TXT_NOT_PROCCED)
        }
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "The incantation on this scroll will instantly strip from " +
                "the reader's weapon, armor, rings and carried items any evil " +
                "enchantments that might prevent the wearer from removing them."
    }

    override fun price(): Int {
        return if (isKnown()) 30 * quantity else super.price()
    }

    companion object {
        private const val TXT_PROCCED = "Your pack glows with a cleansing light, and a malevolent energy disperses."
        private const val TXT_NOT_PROCCED = "Your pack glows with a cleansing light, but nothing happens."
        fun uncurse(hero: Hero, vararg items: Item?): Boolean {
            var procced = false
            for (i in 0 until items.size) {
                val item: Item? = items[i]
                if (item != null && item.cursed) {
                    item.cursed = false
                    procced = true
                }
            }
            if (procced) {
                hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10)
            }
            return procced
        }
    }

    init {
        name = "Scroll of Remove Curse"
    }
}