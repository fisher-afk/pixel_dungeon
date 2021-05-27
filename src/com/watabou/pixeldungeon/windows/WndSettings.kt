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
package com.watabou.pixeldungeon.windows

import android.os.Build
import com.watabou.noosa.Camera

class WndSettings(inGame: Boolean) : Window() {
    private var btnZoomOut: RedButton? = null
    private var btnZoomIn: RedButton? = null
    private fun zoom(value: Float) {
        Camera.main.zoom(value)
        PixelDungeon.zoom((value - PixelScene.defaultZoom) as Int)
        updateEnabled()
    }

    private fun updateEnabled() {
        val zoom: Float = Camera.main.zoom
        btnZoomIn.enable(zoom < PixelScene.maxZoom)
        btnZoomOut.enable(zoom > PixelScene.minZoom)
    }

    private fun orientationText(): String {
        return if (PixelDungeon.landscape()) TXT_SWITCH_PORT else TXT_SWITCH_LAND
    }

    companion object {
        private const val TXT_ZOOM_IN = "+"
        private const val TXT_ZOOM_OUT = "-"
        private const val TXT_ZOOM_DEFAULT = "Default Zoom"
        private const val TXT_SCALE_UP = "Scale up UI"
        private const val TXT_IMMERSIVE = "Immersive mode"
        private const val TXT_MUSIC = "Music"
        private const val TXT_SOUND = "Sound FX"
        private const val TXT_BRIGHTNESS = "Brightness"
        private const val TXT_QUICKSLOT = "Second quickslot"
        private const val TXT_SWITCH_PORT = "Switch to portrait"
        private const val TXT_SWITCH_LAND = "Switch to landscape"
        private const val WIDTH = 112
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }

    init {
        var btnImmersive: CheckBox? = null
        if (inGame) {
            val w = BTN_HEIGHT
            btnZoomOut = object : RedButton(TXT_ZOOM_OUT) {
                protected fun onClick() {
                    zoom(Camera.main.zoom - 1)
                }
            }
            add(btnZoomOut.setRect(0, 0, w, BTN_HEIGHT))
            btnZoomIn = object : RedButton(TXT_ZOOM_IN) {
                protected fun onClick() {
                    zoom(Camera.main.zoom + 1)
                }
            }
            add(btnZoomIn.setRect(WIDTH - w, 0, w, BTN_HEIGHT))
            add(object : RedButton(TXT_ZOOM_DEFAULT) {
                protected fun onClick() {
                    zoom(PixelScene.defaultZoom)
                }
            }.setRect(btnZoomOut.right(), 0, WIDTH - btnZoomIn.width() - btnZoomOut.width(), BTN_HEIGHT))
            updateEnabled()
        } else {
            val btnScaleUp: CheckBox = object : CheckBox(TXT_SCALE_UP) {
                protected fun onClick() {
                    super.onClick()
                    PixelDungeon.scaleUp(checked())
                }
            }
            btnScaleUp.setRect(0, 0, WIDTH, BTN_HEIGHT)
            btnScaleUp.checked(PixelDungeon.scaleUp())
            add(btnScaleUp)
            btnImmersive = object : CheckBox(TXT_IMMERSIVE) {
                protected fun onClick() {
                    super.onClick()
                    PixelDungeon.immerse(checked())
                }
            }
            btnImmersive.setRect(0, btnScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT)
            btnImmersive.checked(PixelDungeon.immersed())
            btnImmersive.enable(Build.VERSION.SDK_INT >= 19)
            add(btnImmersive)
        }
        val btnMusic: CheckBox = object : CheckBox(TXT_MUSIC) {
            protected fun onClick() {
                super.onClick()
                PixelDungeon.music(checked())
            }
        }
        btnMusic.setRect(0, (if (btnImmersive != null) btnImmersive.bottom() else BTN_HEIGHT) + GAP, WIDTH, BTN_HEIGHT)
        btnMusic.checked(PixelDungeon.music())
        add(btnMusic)
        val btnSound: CheckBox = object : CheckBox(TXT_SOUND) {
            protected fun onClick() {
                super.onClick()
                PixelDungeon.soundFx(checked())
                Sample.INSTANCE.play(Assets.SND_CLICK)
            }
        }
        btnSound.setRect(0, btnMusic.bottom() + GAP, WIDTH, BTN_HEIGHT)
        btnSound.checked(PixelDungeon.soundFx())
        add(btnSound)
        if (inGame) {
            val btnBrightness: CheckBox = object : CheckBox(TXT_BRIGHTNESS) {
                protected fun onClick() {
                    super.onClick()
                    PixelDungeon.brightness(checked())
                }
            }
            btnBrightness.setRect(0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT)
            btnBrightness.checked(PixelDungeon.brightness())
            add(btnBrightness)
            val btnQuickslot: CheckBox = object : CheckBox(TXT_QUICKSLOT) {
                protected fun onClick() {
                    super.onClick()
                    Toolbar.secondQuickslot(checked())
                }
            }
            btnQuickslot.setRect(0, btnBrightness.bottom() + GAP, WIDTH, BTN_HEIGHT)
            btnQuickslot.checked(Toolbar.secondQuickslot())
            add(btnQuickslot)
            resize(WIDTH, btnQuickslot.bottom() as Int)
        } else {
            val btnOrientation: RedButton = object : RedButton(orientationText()) {
                protected fun onClick() {
                    PixelDungeon.landscape(!PixelDungeon.landscape())
                }
            }
            btnOrientation.setRect(0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT)
            add(btnOrientation)
            resize(WIDTH, btnOrientation.bottom() as Int)
        }
    }
}