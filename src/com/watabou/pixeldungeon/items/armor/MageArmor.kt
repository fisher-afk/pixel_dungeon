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

import com.watabou.noosa.audio.Sample

class MageArmor : ClassArmor() {
    override fun special(): String {
        return AC_SPECIAL
    }

    override fun desc(): String {
        return "Wearing this gorgeous robe, a mage can cast a spell of molten earth: all the enemies " +
                "in his field of view will be set on fire and unable to move at the same time."
    }

    override fun doSpecial() {
        for (mob in Dungeon.level.mobs) {
            if (Level.fieldOfView.get(mob.pos)) {
                Buff.affect(mob, Burning::class.java).reignite(mob)
                Buff.prolong(mob, Roots::class.java, 3)
            }
        }
        curUser.HP -= curUser.HP / 3
        curUser.spend(Actor.TICK)
        curUser.sprite.operate(curUser.pos)
        curUser.busy()
        curUser.sprite.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4)
        Sample.INSTANCE.play(Assets.SND_READ)
    }

    override fun doEquip(hero: Hero): Boolean {
        return if (hero.heroClass === HeroClass.MAGE) {
            super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_MAGE)
            false
        }
    }

    companion object {
        private const val AC_SPECIAL = "MOLTEN EARTH"
        private const val TXT_NOT_MAGE = "Only mages can use this armor!"
    }

    init {
        name = "mage robe"
        image = ItemSpriteSheet.ARMOR_MAGE
    }
}