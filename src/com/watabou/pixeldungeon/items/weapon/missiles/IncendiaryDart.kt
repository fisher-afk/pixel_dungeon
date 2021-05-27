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
package com.watabou.pixeldungeon.items.weapon.missiles

import com.watabou.pixeldungeon.Dungeon

class IncendiaryDart @JvmOverloads constructor(number: Int = 1) : MissileWeapon() {
    fun min(): Int {
        return 1
    }

    fun max(): Int {
        return 2
    }

    protected override fun onThrow(cell: Int) {
        val enemy: Char = Actor.findChar(cell)
        if (enemy == null || enemy === curUser) {
            if (Level.flamable.get(cell)) {
                GameScene.add(Blob.seed(cell, 4, Fire::class.java))
            } else {
                super.onThrow(cell)
            }
        } else {
            if (!curUser.shoot(enemy, this)) {
                Dungeon.level.drop(this, cell).sprite.drop()
            }
        }
    }

    override fun proc(attacker: Char, defender: Char?, damage: Int) {
        Buff.affect(defender, Burning::class.java).reignite(defender)
        super.proc(attacker, defender, damage)
    }

    fun desc(): String {
        return "The spike on each of these darts is designed to pin it to its target " +
                "while the unstable compounds strapped to its length burst into brilliant flames."
    }

    override fun random(): Item {
        quantity = Random.Int(3, 6)
        return this
    }

    fun price(): Int {
        return 10 * quantity
    }

    init {
        name = "incendiary dart"
        image = ItemSpriteSheet.INCENDIARY_DART
        STR = 12
    }

    init {
        quantity = number
    }
}