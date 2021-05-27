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
package com.watabou.pixeldungeon.items.armor

import com.watabou.pixeldungeon.Dungeon

class HuntressArmor : ClassArmor() {
    private val targets: HashMap<Callback, Mob> = HashMap<Callback, Mob>()
    override fun special(): String {
        return AC_SPECIAL
    }

    override fun doSpecial() {
        val proto: Item = Shuriken()
        for (mob in Dungeon.level.mobs) {
            if (Level.fieldOfView.get(mob.pos)) {
                val callback: Callback = object : Callback() {
                    fun call() {
                        curUser.attack(targets[this])
                        targets.remove(this)
                        if (targets.isEmpty()) {
                            curUser.spendAndNext(curUser.attackDelay())
                        }
                    }
                }
                (curUser.sprite.parent.recycle(MissileSprite::class.java) as MissileSprite).reset(
                    curUser.pos,
                    mob.pos,
                    proto,
                    callback
                )
                targets[callback] = mob
            }
        }
        if (targets.size == 0) {
            GLog.w(TXT_NO_ENEMIES)
            return
        }
        curUser.HP -= curUser.HP / 3
        curUser.sprite.zap(curUser.pos)
        curUser.busy()
    }

    override fun doEquip(hero: Hero): Boolean {
        return if (hero.heroClass === HeroClass.HUNTRESS) {
            super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_HUNTRESS)
            false
        }
    }

    override fun desc(): String {
        return "A huntress in such cloak can create a fan of spectral blades. Each of these blades " +
                "will target a single enemy in the huntress's field of view, inflicting damage depending " +
                "on her currently equipped melee weapon."
    }

    companion object {
        private const val TXT_NO_ENEMIES = "No enemies in sight"
        private const val TXT_NOT_HUNTRESS = "Only huntresses can use this armor!"
        private const val AC_SPECIAL = "SPECTRAL BLADES"
    }

    init {
        name = "huntress cloak"
        image = ItemSpriteSheet.ARMOR_HUNTRESS
    }
}