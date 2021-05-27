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

import com.watabou.gltextures.TextureCache

class HealthIndicator : Component() {
    private var target: Char? = null
    private var bg: Image? = null
    private var level: Image? = null
    protected fun createChildren() {
        bg = Image(TextureCache.createSolid(-0x340000))
        bg.scale.y = HEIGHT
        add(bg)
        level = Image(TextureCache.createSolid(-0xff3400))
        level.scale.y = HEIGHT
        add(level)
    }

    fun update() {
        super.update()
        if (target != null && target.isAlive() && target.sprite.visible) {
            val sprite: CharSprite = target.sprite
            bg.scale.x = sprite.width
            level.scale.x = sprite.width * target.HP / target.HT
            level.x = sprite.x
            bg.x = level.x
            level.y = sprite.y - HEIGHT - 1
            bg.y = level.y
            visible = true
        } else {
            visible = false
        }
    }

    fun target(ch: Char?) {
        target = if (ch != null && ch.isAlive()) {
            ch
        } else {
            null
        }
    }

    fun target(): Char? {
        return target
    }

    companion object {
        private const val HEIGHT = 2f
        var instance: HealthIndicator
    }

    init {
        instance = this
    }
}