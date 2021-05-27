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
package com.watabou.pixeldungeon.scenes

import com.watabou.input.Touchscreen.Touch

class CellSelector(map: DungeonTilemap?) : TouchArea(map) {
    var listener: Listener? = null
    var enabled = false
    private val dragThreshold: Float
    protected fun onClick(touch: Touch) {
        if (dragging) {
            dragging = false
        } else {
            select(
                (target as DungeonTilemap).screenToTile(
                    touch.current.x as Int,
                    touch.current.y as Int
                )
            )
        }
    }

    fun select(cell: Int) {
        if (enabled && listener != null && cell != -1) {
            listener!!.onSelect(cell)
            GameScene.ready()
        } else {
            GameScene.cancel()
        }
    }

    private var pinching = false
    private var another: Touch? = null
    private var startZoom = 0f
    private var startSpan = 0f
    protected fun onTouchDown(t: Touch) {
        if (t !== touch && another == null) {
            if (!touch.down) {
                touch = t
                onTouchDown(t)
                return
            }
            pinching = true
            another = t
            startSpan = PointF.distance(touch.current, another.current)
            startZoom = camera.zoom
            dragging = false
        }
    }

    protected fun onTouchUp(t: Touch) {
        if (pinching && (t === touch || t === another)) {
            pinching = false
            val zoom = Math.round(camera.zoom).toInt()
            camera.zoom(zoom)
            PixelDungeon.zoom((zoom - PixelScene.defaultZoom) as Int)
            dragging = true
            if (t === touch) {
                touch = another
            }
            another = null
            lastPos.set(touch.current)
        }
    }

    private var dragging = false
    private val lastPos: PointF = PointF()
    protected fun onDrag(t: Touch) {
        camera.target = null
        if (pinching) {
            val curSpan: Float = PointF.distance(touch.current, another.current)
            camera.zoom(
                GameMath.gate(
                    PixelScene.minZoom,
                    startZoom * curSpan / startSpan,
                    PixelScene.maxZoom
                )
            )
        } else {
            if (!dragging && PointF.distance(t.current, t.start) > dragThreshold) {
                dragging = true
                lastPos.set(t.current)
            } else if (dragging) {
                camera.scroll.offset(PointF.diff(lastPos, t.current).invScale(camera.zoom))
                lastPos.set(t.current)
            }
        }
    }

    fun cancel() {
        if (listener != null) {
            listener!!.onSelect(null)
        }
        GameScene.ready()
    }

    interface Listener {
        fun onSelect(cell: Int?)
        fun prompt(): String?
    }

    init {
        camera = map.camera()
        dragThreshold = PixelScene.defaultZoom * DungeonTilemap.SIZE / 2
    }
}