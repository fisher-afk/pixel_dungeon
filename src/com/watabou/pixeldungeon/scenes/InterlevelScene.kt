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

import com.watabou.noosa.BitmapText

class InterlevelScene : PixelScene() {
    enum class Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, NONE
    }

    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    private var phase: Phase? = null
    private var timeLeft = 0f
    private var message: BitmapText? = null
    private var thread: Thread? = null
    private var error: String? = null
    override fun create() {
        super.create()
        var text = ""
        when (mode) {
            Mode.DESCEND -> text = TXT_DESCENDING
            Mode.ASCEND -> text = TXT_ASCENDING
            Mode.CONTINUE -> text = TXT_LOADING
            Mode.RESURRECT -> text = TXT_RESURRECTING
            Mode.RETURN -> text = TXT_RETURNING
            Mode.FALL -> text = TXT_FALLING
            else -> {
            }
        }
        message = PixelScene.createText(text, 9)
        message.measure()
        message.x = (Camera.main.width - message.width()) / 2
        message.y = (Camera.main.height - message.height()) / 2
        add(message)
        phase = Phase.FADE_IN
        timeLeft = TIME_TO_FADE
        thread = object : Thread() {
            override fun run() {
                try {
                    Generator.reset()
                    when (mode) {
                        Mode.DESCEND -> descend()
                        Mode.ASCEND -> ascend()
                        Mode.CONTINUE -> restore()
                        Mode.RESURRECT -> resurrect()
                        Mode.RETURN -> returnTo()
                        Mode.FALL -> fall()
                        else -> {
                        }
                    }
                    if (Dungeon.depth % 5 === 0) {
                        Sample.INSTANCE.load(Assets.SND_BOSS)
                    }
                } catch (e: FileNotFoundException) {
                    error = ERR_FILE_NOT_FOUND
                } catch (e: Exception) {
                    error = ERR_GENERIC
                }
                if (phase == Phase.STATIC && error == null) {
                    phase = Phase.FADE_OUT
                    timeLeft = TIME_TO_FADE
                }
            }
        }
        thread.start()
    }

    fun update() {
        super.update()
        val p = timeLeft / TIME_TO_FADE
        when (phase) {
            Phase.FADE_IN -> {
                message.alpha(1 - p)
                if (Game.elapsed.let { timeLeft -= it; timeLeft } <= 0) {
                    if (!thread!!.isAlive && error == null) {
                        phase = Phase.FADE_OUT
                        timeLeft = TIME_TO_FADE
                    } else {
                        phase = Phase.STATIC
                    }
                }
            }
            Phase.FADE_OUT -> {
                message.alpha(p)
                if (mode == Mode.CONTINUE || mode == Mode.DESCEND && Dungeon.depth === 1) {
                    Music.INSTANCE.volume(p)
                }
                if (Game.elapsed.let { timeLeft -= it; timeLeft } <= 0) {
                    Game.switchScene(GameScene::class.java)
                }
            }
            Phase.STATIC -> if (error != null) {
                add(object : WndError(error) {
                    fun onBackPressed() {
                        super.onBackPressed()
                        Game.switchScene(StartScene::class.java)
                    }
                })
                error = null
            }
        }
    }

    @Throws(Exception::class)
    private fun descend() {
        Actor.fixTime()
        if (Dungeon.hero == null) {
            Dungeon.init()
            if (noStory) {
                Dungeon.chapters.add(WndStory.ID_SEWERS)
                noStory = false
            }
            GameLog.wipe()
        } else {
            Dungeon.saveLevel()
        }
        val level: Level
        level = if (Dungeon.depth >= Statistics.deepestFloor) {
            Dungeon.newLevel()
        } else {
            Dungeon.depth++
            Dungeon.loadLevel(Dungeon.hero.heroClass)
        }
        Dungeon.switchLevel(level, level.entrance)
    }

    @Throws(Exception::class)
    private fun fall() {
        Actor.fixTime()
        Dungeon.saveLevel()
        val level: Level
        level = if (Dungeon.depth >= Statistics.deepestFloor) {
            Dungeon.newLevel()
        } else {
            Dungeon.depth++
            Dungeon.loadLevel(Dungeon.hero.heroClass)
        }
        Dungeon.switchLevel(level, if (fallIntoPit) level.pitCell() else level.randomRespawnCell())
    }

    @Throws(Exception::class)
    private fun ascend() {
        Actor.fixTime()
        Dungeon.saveLevel()
        Dungeon.depth--
        val level: Level = Dungeon.loadLevel(Dungeon.hero.heroClass)
        Dungeon.switchLevel(level, level.exit)
    }

    @Throws(Exception::class)
    private fun returnTo() {
        Actor.fixTime()
        Dungeon.saveLevel()
        Dungeon.depth = returnDepth
        val level: Level = Dungeon.loadLevel(Dungeon.hero.heroClass)
        Dungeon.switchLevel(level, if (Level.resizingNeeded) level.adjustPos(returnPos) else returnPos)
    }

    @Throws(Exception::class)
    private fun restore() {
        Actor.fixTime()
        GameLog.wipe()
        Dungeon.loadGame(StartScene.curClass)
        if (Dungeon.depth === -1) {
            Dungeon.depth = Statistics.deepestFloor
            Dungeon.switchLevel(Dungeon.loadLevel(StartScene.curClass), -1)
        } else {
            val level: Level = Dungeon.loadLevel(StartScene.curClass)
            Dungeon.switchLevel(
                level,
                if (Level.resizingNeeded) level.adjustPos(Dungeon.hero.pos) else Dungeon.hero.pos
            )
        }
    }

    @Throws(Exception::class)
    private fun resurrect() {
        Actor.fixTime()
        if (Dungeon.bossLevel()) {
            Dungeon.hero.resurrect(Dungeon.depth)
            Dungeon.depth--
            val level: Level = Dungeon.newLevel()
            Dungeon.switchLevel(level, level.entrance)
        } else {
            Dungeon.hero.resurrect(-1)
            Dungeon.resetLevel()
        }
    }

    protected fun onBackPressed() {
        // Do nothing
    }

    companion object {
        private const val TIME_TO_FADE = 0.3f
        private const val TXT_DESCENDING = "Descending..."
        private const val TXT_ASCENDING = "Ascending..."
        private const val TXT_LOADING = "Loading..."
        private const val TXT_RESURRECTING = "Resurrecting..."
        private const val TXT_RETURNING = "Returning..."
        private const val TXT_FALLING = "Falling..."
        private const val ERR_FILE_NOT_FOUND = "File not found. For some reason."
        private const val ERR_GENERIC = "Something went wrong..."
        var mode: Mode? = null
        var returnDepth = 0
        var returnPos = 0
        var noStory = false
        var fallIntoPit = false
    }
}