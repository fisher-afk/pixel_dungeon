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

import com.watabou.noosa.Camera

class Compass(cell: Int) : Image() {
    private val cell: Int
    private val cellCenter: PointF
    private val lastScroll: PointF = PointF()
    fun update() {
        super.update()
        if (!visible) {
            visible = Dungeon.level.visited.get(cell) || Dungeon.level.mapped.get(cell)
        }
        if (visible) {
            val scroll: PointF = Camera.main.scroll
            if (!scroll.equals(lastScroll)) {
                lastScroll.set(scroll)
                val center: PointF = Camera.main.center().offset(scroll)
                angle = Math.atan2(cellCenter.x - center.x, center.y - cellCenter.y).toFloat() * RAD_2_G
            }
        }
    }

    companion object {
        private const val RAD_2_G = 180f / 3.1415926f
        private const val RADIUS = 12f
    }

    init {
        copy(Icons.COMPASS.get())
        origin.set(width / 2, RADIUS)
        this.cell = cell
        cellCenter = DungeonTilemap.tileCenterToWorld(cell)
        visible = false
    }
}