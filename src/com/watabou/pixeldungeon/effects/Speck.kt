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

class Speck : Image() {
    private var type = 0
    private var lifespan = 0f
    private var left = 0f
    fun reset(index: Int, x: Float, y: Float, type: Int) {
        var y = y
        revive()
        this.type = type
        when (type) {
            DISCOVER -> frame(film.get(LIGHT))
            EVOKE, MASTERY, KIT, FORGE -> frame(film.get(STAR))
            RATTLE -> frame(film.get(BONE))
            JET, TOXIC, PARALYSIS, CONFUSION, DUST -> frame(film.get(STEAM))
            else -> frame(film.get(type))
        }
        x = x - origin.x
        this.y = y - origin.y
        resetColor()
        scale.set(1)
        speed.set(0)
        acc.set(0)
        angle = 0
        angularSpeed = 0
        when (type) {
            HEALING -> {
                speed.set(0, -20)
                lifespan = 1f
            }
            STAR -> {
                speed.polar(Random.Float(2 * 3.1415926f), Random.Float(128))
                acc.set(0, 128)
                angle = Random.Float(360)
                angularSpeed = Random.Float(-360, +360)
                lifespan = 1f
            }
            FORGE -> {
                speed.polar(-Random.Float(3.1415926f), Random.Float(64))
                acc.set(0, 128)
                angle = Random.Float(360)
                angularSpeed = Random.Float(-360, +360)
                lifespan = 0.51f
            }
            EVOKE -> {
                speed.polar(-Random.Float(3.1415926f), 50)
                acc.set(0, 50)
                angle = Random.Float(360)
                angularSpeed = Random.Float(-180, +180)
                lifespan = 1f
            }
            KIT -> {
                speed.polar(index * 3.1415926f / 5, 50)
                acc.set(-speed.x, -speed.y)
                angle = index * 36
                angularSpeed = 360
                lifespan = 1f
            }
            MASTERY -> {
                speed.set(if (Random.Int(2) === 0) Random.Float(-128, -64) else Random.Float(+64, +128), 0)
                angularSpeed = if (speed.x < 0) -180 else +180
                acc.set(-speed.x, 0)
                lifespan = 0.5f
            }
            LIGHT -> {
                angle = Random.Float(360)
                angularSpeed = 90
                lifespan = 1f
            }
            DISCOVER -> {
                angle = Random.Float(360)
                angularSpeed = 90
                lifespan = 0.5f
                am = 0
            }
            QUESTION -> lifespan = 0.8f
            UP -> {
                speed.set(0, -20)
                lifespan = 1f
            }
            SCREAM -> lifespan = 0.9f
            BONE -> {
                lifespan = 0.2f
                speed.polar(Random.Float(2 * 3.1415926f), 24 / lifespan)
                acc.set(0, 128)
                angle = Random.Float(360)
                angularSpeed = 360
            }
            RATTLE -> {
                lifespan = 0.5f
                speed.set(0, -200)
                acc.set(0, -2 * speed.y / lifespan)
                angle = Random.Float(360)
                angularSpeed = 360
            }
            WOOL -> {
                lifespan = 0.5f
                speed.set(0, -50)
                angle = Random.Float(360)
                angularSpeed = Random.Float(-360, +360)
            }
            ROCK -> {
                angle = Random.Float(360)
                angularSpeed = Random.Float(-360, +360)
                scale.set(Random.Float(1, 2))
                speed.set(0, 64)
                lifespan = 0.2f
                y -= speed.y * lifespan
            }
            NOTE -> {
                angularSpeed = Random.Float(-30, +30)
                speed.polar((angularSpeed - 90) * PointF.G2R, 30)
                lifespan = 1f
            }
            CHANGE -> {
                angle = Random.Float(360)
                speed.polar((angle - 90) * PointF.G2R, Random.Float(4, 12))
                lifespan = 1.5f
            }
            HEART -> {
                speed.set(Random.Int(-10, +10), -40)
                angularSpeed = Random.Float(-45, +45)
                lifespan = 1f
            }
            BUBBLE -> {
                speed.set(0, -15)
                scale.set(Random.Float(0.8f, 1))
                lifespan = Random.Float(0.8f, 1.5f)
            }
            STEAM -> {
                speed.y = -Random.Float(20, 30)
                angularSpeed = Random.Float(+180)
                angle = Random.Float(360)
                lifespan = 1f
            }
            JET -> {
                speed.y = +32
                acc.y = -64
                angularSpeed = Random.Float(180, 360)
                angle = Random.Float(360)
                lifespan = 0.5f
            }
            TOXIC -> {
                hardlight(0x50FF60)
                angularSpeed = 30
                angle = Random.Float(360)
                lifespan = Random.Float(1f, 3f)
            }
            PARALYSIS -> {
                hardlight(0xFFFF66)
                angularSpeed = -30
                angle = Random.Float(360)
                lifespan = Random.Float(1f, 3f)
            }
            CONFUSION -> {
                hardlight(Random.Int(0x1000000) or 0x000080)
                angularSpeed = Random.Float(-20, +20)
                angle = Random.Float(360)
                lifespan = Random.Float(1f, 3f)
            }
            DUST -> {
                hardlight(0xFFFF66)
                angle = Random.Float(360)
                speed.polar(Random.Float(2 * 3.1415926f), Random.Float(16, 48))
                lifespan = 0.5f
            }
            COIN -> {
                speed.polar(-PointF.PI * Random.Float(0.3f, 0.7f), Random.Float(48, 96))
                acc.y = 256
                lifespan = -speed.y / acc.y * 2
            }
        }
        left = lifespan
    }

