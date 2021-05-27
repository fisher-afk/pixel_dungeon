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

class StartScene : PixelScene() {
    private var buttonX = 0f
    private var buttonY = 0f
    private var btnLoad: GameButton? = null
    private var btnNewGame: GameButton? = null
    private var huntressUnlocked = false
    private var unlock: Group? = null
    override fun create() {
        super.create()
        Badges.loadGlobal()
        uiCamera.visible = false
        val w: Int = Camera.main.width
        val h: Int = Camera.main.height
        val width: Float
        val height: Float
        if (PixelDungeon.landscape()) {
            width = WIDTH_L
            height = HEIGHT_L
        } else {
            width = WIDTH_P
            height = HEIGHT_P
        }
        val left = (w - width) / 2
        var top = (h - height) / 2
        val bottom = h - top
        val archs = Archs()
        archs.setSize(w, h)
        add(archs)
        val title: Image = BannerSprites.get(Type.SELECT_YOUR_HERO)
        title.x = align((w - title.width()) / 2)
        title.y = align(top)
        add(title)
        buttonX = left
        buttonY = bottom - BUTTON_HEIGHT
        btnNewGame = object : GameButton(TXT_NEW) {
            protected fun onClick() {
                if (GamesInProgress.check(curClass) != null) {
                    this@StartScene.add(object : WndOptions(TXT_REALLY, TXT_WARNING, TXT_YES, TXT_NO) {
                        protected fun onSelect(index: Int) {
                            if (index == 0) {
                                startNewGame()
                            }
                        }
                    })
                } else {
                    startNewGame()
                }
            }
        }
        add(btnNewGame)
        btnLoad = object : GameButton(TXT_LOAD) {
            protected fun onClick() {
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
                Game.switchScene(InterlevelScene::class.java)
            }
        }
        add(btnLoad)
        val centralHeight: Float = buttonY - title.y - title.height()
        val classes: Array<HeroClass> = arrayOf<HeroClass>(
            HeroClass.WARRIOR, HeroClass.MAGE, HeroClass.ROGUE, HeroClass.HUNTRESS
        )
        for (cl in classes) {
            val shield: ClassShield = ClassShield(cl)
            shields[cl] = shield
            add(shield)
        }
        if (PixelDungeon.landscape()) {
            val shieldW = width / 4
            val shieldH = Math.min(centralHeight, shieldW)
            top = title.y + title.height + (centralHeight - shieldH) / 2
            for (i in classes.indices) {
                val shield = shields[classes[i]]
                shield.setRect(left + i * shieldW, top, shieldW, shieldH)
            }
            val challenge: ChallengeButton = ChallengeButton()
            challenge.setPos(
                w / 2 - challenge.width() / 2,
                top + shieldH - challenge.height() / 2
            )
            add(challenge)
        } else {
            val shieldW = width / 2
            val shieldH = Math.min(centralHeight / 2, shieldW * 1.2f)
            top = title.y + title.height() + centralHeight / 2 - shieldH
            for (i in classes.indices) {
                val shield = shields[classes[i]]
                shield.setRect(
                    left + i % 2 * shieldW,
                    top + i / 2 * shieldH,
                    shieldW, shieldH
                )
            }
            val challenge: ChallengeButton = ChallengeButton()
            challenge.setPos(
                w / 2 - challenge.width() / 2,
                top + shieldH - challenge.height() / 2
            )
            add(challenge)
        }
        unlock = Group()
        add(unlock)
        if (!Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_3).also { huntressUnlocked = it }) {
            val text: BitmapTextMultiline = PixelScene.createMultiline(TXT_UNLOCK, 9)
            text.maxWidth = width.toInt()
            text.measure()
            var pos: Float = bottom - BUTTON_HEIGHT + (BUTTON_HEIGHT - text.height()) / 2
            for (line in text.LineSplitter().split()) {
                line.measure()
                line.hardlight(0xFFFF00)
                line.x = PixelScene.align(w / 2 - line.width() / 2)
                line.y = PixelScene.align(pos)
                unlock.add(line)
                pos += line.height()
            }
        }
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0)
        add(btnExit)
        curClass = null
        updateClass(HeroClass.values().get(PixelDungeon.lastClass()))
        fadeIn()
        Badges.loadingListener = object : Callback() {
            fun call() {
                if (Game.scene() === this@StartScene) {
                    PixelDungeon.switchNoFade(StartScene::class.java)
                }
            }
        }
    }

    override fun destroy() {
        Badges.saveGlobal()
        Badges.loadingListener = null
        super.destroy()
    }

    private fun updateClass(cl: HeroClass) {
        if (curClass === cl) {
            add(WndClass(cl))
            return
        }
        if (curClass != null) {
            shields[curClass]!!
                .highlight(false)
        }
        shields[cl.also {
            curClass = it
        }]!!.highlight(true)
        if (cl !== HeroClass.HUNTRESS || huntressUnlocked) {
            unlock.visible = false
            val info: Info = GamesInProgress.check(curClass)
            if (info != null) {
                btnLoad.visible = true
                btnLoad!!.secondary(Utils.format(TXT_DPTH_LVL, info.depth, info.level), info.challenges)
                btnNewGame.visible = true
                btnNewGame!!.secondary(TXT_ERASE, false)
                val w: Float = (Camera.main.width - GAP) / 2 - buttonX
                btnLoad.setRect(
                    buttonX, buttonY, w, BUTTON_HEIGHT
                )
                btnNewGame.setRect(
                    btnLoad.right() + GAP, buttonY, w, BUTTON_HEIGHT
                )
            } else {
                btnLoad.visible = false
                btnNewGame.visible = true
                btnNewGame!!.secondary(null, false)
                btnNewGame.setRect(buttonX, buttonY, Camera.main.width - buttonX * 2, BUTTON_HEIGHT)
            }
        } else {
            unlock.visible = true
            btnLoad.visible = false
            btnNewGame.visible = false
        }
    }

    private fun startNewGame() {
        Dungeon.hero = null
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND
        if (PixelDungeon.intro()) {
            PixelDungeon.intro(false)
            Game.switchScene(IntroScene::class.java)
        } else {
            Game.switchScene(InterlevelScene::class.java)
        }
    }

    protected fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }

    private class GameButton(primary: String?) : RedButton(primary) {
        private var secondary: BitmapText? = null
        protected fun createChildren() {
            super.createChildren()
            secondary = createText(6)
            add(secondary)
        }

        protected fun layout() {
            super.layout()
            if (secondary.text().length() > 0) {
                text.y = align(y + (height - text.height() - secondary.baseLine()) / 2)
                secondary.x = align(x + (width - secondary.width()) / 2)
                secondary.y = align(text.y + text.height())
            } else {
                text.y = align(y + (height - text.baseLine()) / 2)
            }
        }

        fun secondary(text: String?, highlighted: Boolean) {
            secondary.text(text)
            secondary.measure()
            secondary.hardlight(if (highlighted) SECONDARY_COLOR_H else SECONDARY_COLOR_N)
        }

        companion object {
            private const val SECONDARY_COLOR_N = 0xCACFC2
            private const val SECONDARY_COLOR_H = 0xFFFF88
        }

        init {
            secondary.text(null)
        }
    }

    private inner class ClassShield(cl: HeroClass) : Button() {
        private val cl: HeroClass
        private var avatar: Image? = null
        private var name: BitmapText? = null
        private var emitter: Emitter? = null
        private var brightness: Float
        private var normal = 0
        private var highlighted = 0
        protected fun createChildren() {
            super.createChildren()
            avatar = Image(Assets.AVATARS)
            add(avatar)
            name = PixelScene.createText(9)
            add(name)
            emitter = BitmaskEmitter(avatar)
            add(emitter)
        }

        protected fun layout() {
            super.layout()
            avatar.x = align(x + (width - avatar.width()) / 2)
            avatar.y = align(y + (height - avatar.height() - name.height()) / 2)
            name.x = align(x + (width - name.width()) / 2)
            name.y = avatar.y + avatar.height() + Companion.SCALE
        }

        protected fun onTouchDown() {
            emitter.revive()
            emitter.start(Speck.factory(Speck.LIGHT), 0.05f, 7)
            Sample.INSTANCE.play(Assets.SND_CLICK, 1, 1, 1.2f)
            updateClass(cl)
        }

        fun update() {
            super.update()
            if (brightness < 1.0f && brightness > Companion.MIN_BRIGHTNESS) {
                if (Game.elapsed.let { brightness -= it; brightness } <= Companion.MIN_BRIGHTNESS) {
                    brightness = Companion.MIN_BRIGHTNESS
                }
                updateBrightness()
            }
        }

        fun highlight(value: Boolean) {
            if (value) {
                brightness = 1.0f
                name.hardlight(highlighted)
            } else {
                brightness = 0.999f
                name.hardlight(normal)
            }
            updateBrightness()
        }

        private fun updateBrightness() {
            avatar.am = brightness
            avatar.rm = avatar.am
            avatar.bm = avatar.rm
            avatar.gm = avatar.bm
        }

        companion object {
            private const val MIN_BRIGHTNESS = 0.6f
            private const val BASIC_NORMAL = 0x444444
            private const val BASIC_HIGHLIGHTED = 0xCACFC2
            private const val MASTERY_NORMAL = 0x666644
            private const val MASTERY_HIGHLIGHTED = 0xFFFF88
            private const val WIDTH = 24
            private const val HEIGHT = 28
            private const val SCALE = 2
        }

        init {
            this.cl = cl
            avatar.frame(cl.ordinal * Companion.WIDTH, 0, Companion.WIDTH, Companion.HEIGHT)
            avatar.scale.set(Companion.SCALE)
            if (Badges.isUnlocked(cl.masteryBadge())) {
                normal = Companion.MASTERY_NORMAL
                highlighted = Companion.MASTERY_HIGHLIGHTED
            } else {
                normal = Companion.BASIC_NORMAL
                highlighted = Companion.BASIC_HIGHLIGHTED
            }
            name.text(cl.name)
            name.measure()
            name.hardlight(normal)
            brightness = Companion.MIN_BRIGHTNESS
            updateBrightness()
        }
    }

    private inner class ChallengeButton : Button() {
        private var image: Image? = null
        protected fun createChildren() {
            super.createChildren()
            image = Icons.get(if (PixelDungeon.challenges() > 0) Icons.CHALLENGE_ON else Icons.CHALLENGE_OFF)
            add(image)
        }

        protected fun layout() {
            super.layout()
            image.x = align(x)
            image.y = align(y)
        }

        protected fun onClick() {
            if (Badges.isUnlocked(Badges.Badge.VICTORY)) {
                this@StartScene.add(object : WndChallenges(PixelDungeon.challenges(), true) {
                    fun onBackPressed() {
                        super.onBackPressed()
                        image.copy(Icons.get(if (PixelDungeon.challenges() > 0) Icons.CHALLENGE_ON else Icons.CHALLENGE_OFF))
                    }
                })
            } else {
                this@StartScene.add(WndMessage(TXT_WIN_THE_GAME))
            }
        }

        protected fun onTouchDown() {
            Sample.INSTANCE.play(Assets.SND_CLICK)
        }

        init {
            width = image.width
            height = image.height
            image.am = if (Badges.isUnlocked(Badges.Badge.VICTORY)) 1.0f else 0.5f
        }
    }

    companion object {
        private const val BUTTON_HEIGHT = 24f
        private const val GAP = 2f
        private const val TXT_LOAD = "Load Game"
        private const val TXT_NEW = "New Game"
        private const val TXT_ERASE = "Erase current game"
        private const val TXT_DPTH_LVL = "Depth: %d, level: %d"
        private const val TXT_REALLY = "Do you really want to start new game?"
        private const val TXT_WARNING = "Your current game progress will be erased."
        private const val TXT_YES = "Yes, start new game"
        private const val TXT_NO = "No, return to main menu"
        private const val TXT_UNLOCK = "To unlock this character class, slay the 3rd boss with any other class"
        private const val TXT_WIN_THE_GAME = "To unlock \"Challenges\", win the game with any character class."
        private const val WIDTH_P = 116f
        private const val HEIGHT_P = 220f
        private const val WIDTH_L = 224f
        private const val HEIGHT_L = 124f
        private val shields: HashMap<HeroClass?, ClassShield> = HashMap<HeroClass?, ClassShield>()
        var curClass: HeroClass? = null
    }
}