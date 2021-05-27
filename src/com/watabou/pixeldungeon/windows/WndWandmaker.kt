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

class WndWandmaker(wandmaker: Wandmaker, item: Item) : WndQuest(wandmaker, TXT_MESSAGE, TXT_BATTLE, TXT_NON_BATTLE) {
    private val wandmaker: Wandmaker
    private val questItem: Item
    protected override fun onSelect(index: Int) {
        questItem.detach(Dungeon.hero.belongings.backpack)
        val reward: Item = if (index == 0) Wandmaker.Quest.wand1 else Wandmaker.Quest.wand2
        reward.identify()
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Hero.TXT_YOU_NOW_HAVE, reward.name())
        } else {
            Dungeon.level.drop(reward, wandmaker.pos).sprite.drop()
        }
        wandmaker.yell(Utils.format(TXT_FARAWELL, Dungeon.hero.className()))
        wandmaker.destroy()
        wandmaker.sprite.die()
        Wandmaker.Quest.complete()
    }

    companion object {
        private const val TXT_MESSAGE = "Oh, I see you have succeeded! I do hope it hasn't troubled you too much. " +
                "As I promised, you can choose one of my high quality wands."
        private const val TXT_BATTLE = "Battle wand"
        private const val TXT_NON_BATTLE = "Non-battle wand"
        private const val TXT_FARAWELL = "Good luck in your quest, %s!"
    }

    init {
        this.wandmaker = wandmaker
        questItem = item
    }
}