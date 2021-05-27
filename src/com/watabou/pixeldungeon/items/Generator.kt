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

import com.watabou.pixeldungeon.Dungeon

object Generator {
    private val categoryProbs = HashMap<Category, Float>()
    fun reset() {
        for (cat in Category.values()) {
            categoryProbs[cat] = cat.prob
        }
    }

    fun random(): Item {
        return random(Random.chances(categoryProbs))
    }

    fun random(cat: Category): Item? {
        return try {
            categoryProbs[cat] = categoryProbs[cat]!! / 2
            when (cat) {
                Category.ARMOR -> randomArmor()
                Category.WEAPON -> randomWeapon()
                else -> (cat.classes[Random.chances(cat.probs)].newInstance() as Item).random()
            }
        } catch (e: Exception) {
            null
        }
    }

    fun random(cl: Class<out Item?>): Item? {
        return try {
            cl.newInstance()!!.random()
        } catch (e: Exception) {
            null
        }
    }

    @Throws(Exception::class)
    fun randomArmor(): Armor {
        val curStr: Int = Hero.STARTING_STR + Dungeon.potionOfStrength
        val cat = Category.ARMOR
        val a1: Armor = cat.classes[Random.chances(cat.probs)].newInstance() as Armor
        val a2: Armor = cat.classes[Random.chances(cat.probs)].newInstance() as Armor
        a1.random()
        a2.random()
        return if (Math.abs(curStr - a1.STR) < Math.abs(curStr - a2.STR)) a1 else a2
    }

    @Throws(Exception::class)
    fun randomWeapon(): Weapon {
        val curStr: Int = Hero.STARTING_STR + Dungeon.potionOfStrength
        val cat = Category.WEAPON
        val w1: Weapon = cat.classes[Random.chances(cat.probs)].newInstance() as Weapon
        val w2: Weapon = cat.classes[Random.chances(cat.probs)].newInstance() as Weapon
        w1.random()
        w2.random()
        return if (Math.abs(curStr - w1.STR) < Math.abs(curStr - w2.STR)) w1 else w2
    }

    enum class Category(var prob: Float, superClass: Class<out Item?>) {
        WEAPON(15, Weapon::class.java), ARMOR(10, Armor::class.java), POTION(50, Potion::class.java), SCROLL(
            40,
            Scroll::class.java
        ),
        WAND(4, Wand::class.java), RING(2, Ring::class.java), SEED(5, Plant.Seed::class.java), FOOD(
            0,
            Food::class.java
        ),
        GOLD(50, Gold::class.java), MISC(5, Item::class.java);

        var classes: Array<Class<*>>
        var probs: FloatArray
        var superClass: Class<out Item?>

        companion object {
            fun order(item: Item?): Int {
                for (i in values().indices) {
                    if (values()[i].superClass.isInstance(item)) {
                        return i
                    }
                }
                return if (item is Bag) Int.MAX_VALUE else Int.MAX_VALUE - 1
            }
        }

        init {
            this.superClass = superClass
        }
    }

    init {
        Category.GOLD.classes = arrayOf(
            Gold::class.java
        )
        Category.GOLD.probs = floatArrayOf(1f)
        Category.SCROLL.classes = arrayOf(
            ScrollOfIdentify::class.java,
            ScrollOfTeleportation::class.java,
            ScrollOfRemoveCurse::class.java,
            ScrollOfRecharging::class.java,
            ScrollOfMagicMapping::class.java,
            ScrollOfChallenge::class.java,
            ScrollOfTerror::class.java,
            ScrollOfLullaby::class.java,
            ScrollOfPsionicBlast::class.java,
            ScrollOfMirrorImage::class.java,
            ScrollOfUpgrade::class.java,
            ScrollOfEnchantment::class.java
        )
        Category.SCROLL.probs = floatArrayOf(30f, 10f, 15f, 10f, 15f, 12f, 8f, 8f, 4f, 6f, 0f, 1f)
        Category.POTION.classes = arrayOf(
            PotionOfHealing::class.java,
            PotionOfExperience::class.java,
            PotionOfToxicGas::class.java,
            PotionOfParalyticGas::class.java,
            PotionOfLiquidFlame::class.java,
            PotionOfLevitation::class.java,
            PotionOfStrength::class.java,
            PotionOfMindVision::class.java,
            PotionOfPurity::class.java,
            PotionOfInvisibility::class.java,
            PotionOfMight::class.java,
            PotionOfFrost::class.java
        )
        Category.POTION.probs = floatArrayOf(45f, 4f, 15f, 10f, 15f, 10f, 0f, 20f, 12f, 10f, 0f, 10f)
        Category.WAND.classes = arrayOf(
            WandOfTeleportation::class.java,
            WandOfSlowness::class.java,
            WandOfFirebolt::class.java,
            WandOfRegrowth::class.java,
            WandOfPoison::class.java,
            WandOfBlink::class.java,
            WandOfLightning::class.java,
            WandOfAmok::class.java,
            WandOfReach::class.java,
            WandOfFlock::class.java,
            WandOfMagicMissile::class.java,
            WandOfDisintegration::class.java,
            WandOfAvalanche::class.java
        )
        Category.WAND.probs = floatArrayOf(10f, 10f, 15f, 6f, 10f, 11f, 15f, 10f, 6f, 10f, 0f, 5f, 5f)
        Category.WEAPON.classes = arrayOf(
            Dagger::class.java,
            Knuckles::class.java,
            Quarterstaff::class.java,
            Spear::class.java,
            Mace::class.java,
            Sword::class.java,
            Longsword::class.java,
            BattleAxe::class.java,
            WarHammer::class.java,
            Glaive::class.java,
            ShortSword::class.java,
            Dart::class.java,
            Javelin::class.java,
            IncendiaryDart::class.java,
            CurareDart::class.java,
            Shuriken::class.java,
            Boomerang::class.java,
            Tamahawk::class.java
        )
        Category.WEAPON.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 1f)
        Category.ARMOR.classes = arrayOf(
            ClothArmor::class.java,
            LeatherArmor::class.java,
            MailArmor::class.java,
            ScaleArmor::class.java,
            PlateArmor::class.java
        )
        Category.ARMOR.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f)
        Category.FOOD.classes = arrayOf(
            Food::class.java,
            Pasty::class.java,
            MysteryMeat::class.java
        )
        Category.FOOD.probs = floatArrayOf(4f, 1f, 0f)
        Category.RING.classes = arrayOf(
            RingOfMending::class.java,
            RingOfDetection::class.java,
            RingOfShadows::class.java,
            RingOfPower::class.java,
            RingOfHerbalism::class.java,
            RingOfAccuracy::class.java,
            RingOfEvasion::class.java,
            RingOfSatiety::class.java,
            RingOfHaste::class.java,
            RingOfElements::class.java,
            RingOfHaggler::class.java,
            RingOfThorns::class.java
        )
        Category.RING.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 0f)
        Category.SEED.classes = arrayOf(
            Firebloom.Seed::class.java,
            Icecap.Seed::class.java,
            Sorrowmoss.Seed::class.java,
            Dreamweed.Seed::class.java,
            Sungrass.Seed::class.java,
            Earthroot.Seed::class.java,
            Fadeleaf.Seed::class.java,
            Rotberry.Seed::class.java
        )
        Category.SEED.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f)
        Category.MISC.classes = arrayOf(
            Bomb::class.java,
            Honeypot::class.java
        )
        Category.MISC.probs = floatArrayOf(2f, 1f)
    }
}