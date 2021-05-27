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

class Boomerang : MissileWeapon() {
    fun min(): Int {
        return if (isBroken()) 1 else 1 + level()
    }

    fun max(): Int {
        return if (isBroken()) 4 else 4 + 2 * level()
    }

    override val isUpgradable: Boolean
        get() = true

    fun upgrade(): Item {
        return upgrade(false)
    }

    fun upgrade(enchant: Boolean): Item {
        super.upgrade(enchant)
        updateQuickslot()
        return this
    }

    fun maxDurability(lvl: Int): Int {
        return 8 * if (lvl < 16) 16 - lvl else 1
    }

    fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)
        if (attacker is Hero && (attacker as Hero).rangedWeapon === this) {
            circleBack(defender.pos, attacker as Hero)
        }
    }

    protected override fun miss(cell: Int) {
        circleBack(cell, curUser)
    }

    private fun circleBack(from: Int, owner: Hero) {
        (curUser.sprite.parent.recycle(MissileSprite::class.java) as MissileSprite).reset(
            from,
            curUser.pos,
            curItem,
            null
        )
        if (throwEquiped) {
            owner.belongings.weapon = this
            owner.spend(-TIME_TO_EQUIP)
        } else if (!collect(curUser.belongings.backpack)) {
            Dungeon.level.drop(this, owner.pos).sprite.drop()
        }
    }

    private var throwEquiped = false
    fun cast(user: Hero?, dst: Int) {
        throwEquiped = isEquipped(user)
        super.cast(user, dst)
    }

    fun desc(): String {
        return "Thrown to the enemy this flat curved wooden missile will return to the hands of its thrower."
    }

    init {
        name = "boomerang"
        image = ItemSpriteSheet.BOOMERANG
        STR = 10
        stackable = false
    }
}