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

import com.watabou.gltextures.Gradient

class Flare @SuppressLint("FloatMath") constructor(nRays: Int, radius: Float) : Visual(0, 0, 0, 0) {
    private var duration = 0f
    private var lifespan = 0f
    private var lightMode = true
    private val texture: SmartTexture
    private val vertices: FloatBuffer
    private val indices: ShortBuffer
    private val nRays: Int
    fun color(color: Int, lightMode: Boolean): Flare {
        this.lightMode = lightMode
        hardlight(color)
        return this
    }

    fun show(visual: Visual, duration: Float): Flare {
        point(visual.center())
        visual.parent.addToBack(this)
        this.duration = duration
        lifespan = this.duration
        return this
    }

    fun show(parent: Group, pos: PointF?, duration: Float): Flare {
        point(pos)
        parent.add(this)
        this.duration = duration
        lifespan = this.duration
        return this
    }

    fun update() {
        super.update()
        if (duration > 0) {
            if (Game.elapsed.let { lifespan -= it; lifespan } > 0) {
                var p = 1 - lifespan / duration // 0 -> 1
                p = if (p < 0.25f) p * 4 else (1 - p) * 1.333f
                scale.set(p)
                alpha(p)
            } else {
                killAndErase()
            }
        }
    }

    fun draw() {
        super.draw()
        if (lightMode) {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            drawRays()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            drawRays()
        }
    }

    private fun drawRays() {
        val script: NoosaScript = NoosaScript.get()
        texture.bind()
        script.uModel.valueM4(matrix)
        script.lighting(
            rm, gm, bm, am,
            ra, ga, ba, aa
        )
        script.camera(camera)
        script.drawElements(vertices, indices, nRays * 3)
    }

    init {
        val gradient = intArrayOf(-0x1, 0x00FFFFFF)
        texture = Gradient(gradient)
        this.nRays = nRays
        angle = 45
        angularSpeed = 180
        vertices =
            ByteBuffer.allocateDirect((nRays * 2 + 1) * 4 * (java.lang.Float.SIZE / 8)).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        indices = ByteBuffer.allocateDirect(nRays * 3 * java.lang.Short.SIZE / 8).order(ByteOrder.nativeOrder())
            .asShortBuffer()
        val v = FloatArray(4)
        v[0] = 0
        v[1] = 0
        v[2] = 0.25f
        v[3] = 0
        vertices.put(v)
        v[2] = 0.75f
        v[3] = 0
        for (i in 0 until nRays) {
            var a = i * 3.1415926f * 2 / nRays
            v[0] = FloatMath.cos(a) * radius
            v[1] = FloatMath.sin(a) * radius
            vertices.put(v)
            a += 3.1415926f * 2 / nRays / 2
            v[0] = FloatMath.cos(a) * radius
            v[1] = FloatMath.sin(a) * radius
            vertices.put(v)
            indices.put(0.toShort())
            indices.put((1 + i * 2).toShort())
            indices.put((2 + i * 2).toShort())
        }
        indices.position(0)
    }
}