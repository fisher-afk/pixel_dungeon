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

import com.watabou.input.Touchscreen

class PixelScene : Scene() {
    fun create() {
        super.create()
        GameScene.scene = null
        val minWidth: Float
        val minHeight: Float
        if (PixelDungeon.landscape()) {
            minWidth = MIN_WIDTH_L
            minHeight = MIN_HEIGHT_L
        } else {
            minWidth = MIN_WIDTH_P
            minHeight = MIN_HEIGHT_P
        }
        defaultZoom = Math.ceil(Game.density * 2.5) as Int.toFloat()
        while ((Game.width / defaultZoom < minWidth ||
                    Game.height / defaultZoom < minHeight) && defaultZoom > 1
        ) {
            defaultZoom--
        }
        if (PixelDungeon.scaleUp()) {
            while (Game.width / (defaultZoom + 1) >= minWidth &&
                Game.height / (defaultZoom + 1) >= minHeight
            ) {
                defaultZoom++
            }
        }
        minZoom = 1f
        maxZoom = defaultZoom * 2
        Camera.reset(PixelCamera(defaultZoom))
        val uiZoom = defaultZoom
        uiCamera = Camera.createFullscreen(uiZoom)
        Camera.add(uiCamera)
        if (font1x == null) {

            // 3x5 (6)
            font1x = Font.colorMarked(
                BitmapCache.get(Assets.FONTS1X), 0x00000000, BitmapText.Font.LATIN_FULL
            )
            font1x.baseLine = 6
            font1x.tracking = -1

            // 5x8 (10)
            font15x = Font.colorMarked(
                BitmapCache.get(Assets.FONTS15X), 12, 0x00000000, BitmapText.Font.LATIN_FULL
            )
            font15x.baseLine = 9
            font15x.tracking = -1

            // 6x10 (12)
            font2x = Font.colorMarked(
                BitmapCache.get(Assets.FONTS2X), 14, 0x00000000, BitmapText.Font.LATIN_FULL
            )
            font2x.baseLine = 11
            font2x.tracking = -1

            // 7x12 (15)
            font25x = Font.colorMarked(
                BitmapCache.get(Assets.FONTS25X), 17, 0x00000000, BitmapText.Font.LATIN_FULL
            )
            font25x.baseLine = 13
            font25x.tracking = -1

            // 9x15 (18)
            font3x = Font.colorMarked(
                BitmapCache.get(Assets.FONTS3X), 22, 0x00000000, BitmapText.Font.LATIN_FULL
            )
            font3x.baseLine = 17
            font3x.tracking = -2
        }
    }

    fun destroy() {
        super.destroy()
        Touchscreen.event.removeAll()
    }

    protected fun fadeIn() {
        if (noFade) {
            noFade = false
        } else {
            fadeIn(-0x1000000, false)
        }
    }

    protected fun fadeIn(color: Int, light: Boolean) {
        add(Fader(color, light))
    }

    protected class Fader(color: Int, private val light: Boolean) : ColorBlock(uiCamera.width, uiCamera.height, color) {
        private var time: Float
        fun update() {
            super.update()
            if (Game.elapsed.let { time -= it; time } <= 0) {
                alpha(0f)
                parent.remove(this)
            } else {
                alpha(time / FADE_TIME)
            }
        }

        fun draw() {
            if (light) {
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
                super.draw()
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            } else {
                super.draw()
            }
        }

        companion object {
            private const val FADE_TIME = 1f
        }

        init {
            camera = uiCamera
            alpha(1f)
            time = FADE_TIME
        }
    }

