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

import com.watabou.pixeldungeon.PixelDungeon
import java.lang.reflect.Method

class Room : Rect(), Node, Bundlable {
    var neigbours = HashSet<Room>()
    var connected = HashMap<Room?, Door?>()
    var distance = 0
    var price = 1

    enum class Type(painter: Class<out Painter?>?) {
        NULL(null), STANDARD(StandardPainter::class.java), ENTRANCE(EntrancePainter::class.java), EXIT(ExitPainter::class.java), BOSS_EXIT(
            BossExitPainter::class.java
        ),
        TUNNEL(TunnelPainter::class.java), PASSAGE(PassagePainter::class.java), SHOP(
            ShopPainter::class.java
        ),
        BLACKSMITH(BlacksmithPainter::class.java), TREASURY(TreasuryPainter::class.java), ARMORY(
            ArmoryPainter::class.java
        ),
        LIBRARY(LibraryPainter::class.java), LABORATORY(LaboratoryPainter::class.java), VAULT(
            VaultPainter::class.java
        ),
        TRAPS(TrapsPainter::class.java), STORAGE(StoragePainter::class.java), MAGIC_WELL(
            MagicWellPainter::class.java
        ),
        GARDEN(GardenPainter::class.java), CRYPT(CryptPainter::class.java), STATUE(
            StatuePainter::class.java
        ),
        POOL(PoolPainter::class.java), RAT_KING(RatKingPainter::class.java), WEAK_FLOOR(
            WeakFloorPainter::class.java
        ),
        PIT(PitPainter::class.java), ALTAR(AltarPainter::class.java);

        private var paint: Method? = null
        fun paint(level: Level?, room: Room?) {
            try {
                paint!!.invoke(null, level, room)
            } catch (e: Exception) {
                PixelDungeon.reportException(e)
            }
        }

        init {
            paint = try {
                painter!!.getMethod("paint", Level::class.java, Room::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    var type = Type.NULL
    @JvmOverloads
    fun random(m: Int = 0): Int {
        val x: Int = Random.Int(left + 1 + m, right - m)
        val y: Int = Random.Int(top + 1 + m, bottom - m)
        return x + y * Level.WIDTH
    }

    fun addNeigbour(other: Room) {
        val i: Rect = intersect(other)
        if (i.width() === 0 && i.height() >= 3 ||
            i.height() === 0 && i.width() >= 3
        ) {
            neigbours.add(other)
            other.neigbours.add(this)
        }
    }

    fun connect(room: Room) {
        if (!connected.containsKey(room)) {
            connected[room] = null
            room.connected[this] = null
        }
    }

    fun entrance(): Door? {
        return connected.values.iterator().next()
    }

    fun inside(p: Int): Boolean {
        val x: Int = p % Level.WIDTH
        val y: Int = p / Level.WIDTH
        return x > left && y > top && x < right && y < bottom
    }

    fun center(): Point {
        return Point(
            (left + right) / 2 + if (right - left and 1 === 1) Random.Int(2) else 0,
            (top + bottom) / 2 + if (bottom - top and 1 === 1) Random.Int(2) else 0
        )
    }

    // **** Graph.Node interface ****
    fun distance(): Int {
        return distance
    }

    fun distance(value: Int) {
        distance = value
    }

    fun price(): Int {
        return price
    }

    fun price(value: Int) {
        price = value
    }

    fun edges(): Collection<Room> {
        return neigbours
    }

    // FIXME: use proper string constants
    fun storeInBundle(bundle: Bundle) {
        bundle.put("left", left)
        bundle.put("top", top)
        bundle.put("right", right)
        bundle.put("bottom", bottom)
        bundle.put("type", type.toString())
    }

    fun restoreFromBundle(bundle: Bundle) {
        left = bundle.getInt("left")
        top = bundle.getInt("top")
        right = bundle.getInt("right")
        bottom = bundle.getInt("bottom")
        type = Type.valueOf(bundle.getString("type"))
    }

    class Door(x: Int, y: Int) : Point(x, y) {
        enum class Type {
            EMPTY, TUNNEL, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED
        }

        var type = Type.EMPTY
        fun set(type: Type) {
            if (type.compareTo(this.type) > 0) {
                this.type = type
            }
        }
    }

    companion object {
        val SPECIALS: ArrayList<Type> = ArrayList<Type>(
            Arrays.asList(
                Type.ARMORY, Type.WEAK_FLOOR, Type.MAGIC_WELL, Type.CRYPT, Type.POOL, Type.GARDEN, Type.LIBRARY,
                Type.TREASURY, Type.TRAPS, Type.STORAGE, Type.STATUE, Type.LABORATORY, Type.VAULT, Type.ALTAR
            )
        )

        fun shuffleTypes() {
            val size = SPECIALS.size
            for (i in 0 until size - 1) {
                val j: Int = Random.Int(i, size)
                if (j != i) {
                    val t = SPECIALS[i]
                    SPECIALS[i] = SPECIALS[j]
                    SPECIALS[j] = t
                }
            }
        }

        fun useType(type: Type) {
            if (SPECIALS.remove(type)) {
                SPECIALS.add(type)
            }
        }

        private const val ROOMS = "rooms"
        fun restoreRoomsFromBundle(bundle: Bundle) {
            if (bundle.contains(ROOMS)) {
                SPECIALS.clear()
                for (type in bundle.getStringArray(ROOMS)) {
                    SPECIALS.add(
                        Type.valueOf(
                            type!!
                        )
                    )
                }
            } else {
                shuffleTypes()
            }
        }

        fun storeRoomsInBundle(bundle: Bundle) {
            val array = arrayOfNulls<String>(SPECIALS.size)
            for (i in array.indices) {
                array[i] = SPECIALS[i].toString()
            }
            bundle.put(ROOMS, array)
        }
    }
}