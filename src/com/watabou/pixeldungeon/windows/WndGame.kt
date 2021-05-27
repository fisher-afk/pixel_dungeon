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

import com.watabou.noosa.Game

class WndGame : Window() {
    private var pos = 0
    private fun addButton(btn: RedButton) {
        add(btn)
        btn.setRect(0, if (pos > 0) GAP.let { pos += it; pos } else 0, WIDTH, BTN_HEIGHT)
        pos += BTN_HEIGHT
    }

    private fun addButtons(btn1: RedButton, btn2: RedButton) {
        add(btn1)
        btn1.setRect(0, if (pos > 0) GAP.let { pos += it; pos } else 0, (WIDTH - GAP) / 2, BTN_HEIGHT)
        add(btn2)
        btn2.setRect(btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT)
        pos += BTN_HEIGHT
    }

    companion object {
        private const val TXT_SETTINGS = "Settings"
        private const val TXT_CHALLEGES = "Challenges"
        private const val TXT_RANKINGS = "Rankings"
        private const val TXT_START = "Start New Game"
        private const val TXT_MENU = "Main Menu"
        private const val TXT_EXIT = "Exit Game"
        private const val TXT_RETURN = "Return to Game"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }

    init {
        addButton(object : RedButton(TXT_SETTINGS) {
            protected fun onClick() {
                hide()
                GameScene.show(WndSettings(true))
            }
        })
        if (Dungeon.challenges > 0) {
            addButton(object : RedButton(TXT_CHALLEGES) {
                protected fun onClick() {
                    hide()
                    GameScene.show(WndChallenges(Dungeon.challenges, false))
                }
            })
        }
        if (!Dungeon.hero.isAlive()) {
            var btnStart: RedButton
            addButton(object : RedButton(TXT_START) {
                protected fun onClick() {
                    Dungeon.hero = null
                    PixelDungeon.challenges(Dungeon.challenges)
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND
                    InterlevelScene.noStory = true
                    Game.switchScene(InterlevelScene::class.java)
                }
            }.also { btnStart = it })
            btnStart.icon(Icons.get(Dungeon.hero.heroClass))
            addButton(object : RedButton(TXT_RANKINGS) {
                protected fun onClick() {
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND
                    Game.switchScene(RankingsScene::class.java)
                }
            })
        }
        addButtons(
            object : RedButton(TXT_MENU) {
                protected fun onClick() {
                    try {
                        Dungeon.saveAll()
                    } catch (e: IOException) {
                        // Do nothing
                    }
                    Game.switchScene(TitleScene::class.java)
                }
            }, object : RedButton(TXT_EXIT) {
                protected fun onClick() {
                    Game.instance.finish()
                }
            }
        )
        addButton(object : RedButton(TXT_RETURN) {
            protected fun onClick() {
                hide()
            }
        })
        resize(WIDTH, pos)
    }
}