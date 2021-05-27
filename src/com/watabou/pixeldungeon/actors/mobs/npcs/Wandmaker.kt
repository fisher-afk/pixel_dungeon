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

import com.watabou.pixeldungeon.Dungeon

class Wandmaker : NPC() {
    protected fun act(): Boolean {
        throwItem()
        return super.act()
    }

    fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    fun defenseVerb(): String {
        return "absorbed"
    }

    fun damage(dmg: Int, src: Any?) {}
    fun add(buff: Buff?) {}
    fun reset(): Boolean {
        return true
    }

    override fun interact() {
        sprite.turnTo(pos, Dungeon.hero.pos)
        Quest.type!!.handler!!.interact(this)
    }

    private fun tell(format: String?, vararg args: Any) {
        GameScene.show(WndQuest(this, Utils.format(format, args)))
    }

    fun description(): String {
        return "This old but hale gentleman wears a slightly confused " +
                "expression. He is protected by a magic shield."
    }

    object Quest {
        private var type: Type? = null
        private var spawned = false
        var given = false
        var wand1: Wand? = null
        var wand2: Wand? = null
        fun reset() {
            spawned = false
            wand1 = null
            wand2 = null
        }

        private const val NODE = "wandmaker"
        private const val SPAWNED = "spawned"
        private const val TYPE = "type"
        private const val ALTERNATIVE = "alternative"
        private const val GIVEN = "given"
        private const val WAND1 = "wand1"
        private const val WAND2 = "wand2"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(TYPE, type.toString())
                node.put(GIVEN, given)
                node.put(WAND1, wand1)
                node.put(WAND2, wand2)
            }
            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {
            val node: Bundle = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                type = node.getEnum(TYPE, Type::class.java)
                if (type == Type.ILLEGAL) {
                    type = if (node.getBoolean(ALTERNATIVE)) Type.DUST else Type.BERRY
                }
                given = node.getBoolean(GIVEN)
                wand1 = node.get(WAND1) as Wand
                wand2 = node.get(WAND2) as Wand
            } else {
                reset()
            }
        }

        fun spawn(level: PrisonLevel, room: Room) {
            if (!spawned && Dungeon.depth > 6 && Random.Int(10 - Dungeon.depth) === 0) {
                val npc = Wandmaker()
                do {
                    npc.pos = room.random()
                } while (level.map.get(npc.pos) === Terrain.ENTRANCE || level.map.get(npc.pos) === Terrain.SIGN)
                level.mobs.add(npc)
                Actor.occupyCell(npc)
                spawned = true
                when (Random.Int(3)) {
                    0 -> type = Type.BERRY
                    1 -> type = Type.DUST
                    2 -> {
                        type = Type.FISH
                        var water = 0
                        var i = 0
                        while (i < Level.LENGTH) {
                            if (Level.water.get(i)) {
                                if (++water > Level.LENGTH / 16) {
                                    type = if (Random.Int(2) === 0) Type.BERRY else Type.DUST
                                    break
                                }
                            }
                            i++
                        }
                    }
                }
                given = false
                when (Random.Int(5)) {
                    0 -> wand1 = WandOfAvalanche()
                    1 -> wand1 = WandOfDisintegration()
                    2 -> wand1 = WandOfFirebolt()
                    3 -> wand1 = WandOfLightning()
                    4 -> wand1 = WandOfPoison()
                }
                wand1.random().upgrade()
                when (Random.Int(5)) {
                    0 -> wand2 = WandOfAmok()
                    1 -> wand2 = WandOfBlink()
                    2 -> wand2 = WandOfRegrowth()
                    3 -> wand2 = WandOfSlowness()
                    4 -> wand2 = WandOfReach()
                }
                wand2.random().upgrade()
            }
        }

        fun complete() {
            wand1 = null
            wand2 = null
            Journal.remove(Journal.Feature.WANDMAKER)
        }

        internal enum class Type(var handler: QuestHandler?) {
            ILLEGAL(null), BERRY(berryQuest), DUST(dustQuest), FISH(fishQuest);
        }
    }

    abstract class QuestHandler {
        protected var txtQuest1: String? = null
        protected var txtQuest2: String? = null
        fun interact(wandmaker: Wandmaker) {
            if (Quest.given) {
                val item: Item? = checkItem()
                if (item != null) {
                    GameScene.show(WndWandmaker(wandmaker, item))
                } else {
                    wandmaker.tell(txtQuest2, Dungeon.hero.className())
                }
            } else {
                wandmaker.tell(txtQuest1)
                Quest.given = true
                placeItem()
                Journal.add(Journal.Feature.WANDMAKER)
            }
        }

        protected abstract fun checkItem(): Item?
        protected abstract fun placeItem()
    }

    companion object {
        private val berryQuest: QuestHandler = object : QuestHandler() {
            override fun checkItem(): Item {
                return Dungeon.hero.belongings.getItem(Rotberry.Seed::class.java)
            }

            override fun placeItem() {
                var shrubPos: Int = Dungeon.level.randomRespawnCell()
                while (Dungeon.level.heaps.get(shrubPos) != null) {
                    shrubPos = Dungeon.level.randomRespawnCell()
                }
                Dungeon.level.plant(Seed(), shrubPos)
            }

            init {
                txtQuest1 =
                    "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient - " +
                            "a _Rotberry seed_. Being a magic user, I'm quite able to defend myself against local monsters, " +
                            "but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be " +
                            "happy to pay for your service with one of my best wands."
                txtQuest2 = "Any luck with a _Rotberry seed_, %s? No? Don't worry, I'm not in a hurry."
            }
        }
        private val dustQuest: QuestHandler = object : QuestHandler() {
            override fun checkItem(): Item {
                return Dungeon.hero.belongings.getItem(CorpseDust::class.java)
            }

            override fun placeItem() {
                val candidates: ArrayList<Heap> = ArrayList<Heap>()
                for (heap in Dungeon.level.heaps.values()) {
                    if (heap.type === Heap.Type.SKELETON && !Dungeon.visible.get(heap.pos)) {
                        candidates.add(heap)
                    }
                }
                if (candidates.size > 0) {
                    Random.element(candidates).drop(CorpseDust())
                } else {
                    var pos: Int = Dungeon.level.randomRespawnCell()
                    while (Dungeon.level.heaps.get(pos) != null) {
                        pos = Dungeon.level.randomRespawnCell()
                    }
                    val heap: Heap = Dungeon.level.drop(CorpseDust(), pos)
                    heap.type = Heap.Type.SKELETON
                    heap.sprite.link()
                }
            }

            init {
                txtQuest1 =
                    "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient - " +
                            "_corpse dust_. It can be gathered from skeletal remains and there is an ample number of them in the dungeon. " +
                            "Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, " +
                            "it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands."
                txtQuest2 = "Any luck with _corpse dust_, %s? Bone piles are the most obvious places to look."
            }
        }
        private val fishQuest: QuestHandler = object : QuestHandler() {
            override fun checkItem(): Item {
                return Dungeon.hero.belongings.getItem(PhantomFish::class.java)
            }

            override fun placeItem() {
                var heap: Heap? = null
                for (i in 0..99) {
                    val pos: Int = Random.Int(Level.LENGTH)
                    if (Level.water.get(pos)) {
                        heap = Dungeon.level.drop(PhantomFish(), pos)
                        heap.type = Heap.Type.HIDDEN
                        heap.sprite.link()
                        return
                    }
                }
                if (heap == null) {
                    var pos: Int = Dungeon.level.randomRespawnCell()
                    while (Dungeon.level.heaps.get(pos) != null) {
                        pos = Dungeon.level.randomRespawnCell()
                    }
                    Dungeon.level.drop(PhantomFish(), pos)
                }
            }

            init {
                txtQuest1 =
                    "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient: " +
                            "a _phantom fish_. You can catch it with your bare hands, but it's very hard to notice in the water. " +
                            "Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, " +
                            "it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands."
                txtQuest2 =
                    "Any luck with a _phantom fish_, %s? You may want to try searching for it in one of the local pools."
            }
        }
    }

    init {
        name = "old wandmaker"
        spriteClass = WandmakerSprite::class.java
    }
}