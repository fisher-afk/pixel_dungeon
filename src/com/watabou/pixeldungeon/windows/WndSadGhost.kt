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
package com.watabou.pixeldungeon.windows

import com.watabou.pixeldungeon.Dungeon

class WndSadGhost(ghost: Ghost, item: Item?, text: String?) : WndQuest(ghost, text, TXT_WEAPON, TXT_ARMOR) {
    private val ghost: Ghost
    private val questItem: Item?
    protected override fun onSelect(index: Int) {
        if (questItem != null) {
            questItem.detach(Dungeon.hero.belongings.backpack)
        }
        val reward: Item = if (index == 0) Ghost.Quest.weapon else Ghost.Quest.armor
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Hero.TXT_YOU_NOW_HAVE, reward.name())
        } else {
            Dungeon.level.drop(reward, ghost.pos).sprite.drop()
        }
        ghost.yell("Farewell, adventurer!")
        ghost.die(null)
        Ghost.Quest.complete()
    }

    companion object {
        private const val TXT_WEAPON = "Ghost's weapon"
        private const val TXT_ARMOR = "Ghost's armor"
    }

    init {
        this.ghost = ghost
        questItem = item
    }
}