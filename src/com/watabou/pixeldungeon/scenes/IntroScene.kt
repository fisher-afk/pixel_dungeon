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
package com.watabou.pixeldungeon.scenes

import com.watabou.noosa.Game

class IntroScene : PixelScene() {
    override fun create() {
        super.create()
        add(object : WndStory(TEXT) {
            fun hide() {
                super.hide()
                Game.switchScene(InterlevelScene::class.java)
            }
        })
        fadeIn()
    }

    companion object {
        private const val TEXT =
            "Many heroes of all kinds ventured into the Dungeon before you. Some of them have returned with treasures and magical " +
                    "artifacts, most have never been heard of since. But none have succeeded in retrieving the Amulet of Yendor, " +
                    "which is told to be hidden in the depths of the Dungeon.\n\n" +
                    "" +
                    "You consider yourself ready for the challenge, but most importantly, you feel that fortune smiles on you. " +
                    "It's time to start your own adventure!"
    }
}