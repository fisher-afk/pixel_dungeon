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
package com.watabou.pixeldungeon.actors.blobs

import com.watabou.noosa.audio.Sample

class SacrificialFire : Blob() {
    protected var pos = 0
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until LENGTH) {
            if (cur.get(i) > 0) {
                pos = i
                break
            }
        }
    }

    protected override fun evolve() {
        off.get(pos) = cur.get(pos)
        volume = off.get(pos)
        val ch: Char = Actor.findChar(pos)
        if (ch != null) {
            if (Dungeon.visible.get(pos) && ch.buff(Marked::class.java) == null) {
                ch.sprite.emitter().burst(SacrificialParticle.FACTORY, 20)
                Sample.INSTANCE.play(Assets.SND_BURNING)
            }
            Buff.prolong(ch, Marked::class.java, Marked.DURATION)
        }
        if (Dungeon.visible.get(pos)) {
            Journal.add(Feature.SACRIFICIAL_FIRE)
        }
    }

    override fun seed(cell: Int, amount: Int) {
        cur.get(pos) = 0
        pos = cell
        cur.get(pos) = amount
        volume = cur.get(pos)
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(SacrificialParticle.FACTORY, 0.04f)
    }

    override fun tileDesc(): String {
        return "Sacrificial fire burns here. Every creature touched by this fire is marked as an offering for the spirits of the dungeon."
    }

    class Marked : FlavourBuff() {
        fun icon(): Int {
            return BuffIndicator.SACRIFICE
        }

        override fun toString(): String {
            return "Marked for sacrifice"
        }

        fun detach() {
            if (!target.isAlive()) {
                sacrifice(target)
            }
            super.detach()
        }

        companion object {
            const val DURATION = 5f
        }
    }

    companion object {
        private const val TXT_WORTHY = "\"Your sacrifice is worthy...\" "
        private const val TXT_UNWORTHY = "\"Your sacrifice is unworthy...\" "
        private const val TXT_REWARD = "\"Your sacrifice is worthy and so you are!\" "
        fun sacrifice(ch: Char) {
            Wound.hit(ch)
            val fire = Dungeon.level.blobs.get(SacrificialFire::class.java) as SacrificialFire
            if (fire != null) {
                var exp = 0
                if (ch is Mob) {
                    exp = (ch as Mob).exp() * Random.IntRange(1, 3)
                } else if (ch is Hero) {
                    exp = (ch as Hero).maxExp()
                }
                if (exp > 0) {
                    val volume: Int = fire.volume - exp
                    if (volume > 0) {
                        fire.seed(fire.pos, volume)
                        GLog.w(TXT_WORTHY)
                    } else {
                        fire.seed(fire.pos, 0)
                        Journal.remove(Feature.SACRIFICIAL_FIRE)
                        GLog.w(TXT_REWARD)
                        GameScene.effect(
                            Flare(7, 32).color(0x66FFFF, true)
                                .show(ch.sprite.parent, DungeonTilemap.tileCenterToWorld(fire.pos), 2f)
                        )
                        Dungeon.level.drop(ScrollOfWipeOut(), fire.pos).sprite.drop()
                    }
                } else {
                    GLog.w(TXT_UNWORTHY)
                }
            }
        }
    }
}