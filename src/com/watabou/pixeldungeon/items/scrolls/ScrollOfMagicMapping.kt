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

class ScrollOfMagicMapping : Scroll() {
    protected override fun doRead() {
        val length: Int = Level.LENGTH
        val map: IntArray = Dungeon.level.map
        val mapped: BooleanArray = Dungeon.level.mapped
        val discoverable: BooleanArray = Level.discoverable
        var noticed = false
        for (i in 0 until length) {
            val terr = map[i]
            if (discoverable[i]) {
                mapped[i] = true
                if (Terrain.flags.get(terr) and Terrain.SECRET !== 0) {
                    Level.set(i, Terrain.discover(terr))
                    GameScene.updateMap(i)
                    if (Dungeon.visible.get(i)) {
                        GameScene.discoverTile(i, terr)
                        discover(i)
                        noticed = true
                    }
                }
            }
        }
        Dungeon.observe()
        GLog.i(TXT_LAYOUT)
        if (noticed) {
            Sample.INSTANCE.play(Assets.SND_SECRET)
        }
        SpellSprite.show(curUser, SpellSprite.MAP)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        setKnown()
        readAnimation()
    }

    fun desc(): String {
        return "When this scroll is read, an image of crystal clarity will be etched into your memory, " +
                "alerting you to the precise layout of the level and revealing all hidden secrets. " +
                "The locations of items and creatures will remain unknown."
    }

    override fun price(): Int {
        return if (isKnown()) 25 * quantity else super.price()
    }

    companion object {
        private const val TXT_LAYOUT = "You are now aware of the level layout."
        fun discover(cell: Int) {
            CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.1f, 4)
        }
    }

    init {
        name = "Scroll of Magic Mapping"
    }
}