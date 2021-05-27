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

class Ghost : NPC() {
    fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    fun defenseVerb(): String {
        return "evaded"
    }

    fun speed(): Float {
        return 0.5f
    }

    protected fun chooseEnemy(): Char? {
        return null
    }

    fun damage(dmg: Int, src: Any?) {}
    fun add(buff: Buff?) {}
    fun reset(): Boolean {
        return true
    }

    override fun interact() {
        sprite.turnTo(pos, Dungeon.hero.pos)
        Sample.INSTANCE.play(Assets.SND_GHOST)
        Quest.type!!.handler!!.interact(this)
    }

    fun description(): String {
        return "The ghost is barely visible. It looks like a shapeless " +
                "spot of faint light with a sorrowful face."
    }

    companion object {
        private val IMMUNITIES = HashSet<Class<*>>()
        fun replace(a: Mob, b: Mob) {
            val FADE_TIME = 0.5f
            a.destroy()
            a.sprite.parent.add(object : AlphaTweener(a.sprite, 0, FADE_TIME) {
                protected fun onComplete() {
                    a.sprite.killAndErase()
                    parent.erase(this)
                }
            })
            b.pos = a.pos
            GameScene.add(b)
            b.sprite.flipHorizontal = a.sprite.flipHorizontal
            b.sprite.alpha(0)
            b.sprite.parent.add(AlphaTweener(b.sprite, 1, FADE_TIME))
        }

        private val roseQuest: QuestHandler = object : QuestHandler() {
            private static
            val TXT_ROSE1 = "Hello adventurer... Once I was like you - strong and confident... " +
                    "And now I'm dead... But I can't leave this place... Not until I have my _dried rose_... " +
                    "It's very important to me... Some monster stole it from my body..."
            private static
            val TXT_ROSE2 = "Please... Help me... _Find the rose_..."
            private static
            val TXT_ROSE3 = "Yes! Yes!!! This is it! Please give it to me! " +
                    "And you can take one of these items, maybe they " +
                    "will be useful to you in your journey..."

            override fun interact(ghost: Ghost?) {
                if (Quest.given) {
                    val item: Item = Dungeon.hero.belongings.getItem(DriedRose::class.java)
                    if (item != null) {
                        GameScene.show(WndSadGhost(ghost, item, TXT_ROSE3))
                    } else {
                        GameScene.show(WndQuest(ghost, TXT_ROSE2))
                        relocate(ghost!!)
                    }
                } else {
                    GameScene.show(WndQuest(ghost, TXT_ROSE1))
                    Quest.given = true
                    Journal.add(Journal.Feature.GHOST)
                }
            }
        }
        private val ratQuest: QuestHandler = object : QuestHandler() {
            private static
            val TXT_RAT1 = "Hello adventurer... Once I was like you - strong and confident... " +
                    "And now I'm dead... But I can't leave this place... Not until I have my revenge... " +
                    "Slay the _fetid rat_, that has taken my life..."
            private static
            val TXT_RAT2 = "Please... Help me... _Slay the abomination_..."
            private static
            val TXT_RAT3 = "Yes! The ugly creature is slain and I can finally rest... " +
                    "Please take one of these items, maybe they " +
                    "will be useful to you in your journey..."

            override fun interact(ghost: Ghost?) {
                if (Quest.given) {
                    val item: Item = Dungeon.hero.belongings.getItem(RatSkull::class.java)
                    if (item != null) {
                        GameScene.show(WndSadGhost(ghost, item, TXT_RAT3))
                    } else {
                        GameScene.show(WndQuest(ghost, TXT_RAT2))
                        relocate(ghost!!)
                    }
                } else {
                    GameScene.show(WndQuest(ghost, TXT_RAT1))
                    Quest.given = true
                    Journal.add(Journal.Feature.GHOST)
                }
            }
        }
        private val curseQuest: QuestHandler = object : QuestHandler() {
            private static
            val TXT_CURSE1 = "Hello adventurer... Once I was like you - strong and confident... " +
                    "And now I'm dead... But I can't leave this place, as I am bound by a horrid curse... " +
                    "Please... Help me... _Destroy the curse_..."
            private static
            val TXT_CURSE2 = "Thank you, %s! The curse is broken and I can finally rest... " +
                    "Please take one of these items, maybe they " +
                    "will be useful to you in your journey..."
            private static
            val TXT_YES = "Yes, I will do it for you"
            private static
            val TXT_NO = "No, I can't help you"
            override fun interact(ghost: Ghost) {
                if (Quest.given) {
                    GameScene.show(WndSadGhost(ghost, null, Utils.format(TXT_CURSE2, Dungeon.hero.className())))
                } else {
                    GameScene.show(object : WndQuest(ghost, TXT_CURSE1, TXT_YES, TXT_NO) {
                        protected fun onSelect(index: Int) {
                            if (index == 0) {
                                Quest.given = true
                                val d = CursePersonification()
                                replace(ghost, d)
                                d.sprite.emitter().burst(ShadowParticle.CURSE, 5)
                                Sample.INSTANCE.play(Assets.SND_GHOST)
                                Dungeon.hero.next()
                            } else {
                                relocate(ghost)
                            }
                        }
                    })
                    Journal.add(Journal.Feature.GHOST)
                }
            }
        }

        init {
            IMMUNITIES.add(Paralysis::class.java)
            IMMUNITIES.add(Roots::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    object Quest {
        private var type: Type? = null
        private var spawned = false
        var given = false
        private var processed = false
        private var depth = 0
        private var left2kill = 0
        var weapon: Weapon? = null
        var armor: Armor? = null
        fun reset() {
            spawned = false
            weapon = null
            armor = null
        }

        private const val NODE = "sadGhost"
        private const val SPAWNED = "spawned"
        private const val TYPE = "type"
        private const val ALTERNATIVE = "alternative"
        private const val LEFT2KILL = "left2kill"
        private const val GIVEN = "given"
        private const val PROCESSED = "processed"
        private const val DEPTH = "depth"
        private const val WEAPON = "weapon"
        private const val ARMOR = "armor"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(TYPE, type.toString())
                if (type == Type.ROSE) {
                    node.put(LEFT2KILL, left2kill)
                }
                node.put(GIVEN, given)
                node.put(DEPTH, depth)
                node.put(PROCESSED, processed)
                node.put(WEAPON, weapon)
                node.put(ARMOR, armor)
            }
            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {
            val node: Bundle = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                type = node.getEnum(TYPE, Type::class.java)
                if (type == Type.ILLEGAL) {
                    type = if (node.getBoolean(ALTERNATIVE)) Type.RAT else Type.ROSE
                }
                if (type == Type.ROSE) {
                    left2kill = node.getInt(LEFT2KILL)
                }
                given = node.getBoolean(GIVEN)
                depth = node.getInt(DEPTH)
                processed = node.getBoolean(PROCESSED)
                weapon = node.get(WEAPON) as Weapon
                armor = node.get(ARMOR) as Armor
            } else {
                reset()
            }
        }

        fun spawn(level: SewerLevel) {
            if (!spawned && Dungeon.depth > 1 && Random.Int(5 - Dungeon.depth) === 0) {
                val ghost = Ghost()
                do {
                    ghost.pos = level.randomRespawnCell()
                } while (ghost.pos === -1)
                level.mobs.add(ghost)
                Actor.occupyCell(ghost)
                spawned = true
                when (Random.Int(3)) {
                    0 -> {
                        type = Type.ROSE
                        left2kill = 8
                    }
                    1 -> type = Type.RAT
                    2 -> type = Type.CURSE
                }
                given = false
                processed = false
                depth = Dungeon.depth
                for (i in 0..3) {
                    var another: Item
                    do {
                        another = Generator.random(Generator.Category.WEAPON) as Weapon
                    } while (another is MissileWeapon)
                    if (weapon == null || another.level() > weapon.level()) {
                        weapon = another as Weapon
                    }
                }
                if (Dungeon.isChallenged(Challenges.NO_ARMOR)) {
                    armor = ClothArmor().degrade() as Armor
                } else {
                    armor = Generator.random(Generator.Category.ARMOR) as Armor
                    for (i in 0..2) {
                        val another: Item = Generator.random(Generator.Category.ARMOR)
                        if (another.level() > armor.level()) {
                            armor = another as Armor
                        }
                    }
                }
                weapon.identify()
                armor.identify()
            }
        }

        fun processSewersKill(pos: Int) {
            if (spawned && given && !processed && depth == Dungeon.depth) {
                when (type) {
                    Type.ROSE -> if (Random.Int(left2kill) === 0) {
                        Dungeon.level.drop(DriedRose(), pos).sprite.drop()
                        processed = true
                    } else {
                        left2kill--
                    }
                    Type.RAT -> {
                        val rat = FetidRat()
                        rat.pos = Dungeon.level.randomRespawnCell()
                        if (rat.pos !== -1) {
                            GameScene.add(rat)
                            processed = true
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        fun complete() {
            weapon = null
            armor = null
            Journal.remove(Journal.Feature.GHOST)
        }

        internal enum class Type(var handler: QuestHandler?) {
            ILLEGAL(null), ROSE(roseQuest), RAT(ratQuest), CURSE(curseQuest);
        }
    }

    abstract class QuestHandler {
        abstract fun interact(ghost: Ghost?)
        protected fun relocate(ghost: Ghost) {
            var newPos = -1
            for (i in 0..9) {
                newPos = Dungeon.level.randomRespawnCell()
                if (newPos != -1) {
                    break
                }
            }
            if (newPos != -1) {
                Actor.freeCell(ghost.pos)
                CellEmitter.get(ghost.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
                ghost.pos = newPos
                ghost.sprite.place(ghost.pos)
                ghost.sprite.visible = Dungeon.visible.get(ghost.pos)
            }
        }
    }

    init {
        name = "sad ghost"
        spriteClass = GhostSprite::class.java
        flying = true
        state = WANDERING
    }

    init {
        Sample.INSTANCE.load(Assets.SND_GHOST)
    }
}