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

class TomeOfMastery : Item() {
    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_READ) {
            if (hero.buff(Blindness::class.java) != null) {
                GLog.w(TXT_BLINDED)
                return
            }
            curUser = hero
            when (hero.heroClass) {
                WARRIOR -> read(hero, HeroSubClass.GLADIATOR, HeroSubClass.BERSERKER)
                MAGE -> read(hero, HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK)
                ROGUE -> read(hero, HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER)
                HUNTRESS -> read(hero, HeroSubClass.SNIPER, HeroSubClass.WARDEN)
            }
        } else {
            super.execute(hero, action)
        }
    }

    override fun doPickUp(hero: Hero): Boolean {
        Badges.validateMastery()
        return super.doPickUp(hero)
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun info(): String {
        return "This worn leather book is not that thick, but you feel somehow, " +
                "that you can gather a lot from it. Remember though that reading " +
                "this tome may require some time."
    }

    private fun read(hero: Hero, sc1: HeroSubClass, sc2: HeroSubClass) {
        if (hero.subClass === sc1) {
            GameScene.show(WndChooseWay(this, sc2))
        } else if (hero.subClass === sc2) {
            GameScene.show(WndChooseWay(this, sc1))
        } else {
            GameScene.show(WndChooseWay(this, sc1, sc2))
        }
    }

    fun choose(way: HeroSubClass) {
        detach(curUser.belongings.backpack)
        curUser.spend(TIME_TO_READ)
        curUser.busy()
        curUser.subClass = way
        curUser.sprite.operate(curUser.pos)
        Sample.INSTANCE.play(Assets.SND_MASTERY)
        SpellSprite.show(curUser, SpellSprite.MASTERY)
        curUser.sprite.emitter().burst(Speck.factory(Speck.MASTERY), 12)
        GLog.w("You have chosen the way of the %s!", Utils.capitalize(way.title()))
        if (way === HeroSubClass.BERSERKER && curUser.HP <= curUser.HT * Fury.LEVEL) {
            Buff.affect(curUser, Fury::class.java)
        }
    }

    companion object {
        private const val TXT_BLINDED = "You can't read while blinded"
        const val TIME_TO_READ = 10f
        const val AC_READ = "READ"
    }

    init {
        stackable = false
        name =
            if (Dungeon.hero != null && Dungeon.hero.subClass !== HeroSubClass.NONE) "Tome of Remastery" else "Tome of Mastery"
        image = ItemSpriteSheet.MASTERY
        unique = true
    }
}