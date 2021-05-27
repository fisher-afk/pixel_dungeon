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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object Dungeon {
    var potionOfStrength = 0
    var scrollsOfUpgrade = 0
    var scrollsOfEnchantment = 0
    var dewVial // true if the dew vial can be spawned
            = false
    var challenges = 0
    var hero: Hero? = null
    var level: Level? = null
    var depth = 0
    var gold = 0

    // Reason of death
    var resultDescription: String? = null
    var chapters: HashSet<Int>? = null

    // Hero's field of view
    var visible = BooleanArray(Level.LENGTH)
    var nightMode = false
    var droppedItems: SparseArray<ArrayList<Item>>? = null
    fun init() {
        challenges = PixelDungeon.challenges()
        Actor.clear()
        PathFinder.setMapSize(Level.WIDTH, Level.HEIGHT)
        Scroll.initLabels()
        Potion.initColors()
        Wand.initWoods()
        Ring.initGems()
        Statistics.reset()
        Journal.reset()
        depth = 0
        gold = 0
        droppedItems = SparseArray<ArrayList<Item>>()
        potionOfStrength = 0
        scrollsOfUpgrade = 0
        scrollsOfEnchantment = 0
        dewVial = true
        chapters = HashSet()
        Ghost.Quest.reset()
        Wandmaker.Quest.reset()
        Blacksmith.Quest.reset()
        Imp.Quest.reset()
        Room.shuffleTypes()
        QuickSlot.primaryValue = null
        QuickSlot.secondaryValue = null
        hero = Hero()
        hero.live()
        Badges.reset()
        StartScene.curClass.initHero(hero)
    }

    fun isChallenged(mask: Int): Boolean {
        return challenges and mask != 0
    }

    fun newLevel(): Level {
        level = null
        Actor.clear()
        depth++
        if (depth > Statistics.deepestFloor) {
            Statistics.deepestFloor = depth
            if (Statistics.qualifiedForNoKilling) {
                Statistics.completedWithNoKilling = true
            } else {
                Statistics.completedWithNoKilling = false
            }
        }
        Arrays.fill(visible, false)
        val level: Level
        when (depth) {
            1, 2, 3, 4 -> level = SewerLevel()
            5 -> level = SewerBossLevel()
            6, 7, 8, 9 -> level = PrisonLevel()
            10 -> level = PrisonBossLevel()
            11, 12, 13, 14 -> level = CavesLevel()
            15 -> level = CavesBossLevel()
            16, 17, 18, 19 -> level = CityLevel()
            20 -> level = CityBossLevel()
            21 -> level = LastShopLevel()
            22, 23, 24 -> level = HallsLevel()
            25 -> level = HallsBossLevel()
            26 -> level = LastLevel()
            else -> {
                level = DeadEndLevel()
                Statistics.deepestFloor--
            }
        }
        level.create()
        Statistics.qualifiedForNoKilling = !bossLevel()
        return level
    }

    fun resetLevel() {
        Actor.clear()
        Arrays.fill(visible, false)
        level.reset()
        switchLevel(level, level.entrance)
    }

    fun shopOnLevel(): Boolean {
        return depth == 6 || depth == 11 || depth == 16
    }

    @JvmOverloads
    fun bossLevel(depth: Int = this.depth): Boolean {
        return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25
    }

    fun switchLevel(level: Level?, pos: Int) {
        nightMode = Date().hours < 7
        Dungeon.level = level
        Actor.init()
        val respawner: Actor = level.respawner()
        if (respawner != null) {
            Actor.add(level.respawner())
        }
        hero.pos = if (pos != -1) pos else level.exit
        val light: Light = hero.buff(Light::class.java)
        hero.viewDistance = if (light == null) level.viewDistance else Math.max(Light.DISTANCE, level.viewDistance)
        observe()
    }

    fun dropToChasm(item: Item?) {
        val depth = depth + 1
        var dropped: ArrayList<Item?>? = droppedItems.get(depth)
        if (dropped == null) {
            droppedItems.put(depth, ArrayList<Item>().also { dropped = it })
        }
        dropped!!.add(item)
    }

    fun posNeeded(): Boolean {
        val quota = intArrayOf(4, 2, 9, 4, 14, 6, 19, 8, 24, 9)
        return chance(quota, potionOfStrength)
    }

    fun souNeeded(): Boolean {
        val quota = intArrayOf(5, 3, 10, 6, 15, 9, 20, 12, 25, 13)
        return chance(quota, scrollsOfUpgrade)
    }

    fun soeNeeded(): Boolean {
        return Random.Int(12 * (1 + scrollsOfEnchantment)) < depth
    }

    private fun chance(quota: IntArray, number: Int): Boolean {
        var i = 0
        while (i < quota.size) {
            val qDepth = quota[i]
            if (depth <= qDepth) {
                val qNumber = quota[i + 1]
                return Random.Float() < (qNumber - number).toFloat() / (qDepth - depth + 1)
            }
            i += 2
        }
        return false
    }

    private const val RG_GAME_FILE = "game.dat"
    private const val RG_DEPTH_FILE = "depth%d.dat"
    private const val WR_GAME_FILE = "warrior.dat"
    private const val WR_DEPTH_FILE = "warrior%d.dat"
    private const val MG_GAME_FILE = "mage.dat"
    private const val MG_DEPTH_FILE = "mage%d.dat"
    private const val RN_GAME_FILE = "ranger.dat"
    private const val RN_DEPTH_FILE = "ranger%d.dat"
    private const val VERSION = "version"
    private const val CHALLENGES = "challenges"
    private const val HERO = "hero"
    private const val GOLD = "gold"
    private const val DEPTH = "depth"
    private const val LEVEL = "level"
    private const val DROPPED = "dropped%d"
    private const val POS = "potionsOfStrength"
    private const val SOU = "scrollsOfEnhancement"
    private const val SOE = "scrollsOfEnchantment"
    private const val DV = "dewVial"
    private const val CHAPTERS = "chapters"
    private const val QUESTS = "quests"
    private const val BADGES = "badges"
    fun gameFile(cl: HeroClass?): String {
        return when (cl) {
            HeroClass.WARRIOR -> WR_GAME_FILE
            HeroClass.MAGE -> MG_GAME_FILE
            HeroClass.HUNTRESS -> RN_GAME_FILE
            else -> RG_GAME_FILE
        }
    }

    private fun depthFile(cl: HeroClass): String {
        return when (cl) {
            HeroClass.WARRIOR -> WR_DEPTH_FILE
            HeroClass.MAGE -> MG_DEPTH_FILE
            HeroClass.HUNTRESS -> RN_DEPTH_FILE
            else -> RG_DEPTH_FILE
        }
    }

    @Throws(IOException::class)
    fun saveGame(fileName: String?) {
        try {
            val bundle = Bundle()
            bundle.put(VERSION, Game.version)
            bundle.put(CHALLENGES, challenges)
            bundle.put(HERO, hero)
            bundle.put(GOLD, gold)
            bundle.put(DEPTH, depth)
            for (d in droppedItems.keyArray()) {
                bundle.put(String.format(DROPPED, d), droppedItems.get(d))
            }
            bundle.put(POS, potionOfStrength)
            bundle.put(SOU, scrollsOfUpgrade)
            bundle.put(SOE, scrollsOfEnchantment)
            bundle.put(DV, dewVial)
            var count = 0
            val ids = IntArray(chapters!!.size)
            for (id in chapters!!) {
                ids[count++] = id
            }
            bundle.put(CHAPTERS, ids)
            val quests = Bundle()
            Ghost.Quest.storeInBundle(quests)
            Wandmaker.Quest.storeInBundle(quests)
            Blacksmith.Quest.storeInBundle(quests)
            Imp.Quest.storeInBundle(quests)
            bundle.put(QUESTS, quests)
            Room.storeRoomsInBundle(bundle)
            Statistics.storeInBundle(bundle)
            Journal.storeInBundle(bundle)
            QuickSlot.save(bundle)
            Scroll.save(bundle)
            Potion.save(bundle)
            Wand.save(bundle)
            Ring.save(bundle)
            val badges = Bundle()
            Badges.saveLocal(badges)
            bundle.put(BADGES, badges)
            val output: OutputStream = Game.instance.openFileOutput(fileName, Game.MODE_PRIVATE)
            Bundle.write(bundle, output)
            output.close()
        } catch (e: Exception) {
            GamesInProgress.setUnknown(hero.heroClass)
        }
    }

    @Throws(IOException::class)
    fun saveLevel() {
        val bundle = Bundle()
        bundle.put(LEVEL, level)
        val output: OutputStream =
            Game.instance.openFileOutput(Utils.format(depthFile(hero.heroClass), depth), Game.MODE_PRIVATE)
        Bundle.write(bundle, output)
        output.close()
    }

    @Throws(IOException::class)
    fun saveAll() {
        if (hero.isAlive()) {
            Actor.fixTime()
            saveGame(gameFile(hero.heroClass))
            saveLevel()
            GamesInProgress.set(hero.heroClass, depth, hero.lvl, challenges != 0)
        } else if (WndResurrect.instance != null) {
            WndResurrect.instance.hide()
            Hero.reallyDie(WndResurrect.causeOfDeath)
        }
    }

    @Throws(IOException::class)
    fun loadGame(cl: HeroClass?) {
        loadGame(gameFile(cl), true)
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun loadGame(fileName: String?, fullLoad: Boolean = false) {
        val bundle: Bundle = gameBundle(fileName)
        challenges = bundle.getInt(CHALLENGES)
        level = null
        depth = -1
        if (fullLoad) {
            PathFinder.setMapSize(Level.WIDTH, Level.HEIGHT)
        }
        Scroll.restore(bundle)
        Potion.restore(bundle)
        Wand.restore(bundle)
        Ring.restore(bundle)
        potionOfStrength = bundle.getInt(POS)
        scrollsOfUpgrade = bundle.getInt(SOU)
        scrollsOfEnchantment = bundle.getInt(SOE)
        dewVial = bundle.getBoolean(DV)
        if (fullLoad) {
            chapters = HashSet()
            val ids: IntArray = bundle.getIntArray(CHAPTERS)
            if (ids != null) {
                for (id in ids) {
                    chapters!!.add(id)
                }
            }
            val quests: Bundle = bundle.getBundle(QUESTS)
            if (!quests.isNull()) {
                Ghost.Quest.restoreFromBundle(quests)
                Wandmaker.Quest.restoreFromBundle(quests)
                Blacksmith.Quest.restoreFromBundle(quests)
                Imp.Quest.restoreFromBundle(quests)
            } else {
                Ghost.Quest.reset()
                Wandmaker.Quest.reset()
                Blacksmith.Quest.reset()
                Imp.Quest.reset()
            }
            Room.restoreRoomsFromBundle(bundle)
        }
        val badges: Bundle = bundle.getBundle(BADGES)
        if (!badges.isNull()) {
            Badges.loadLocal(badges)
        } else {
            Badges.reset()
        }
        QuickSlot.restore(bundle)
        val version: String = bundle.getString(VERSION)
        hero = null
        hero = bundle.get(HERO) as Hero
        QuickSlot.compress()
        gold = bundle.getInt(GOLD)
        depth = bundle.getInt(DEPTH)
        Statistics.restoreFromBundle(bundle)
        Journal.restoreFromBundle(bundle)
        droppedItems = SparseArray<ArrayList<Item>>()
        for (i in 2..Statistics.deepestFloor + 1) {
            val dropped: ArrayList<Item> = ArrayList<Item>()
            for (b in bundle.getCollection(String.format(DROPPED, i))) {
                dropped.add(b as Item)
            }
            if (!dropped.isEmpty()) {
                droppedItems.put(i, dropped)
            }
        }
    }

    @Throws(IOException::class)
    fun loadLevel(cl: HeroClass): Level {
        level = null
        Actor.clear()
        val input: InputStream = Game.instance.openFileInput(Utils.format(depthFile(cl), depth))
        val bundle: Bundle = Bundle.read(input)
        input.close()
        return bundle.get("level") as Level
    }

    fun deleteGame(cl: HeroClass, deleteLevels: Boolean) {
        Game.instance.deleteFile(gameFile(cl))
        if (deleteLevels) {
            var depth = 1
            while (Game.instance.deleteFile(Utils.format(depthFile(cl), depth))) {
                depth++
            }
        }
        GamesInProgress.delete(cl)
    }

    @Throws(IOException::class)
    fun gameBundle(fileName: String?): Bundle {
        val input: InputStream = Game.instance.openFileInput(fileName)
        val bundle: Bundle = Bundle.read(input)
        input.close()
        return bundle
    }

    fun preview(info: Info, bundle: Bundle) {
        info.depth = bundle.getInt(DEPTH)
        info.challenges = bundle.getInt(CHALLENGES) !== 0
        if (info.depth === -1) {
            info.depth = bundle.getInt("maxDepth") // FIXME
        }
        Hero.preview(info, bundle.getBundle(HERO))
    }

    fun fail(desc: String?) {
        resultDescription = desc
        if (hero.belongings.getItem(Ankh::class.java) == null) {
            Rankings.INSTANCE.submit(false)
        }
    }

    fun win(desc: String?) {
        hero.belongings.identify()
        if (challenges != 0) {
            Badges.validateChampion()
        }
        resultDescription = desc
        Rankings.INSTANCE.submit(true)
    }

    fun observe() {
        if (level == null) {
            return
        }
        level.updateFieldOfView(hero)
        System.arraycopy(Level.fieldOfView, 0, visible, 0, visible.size)
        BArray.or(level.visited, visible, level.visited)
        GameScene.afterObserve()
    }

    private val passable = BooleanArray(Level.LENGTH)
    fun findPath(ch: Char, from: Int, to: Int, pass: BooleanArray, visible: BooleanArray): Int {
        if (Level.adjacent(from, to)) {
            return if (Actor.findChar(to) == null && (pass[to] || Level.avoid.get(to))) to else -1
        }
        if (ch.flying || ch.buff(Amok::class.java) != null || ch.buff(Rage::class.java) != null) {
            BArray.or(pass, Level.avoid, passable)
        } else {
            System.arraycopy(pass, 0, passable, 0, Level.LENGTH)
        }
        for (actor in Actor.all()) {
            if (actor is Char) {
                val pos: Int = (actor as Char).pos
                if (visible[pos]) {
                    passable[pos] = false
                }
            }
        }
        return PathFinder.getStep(from, to, passable)
    }

    fun flee(ch: Char, cur: Int, from: Int, pass: BooleanArray?, visible: BooleanArray): Int {
        if (ch.flying) {
            BArray.or(pass, Level.avoid, passable)
        } else {
            System.arraycopy(pass, 0, passable, 0, Level.LENGTH)
        }
        for (actor in Actor.all()) {
            if (actor is Char) {
                val pos: Int = (actor as Char).pos
                if (visible[pos]) {
                    passable[pos] = false
                }
            }
        }
        passable[cur] = true
        return PathFinder.getStepBack(cur, from, passable)
    }
}