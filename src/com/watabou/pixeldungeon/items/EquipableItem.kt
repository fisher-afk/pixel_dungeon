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
package com.watabou.pixeldungeon.items

import com.watabou.noosa.audio.Sample

abstract class EquipableItem : Item() {
    fun execute(hero: Hero, action: String) {
        if (action == AC_EQUIP) {
            doEquip(hero)
        } else if (action == AC_UNEQUIP) {
            doUnequip(hero, true)
        } else {
            super.execute(hero, action)
        }
    }

    override fun doDrop(hero: Hero) {
        if (!isEquipped(hero) || doUnequip(hero, false, false)) {
            super.doDrop(hero)
        }
    }

    override fun cast(user: Hero, dst: Int) {
        if (isEquipped(user)) {
            if (quantity === 1 && !this.doUnequip(user, false, false)) {
                return
            }
        }
        super.cast(user, dst)
    }

    protected fun time2equip(hero: Hero?): Float {
        return 1
    }

    abstract fun doEquip(hero: Hero?): Boolean
    fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        if (cursed) {
            GLog.w(TXT_UNEQUIP_CURSED, name())
            return false
        }
        if (single) {
            hero.spendAndNext(time2equip(hero))
        } else {
            hero.spend(time2equip(hero))
        }
        if (collect && !collect(hero.belongings.backpack)) {
            Dungeon.level.drop(this, hero.pos)
        }
        return true
    }

    fun doUnequip(hero: Hero, collect: Boolean): Boolean {
        return doUnequip(hero, collect, true)
    }

    companion object {
        private const val TXT_UNEQUIP_CURSED = "You can't remove cursed %s!"
        const val AC_EQUIP = "EQUIP"
        const val AC_UNEQUIP = "UNEQUIP"
        protected fun equipCursed(hero: Hero) {
            hero.sprite.emitter().burst(ShadowParticle.CURSE, 6)
            Sample.INSTANCE.play(Assets.SND_CURSED)
        }
    }
}