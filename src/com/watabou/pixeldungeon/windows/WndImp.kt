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

import com.watabou.noosa.BitmapTextMultiline

class WndImp(imp: Imp, tokens: DwarfToken) : Window() {
    private fun takeReward(imp: Imp, tokens: DwarfToken, reward: Item) {
        hide()
        tokens.detachAll(Dungeon.hero.belongings.backpack)
        reward.identify()
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Hero.TXT_YOU_NOW_HAVE, reward.name())
        } else {
            Dungeon.level.drop(reward, imp.pos).sprite.drop()
        }
        imp.flee()
        Imp.Quest.complete()
    }

    companion object {
        private const val TXT_MESSAGE = "Oh yes! You are my hero!\n" +
                "Regarding your reward, I don't have cash with me right now, but I have something better for you. " +
                "This is my family heirloom ring: my granddad took it off a dead paladin's finger."
        private const val TXT_REWARD = "Take the ring"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }

    init {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(tokens.image(), null))
        titlebar.label(Utils.capitalize(tokens.name()))
        titlebar.setRect(0, 0, WIDTH, 0)
        add(titlebar)
        val message: BitmapTextMultiline = PixelScene.createMultiline(TXT_MESSAGE, 6)
        message.maxWidth = WIDTH
        message.measure()
        message.y = titlebar.bottom() + GAP
        add(message)
        val btnReward: RedButton = object : RedButton(TXT_REWARD) {
            protected fun onClick() {
                takeReward(imp, tokens, Imp.Quest.reward)
            }
        }
        btnReward.setRect(0, message.y + message.height() + GAP, WIDTH, BTN_HEIGHT)
        add(btnReward)
        resize(WIDTH, btnReward.bottom() as Int)
    }
}