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
package com.watabou.pixeldungeon.actors.mobs

import com.watabou.noosa.audio.Sample

class Mimic : Mob() {
    private var level = 0
    var items: ArrayList<Item>? = null
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEMS, items)
        bundle.put(LEVEL, level)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        items = ArrayList<Item>(bundle.getCollection(ITEMS) as Collection<Item?>)
        adjustStats(bundle.getInt(LEVEL))
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(HT / 10, HT / 4)
    }

    fun attackSkill(target: Char?): Int {
        return 9 + level
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        if (enemy === Dungeon.hero && Random.Int(3) === 0) {
            val gold = Gold(Random.Int(Dungeon.gold / 10, Dungeon.gold / 2))
            if (gold.quantity() > 0) {
                Dungeon.gold -= gold.quantity()
                Dungeon.level.drop(gold, Dungeon.hero.pos).sprite.drop()
            }
        }
        return super.attackProc(enemy, damage)
    }

    fun adjustStats(level: Int) {
        this.level = level
        HT = (3 + level) * 4
        EXP = 2 + 2 * (level - 1) / 5
        defenseSkill = attackSkill(null) / 2
        enemySeen = true
    }

    override fun die(cause: Any?) {
        super.die(cause)
        if (items != null) {
            for (item in items!!) {
                Dungeon.level.drop(item, pos).sprite.drop()
            }
        }
    }

    override fun reset(): Boolean {
        state = WANDERING
        return true
    }

    override fun description(): String {
        return "Mimics are magical creatures which can take any shape they wish. In dungeons they almost always " +
                "choose a shape of a treasure chest, because they know how to beckon an adventurer."
    }

    companion object {
        private const val LEVEL = "level"
        private const val ITEMS = "items"
        fun spawnAt(pos: Int, items: List<Item?>?): Mimic? {
            val ch: Char = Actor.findChar(pos)
            if (ch != null) {
                val candidates = ArrayList<Int>()
                for (n in Level.NEIGHBOURS8) {
                    val cell = pos + n
                    if ((Level.passable.get(cell) || Level.avoid.get(cell)) && Actor.findChar(cell) == null) {
                        candidates.add(cell)
                    }
                }
                if (candidates.size > 0) {
                    val newPos: Int = Random.element(candidates)
                    Actor.addDelayed(Pushing(ch, ch.pos, newPos), -1)
                    ch.pos = newPos
                    // FIXME
                    if (ch is Mob) {
                        Dungeon.level.mobPress(ch as Mob)
                    } else {
                        Dungeon.level.press(newPos, ch)
                    }
                } else {
                    return null
                }
            }
            val m = Mimic()
            m.items = ArrayList<Item>(items)
            m.adjustStats(Dungeon.depth)
            m.HP = m.HT
            m.pos = pos
            m.state = m.HUNTING
            GameScene.add(m, 1)
            m.sprite.turnTo(pos, Dungeon.hero.pos)
            if (Dungeon.visible.get(m.pos)) {
                CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10)
                Sample.INSTANCE.play(Assets.SND_MIMIC)
            }
            return m
        }

        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            IMMUNITIES.add(ScrollOfPsionicBlast::class.java)
        }
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "mimic"
        spriteClass = MimicSprite::class.java
    }
}