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
package com.watabou.pixeldungeon.ui

import com.watabou.input.Touchscreen.Touch

class ScrollPane(content: Component) : Component() {
    protected var controller: TouchController? = null
    protected var content: Component
    protected var thumb: ColorBlock? = null
    protected var minX = 0f
    protected var minY = 0f
    protected var maxX = 0f
    protected var maxY = 0f
    fun destroy() {
        super.destroy()
        Camera.remove(content.camera)
    }

    fun scrollTo(x: Float, y: Float) {
        content.camera.scroll.set(x, y)
    }

    protected fun createChildren() {
        controller = TouchController()
        add(controller)
        thumb = ColorBlock(1, 1, THUMB_COLOR)
        thumb.am = THUMB_ALPHA
        add(thumb)
    }

    protected fun layout() {
        content.setPos(0, 0)
        controller.x = x
        controller.y = y
        controller.width = width
        controller.height = height
        val p: Point = camera().cameraToScreen(x, y)
        val cs: Camera = content.camera
        cs.x = p.x
        cs.y = p.y
        cs.resize(width as Int, height as Int)
        thumb.visible = height < content.height()
        if (thumb.visible) {
            thumb.scale.set(2, height * height / content.height())
            thumb.x = right() - thumb.width()
            thumb.y = y
        }
    }

    fun content(): Component {
        return content
    }

    fun onClick(x: Float, y: Float) {}
    inner class TouchController : TouchArea(0, 0, 0, 0) {
        private val dragThreshold: Float
        protected fun onClick(touch: Touch) {
            if (dragging) {
                dragging = false
                thumb.am = THUMB_ALPHA
            } else {
                val p: PointF = content.camera.screenToCamera(touch.current.x as Int, touch.current.y as Int)
                this@ScrollPane.onClick(p.x, p.y)
            }
        }

        private var dragging = false
        private val lastPos: PointF = PointF()
        protected fun onDrag(t: Touch) {
            if (dragging) {
                val c: Camera = content.camera
                c.scroll.offset(PointF.diff(lastPos, t.current).invScale(c.zoom))
                if (c.scroll.x + width > content.width()) {
                    c.scroll.x = content.width() - width
                }
                if (c.scroll.x < 0) {
                    c.scroll.x = 0
                }
                if (c.scroll.y + height > content.height()) {
                    c.scroll.y = content.height() - height
                }
                if (c.scroll.y < 0) {
                    c.scroll.y = 0
                }
                thumb.y = y + height * c.scroll.y / content.height()
                lastPos.set(t.current)
            } else if (PointF.distance(t.current, t.start) > dragThreshold) {
                dragging = true
                lastPos.set(t.current)
                thumb.am = 1
            }
        }

        init {
            dragThreshold = PixelScene.defaultZoom * 8
        }
    }

    companion object {
        protected const val THUMB_COLOR = -0x847f8d
        protected const val THUMB_ALPHA = 0.5f
    }

    init {
        this.content = content
        addToBack(content)
        width = content.width()
        height = content.height()
        content.camera = Camera(0, 0, 1, 1, PixelScene.defaultZoom)
        Camera.add(content.camera)
    }
}