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

import com.watabou.input.Keys

class Window @JvmOverloads constructor(
    width: Int = 0,
    height: Int = 0,
    chrome: NinePatch = Chrome.get(Chrome.Type.WINDOW)
) : Group(), Signal.Listener<Key?> {
    protected var width: Int
    protected var height: Int
    protected var blocker: TouchArea
    protected var shadow: ShadowBox
    protected var chrome: NinePatch

    constructor(width: Int, height: Int) : this(width, height, Chrome.get(Chrome.Type.WINDOW)) {}

    fun resize(w: Int, h: Int) {
        width = w
        height = h
        chrome.size(
            width + chrome.marginHor(),
            height + chrome.marginVer()
        )
        camera.resize(chrome.width as Int, chrome.height as Int)
        camera.x = (Game.width - camera.screenWidth()) as Int / 2
        camera.y = (Game.height - camera.screenHeight()) as Int / 2
        shadow.boxRect(camera.x / camera.zoom, camera.y / camera.zoom, chrome.width(), chrome.height)
    }

    fun hide() {
        parent.erase(this)
        destroy()
    }

    fun destroy() {
        super.destroy()
        Camera.remove(camera)
        Keys.event.remove(this)
    }

    fun onSignal(key: Key) {
        if (key.pressed) {
            when (key.code) {
                Keys.BACK -> onBackPressed()
                Keys.MENU -> onMenuPressed()
            }
        }
        Keys.event.cancel()
    }

    fun onBackPressed() {
        hide()
    }

    fun onMenuPressed() {}

    companion object {
        const val TITLE_COLOR = 0xFFFF44
    }

    init {
        blocker = object : TouchArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height) {
            protected fun onClick(touch: Touch) {
                if (!this@Window.chrome.overlapsScreenPoint(
                        touch.current.x as Int,
                        touch.current.y as Int
                    )
                ) {
                    onBackPressed()
                }
            }
        }
        blocker.camera = PixelScene.uiCamera
        add(blocker)
        this.chrome = chrome
        this.width = width
        this.height = height
        shadow = ShadowBox()
        shadow.am = 0.5f
        shadow.camera = if (PixelScene.uiCamera.visible) PixelScene.uiCamera else Camera.main
        add(shadow)
        chrome.x = -chrome.marginLeft()
        chrome.y = -chrome.marginTop()
        chrome.size(
            width - chrome.x + chrome.marginRight(),
            height - chrome.y + chrome.marginBottom()
        )
        add(chrome)
        camera = Camera(
            0, 0,
            chrome.width as Int,
            chrome.height as Int,
            PixelScene.defaultZoom
        )
        camera.x = (Game.width - camera.width * camera.zoom) as Int / 2
        camera.y = (Game.height - camera.height * camera.zoom) as Int / 2
        camera.scroll.set(chrome.x, chrome.y)
        Camera.add(camera)
        shadow.boxRect(
            camera.x / camera.zoom,
            camera.y / camera.zoom,
            chrome.width(), chrome.height
        )
        Keys.event.add(this)
    }
}