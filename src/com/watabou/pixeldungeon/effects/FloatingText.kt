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

import com.watabou.noosa.BitmapText

class FloatingText : BitmapText() {
    private var timeLeft = 0f
    private var key = -1
    private var cameraZoom = -1f
    fun update() {
        super.update()
        if (timeLeft > 0) {
            if (Game.elapsed.let { timeLeft -= it; timeLeft } <= 0) {
                kill()
            } else {
                val p = timeLeft / LIFESPAN
                alpha(if (p > 0.5f) 1 else p * 2)
            }
        }
    }

    fun kill() {
        if (key != -1) {
            stacks.get(key).remove(this)
            key = -1
        }
        super.kill()
    }

    fun destroy() {
        kill()
        super.destroy()
    }

    fun reset(x: Float, y: Float, text: String?, color: Int) {
        revive()
        if (cameraZoom != Camera.main.zoom) {
            cameraZoom = Camera.main.zoom
            PixelScene.chooseFont(9, cameraZoom)
            font = PixelScene.font
            scale.set(PixelScene.scale)
        }
        text(text)
        hardlight(color)
        measure()
        x = PixelScene.align(x - width() / 2)
        y = y - height()
        timeLeft = LIFESPAN
    }

    companion object {
        private const val LIFESPAN = 1f
        private val DISTANCE: Float = DungeonTilemap.SIZE
        private val stacks: SparseArray<ArrayList<FloatingText>> = SparseArray<ArrayList<FloatingText>>()

        /* STATIC METHODS */
        fun show(x: Float, y: Float, text: String?, color: Int) {
            GameScene.status().reset(x, y, text, color)
        }

        fun show(x: Float, y: Float, key: Int, text: String?, color: Int) {
            val txt: FloatingText = GameScene.status()
            txt.reset(x, y, text, color)
            push(txt, key)
        }

        private fun push(txt: FloatingText, key: Int) {
            txt.key = key
            var stack: ArrayList<FloatingText> = stacks.get(key)
            if (stack == null) {
                stack = ArrayList()
                stacks.put(key, stack)
            }
            if (stack.size > 0) {
                var below = txt
                var aboveIndex = stack.size - 1
                while (aboveIndex >= 0) {
                    val above = stack[aboveIndex]
                    if (above.y + above.height() > below.y) {
                        above.y = below.y - above.height()
                        below = above
                        aboveIndex--
                    } else {
                        break
                    }
                }
            }
            stack.add(txt)
        }
    }

    init {
        speed.y = -DISTANCE / LIFESPAN
    }
}