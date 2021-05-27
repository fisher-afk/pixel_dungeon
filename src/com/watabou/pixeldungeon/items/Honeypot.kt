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

import com.watabou.noosa.audio.Sample

class Honeypot : Item() {
    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_SHATTER)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_SHATTER) {
            hero.sprite.zap(hero.pos)
            shatter(hero.pos)
            detach(hero.belongings.backpack)
            hero.spendAndNext(TIME_TO_THROW)
        } else {
            super.execute(hero, action)
        }
    }

    protected override fun onThrow(cell: Int) {
        if (Level.pit.get(cell)) {
            super.onThrow(cell)
        } else {
            shatter(cell)
        }
    }

    private fun shatter(pos: Int) {
        Sample.INSTANCE.play(Assets.SND_SHATTER)
        if (Dungeon.visible.get(pos)) {
            Splash.at(pos, 0xffd500, 5)
        }
        var newPos = pos
        if (Actor.findChar(pos) != null) {
            val candidates = ArrayList<Int>()
            val passable: BooleanArray = Level.passable
            for (n in Level.NEIGHBOURS4) {
                val c = pos + n
                if (passable[c] && Actor.findChar(c) == null) {
                    candidates.add(c)
                }
            }
            newPos = if (candidates.size > 0) Random.element(candidates) else -1
        }
        if (newPos != -1) {
            val bee = Bee()
            bee.spawn(Dungeon.depth)
            bee.HP = bee.HT
            bee.pos = newPos
            GameScene.add(bee)
            Actor.addDelayed(Pushing(bee, pos, newPos), -1)
            bee.sprite.alpha(0)
            bee.sprite.parent.add(AlphaTweener(bee.sprite, 1, 0.15f))
            Sample.INSTANCE.play(Assets.SND_BEE)
        }
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun price(): Int {
        return 50 * quantity
    }

    override fun info(): String {
        return "There is not much honey in this small honeypot, but there is a golden bee there and it doesn't want to leave it."
    }

    companion object {
        const val AC_SHATTER = "SHATTER"
    }

    init {
        name = "honeypot"
        image = ItemSpriteSheet.HONEYPOT
        defaultAction = AC_THROW
        stackable = true
    }
}