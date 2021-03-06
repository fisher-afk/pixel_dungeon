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
package com.watabou.pixeldungeon.effects

import com.watabou.noosa.particles.Emitter

object CellEmitter {
    operator fun get(cell: Int): Emitter {
        val p: PointF = DungeonTilemap.tileToWorld(cell)
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(p.x, p.y, DungeonTilemap.SIZE, DungeonTilemap.SIZE)
        return emitter
    }

    fun center(cell: Int): Emitter {
        val p: PointF = DungeonTilemap.tileToWorld(cell)
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(p.x + DungeonTilemap.SIZE / 2, p.y + DungeonTilemap.SIZE / 2)
        return emitter
    }

    fun bottom(cell: Int): Emitter {
        val p: PointF = DungeonTilemap.tileToWorld(cell)
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(p.x, p.y + DungeonTilemap.SIZE, DungeonTilemap.SIZE, 0)
        return emitter
    }
}