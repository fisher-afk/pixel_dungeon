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
package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.noosa.audio.Sample

class Blacksmith : NPC() {
    protected fun act(): Boolean {
        throwItem()
        return super.act()
    }

    override fun interact() {
        sprite.turnTo(pos, Dungeon.hero.pos)
        if (!Quest.given) {
            GameScene.show(object : WndQuest(
                this,
                if (Quest.alternative) TXT_BLOOD_1 else TXT_GOLD_1
            ) {
                fun onBackPressed() {
                    super.onBackPressed()
                    Quest.given = true
                    Quest.completed = false
                    val pick = Pickaxe()
                    if (pick.doPickUp(Dungeon.hero)) {
                        GLog.i(Hero.TXT_YOU_NOW_HAVE, pick.name())
                    } else {
                        Dungeon.level.drop(pick, Dungeon.hero.pos).sprite.drop()
                    }
                }
            })
            Journal.add(Journal.Feature.TROLL)
        } else if (!Quest.completed) {
            if (Quest.alternative) {
                val pick: Pickaxe = Dungeon.hero.belongings.getItem(Pickaxe::class.java)
                if (pick == null) {
                    tell(TXT2)
                } else if (!pick.bloodStained) {
                    tell(TXT4)
                } else {
                    if (pick.isEquipped(Dungeon.hero)) {
                        pick.doUnequip(Dungeon.hero, false)
                    }
                    pick.detach(Dungeon.hero.belongings.backpack)
                    tell(TXT_COMPLETED)
                    Quest.completed = true
                    Quest.reforged = false
                }
            } else {
                val pick: Pickaxe = Dungeon.hero.belongings.getItem(Pickaxe::class.java)
                val gold: DarkGold = Dungeon.hero.belongings.getItem(DarkGold::class.java)
                if (pick == null) {
                    tell(TXT2)
                } else if (gold == null || gold.quantity() < 15) {
                    tell(TXT3)
                } else {
                    if (pick.isEquipped(Dungeon.hero)) {
                        pick.doUnequip(Dungeon.hero, false)
                    }
                    pick.detach(Dungeon.hero.belongings.backpack)
                    gold.detachAll(Dungeon.hero.belongings.backpack)
                    tell(TXT_COMPLETED)
                    Quest.completed = true
                    Quest.reforged = false
                }
            }
        } else if (!Quest.reforged) {
            GameScene.show(WndBlacksmith(this, Dungeon.hero))
        } else {
            tell(TXT_GET_LOST)
        }
    }

    private fun tell(text: String) {
        GameScene.show(WndQuest(this, text))
    }

    fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    fun damage(dmg: Int, src: Any?) {}
    fun add(buff: Buff?) {}
    fun reset(): Boolean {
        return true
    }

    fun description(): String {
        return "This troll blacksmith looks like all trolls look: he is tall and lean, and his skin resembles stone " +
                "in both color and texture. The troll blacksmith is tinkering with unproportionally small tools."
    }

    object Quest {
        private var spawned = false
        var alternative = false
        var given = false
        var completed = false
        var reforged = false
        fun reset() {
            spawned = false
            given = false
            completed = false
            reforged = false
        }

        private const val NODE = "blacksmith"
        private const val SPAWNED = "spawned"
        private const val ALTERNATIVE = "alternative"
        private const val GIVEN = "given"
        private const val COMPLETED = "completed"
        private const val REFORGED = "reforged"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(ALTERNATIVE, alternative)
                node.put(GIVEN, given)
                node.put(COMPLETED, completed)
                node.put(REFORGED, reforged)
            }
            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {
            val node: Bundle = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                alternative = node.getBoolean(ALTERNATIVE)
                given = node.getBoolean(GIVEN)
                completed = node.getBoolean(COMPLETED)
                reforged = node.getBoolean(REFORGED)
            } else {
                reset()
            }
        }

        fun spawn(rooms: Collection<Room>) {
            if (!spawned && Dungeon.depth > 11 && Random.Int(15 - Dungeon.depth) === 0) {
                var blacksmith: Room? = null
                for (r in rooms) {
                    if (r.type === Type.STANDARD && r.width() > 4 && r.height() > 4) {
                        blacksmith = r
                        blacksmith.type = Type.BLACKSMITH
                        spawned = true
                        alternative = Random.Int(2) === 0
                        given = false
                        break
                    }
                }
            }
        }
    }

    companion object {
        private const val TXT_GOLD_1 =
            "Hey human! Wanna be useful, eh? Take dis pickaxe and mine me some _dark gold ore_, _15 pieces_ should be enough. " +
                    "What do you mean, how am I gonna pay? You greedy...\n" +
                    "Ok, ok, I don't have money to pay, but I can do some smithin' for you. Consider yourself lucky, " +
                    "I'm the only blacksmith around."
        private const val TXT_BLOOD_1 =
            "Hey human! Wanna be useful, eh? Take dis pickaxe and _kill a bat_ wit' it, I need its blood on the head. " +
                    "What do you mean, how am I gonna pay? You greedy...\n" +
                    "Ok, ok, I don't have money to pay, but I can do some smithin' for you. Consider yourself lucky, " +
                    "I'm the only blacksmith around."
        private const val TXT2 = "Are you kiddin' me? Where is my pickaxe?!"
        private const val TXT3 = "Dark gold ore. 15 pieces. Seriously, is it dat hard?"
        private const val TXT4 = "I said I need bat blood on the pickaxe. Chop chop!"
        private const val TXT_COMPLETED = "Oh, you have returned... Better late dan never."
        private const val TXT_GET_LOST = "I'm busy. Get lost!"
        private const val TXT_LOOKS_BETTER = "your %s certainly looks better now"
        fun verify(item1: Item, item2: Item): String? {
            if (item1 === item2) {
                return "Select 2 different items, not the same item twice!"
            }
            if (item1.getClass() !== item2.getClass()) {
                return "Select 2 items of the same type!"
            }
            if (!item1.isIdentified() || !item2.isIdentified()) {
                return "I need to know what I'm working with, identify them first!"
            }
            if (item1.cursed || item2.cursed) {
                return "I don't work with cursed items!"
            }
            if (item1.level() < 0 || item2.level() < 0) {
                return "It's a junk, the quality is too poor!"
            }
            return if (!item1.isUpgradable() || !item2.isUpgradable()) {
                "I can't reforge these items!"
            } else null
        }

        fun upgrade(item1: Item, item2: Item) {
            val first: Item
            val second: Item
            if (item2.level() > item1.level()) {
                first = item2
                second = item1
            } else {
                first = item1
                second = item2
            }
            Sample.INSTANCE.play(Assets.SND_EVOKE)
            ScrollOfUpgrade.upgrade(Dungeon.hero)
            Item.evoke(Dungeon.hero)
            if (first.isEquipped(Dungeon.hero)) {
                (first as EquipableItem).doUnequip(Dungeon.hero, true)
            }
            first.upgrade()
            GLog.p(TXT_LOOKS_BETTER, first.name())
            Dungeon.hero.spendAndNext(2f)
            Badges.validateItemLevelAquired(first)
            if (second.isEquipped(Dungeon.hero)) {
                (second as EquipableItem).doUnequip(Dungeon.hero, false)
            }
            second.detachAll(Dungeon.hero.belongings.backpack)
            Quest.reforged = true
            Journal.remove(Journal.Feature.TROLL)
        }
    }

    init {
        name = "troll blacksmith"
        spriteClass = BlacksmithSprite::class.java
    }
}