    @SuppressLint("FloatMath")
    fun update() {
        super.update()
        left -= Game.elapsed
        if (left <= 0) {
            kill()
        } else {
            val p = 1 - left / lifespan // 0 -> 1
            when (type) {
                STAR, FORGE -> {
                    scale.set(1 - p)
                    am = if (p < 0.2f) p * 5f else (1 - p) * 1.25f
                }
                KIT, MASTERY -> am = 1 - p * p
                EVOKE, HEALING -> am = if (p < 0.5f) 1 else 2 - p * 2
                LIGHT -> am = scale.set(if (p < 0.2f) p * 5f else (1 - p) * 1.25f).x
                DISCOVER -> {
                    am = 1 - p
                    scale.set((if (p < 0.5f) p else 1 - p) * 2)
                }
                QUESTION -> scale.set((Math.sqrt(if (p < 0.5f) p else 1 - p.toDouble()) * 3).toFloat())
                UP -> scale.set((Math.sqrt(if (p < 0.5f) p else 1 - p.toDouble()) * 2).toFloat())
                SCREAM -> {
                    am = Math.sqrt(((if (p < 0.5f) p else 1 - p) * 2f).toDouble()).toFloat()
                    scale.set(p * 7)
                }
                BONE, RATTLE -> am = if (p < 0.9f) 1 else (1 - p) * 10
                ROCK -> am = if (p < 0.2f) p * 5 else 1
                NOTE -> am = 1 - p * p
                WOOL -> scale.set(1 - p)
                CHANGE -> {
                    am = FloatMath.sqrt((if (p < 0.5f) p else 1 - p) * 2) as Float
                    scale.y = (1 + p) * 0.5f
                    scale.x = scale.y * FloatMath.cos(left * 15)
                }
                HEART -> {
                    scale.set(1 - p)
                    am = 1 - p * p
                }
                BUBBLE -> am = if (p < 0.2f) p * 5 else 1
                STEAM, TOXIC, PARALYSIS, CONFUSION, DUST -> {
                    am = if (p < 0.5f) p else 1 - p
                    scale.set(1 + p * 2)
                }
                JET -> {
                    am = (if (p < 0.5f) p else 1 - p) * 2
                    scale.set(p * 1.5f)
                }
                COIN -> {
                    scale.x = FloatMath.cos(left * 5)
                    run {
                        bm = (Math.abs(scale.x) + 1) * 0.5f
                        gm = bm
                        rm = gm
                    }
                    am = if (p < 0.9f) 1 else (1 - p) * 10
                }
            }
        }
    }

    companion object {
        const val HEALING = 0
        const val STAR = 1
        const val LIGHT = 2
        const val QUESTION = 3
        const val UP = 4
        const val SCREAM = 5
        const val BONE = 6
        const val WOOL = 7
        const val ROCK = 8
        const val NOTE = 9
        const val CHANGE = 10
        const val HEART = 11
        const val BUBBLE = 12
        const val STEAM = 13
        const val COIN = 14
        const val DISCOVER = 101
        const val EVOKE = 102
        const val MASTERY = 103
        const val KIT = 104
        const val RATTLE = 105
        const val JET = 106
        const val TOXIC = 107
        const val PARALYSIS = 108
        const val DUST = 109
        const val FORGE = 110
        const val CONFUSION = 111
        private const val SIZE = 7
        private var film: TextureFilm? = null
        private val factories: SparseArray<Emitter.Factory> = SparseArray<Emitter.Factory>()
        fun factory(type: Int): Emitter.Factory? {
            return factory(type, false)
        }

        fun factory(type: Int, lightMode: Boolean): Emitter.Factory? {
            var factory: Emitter.Factory = factories.get(type)
            if (factory == null) {
                factory = object : Factory() {
                    fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                        val p = emitter.recycle(Speck::class.java) as Speck
                        p.reset(index, x, y, type)
                    }

                    fun lightMode(): Boolean {
                        return lightMode
                    }
                }
                factories.put(type, factory)
            }
            return factory
        }
    }

    init {
        texture(Assets.SPECKS)
        if (film == null) {
            film = TextureFilm(texture, SIZE, SIZE)
        }
        origin.set(SIZE / 2f)
    }
}