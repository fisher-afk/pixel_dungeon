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

import com.watabou.noosa.BitmapTextMultiline

class IconTitle : Component {
    protected var imIcon: Image? = null
    protected var tfLabel: BitmapTextMultiline? = null
    protected var health: HealthBar? = null
    private var healthLvl = Float.NaN

    constructor() : super() {}
    constructor(item: Item) : this(
        ItemSprite(item.image(), item.glowing()),
        Utils.capitalize(item.toString())
    ) {
    }

    constructor(icon: Image?, label: String?) : super() {
        icon(icon)
        label(label)
    }

    protected fun createChildren() {
        imIcon = Image()
        add(imIcon)
        tfLabel = PixelScene.createMultiline(FONT_SIZE)
        tfLabel.hardlight(Window.TITLE_COLOR)
        add(tfLabel)
        health = HealthBar()
        add(health)
    }

    protected fun layout() {
        health.visible = !java.lang.Float.isNaN(healthLvl)
        imIcon.x = x
        imIcon.y = y
        tfLabel.x = PixelScene.align(PixelScene.uiCamera, imIcon.x + imIcon.width() + GAP)
        tfLabel.maxWidth = (width - tfLabel.x)
        tfLabel.measure()
        tfLabel.y = PixelScene.align(
            PixelScene.uiCamera,
            if (imIcon.height > tfLabel.height()) imIcon.y + (imIcon.height() - tfLabel.baseLine()) / 2 else imIcon.y
        )
        if (health.visible) {
            health.setRect(
                tfLabel.x,
                Math.max(tfLabel.y + tfLabel.height(), imIcon.y + imIcon.height() - health.height()),
                tfLabel.maxWidth,
                0
            )
            height = health.bottom()
        } else {
            height = Math.max(imIcon.y + imIcon.height(), tfLabel.y + tfLabel.height())
        }
    }

    fun icon(icon: Image) {
        remove(imIcon)
        add(icon.also { imIcon = it })
    }

    fun label(label: String?) {
        tfLabel.text(label)
    }

    fun label(label: String?, color: Int) {
        tfLabel.text(label)
        tfLabel.hardlight(color)
    }

    fun color(color: Int) {
        tfLabel.hardlight(color)
    }

    fun health(value: Float) {
        health.level(value.also { healthLvl = it })
        layout()
    }

    companion object {
        private const val FONT_SIZE = 9f
        private const val GAP = 2f
    }
}