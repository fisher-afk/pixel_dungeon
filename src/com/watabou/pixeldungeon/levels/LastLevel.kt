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
package com.watabou.pixeldungeon.levels

import com.watabou.noosa.Scene

class LastLevel : Level() {
    private var pedestal = 0
    override fun tilesTex(): String {
        return Assets.TILES_HALLS
    }

    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }

    protected override fun build(): Boolean {
        Arrays.fill(map, Terrain.WALL)
        Painter.fill(this, 1, 1, SIZE, SIZE, Terrain.WATER)
        Painter.fill(this, 2, 2, SIZE - 2, SIZE - 2, Terrain.EMPTY)
        Painter.fill(this, SIZE / 2, SIZE / 2, 3, 3, Terrain.EMPTY_SP)
        entrance = SIZE * WIDTH + SIZE / 2 + 1
        map.get(entrance) = Terrain.ENTRANCE
        exit = entrance - WIDTH * SIZE
        map.get(exit) = Terrain.LOCKED_EXIT
        pedestal = (SIZE / 2 + 1) * (WIDTH + 1)
        map.get(pedestal) = Terrain.PEDESTAL
        map.get(pedestal + 1) = Terrain.STATUE_SP
        map.get(pedestal - 1) = map.get(pedestal + 1)
        feeling = Feeling.NONE
        return true
    }

    protected override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(10) === 0) {
                map.get(i) = Terrain.EMPTY_DECO
            }
        }
    }

    protected override fun createMobs() {}
    protected override fun createItems() {
        drop(Amulet(), pedestal)
    }

    override fun randomRespawnCell(): Int {
        return -1
    }

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Cold lava"
            Terrain.GRASS -> "Embermoss"
            Terrain.HIGH_GRASS -> "Emberfungi"
            Terrain.STATUE, Terrain.STATUE_SP -> "Pillar"
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "It looks like lava, but it's cold and probably safe to touch."
            Terrain.STATUE, Terrain.STATUE_SP -> "The pillar is made of real humanoid skulls. Awesome."
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        HallsLevel.addVisuals(this, scene)
    }

    companion object {
        private const val SIZE = 7
    }

    init {
        color1 = 0x801500
        color2 = 0xa68521
    }
}