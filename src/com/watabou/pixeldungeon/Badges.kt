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
package com.watabou.pixeldungeon

import com.watabou.noosa.Game

object Badges {
    private var global: HashSet<Badge?>? = null
    private var local = HashSet<Badge?>()
    private var saveNeeded = false
    var loadingListener: Callback? = null
    fun reset() {
        local.clear()
        loadGlobal()
    }

    private const val BADGES_FILE = "badges.dat"
    private const val BADGES = "badges"
    private fun restore(bundle: Bundle): HashSet<Badge?> {
        val badges = HashSet<Badge?>()
        val names: Array<String> = bundle.getStringArray(BADGES)
        for (i in names.indices) {
            try {
                badges.add(Badge.valueOf(names[i]))
            } catch (e: Exception) {
            }
        }
        return badges
    }

    private fun store(bundle: Bundle?, badges: HashSet<Badge?>?) {
        var count = 0
        val names = arrayOfNulls<String>(badges!!.size)
        for (badge in badges) {
            names[count++] = badge.toString()
        }
        bundle.put(BADGES, names)
    }

    fun loadLocal(bundle: Bundle) {
        local = restore(bundle)
    }

    fun saveLocal(bundle: Bundle?) {
        store(bundle, local)
    }

    fun loadGlobal() {
        if (global == null) {
            try {
                val input: InputStream = Game.instance.openFileInput(BADGES_FILE)
                val bundle: Bundle = Bundle.read(input)
                input.close()
                global = restore(bundle)
            } catch (e: IOException) {
                global = HashSet()
            }
        }
    }

    fun saveGlobal() {
        var bundle: Bundle? = null
        if (saveNeeded) {
            bundle = Bundle()
            store(bundle, global)
            try {
                val output: OutputStream = Game.instance.openFileOutput(BADGES_FILE, Game.MODE_PRIVATE)
                Bundle.write(bundle, output)
                output.close()
                saveNeeded = false
            } catch (e: IOException) {
            }
        }
    }

