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
package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.tweeners.PosTweener

class MissileSprite : ItemSprite(), Tweener.Listener {
    private var callback: Callback? = null
    fun reset(from: Int, to: Int, item: Item?, listener: Callback?) {
        if (item == null) {
            reset(from, to, 0, null, listener)
        } else {
            reset(from, to, item.image(), item.glowing(), listener)
        }
    }

    fun reset(from: Int, to: Int, image: Int, glowing: Glowing?, listener: Callback?) {
        revive()
        view(image, glowing)
        callback = listener
        point(DungeonTilemap.tileToWorld(from))
        val dest: PointF = DungeonTilemap.tileToWorld(to)
        val d: PointF = PointF.diff(dest, point())
        speed.set(d).normalize().scale(SPEED)
        if (image == 31 || image == 108 || image == 109 || image == 110) {
            angularSpeed = 0
            angle = 135 - (Math.atan2(d.x, d.y) / 3.1415926 * 180).toFloat()
        } else {
            angularSpeed = if (image == 15 || image == 106) 1440 else 720
        }
        val tweener = PosTweener(this, dest, d.length() / SPEED)
        tweener.listener = this
        parent.add(tweener)
    }

    fun onComplete(tweener: Tweener?) {
        kill()
        if (callback != null) {
            callback.call()
        }
    }

    companion object {
        private const val SPEED = 240f
    }

    init {
        originToCenter()
    }
}