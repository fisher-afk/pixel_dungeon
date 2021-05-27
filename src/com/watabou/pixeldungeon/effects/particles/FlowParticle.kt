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
package com.watabou.pixeldungeon.effects.particles

import com.watabou.noosa.Game

class FlowParticle : PixelParticle() {
    fun reset(x: Float, y: Float) {
        revive()
        left = lifespan
        x = x
        y = y
        am = 0
        size(0)
        speed.set(0)
    }

    fun update() {
        super.update()
        val p: Float = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * 0.6f
        size((1 - p) * 4)
    }

    class Flow(private val pos: Int) : Group() {
        private val x: Float
        private val y: Float
        private var delay: Float
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
                if (Game.elapsed.let { delay -= it; delay } <= 0) {
                    delay = Random.Float(DELAY)
                    (recycle(FlowParticle::class.java) as FlowParticle).reset(
                        x + Random.Float(DungeonTilemap.SIZE), y
                    )
                }
            }
        }

        companion object {
            private const val DELAY = 0.1f
        }

        init {
            val p: PointF = DungeonTilemap.tileToWorld(pos)
            x = p.x
            y = p.y + DungeonTilemap.SIZE - 1
            delay = Random.Float(DELAY)
        }
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(FlowParticle::class.java) as FlowParticle).reset(x, y)
            }
        }
    }

    init {
        lifespan = 0.6f
        acc.set(0, 32)
        angularSpeed = Random.Float(-360, +360)
    }
}