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
package com.watabou.pixeldungeon.actors.hero

import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.items.TomeOfMastery
import com.watabou.pixeldungeon.items.armor.ClothArmor
import com.watabou.pixeldungeon.items.bags.Keyring
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.rings.RingOfShadows
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.wands.WandOfMagicMissile
import com.watabou.pixeldungeon.items.weapon.melee.Dagger
import com.watabou.pixeldungeon.items.weapon.melee.Knuckles
import com.watabou.pixeldungeon.items.weapon.melee.ShortSword
import com.watabou.pixeldungeon.items.weapon.missiles.Dart
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.utils.Bundle

enum class HeroClass(private val title: String) {
    WARRIOR("warrior"), MAGE("mage"), ROGUE("rogue"), HUNTRESS("huntress");

    fun initHero(hero: com.watabou.pixeldungeon.actors.hero.Hero) {
        hero.heroClass = this
        initCommon(hero)
        when (this) {
            WARRIOR -> initWarrior(hero)
            MAGE -> initMage(hero)
            ROGUE -> initRogue(hero)
            HUNTRESS -> initHuntress(hero)
        }
        if (Badges.isUnlocked(masteryBadge())) {
            TomeOfMastery().collect()
        }
        hero.updateAwareness()
    }

    fun masteryBadge(): Badges.Badge? {
        return when (this) {
            WARRIOR -> Badges.Badge.MASTERY_WARRIOR
            MAGE -> Badges.Badge.MASTERY_MAGE
            ROGUE -> Badges.Badge.MASTERY_ROGUE
            HUNTRESS -> Badges.Badge.MASTERY_HUNTRESS
        }
        return null
    }

    fun title(): String {
        return title
    }

    fun spritesheet(): String? {
        return when (this) {
            WARRIOR -> Assets.WARRIOR
            MAGE -> Assets.MAGE
            ROGUE -> Assets.ROGUE
            HUNTRESS -> Assets.HUNTRESS
        }
        return null
    }

    fun perks(): Array<String>? {
        return when (this) {
            WARRIOR -> WAR_PERKS
            MAGE -> MAG_PERKS
            ROGUE -> ROG_PERKS
            HUNTRESS -> HUN_PERKS
        }
        return null
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(CLASS, toString())
    }

    companion object {
        val WAR_PERKS = arrayOf(
            "Warriors start with 11 points of Strength.",
            "Warriors start with a unique short sword. This sword can be later \"reforged\" to upgrade another melee weapon.",
            "Warriors are less proficient with missile weapons.",
            "Any piece of food restores some health when eaten.",
            "Potions of Strength are identified from the beginning."
        )
        val MAG_PERKS = arrayOf(
            "Mages start with a unique Wand of Magic Missile. This wand can be later \"disenchanted\" to upgrade another wand.",
            "Mages recharge their wands faster.",
            "When eaten, any piece of food restores 1 charge for all wands in the inventory.",
            "Mages can use wands as a melee weapon.",
            "Scrolls of Identify are identified from the beginning."
        )
        val ROG_PERKS = arrayOf(
            "Rogues start with a Ring of Shadows+1.",
            "Rogues identify a type of a ring on equipping it.",
            "Rogues are proficient with light armor, dodging better while wearing one.",
            "Rogues are proficient in detecting hidden doors and traps.",
            "Rogues can go without food longer.",
            "Scrolls of Magic Mapping are identified from the beginning."
        )
        val HUN_PERKS = arrayOf(
            "Huntresses start with 15 points of Health.",
            "Huntresses start with a unique upgradeable boomerang.",
            "Huntresses are proficient with missile weapons and get a damage bonus for excessive strength when using them.",
            "Huntresses gain more health from dewdrops.",
            "Huntresses sense neighbouring monsters even if they are hidden behind obstacles."
        )

        private fun initCommon(hero: com.watabou.pixeldungeon.actors.hero.Hero) {
            ClothArmor().also { hero.belongings.armor = it }.identify()
            Food().identify().collect()
            Keyring().collect()
        }

        private fun initWarrior(hero: com.watabou.pixeldungeon.actors.hero.Hero) {
            hero.STR = hero.STR + 1
            ShortSword().also { hero.belongings.weapon = it }.identify()
            Dart(8).identify().collect()
            QuickSlot.primaryValue = Dart::class.java
            PotionOfStrength().setKnown()
        }

        private fun initMage(hero: com.watabou.pixeldungeon.actors.hero.Hero) {
            Knuckles().also { hero.belongings.weapon = it }.identify()
            val wand = WandOfMagicMissile()
            wand.identify().collect()
            QuickSlot.primaryValue = wand
            ScrollOfIdentify().setKnown()
        }

        private fun initRogue(hero: com.watabou.pixeldungeon.actors.hero.Hero) {
            Dagger().also { hero.belongings.weapon = it }.identify()
            RingOfShadows().also { hero.belongings.ring1 = it }.upgrade().identify()
            Dart(8).identify().collect()
            hero.belongings.ring1.activate(hero)
            QuickSlot.primaryValue = Dart::class.java
            ScrollOfMagicMapping().setKnown()
        }

        private fun initHuntress(hero: com.watabou.pixeldungeon.actors.hero.Hero) {
            hero.HP = 5.let { hero.HT -= it; hero.HT }
            Dagger().also { hero.belongings.weapon = it }.identify()
            val boomerang = Boomerang()
            boomerang.identify().collect()
            QuickSlot.primaryValue = boomerang
        }

        private const val CLASS = "class"
        fun restoreInBundle(bundle: Bundle): HeroClass {
            val value: String = bundle.getString(CLASS)
            return if (value.length > 0) valueOf(value) else ROGUE
        }
    }
}