    fun validateMonstersSlain() {
        var badge: Badge? = null
        if (!local.contains(Badge.MONSTERS_SLAIN_1) && Statistics.enemiesSlain >= 10) {
            badge = Badge.MONSTERS_SLAIN_1
            local.add(badge)
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_2) && Statistics.enemiesSlain >= 50) {
            badge = Badge.MONSTERS_SLAIN_2
            local.add(badge)
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_3) && Statistics.enemiesSlain >= 150) {
            badge = Badge.MONSTERS_SLAIN_3
            local.add(badge)
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_4) && Statistics.enemiesSlain >= 250) {
            badge = Badge.MONSTERS_SLAIN_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validateGoldCollected() {
        var badge: Badge? = null
        if (!local.contains(Badge.GOLD_COLLECTED_1) && Statistics.goldCollected >= 100) {
            badge = Badge.GOLD_COLLECTED_1
            local.add(badge)
        }
        if (!local.contains(Badge.GOLD_COLLECTED_2) && Statistics.goldCollected >= 500) {
            badge = Badge.GOLD_COLLECTED_2
            local.add(badge)
        }
        if (!local.contains(Badge.GOLD_COLLECTED_3) && Statistics.goldCollected >= 2500) {
            badge = Badge.GOLD_COLLECTED_3
            local.add(badge)
        }
        if (!local.contains(Badge.GOLD_COLLECTED_4) && Statistics.goldCollected >= 7500) {
            badge = Badge.GOLD_COLLECTED_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validateLevelReached() {
        var badge: Badge? = null
        if (!local.contains(Badge.LEVEL_REACHED_1) && Dungeon.hero.lvl >= 6) {
            badge = Badge.LEVEL_REACHED_1
            local.add(badge)
        }
        if (!local.contains(Badge.LEVEL_REACHED_2) && Dungeon.hero.lvl >= 12) {
            badge = Badge.LEVEL_REACHED_2
            local.add(badge)
        }
        if (!local.contains(Badge.LEVEL_REACHED_3) && Dungeon.hero.lvl >= 18) {
            badge = Badge.LEVEL_REACHED_3
            local.add(badge)
        }
        if (!local.contains(Badge.LEVEL_REACHED_4) && Dungeon.hero.lvl >= 24) {
            badge = Badge.LEVEL_REACHED_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validateStrengthAttained() {
        var badge: Badge? = null
        if (!local.contains(Badge.STRENGTH_ATTAINED_1) && Dungeon.hero.STR >= 13) {
            badge = Badge.STRENGTH_ATTAINED_1
            local.add(badge)
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_2) && Dungeon.hero.STR >= 15) {
            badge = Badge.STRENGTH_ATTAINED_2
            local.add(badge)
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_3) && Dungeon.hero.STR >= 17) {
            badge = Badge.STRENGTH_ATTAINED_3
            local.add(badge)
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_4) && Dungeon.hero.STR >= 19) {
            badge = Badge.STRENGTH_ATTAINED_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validateFoodEaten() {
        var badge: Badge? = null
        if (!local.contains(Badge.FOOD_EATEN_1) && Statistics.foodEaten >= 10) {
            badge = Badge.FOOD_EATEN_1
            local.add(badge)
        }
        if (!local.contains(Badge.FOOD_EATEN_2) && Statistics.foodEaten >= 20) {
            badge = Badge.FOOD_EATEN_2
            local.add(badge)
        }
        if (!local.contains(Badge.FOOD_EATEN_3) && Statistics.foodEaten >= 30) {
            badge = Badge.FOOD_EATEN_3
            local.add(badge)
        }
        if (!local.contains(Badge.FOOD_EATEN_4) && Statistics.foodEaten >= 40) {
            badge = Badge.FOOD_EATEN_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validatePotionsCooked() {
        var badge: Badge? = null
        if (!local.contains(Badge.POTIONS_COOKED_1) && Statistics.potionsCooked >= 3) {
            badge = Badge.POTIONS_COOKED_1
            local.add(badge)
        }
        if (!local.contains(Badge.POTIONS_COOKED_2) && Statistics.potionsCooked >= 6) {
            badge = Badge.POTIONS_COOKED_2
            local.add(badge)
        }
        if (!local.contains(Badge.POTIONS_COOKED_3) && Statistics.potionsCooked >= 9) {
            badge = Badge.POTIONS_COOKED_3
            local.add(badge)
        }
        if (!local.contains(Badge.POTIONS_COOKED_4) && Statistics.potionsCooked >= 12) {
            badge = Badge.POTIONS_COOKED_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validatePiranhasKilled() {
        var badge: Badge? = null
        if (!local.contains(Badge.PIRANHAS) && Statistics.piranhasKilled >= 6) {
            badge = Badge.PIRANHAS
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validateItemLevelAquired(item: Item) {

        // This method should be called:
        // 1) When an item gets obtained (Item.collect)
        // 2) When an item gets upgraded (ScrollOfUpgrade, ScrollOfWeaponUpgrade, ShortSword, WandOfMagicMissile)
        // 3) When an item gets identified
        if (!item.levelKnown) {
            return
        }
        var badge: Badge? = null
        if (!local.contains(Badge.ITEM_LEVEL_1) && item.level() >= 3) {
            badge = Badge.ITEM_LEVEL_1
            local.add(badge)
        }
        if (!local.contains(Badge.ITEM_LEVEL_2) && item.level() >= 6) {
            badge = Badge.ITEM_LEVEL_2
            local.add(badge)
        }
        if (!local.contains(Badge.ITEM_LEVEL_3) && item.level() >= 9) {
            badge = Badge.ITEM_LEVEL_3
            local.add(badge)
        }
        if (!local.contains(Badge.ITEM_LEVEL_4) && item.level() >= 12) {
            badge = Badge.ITEM_LEVEL_4
            local.add(badge)
        }
        displayBadge(badge)
    }

    fun validateAllPotionsIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
            !local.contains(Badge.ALL_POTIONS_IDENTIFIED) && Potion.allKnown()
        ) {
            val badge = Badge.ALL_POTIONS_IDENTIFIED
            local.add(badge)
            displayBadge(badge)
            validateAllItemsIdentified()
        }
    }

    fun validateAllScrollsIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
            !local.contains(Badge.ALL_SCROLLS_IDENTIFIED) && Scroll.allKnown()
        ) {
            val badge = Badge.ALL_SCROLLS_IDENTIFIED
            local.add(badge)
            displayBadge(badge)
            validateAllItemsIdentified()
        }
    }

    fun validateAllRingsIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
            !local.contains(Badge.ALL_RINGS_IDENTIFIED) && Ring.allKnown()
        ) {
            val badge = Badge.ALL_RINGS_IDENTIFIED
            local.add(badge)
            displayBadge(badge)
            validateAllItemsIdentified()
        }
    }

    fun validateAllWandsIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
            !local.contains(Badge.ALL_WANDS_IDENTIFIED) && Wand.allKnown()
        ) {
            val badge = Badge.ALL_WANDS_IDENTIFIED
            local.add(badge)
            displayBadge(badge)
            validateAllItemsIdentified()
        }
    }

    fun validateAllBagsBought(bag: Item?) {
        var badge: Badge? = null
        if (bag is SeedPouch) {
            badge = Badge.BAG_BOUGHT_SEED_POUCH
        } else if (bag is ScrollHolder) {
            badge = Badge.BAG_BOUGHT_SCROLL_HOLDER
        } else if (bag is WandHolster) {
            badge = Badge.BAG_BOUGHT_WAND_HOLSTER
        }
        if (badge != null) {
            local.add(badge)
            if (!local.contains(Badge.ALL_BAGS_BOUGHT) &&
                local.contains(Badge.BAG_BOUGHT_SCROLL_HOLDER) &&
                local.contains(Badge.BAG_BOUGHT_SEED_POUCH) &&
                local.contains(Badge.BAG_BOUGHT_WAND_HOLSTER)
            ) {
                badge = Badge.ALL_BAGS_BOUGHT
                local.add(badge)
                displayBadge(badge)
            }
        }
    }

    fun validateAllItemsIdentified() {
        if (!global!!.contains(Badge.ALL_ITEMS_IDENTIFIED) &&
            global!!.contains(Badge.ALL_POTIONS_IDENTIFIED) &&
            global!!.contains(Badge.ALL_SCROLLS_IDENTIFIED) &&
            global!!.contains(Badge.ALL_RINGS_IDENTIFIED) &&
            global!!.contains(Badge.ALL_WANDS_IDENTIFIED)
        ) {
            val badge = Badge.ALL_ITEMS_IDENTIFIED
            displayBadge(badge)
        }
    }

    fun validateDeathFromFire() {
        val badge = Badge.DEATH_FROM_FIRE
        local.add(badge)
        displayBadge(badge)
        validateYASD()
    }

    fun validateDeathFromPoison() {
        val badge = Badge.DEATH_FROM_POISON
        local.add(badge)
        displayBadge(badge)
        validateYASD()
    }

    fun validateDeathFromGas() {
        val badge = Badge.DEATH_FROM_GAS
        local.add(badge)
        displayBadge(badge)
        validateYASD()
    }

    fun validateDeathFromHunger() {
        val badge = Badge.DEATH_FROM_HUNGER
        local.add(badge)
        displayBadge(badge)
        validateYASD()
    }

    fun validateDeathFromGlyph() {
        val badge = Badge.DEATH_FROM_GLYPH
        local.add(badge)
        displayBadge(badge)
    }

    fun validateDeathFromFalling() {
        val badge = Badge.DEATH_FROM_FALLING
        local.add(badge)
        displayBadge(badge)
    }

    private fun validateYASD() {
        if (global!!.contains(Badge.DEATH_FROM_FIRE) &&
            global!!.contains(Badge.DEATH_FROM_POISON) &&
            global!!.contains(Badge.DEATH_FROM_GAS) &&
            global!!.contains(Badge.DEATH_FROM_HUNGER)
        ) {
            val badge = Badge.YASD
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateBossSlain() {
        var badge: Badge? = null
        when (Dungeon.depth) {
            5 -> badge = Badge.BOSS_SLAIN_1
            10 -> badge = Badge.BOSS_SLAIN_2
            15 -> badge = Badge.BOSS_SLAIN_3
            20 -> badge = Badge.BOSS_SLAIN_4
        }
        if (badge != null) {
            local.add(badge)
            displayBadge(badge)
            if (badge == Badge.BOSS_SLAIN_1) {
                when (Dungeon.hero.heroClass) {
                    WARRIOR -> badge = Badge.BOSS_SLAIN_1_WARRIOR
                    MAGE -> badge = Badge.BOSS_SLAIN_1_MAGE
                    ROGUE -> badge = Badge.BOSS_SLAIN_1_ROGUE
                    HUNTRESS -> badge = Badge.BOSS_SLAIN_1_HUNTRESS
                }
                local.add(badge)
                if (!global!!.contains(badge)) {
                    global!!.add(badge)
                    saveNeeded = true
                }
                if (global!!.contains(Badge.BOSS_SLAIN_1_WARRIOR) &&
                    global!!.contains(Badge.BOSS_SLAIN_1_MAGE) &&
                    global!!.contains(Badge.BOSS_SLAIN_1_ROGUE) &&
                    global!!.contains(Badge.BOSS_SLAIN_1_HUNTRESS)
                ) {
                    badge = Badge.BOSS_SLAIN_1_ALL_CLASSES
                    if (!global!!.contains(badge)) {
                        displayBadge(badge)
                        global!!.add(badge)
                        saveNeeded = true
                    }
                }
            } else if (badge == Badge.BOSS_SLAIN_3) {
                badge = when (Dungeon.hero.subClass) {
                    GLADIATOR -> Badge.BOSS_SLAIN_3_GLADIATOR
                    BERSERKER -> Badge.BOSS_SLAIN_3_BERSERKER
                    WARLOCK -> Badge.BOSS_SLAIN_3_WARLOCK
                    BATTLEMAGE -> Badge.BOSS_SLAIN_3_BATTLEMAGE
                    FREERUNNER -> Badge.BOSS_SLAIN_3_FREERUNNER
                    ASSASSIN -> Badge.BOSS_SLAIN_3_ASSASSIN
                    SNIPER -> Badge.BOSS_SLAIN_3_SNIPER
                    WARDEN -> Badge.BOSS_SLAIN_3_WARDEN
                    else -> return
                }
                local.add(badge)
                if (!global!!.contains(badge)) {
                    global!!.add(badge)
                    saveNeeded = true
                }
                if (global!!.contains(Badge.BOSS_SLAIN_3_GLADIATOR) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_BERSERKER) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_WARLOCK) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_BATTLEMAGE) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_FREERUNNER) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_ASSASSIN) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_SNIPER) &&
                    global!!.contains(Badge.BOSS_SLAIN_3_WARDEN)
                ) {
                    badge = Badge.BOSS_SLAIN_3_ALL_SUBCLASSES
                    if (!global!!.contains(badge)) {
                        displayBadge(badge)
                        global!!.add(badge)
                        saveNeeded = true
                    }
                }
            }
        }
    }

    fun validateMastery() {
        var badge: Badge? = null
        when (Dungeon.hero.heroClass) {
            WARRIOR -> badge = Badge.MASTERY_WARRIOR
            MAGE -> badge = Badge.MASTERY_MAGE
            ROGUE -> badge = Badge.MASTERY_ROGUE
            HUNTRESS -> badge = Badge.MASTERY_HUNTRESS
        }
        if (!global!!.contains(badge)) {
            global!!.add(badge)
            saveNeeded = true
        }
    }

    fun validateMasteryCombo(n: Int) {
        if (!local.contains(Badge.MASTERY_COMBO) && n == 7) {
            val badge = Badge.MASTERY_COMBO
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateRingOfHaggler() {
        if (!local.contains(Badge.RING_OF_HAGGLER) && RingOfHaggler().isKnown()) {
            val badge = Badge.RING_OF_HAGGLER
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateRingOfThorns() {
        if (!local.contains(Badge.RING_OF_THORNS) && RingOfThorns().isKnown()) {
            val badge = Badge.RING_OF_THORNS
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateRare(mob: Mob?) {
        var badge: Badge? = null
        if (mob is Albino) {
            badge = Badge.RARE_ALBINO
        } else if (mob is Bandit) {
            badge = Badge.RARE_BANDIT
        } else if (mob is Shielded) {
            badge = Badge.RARE_SHIELDED
        } else if (mob is Senior) {
            badge = Badge.RARE_SENIOR
        } else if (mob is Acidic) {
            badge = Badge.RARE_ACIDIC
        }
        if (!global!!.contains(badge)) {
            global!!.add(badge)
            saveNeeded = true
        }
        if (global!!.contains(Badge.RARE_ALBINO) &&
            global!!.contains(Badge.RARE_BANDIT) &&
            global!!.contains(Badge.RARE_SHIELDED) &&
            global!!.contains(Badge.RARE_SENIOR) &&
            global!!.contains(Badge.RARE_ACIDIC)
        ) {
            badge = Badge.RARE
            displayBadge(badge)
        }
    }

    fun validateVictory() {
        var badge = Badge.VICTORY
        displayBadge(badge)
        when (Dungeon.hero.heroClass) {
            WARRIOR -> badge = Badge.VICTORY_WARRIOR
            MAGE -> badge = Badge.VICTORY_MAGE
            ROGUE -> badge = Badge.VICTORY_ROGUE
            HUNTRESS -> badge = Badge.VICTORY_HUNTRESS
        }
        local.add(badge)
        if (!global!!.contains(badge)) {
            global!!.add(badge)
            saveNeeded = true
        }
        if (global!!.contains(Badge.VICTORY_WARRIOR) &&
            global!!.contains(Badge.VICTORY_MAGE) &&
            global!!.contains(Badge.VICTORY_ROGUE) &&
            global!!.contains(Badge.VICTORY_HUNTRESS)
        ) {
            badge = Badge.VICTORY_ALL_CLASSES
            displayBadge(badge)
        }
    }

    fun validateNoKilling() {
        if (!local.contains(Badge.NO_MONSTERS_SLAIN) && Statistics.completedWithNoKilling) {
            val badge = Badge.NO_MONSTERS_SLAIN
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateGrimWeapon() {
        if (!local.contains(Badge.GRIM_WEAPON)) {
            val badge = Badge.GRIM_WEAPON
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateNightHunter() {
        if (!local.contains(Badge.NIGHT_HUNTER) && Statistics.nightHunt >= 15) {
            val badge = Badge.NIGHT_HUNTER
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateSupporter() {
        global!!.add(Badge.SUPPORTER)
        saveNeeded = true
        PixelScene.showBadge(Badge.SUPPORTER)
    }

    fun validateGamesPlayed() {
        var badge: Badge? = null
        if (Rankings.INSTANCE.totalNumber >= 10) {
            badge = Badge.GAMES_PLAYED_1
        }
        if (Rankings.INSTANCE.totalNumber >= 100) {
            badge = Badge.GAMES_PLAYED_2
        }
        if (Rankings.INSTANCE.totalNumber >= 500) {
            badge = Badge.GAMES_PLAYED_3
        }
        if (Rankings.INSTANCE.totalNumber >= 2000) {
            badge = Badge.GAMES_PLAYED_4
        }
        displayBadge(badge)
    }

    fun validateHappyEnd() {
        displayBadge(Badge.HAPPY_END)
    }

    fun validateChampion() {
        displayBadge(Badge.CHAMPION)
    }

    private fun displayBadge(badge: Badge?) {
        if (badge == null) {
            return
        }
        if (global!!.contains(badge)) {
            if (!badge.meta) {
                GLog.h("Badge endorsed: %s", badge.description)
            }
        } else {
            global!!.add(badge)
            saveNeeded = true
            if (badge.meta) {
                GLog.h("New super badge: %s", badge.description)
            } else {
                GLog.h("New badge: %s", badge.description)
            }
            PixelScene.showBadge(badge)
        }
    }

    fun isUnlocked(badge: Badge?): Boolean {
        return global!!.contains(badge)
    }

    fun disown(badge: Badge?) {
        loadGlobal()
        global!!.remove(badge)
        saveNeeded = true
    }

    fun filtered(global: Boolean): List<Badge?> {
        val filtered = HashSet(if (global) Badges.global else local)
        run {
            val iterator = filtered.iterator()
            while (iterator.hasNext()) {
                val badge = iterator.next()
                if (!global && badge!!.meta || badge!!.image == -1) {
                    iterator.remove()
                }
            }
        }
        leaveBest(
            filtered,
            Badge.MONSTERS_SLAIN_1,
            Badge.MONSTERS_SLAIN_2,
            Badge.MONSTERS_SLAIN_3,
            Badge.MONSTERS_SLAIN_4
        )
        leaveBest(
            filtered,
            Badge.GOLD_COLLECTED_1,
            Badge.GOLD_COLLECTED_2,
            Badge.GOLD_COLLECTED_3,
            Badge.GOLD_COLLECTED_4
        )
        leaveBest(filtered, Badge.BOSS_SLAIN_1, Badge.BOSS_SLAIN_2, Badge.BOSS_SLAIN_3, Badge.BOSS_SLAIN_4)
        leaveBest(filtered, Badge.LEVEL_REACHED_1, Badge.LEVEL_REACHED_2, Badge.LEVEL_REACHED_3, Badge.LEVEL_REACHED_4)
        leaveBest(
            filtered,
            Badge.STRENGTH_ATTAINED_1,
            Badge.STRENGTH_ATTAINED_2,
            Badge.STRENGTH_ATTAINED_3,
            Badge.STRENGTH_ATTAINED_4
        )
        leaveBest(filtered, Badge.FOOD_EATEN_1, Badge.FOOD_EATEN_2, Badge.FOOD_EATEN_3, Badge.FOOD_EATEN_4)
        leaveBest(filtered, Badge.ITEM_LEVEL_1, Badge.ITEM_LEVEL_2, Badge.ITEM_LEVEL_3, Badge.ITEM_LEVEL_4)
        leaveBest(
            filtered,
            Badge.POTIONS_COOKED_1,
            Badge.POTIONS_COOKED_2,
            Badge.POTIONS_COOKED_3,
            Badge.POTIONS_COOKED_4
        )
        leaveBest(filtered, Badge.BOSS_SLAIN_1_ALL_CLASSES, Badge.BOSS_SLAIN_3_ALL_SUBCLASSES)
        leaveBest(filtered, Badge.DEATH_FROM_FIRE, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_GAS, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_HUNGER, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_POISON, Badge.YASD)
        leaveBest(filtered, Badge.ALL_POTIONS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_SCROLLS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_RINGS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_WANDS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.VICTORY, Badge.VICTORY_ALL_CLASSES)
        leaveBest(filtered, Badge.VICTORY, Badge.HAPPY_END)
        leaveBest(filtered, Badge.VICTORY, Badge.CHAMPION)
        leaveBest(filtered, Badge.GAMES_PLAYED_1, Badge.GAMES_PLAYED_2, Badge.GAMES_PLAYED_3, Badge.GAMES_PLAYED_4)
        val list = ArrayList(filtered)
        Collections.sort(list)
        return list
    }

    private fun leaveBest(list: HashSet<Badge?>, vararg badges: Badge) {
        for (i in badges.size - 1 downTo 1) {
            if (list.contains(badges[i])) {
                for (j in 0 until i) {
                    list.remove(badges[j])
                }
                break
            }
        }
    }

    enum class Badge(var description: String = "", var image: Int = -1, var meta: Boolean = false) {
        MONSTERS_SLAIN_1("10 enemies slain", 0), MONSTERS_SLAIN_2(
            "50 enemies slain",
            1
        ),
        MONSTERS_SLAIN_3("150 enemies slain", 2), MONSTERS_SLAIN_4(
            "250 enemies slain",
            3
        ),
        GOLD_COLLECTED_1("100 gold collected", 4), GOLD_COLLECTED_2(
            "500 gold collected",
            5
        ),
        GOLD_COLLECTED_3("2500 gold collected", 6), GOLD_COLLECTED_4(
            "7500 gold collected",
            7
        ),
        LEVEL_REACHED_1("Level 6 reached", 8), LEVEL_REACHED_2(
            "Level 12 reached",
            9
        ),
        LEVEL_REACHED_3("Level 18 reached", 10), LEVEL_REACHED_4(
            "Level 24 reached",
            11
        ),
        ALL_POTIONS_IDENTIFIED("All potions identified", 16), ALL_SCROLLS_IDENTIFIED(
            "All scrolls identified",
            17
        ),
        ALL_RINGS_IDENTIFIED("All rings identified", 18), ALL_WANDS_IDENTIFIED(
            "All wands identified",
            19
        ),
        ALL_ITEMS_IDENTIFIED(
            "All potions, scrolls, rings & wands identified",
            35,
            true
        ),
        BAG_BOUGHT_SEED_POUCH, BAG_BOUGHT_SCROLL_HOLDER, BAG_BOUGHT_WAND_HOLSTER, ALL_BAGS_BOUGHT(
            "All bags bought",
            23
        ),
        DEATH_FROM_FIRE("Death from fire", 24), DEATH_FROM_POISON(
            "Death from poison",
            25
        ),
        DEATH_FROM_GAS("Death from toxic gas", 26), DEATH_FROM_HUNGER(
            "Death from hunger",
            27
        ),
        DEATH_FROM_GLYPH("Death from an enchantment", 57), DEATH_FROM_FALLING(
            "Death from falling down",
            59
        ),
        YASD(
            "Death from fire, poison, toxic gas & hunger",
            34,
            true
        ),
        BOSS_SLAIN_1_WARRIOR, BOSS_SLAIN_1_MAGE, BOSS_SLAIN_1_ROGUE, BOSS_SLAIN_1_HUNTRESS, BOSS_SLAIN_1(
            "1st boss slain",
            12
        ),
        BOSS_SLAIN_2("2nd boss slain", 13), BOSS_SLAIN_3("3rd boss slain", 14), BOSS_SLAIN_4(
            "4th boss slain",
            15
        ),
        BOSS_SLAIN_1_ALL_CLASSES(
            "1st boss slain by Warrior, Mage, Rogue & Huntress",
            32,
            true
        ),
        BOSS_SLAIN_3_GLADIATOR, BOSS_SLAIN_3_BERSERKER, BOSS_SLAIN_3_WARLOCK, BOSS_SLAIN_3_BATTLEMAGE, BOSS_SLAIN_3_FREERUNNER, BOSS_SLAIN_3_ASSASSIN, BOSS_SLAIN_3_SNIPER, BOSS_SLAIN_3_WARDEN, BOSS_SLAIN_3_ALL_SUBCLASSES(
            "3rd boss slain by Gladiator, Berserker, Warlock, Battlemage, Freerunner, Assassin, Sniper & Warden",
            33,
            true
        ),
        RING_OF_HAGGLER("Ring of Haggler obtained", 20), RING_OF_THORNS(
            "Ring of Thorns obtained",
            21
        ),
        STRENGTH_ATTAINED_1("13 points of Strength attained", 40), STRENGTH_ATTAINED_2(
            "15 points of Strength attained",
            41
        ),
        STRENGTH_ATTAINED_3("17 points of Strength attained", 42), STRENGTH_ATTAINED_4(
            "19 points of Strength attained",
            43
        ),
        FOOD_EATEN_1("10 pieces of food eaten", 44), FOOD_EATEN_2(
            "20 pieces of food eaten",
            45
        ),
        FOOD_EATEN_3("30 pieces of food eaten", 46), FOOD_EATEN_4(
            "40 pieces of food eaten",
            47
        ),
        MASTERY_WARRIOR, MASTERY_MAGE, MASTERY_ROGUE, MASTERY_HUNTRESS, ITEM_LEVEL_1(
            "Item of level 3 acquired",
            48
        ),
        ITEM_LEVEL_2("Item of level 6 acquired", 49), ITEM_LEVEL_3(
            "Item of level 9 acquired",
            50
        ),
        ITEM_LEVEL_4(
            "Item of level 12 acquired",
            51
        ),
        RARE_ALBINO, RARE_BANDIT, RARE_SHIELDED, RARE_SENIOR, RARE_ACIDIC, RARE(
            "All rare monsters slain",
            37,
            true
        ),
        VICTORY_WARRIOR, VICTORY_MAGE, VICTORY_ROGUE, VICTORY_HUNTRESS, VICTORY(
            "Amulet of Yendor obtained",
            22
        ),
        VICTORY_ALL_CLASSES(
            "Amulet of Yendor obtained by Warrior, Mage, Rogue & Huntress",
            36,
            true
        ),
        MASTERY_COMBO("7-hit combo", 56), POTIONS_COOKED_1("3 potions cooked", 52), POTIONS_COOKED_2(
            "6 potions cooked",
            53
        ),
        POTIONS_COOKED_3("9 potions cooked", 54), POTIONS_COOKED_4(
            "12 potions cooked",
            55
        ),
        NO_MONSTERS_SLAIN(
            "Level completed without killing any monsters",
            28
        ),
        GRIM_WEAPON("Monster killed by a Grim weapon", 29), PIRANHAS(
            "6 piranhas killed",
            30
        ),
        NIGHT_HUNTER("15 monsters killed at nighttime", 58), GAMES_PLAYED_1(
            "10 games played",
            60,
            true
        ),
        GAMES_PLAYED_2("100 games played", 61, true), GAMES_PLAYED_3(
            "500 games played",
            62,
            true
        ),
        GAMES_PLAYED_4("2000 games played", 63, true), HAPPY_END("Happy end", 38), CHAMPION(
            "Challenge won",
            39,
            true
        ),
        SUPPORTER("Thanks for your support!", 31, true);
    }
}