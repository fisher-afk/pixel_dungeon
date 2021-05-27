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
package com.watabou.pixeldungeon.levels.painters

import com.watabou.pixeldungeon.Dungeon

object ShopPainter : Painter() {
    private var pasWidth = 0
    private var pasHeight = 0
    fun paint(level: Level, room: Room) {
        fill(level, room, Terrain.WALL)
        fill(level, room, 1, Terrain.EMPTY_SP)
        pasWidth = room.width() - 2
        pasHeight = room.height() - 2
        val per = pasWidth * 2 + pasHeight * 2
        val range: Array<Item> = range()
        var pos = xy2p(room, room.entrance()) + (per - range.size) / 2
        for (i in range.indices) {
            val xy: Point = p2xy(room, (pos + per) % per)
            var cell: Int = xy.x + xy.y * Level.WIDTH
            if (level.heaps.get(cell) != null) {
                do {
                    cell = room.random()
                } while (level.heaps.get(cell) != null)
            }
            level.drop(range[i], cell).type = Heap.Type.FOR_SALE
            pos++
        }
        placeShopkeeper(level, room)
        for (door in room.connected.values()) {
            door.set(Room.Door.Type.REGULAR)
        }
    }

    private fun range(): Array<Item> {
        val items: ArrayList<Item> = ArrayList<Item>()
        when (Dungeon.depth) {
            6 -> {
                items.add((if (Random.Int(2) === 0) Quarterstaff() else Spear()).identify())
                items.add(LeatherArmor().identify())
                items.add(SeedPouch())
                items.add(Weightstone())
            }
            11 -> {
                items.add((if (Random.Int(2) === 0) Sword() else Mace()).identify())
                items.add(MailArmor().identify())
                items.add(ScrollHolder())
                items.add(Weightstone())
            }
            16 -> {
                items.add((if (Random.Int(2) === 0) Longsword() else BattleAxe()).identify())
                items.add(ScaleArmor().identify())
                items.add(WandHolster())
                items.add(Weightstone())
            }
            21 -> {
                when (Random.Int(3)) {
                    0 -> items.add(Glaive().identify())
                    1 -> items.add(WarHammer().identify())
                    2 -> items.add(PlateArmor().identify())
                }
                items.add(Torch())
                items.add(Torch())
            }
        }
        items.add(PotionOfHealing())
        for (i in 0..2) {
            items.add(Generator.random(Generator.Category.POTION))
        }
        items.add(ScrollOfIdentify())
        items.add(ScrollOfRemoveCurse())
        items.add(ScrollOfMagicMapping())
        items.add(Generator.random(Generator.Category.SCROLL))
        items.add(OverpricedRation())
        items.add(OverpricedRation())
        items.add(Ankh())
        val range: Array<Item> = items.toTypedArray()
        Random.shuffle(range)
        return range
    }

    private fun placeShopkeeper(level: Level, room: Room) {
        var pos: Int
        do {
            pos = room.random()
        } while (level.heaps.get(pos) != null)
        val shopkeeper: Mob = if (level is LastShopLevel) ImpShopkeeper() else Shopkeeper()
        shopkeeper.pos = pos
        level.mobs.add(shopkeeper)
        if (level is LastShopLevel) {
            for (i in 0 until Level.NEIGHBOURS9.length) {
                val p: Int = shopkeeper.pos + Level.NEIGHBOURS9.get(i)
                if (level.map.get(p) === Terrain.EMPTY_SP) {
                    level.map.get(p) = Terrain.WATER
                }
            }
        }
    }

    private fun xy2p(room: Room, xy: Point): Int {
        return if (xy.y === room.top) {
            xy.x - room.left - 1
        } else if (xy.x === room.right) {
            xy.y - room.top - 1 + pasWidth
        } else if (xy.y === room.bottom) {
            room.right - xy.x - 1 + pasWidth + pasHeight
        } else  /*if (xy.x == room.left)*/ {
            if (xy.y === room.top + 1) {
                0
            } else {
                room.bottom - xy.y - 1 + pasWidth * 2 + pasHeight
            }
        }
    }

    private fun p2xy(room: Room, p: Int): Point {
        return if (p < pasWidth) {
            Point(room.left + 1 + p, room.top + 1)
        } else if (p < pasWidth + pasHeight) {
            Point(room.right - 1, room.top + 1 + (p - pasWidth))
        } else if (p < pasWidth * 2 + pasHeight) {
            Point(
                room.right - 1 - (p - (pasWidth + pasHeight)),
                room.bottom - 1
            )
        } else {
            Point(
                room.left + 1,
                room.bottom - 1 - (p - (pasWidth * 2 + pasHeight))
            )
        }
    }
}