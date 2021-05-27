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

import com.watabou.pixeldungeon.Assets

class DeadEndLevel : Level() {
    override fun tilesTex(): String {
        return Assets.TILES_CAVES
    }

    override fun waterTex(): String {
        return Assets.WATER_HALLS
    }

    protected override fun build(): Boolean {
        Arrays.fill(map, Terrain.WALL)
        for (i in 2 until SIZE) {
            for (j in 2 until SIZE) {
                map.get(i * WIDTH + j) = Terrain.EMPTY
            }
        }
        for (i in 1..SIZE) {
            map.get(WIDTH * i + SIZE) = Terrain.WATER
            map.get(WIDTH * i + 1) = map.get(WIDTH * i + SIZE)
            map.get(WIDTH * SIZE + i) = map.get(WIDTH * i + 1)
            map.get(WIDTH + i) = map.get(WIDTH * SIZE + i)
        }
        entrance = SIZE * WIDTH + SIZE / 2 + 1
        map.get(entrance) = Terrain.ENTRANCE
        exit = -1
        map.get((SIZE / 2 + 1) * (WIDTH + 1)) = Terrain.SIGN
        return true
    }

    protected override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map.get(i) === Terrain.EMPTY && Random.Int(10) === 0) {
                map.get(i) = Terrain.EMPTY_DECO
            } else if (map.get(i) === Terrain.WALL && Random.Int(8) === 0) {
                map.get(i) = Terrain.WALL_DECO
            }
        }
    }

    protected override fun createMobs() {}
    protected override fun createItems() {}
    override fun randomRespawnCell(): Int {
        return -1
    }

    companion object {
        private const val SIZE = 5
    }

    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
    }
}