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

class Lightning(cells: IntArray, length: Int, callback: Callback?) : Group() {
    private var life: Float
    private val length: Int
    private val cx: FloatArray
    private val cy: FloatArray
    private val arcsS: Array<Image?>
    private val arcsE: Array<Image>
    private val callback: Callback?
    fun update() {
        super.update()
        if (Game.elapsed.let { life -= it; life } < 0) {
            killAndErase()
            if (callback != null) {
                callback.call()
            }
        } else {
            val alpha = life / DURATION
            for (i in 0 until length - 1) {
                val sx = cx[i]
                val sy = cy[i]
                val ex = cx[i + 1]
                val ey = cy[i + 1]
                val x2: Float = (sx + ex) / 2 + Random.Float(-4, +4)
                val y2: Float = (sy + ey) / 2 + Random.Float(-4, +4)
                var dx = x2 - sx
                var dy = y2 - sy
                var arc: Image? = arcsS[i]
                arc.am = alpha
                arc.angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
                arc.scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / arc.width
                dx = ex - x2
                dy = ey - y2
                arc = arcsE[i]
                arc.am = alpha
                arc.angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
                arc.scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / arc.width
                arc.x = x2 - arc.origin.x
                arc.y = y2 - arc.origin.x
            }
        }
    }

    fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    companion object {
        private const val DURATION = 0.3f
        private const val A = 180 / Math.PI
    }

    init {
        this.callback = callback
        val proto: Image = Effects.get(Effects.Type.LIGHTNING)
        val ox = 0f
        val oy: Float = proto.height / 2
        this.length = length
        cx = FloatArray(length)
        cy = FloatArray(length)
        for (i in 0 until length) {
            val c = cells[i]
            cx[i] = (c % Level.WIDTH + 0.5f) * DungeonTilemap.SIZE
            cy[i] = (c / Level.WIDTH + 0.5f) * DungeonTilemap.SIZE
        }
        arcsS = arrayOfNulls<Image>(length - 1)
        arcsE = arrayOfNulls<Image>(length - 1)
        for (i in 0 until length - 1) {
            arcsS[i] = Image(proto)
            var arc: Image? = arcsS[i]
            arc.x = cx[i] - arc.origin.x
            arc.y = cy[i] - arc.origin.y
            arc.origin.set(ox, oy)
            add(arc)
            arcsE[i] = Image(proto)
            arc = arcsE[i]
            arc.origin.set(ox, oy)
            add(arc)
        }
        life = DURATION
        Sample.INSTANCE.play(Assets.SND_LIGHTNING)
    }
}