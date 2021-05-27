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
package com.watabou.pixeldungeon.effects

import com.watabou.noosa.Game

class MagicMissile : Emitter() {
    private var callback: Callback? = null
    private var sx = 0f
    private var sy = 0f
    private var time = 0f
    fun reset(from: Int, to: Int, callback: Callback?) {
        reset(from, to, SPEED, callback)
    }

    fun reset(from: Int, to: Int, velocity: Float, callback: Callback?) {
        this.callback = callback
        revive()
        val pf: PointF = DungeonTilemap.tileCenterToWorld(from)
        val pt: PointF = DungeonTilemap.tileCenterToWorld(to)
        x = pf.x
        y = pf.y
        width = 0
        height = 0
        val d: PointF = PointF.diff(pt, pf)
        val speed: PointF = PointF(d).normalize().scale(velocity)
        sx = speed.x
        sy = speed.y
        time = d.length() / velocity
    }

    fun size(size: Float) {
        x -= size / 2
        y -= size / 2
        height = size
        width = height
    }

    fun update() {
        super.update()
        if (on) {
            val d: Float = Game.elapsed
            x += sx * d
            y += sy * d
            if (d.let { time -= it; time } <= 0) {
                on = false
                callback.call()
            }
        }
    }

    class MagicParticle : PixelParticle() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            left = lifespan
        }

        fun update() {
            super.update()
            // alpha: 1 -> 0; size: 1 -> 4
            size(4 - left / lifespan.also { am = it } * 3)
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(MagicParticle::class.java) as MagicParticle).reset(x, y)
                }

                fun lightMode(): Boolean {
                    return true
                }
            }
        }

        init {
            color(0x88CCFF)
            lifespan = 0.5f
            speed.set(Random.Float(-10, +10), Random.Float(-10, +10))
        }
    }

    class EarthParticle : PixelParticle.Shrinking() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            left = lifespan
            size = 4
            speed.set(Random.Float(-10, +10), Random.Float(-10, +10))
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(EarthParticle::class.java) as EarthParticle).reset(x, y)
                }
            }
        }

        init {
            lifespan = 0.5f
            color(ColorMath.random(0x555555, 0x777766))
            acc.set(0, +40)
        }
    }

    class WhiteParticle : PixelParticle() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            left = lifespan
        }

        fun update() {
            super.update()
            // size: 3 -> 0
            size(left / lifespan * 3)
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(WhiteParticle::class.java) as WhiteParticle).reset(x, y)
                }

                fun lightMode(): Boolean {
                    return true
                }
            }
        }

        init {
            lifespan = 0.4f
            am = 0.5f
        }
    }

    class SlowParticle : PixelParticle() {
        private var emitter: Emitter? = null
        fun reset(x: Float, y: Float, emitter: Emitter?) {
            revive()
            x = x
            y = y
            this.emitter = emitter
            left = lifespan
            acc.set(0)
            speed.set(Random.Float(-20, +20), Random.Float(-20, +20))
        }

        fun update() {
            super.update()
            am = left / lifespan
            acc.set((emitter.x - x) * 10, (emitter.y - y) * 10)
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(SlowParticle::class.java) as SlowParticle).reset(x, y, emitter)
                }

                fun lightMode(): Boolean {
                    return true
                }
            }
        }

        init {
            lifespan = 0.6f
            color(0x664422)
            size(2f)
        }
    }

    class ForceParticle : Shrinking() {
        fun reset(index: Int, x: Float, y: Float) {
            super.reset(x, y, 0xFFFFFF, 8, 0.5f)
            speed.polar(PointF.PI2 / 8 * index, 12)
            x -= speed.x * lifespan
            y -= speed.y * lifespan
        }

        fun update() {
            super.update()
            am = (1 - left / lifespan) / 2
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(ForceParticle::class.java) as ForceParticle).reset(index, x, y)
                }
            }
        }
    }

    class ColdParticle : PixelParticle.Shrinking() {
        fun reset(x: Float, y: Float) {
            revive()
            x = x
            y = y
            left = lifespan
            size = 8
        }

        fun update() {
            super.update()
            am = 1 - left / lifespan
        }

        companion object {
            val FACTORY: Emitter.Factory = object : Factory() {
                fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(ColdParticle::class.java) as ColdParticle).reset(x, y)
                }

                fun lightMode(): Boolean {
                    return true
                }
            }
        }

        init {
            lifespan = 0.6f
            color(0x2244FF)
        }
    }

    companion object {
        private const val SPEED = 200f
        fun blueLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.pour(MagicParticle.FACTORY, 0.01f)
        }

        fun fire(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(FlameParticle.FACTORY, 0.01f)
        }

        fun earth(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(2f)
            missile.pour(EarthParticle.FACTORY, 0.01f)
        }

        fun purpleLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(2f)
            missile.pour(PurpleParticle.MISSILE, 0.01f)
        }

        fun whiteLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(WhiteParticle.FACTORY, 0.01f)
        }

        fun wool(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(3f)
            missile.pour(WoolParticle.FACTORY, 0.01f)
        }

        fun poison(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(3f)
            missile.pour(PoisonParticle.MISSILE, 0.01f)
        }

        fun foliage(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(LeafParticle.GENERAL, 0.01f)
        }

        fun slowness(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.pour(SlowParticle.FACTORY, 0.01f)
        }

        fun force(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(0f)
            missile.pour(ForceParticle.FACTORY, 0.01f)
        }

        fun coldLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(ColdParticle.FACTORY, 0.01f)
        }

        fun shadow(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(ShadowParticle.MISSILE, 0.01f)
        }
    }
}