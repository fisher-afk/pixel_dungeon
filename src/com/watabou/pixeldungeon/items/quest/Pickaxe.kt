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
package com.watabou.pixeldungeon.items.quest

import com.watabou.noosa.audio.Sample

class Pickaxe : Weapon() {
    var bloodStained = false
    fun min(): Int {
        return 3
    }

    fun max(): Int {
        return 12
    }

    fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_MINE)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === AC_MINE) {
            if (Dungeon.depth < 11 || Dungeon.depth > 15) {
                GLog.w(TXT_NO_VEIN)
                return
            }
            for (i in 0 until Level.NEIGHBOURS8.length) {
                val pos: Int = hero.pos + Level.NEIGHBOURS8.get(i)
                if (Dungeon.level.map.get(pos) === Terrain.WALL_DECO) {
                    hero.spend(TIME_TO_MINE)
                    hero.busy()
                    hero.sprite.attack(pos, object : Callback() {
                        fun call() {
                            CellEmitter.center(pos).burst(Speck.factory(Speck.STAR), 7)
                            Sample.INSTANCE.play(Assets.SND_EVOKE)
                            Level.set(pos, Terrain.WALL)
                            GameScene.updateMap(pos)
                            val gold = DarkGold()
                            if (gold.doPickUp(Dungeon.hero)) {
                                GLog.i(Hero.TXT_YOU_NOW_HAVE, gold.name())
                            } else {
                                Dungeon.level.drop(gold, hero.pos).sprite.drop()
                            }
                            val hunger: Hunger = hero.buff(Hunger::class.java)
                            if (hunger != null && !hunger.isStarving()) {
                                hunger.satisfy(-Hunger.STARVING / 10)
                                BuffIndicator.refreshHero()
                            }
                            hero.onOperateComplete()
                        }
                    })
                    return
                }
            }
            GLog.w(TXT_NO_VEIN)
        } else {
            super.execute(hero, action)
        }
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    fun proc(attacker: Char?, defender: Char, damage: Int) {
        if (!bloodStained && defender is Bat && defender.HP <= damage) {
            bloodStained = true
            updateQuickslot()
        }
    }

    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(BLOODSTAINED, bloodStained)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        bloodStained = bundle.getBoolean(BLOODSTAINED)
    }

    fun glowing(): Glowing? {
        return if (bloodStained) BLOODY else null
    }

    fun info(): String {
        return "This is a large and sturdy tool for breaking rocks. Probably it can be used as a weapon."
    }

    companion object {
        const val AC_MINE = "MINE"
        const val TIME_TO_MINE = 2f
        private const val TXT_NO_VEIN = "There is no dark gold vein near you to mine"
        private val BLOODY: Glowing = Glowing(0x550000)
        private const val BLOODSTAINED = "bloodStained"
    }

    init {
        name = "pickaxe"
        image = ItemSpriteSheet.PICKAXE
        unique = true
        defaultAction = AC_MINE
        STR = 14
    }
}