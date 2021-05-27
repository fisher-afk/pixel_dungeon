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
package com.watabou.pixeldungeon.items

import com.watabou.noosa.audio.Sample

class Bomb : Item() {
    protected override fun onThrow(cell: Int) {
        if (Level.pit.get(cell)) {
            super.onThrow(cell)
        } else {
            Sample.INSTANCE.play(Assets.SND_BLAST, 2)
            if (Dungeon.visible.get(cell)) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30)
            }
            var terrainAffected = false
            for (n in Level.NEIGHBOURS9) {
                val c = cell + n
                if (c >= 0 && c < Level.LENGTH) {
                    if (Dungeon.visible.get(c)) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4)
                    }
                    if (Level.flamable.get(c)) {
                        Dungeon.level.destroy(c)
                        GameScene.updateMap(c)
                        terrainAffected = true
                    }
                    val ch: Char = Actor.findChar(c)
                    if (ch != null) {
                        val dmg: Int = Random.Int(1 + Dungeon.depth, 10 + Dungeon.depth * 2) - Random.Int(ch.dr())
                        if (dmg > 0) {
                            ch.damage(dmg, this)
                            if (ch.isAlive()) {
                                Buff.prolong(ch, Paralysis::class.java, 2)
                            } else if (ch === Dungeon.hero) {
                                Dungeon.fail(Utils.format(ResultDescriptions.BOMB, Dungeon.depth))
                                GLog.n("You killed yourself with a bomb...")
                            }
                        }
                    }
                }
            }
            if (terrainAffected) {
                Dungeon.observe()
            }
        }
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun random(): Item {
        quantity = Random.IntRange(1, 3)
        return this
    }

    override fun price(): Int {
        return 10 * quantity
    }

    override fun info(): String {
        return "This is a relatively small bomb, filled with black powder. Conveniently, its fuse is lit automatically when the bomb is thrown."
    }

    init {
        name = "bomb"
        image = ItemSpriteSheet.BOMB
        defaultAction = AC_THROW
        stackable = true
    }
}