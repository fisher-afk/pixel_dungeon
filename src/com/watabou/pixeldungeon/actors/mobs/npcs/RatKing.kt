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

class RatKing : NPC() {
    fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    fun speed(): Float {
        return 2f
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
        if (state === SLEEPEING) {
            notice()
            yell("I'm not sleeping!")
            state = WANDERING
        } else {
            yell("What is it? I have no time for this nonsense. My kingdom won't rule itself!")
        }
    }

    fun description(): String {
        return "This rat is a little bigger than a regular marsupial rat " +
                "and it's wearing a tiny crown on its head."
    }

    init {
        name = "rat king"
        spriteClass = RatKingSprite::class.java
        state = SLEEPEING
    }
}