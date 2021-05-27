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

class Imp : NPC() {
    private var seenBefore = false
    protected fun act(): Boolean {
        seenBefore = if (!Quest.given && Dungeon.visible.get(pos)) {
            if (!seenBefore) {
                yell(
                    Utils.format(
                        TXT_HEY,
                        Dungeon.hero.className()
                    )
                )
            }
            true
        } else {
            false
        }
        throwItem()
        return super.act()
    }

    fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    fun defenseVerb(): String {
        return "evaded"
    }

    fun damage(dmg: Int, src: Any?) {}
    fun add(buff: Buff?) {}
    fun reset(): Boolean {
        return true
    }

    override fun interact() {
        sprite.turnTo(pos, Dungeon.hero.pos)
        if (Quest.given) {
            val tokens: DwarfToken = Dungeon.hero.belongings.getItem(DwarfToken::class.java)
            if (tokens != null && (tokens.quantity() >= 8 || !Quest.alternative && tokens.quantity() >= 6)) {
                GameScene.show(WndImp(this, tokens))
            } else {
                tell(if (Quest.alternative) TXT_MONKS2 else TXT_GOLEMS2, Dungeon.hero.className())
            }
        } else {
            tell(if (Quest.alternative) TXT_MONKS1 else TXT_GOLEMS1)
            Quest.given = true
            Quest.isCompleted = false
            Journal.add(Journal.Feature.IMP)
        }
    }

    private fun tell(format: String, vararg args: Any) {
        GameScene.show(
            WndQuest(this, Utils.format(format, args))
        )
    }

    fun flee() {
        yell(Utils.format(TXT_CYA, Dungeon.hero.className()))
        destroy()
        sprite.die()
    }

    fun description(): String {
        return "Imps are lesser demons. They are notable for neither their strength nor their magic talent, " +
                "but they are quite smart and sociable. Many imps prefer to live among non-demons."
    }

    object Quest {
        var alternative = false
        private var spawned = false
        var given = false
        var isCompleted = false
            private set
        var reward: Ring? = null
        fun reset() {
            spawned = false
            reward = null
        }

        private const val NODE = "demon"
        private const val ALTERNATIVE = "alternative"
        private const val SPAWNED = "spawned"
        private const val GIVEN = "given"
        private const val COMPLETED = "completed"
        private const val REWARD = "reward"
        fun storeInBundle(bundle: Bundle) {
            val node = Bundle()
            node.put(SPAWNED, spawned)
            if (spawned) {
                node.put(ALTERNATIVE, alternative)
                node.put(GIVEN, given)
                node.put(COMPLETED, isCompleted)
                node.put(REWARD, reward)
            }
            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {
            val node: Bundle = bundle.getBundle(NODE)
            if (!node.isNull() && node.getBoolean(SPAWNED).also { spawned = it }) {
                alternative = node.getBoolean(ALTERNATIVE)
                given = node.getBoolean(GIVEN)
                isCompleted = node.getBoolean(COMPLETED)
                reward = node.get(REWARD) as Ring
            }
        }

        fun spawn(level: CityLevel, room: Room?) {
            if (!spawned && Dungeon.depth > 16 && Random.Int(20 - Dungeon.depth) === 0) {
                val npc = Imp()
                do {
                    npc.pos = level.randomRespawnCell()
                } while (npc.pos === -1 || level.heaps.get(npc.pos) != null)
                level.mobs.add(npc)
                Actor.occupyCell(npc)
                spawned = true
                alternative = Random.Int(2) === 0
                given = false
                do {
                    reward = Generator.random(Generator.Category.RING) as Ring
                } while (reward.cursed)
                reward.upgrade(2)
                reward.cursed = true
            }
        }

        fun process(mob: Mob) {
            if (spawned && given && !isCompleted) {
                if (alternative && mob is Monk ||
                    !alternative && mob is Golem
                ) {
                    Dungeon.level.drop(DwarfToken(), mob.pos).sprite.drop()
                }
            }
        }

        fun complete() {
            reward = null
            isCompleted = true
            Journal.remove(Journal.Feature.IMP)
        }
    }

    companion object {
        private const val TXT_GOLEMS1 = "Are you an adventurer? I love adventurers! You can always rely on them " +
                "if something needs to be killed. Am I right? For a bounty, of course ;)\n" +
                "In my case this is _golems_ who need to be killed. You see, I'm going to start a " +
                "little business here, but these stupid golems are bad for business! " +
                "It's very hard to negotiate with wandering lumps of granite, damn them! " +
                "So please, kill... let's say _6 of them_ and a reward is yours."
        private const val TXT_MONKS1 = "Are you an adventurer? I love adventurers! You can always rely on them " +
                "if something needs to be killed. Am I right? For a bounty, of course ;)\n" +
                "In my case this is _monks_ who need to be killed. You see, I'm going to start a " +
                "little business here, but these lunatics don't buy anything themselves and " +
                "will scare away other customers. " +
                "So please, kill... let's say _8 of them_ and a reward is yours."
        private const val TXT_GOLEMS2 = "How is your golem safari going?"
        private const val TXT_MONKS2 = "Oh, you are still alive! I knew that your kung-fu is stronger ;) " +
                "Just don't forget to grab these monks' tokens."
        private const val TXT_CYA = "See you, %s!"
        private const val TXT_HEY = "Psst, %s!"
    }

    init {
        name = "ambitious imp"
        spriteClass = ImpSprite::class.java
    }
}