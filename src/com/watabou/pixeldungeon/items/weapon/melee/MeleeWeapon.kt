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
package com.watabou.pixeldungeon.items.weapon.melee

import com.watabou.pixeldungeon.Dungeon

class MeleeWeapon(private val tier: Int, acu: Float, dly: Float) : Weapon() {
    protected fun min0(): Int {
        return tier
    }

    protected fun max0(): Int {
        return ((tier * tier - tier + 10) / ACU * DLY)
    }

    fun min(): Int {
        return if (isBroken()) min0() else min0() + level()
    }

    fun max(): Int {
        return if (isBroken()) max0() else max0() + level() * tier
    }

    fun upgrade(): Item {
        return upgrade(false)
    }

    fun upgrade(enchant: Boolean): Item {
        STR--
        return super.upgrade(enchant)
    }

    fun safeUpgrade(): Item {
        return upgrade(enchantment != null)
    }

    fun degrade(): Item {
        STR++
        return super.degrade()
    }

    fun typicalSTR(): Int {
        return 8 + tier * 2
    }

    fun info(): String {
        val p = "\n\n"
        val info: StringBuilder = StringBuilder(desc())
        val lvl: Int = visiblyUpgraded()
        val quality = if (lvl != 0) if (lvl > 0) if (isBroken()) "broken" else "upgraded" else "degraded" else ""
        info.append(p)
        info.append("This " + name.toString() + " is " + Utils.indefinite(quality))
        info.append(" tier-$tier melee weapon. ")
        if (levelKnown) {
            val min = min()
            val max = max()
            info.append("Its average damage is " + (min + (max - min) / 2) + " points per hit. ")
        } else {
            val min = min0()
            val max = max0()
            info.append(
                "Its typical average damage is " + (min + (max - min) / 2) + " points per hit " +
                        "and usually it requires " + typicalSTR() + " points of strength. "
            )
            if (typicalSTR() > Dungeon.hero.STR()) {
                info.append("Probably this weapon is too heavy for you. ")
            }
        }
        if (DLY !== 1f) {
            info.append("This is a rather " + if (DLY < 1f) "fast" else "slow")
            if (ACU !== 1f) {
                if (ACU > 1f == DLY < 1f) {
                    info.append(" and ")
                } else {
                    info.append(" but ")
                }
                info.append(if (ACU > 1f) "accurate" else "inaccurate")
            }
            info.append(" weapon. ")
        } else if (ACU !== 1f) {
            info.append("This is a rather " + (if (ACU > 1f) "accurate" else "inaccurate") + " weapon. ")
        }
        when (imbue) {
            SPEED -> info.append("It was balanced to make it faster. ")
            ACCURACY -> info.append("It was balanced to make it more accurate. ")
            NONE -> {
            }
        }
        if (enchantment != null) {
            info.append("It is enchanted.")
        }
        if (levelKnown && Dungeon.hero.belongings.backpack.items.contains(this)) {
            if (STR > Dungeon.hero.STR()) {
                info.append(p)
                info.append(
                    "Because of your inadequate strength the accuracy and speed " +
                            "of your attack with this " + name + " is decreased."
                )
            }
            if (STR < Dungeon.hero.STR()) {
                info.append(p)
                info.append(
                    "Because of your excess strength the damage " +
                            "of your attack with this " + name + " is increased."
                )
            }
        }
        if (isEquipped(Dungeon.hero)) {
            info.append(p)
            info.append(
                "You hold the " + name.toString() + " at the ready" +
                        if (cursed) ", and because it is cursed, you are powerless to let go." else "."
            )
        } else {
            if (cursedKnown && cursed) {
                info.append(p)
                info.append("You can feel a malevolent magic lurking within " + name.toString() + ".")
            }
        }
        return info.toString()
    }

    fun price(): Int {
        var price = 20 * (1 shl tier - 1)
        if (enchantment != null) {
            (price *= 1.5).toInt()
        }
        return considerState(price)
    }

    fun random(): Item {
        super.random()
        if (Random.Int(10 + level()) === 0) {
            enchant()
        }
        return this
    }

    init {
        ACU = acu
        DLY = dly
        STR = typicalSTR()
    }
}