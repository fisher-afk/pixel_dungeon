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

class WindParticle : PixelParticle() {
    private var size = 0f
    fun reset(x: Float, y: Float) {
        revive()
        left = lifespan
        super.speed.set(speed)
        super.speed.scale(size)
        x = x - super.speed.x * lifespan / 2
        y = y - super.speed.y * lifespan / 2
        angle += Random.Float(-0.1f, +0.1f)
        speed = PointF().polar(angle, 5)
        am = 0
    }

    fun update() {
        super.update()
        val p: Float = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * size * 0.2f
    }

    class Wind(private val pos: Int) : Group() {
        private val x: Float
        private val y: Float
        private var delay: Float
        fun update() {
            if (Dungeon.visible.get(pos).also { visible = it }) {
                super.update()
                if (Game.elapsed.let { delay -= it; delay } <= 0) {
                    delay = Random.Float(5)
                    (recycle(WindParticle::class.java) as WindParticle).reset(
                        x + Random.Float(DungeonTilemap.SIZE),
                        y + Random.Float(DungeonTilemap.SIZE)
                    )
                }
            }
        }

        init {
            val p: PointF = DungeonTilemap.tileToWorld(pos)
            x = p.x
            y = p.y
            delay = Random.Float(5)
        }
    }

    companion object {
        val FACTORY: Emitter.Factory = object : Factory() {
            fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(WindParticle::class.java) as WindParticle).reset(x, y)
            }
        }
        private var angle: Float = Random.Float(PointF.PI2)
        private var speed: PointF = PointF().polar(angle, 5)
    }

    init {
        lifespan = Random.Float(1, 2)
        scale.set(Random.Float(3).also { size = it })
    }
}