    private class PixelCamera(zoom: Float) : Camera(
        (Game.width - Math.ceil(Game.width / zoom) * zoom) as Int / 2,
        (Game.height - Math.ceil(Game.height / zoom) * zoom) as Int / 2,
        Math.ceil(Game.width / zoom).toInt(),
        Math.ceil(Game.height / zoom).toInt(), zoom
    ) {
        protected fun updateMatrix() {
            val sx = align(this, scroll.x + shakeX)
            val sy = align(this, scroll.y + shakeY)
            matrix.get(0) = +zoom * invW2
            matrix.get(5) = -zoom * invH2
            matrix.get(12) = -1 + x * invW2 - sx * matrix.get(0)
            matrix.get(13) = +1 - y * invH2 - sy * matrix.get(5)
        }
    }

    companion object {
        // Minimum virtual display size for portrait orientation
        const val MIN_WIDTH_P = 128f
        const val MIN_HEIGHT_P = 224f

        // Minimum virtual display size for landscape orientation
        const val MIN_WIDTH_L = 224f
        const val MIN_HEIGHT_L = 160f
        var defaultZoom = 0f
        var minZoom = 0f
        var maxZoom = 0f
        var uiCamera: Camera? = null
        var font1x: BitmapText.Font? = null
        var font15x: BitmapText.Font? = null
        var font2x: BitmapText.Font? = null
        var font25x: BitmapText.Font? = null
        var font3x: BitmapText.Font? = null
        var font: BitmapText.Font? = null
        var scale = 0f
        @JvmOverloads
        fun chooseFont(size: Float, zoom: Float = defaultZoom) {
            val pt = size * zoom
            if (pt >= 19) {
                scale = pt / 19
                if (1.5 <= scale && scale < 2) {
                    font = font25x
                    scale = (pt / 14) as Int.toFloat()
                } else {
                    font = font3x
                    scale = scale as Int.toFloat()
                }
            } else if (pt >= 14) {
                scale = pt / 14
                if (1.8 <= scale && scale < 2) {
                    font = font2x
                    scale = (pt / 12) as Int.toFloat()
                } else {
                    font = font25x
                    scale = scale as Int.toFloat()
                }
            } else if (pt >= 12) {
                scale = pt / 12
                if (1.7 <= scale && scale < 2) {
                    font = font15x
                    scale = (pt / 10) as Int.toFloat()
                } else {
                    font = font2x
                    scale = scale as Int.toFloat()
                }
            } else if (pt >= 10) {
                scale = pt / 10
                if (1.4 <= scale && scale < 2) {
                    font = font1x
                    scale = (pt / 7) as Int.toFloat()
                } else {
                    font = font15x
                    scale = scale as Int.toFloat()
                }
            } else {
                font = font1x
                scale = Math.max(1, (pt / 7).toInt()).toFloat()
            }
            scale /= zoom
        }

        fun createText(size: Float): BitmapText {
            return createText(null, size)
        }

        fun createText(text: String?, size: Float): BitmapText {
            chooseFont(size)
            val result = BitmapText(text, font)
            result.scale.set(scale)
            return result
        }

        fun createMultiline(size: Float): BitmapTextMultiline {
            return createMultiline(null, size)
        }

        fun createMultiline(text: String?, size: Float): BitmapTextMultiline {
            chooseFont(size)
            val result = BitmapTextMultiline(text, font)
            result.scale.set(scale)
            return result
        }

        fun align(camera: Camera, pos: Float): Float {
            return (pos * camera.zoom) as Int / camera.zoom
        }

        // This one should be used for UI elements
        fun align(pos: Float): Float {
            return (pos * defaultZoom).toInt() / defaultZoom
        }

        fun align(v: Visual) {
            val c: Camera = v.camera()
            v.x = align(c, v.x)
            v.y = align(c, v.y)
        }

        var noFade = false
        fun showBadge(badge: Badges.Badge) {
            val banner: BadgeBanner = BadgeBanner.show(badge.image)
            banner.camera = uiCamera
            banner.x = align(banner.camera, (banner.camera.width - banner.width) / 2)
            banner.y = align(banner.camera, (banner.camera.height - banner.height) / 3)
            Game.scene().add(banner)
        }
    }
}