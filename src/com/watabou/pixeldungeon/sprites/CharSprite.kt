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
package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.Game

class CharSprite : MovieClip(), Tweener.Listener, MovieClip.Listener {
    enum class State {
        BURNING, LEVITATING, INVISIBLE, PARALYSED, FROZEN, ILLUMINATED
    }

    protected var idle: Animation? = null
    protected var run: Animation? = null
    protected var attack: Animation? = null
    protected var operate: Animation? = null
    protected var zap: Animation? = null
    protected var die: Animation? = null
    protected var animCallback: Callback? = null
    protected var motion: Tweener? = null
    protected var burning: Emitter? = null
    protected var levitation: Emitter? = null
    protected var iceBlock: IceBlock? = null
    protected var halo: TorchHalo? = null
    protected var emo: EmoIcon? = null
    private var jumpTweener: Tweener? = null
    private var jumpCallback: Callback? = null
    private var flashTime = 0f
    protected var sleeping = false
    var ch: Char? = null
    var isMoving = false
    fun link(ch: Char) {
        this.ch = ch
        ch.sprite = this
        place(ch.pos)
        turnTo(ch.pos, Random.Int(Level.LENGTH))
        ch.updateSpriteState()
    }

    fun worldToCamera(cell: Int): PointF {
        val csize: Int = DungeonTilemap.SIZE
        return PointF(
            (cell % Level.WIDTH + 0.5f) * csize - width * 0.5f,
            (cell / Level.WIDTH + 1.0f) * csize - height
        )
    }

    fun place(cell: Int) {
        point(worldToCamera(cell))
    }

    fun showStatus(color: Int, text: String?, vararg args: Any?) {
        var text = text
        if (visible) {
            if (args.size > 0) {
                text = Utils.format(text, args)
            }
            if (ch != null) {
                FloatingText.show(x + width * 0.5f, y, ch.pos, text, color)
            } else {
                FloatingText.show(x + width * 0.5f, y, text, color)
            }
        }
    }

    fun idle() {
        play(idle)
    }

    fun move(from: Int, to: Int) {
        play(run)
        motion = PosTweener(this, worldToCamera(to), MOVE_INTERVAL)
        motion.listener = this
        parent.add(motion)
        isMoving = true
        turnTo(from, to)
        if (visible && Level.water.get(from) && !ch.flying) {
            GameScene.ripple(from)
        }
        ch.onMotionComplete()
    }

    fun interruptMotion() {
        if (motion != null) {
            onComplete(motion)
        }
    }

    fun attack(cell: Int) {
        turnTo(ch.pos, cell)
        play(attack)
    }

    fun attack(cell: Int, callback: Callback?) {
        animCallback = callback
        turnTo(ch.pos, cell)
        play(attack)
    }

    fun operate(cell: Int) {
        turnTo(ch.pos, cell)
        play(operate)
    }

    fun zap(cell: Int) {
        turnTo(ch.pos, cell)
        play(zap)
    }

    fun turnTo(from: Int, to: Int) {
        val fx: Int = from % Level.WIDTH
        val tx: Int = to % Level.WIDTH
        if (tx > fx) {
            flipHorizontal = false
        } else if (tx < fx) {
            flipHorizontal = true
        }
    }

    fun jump(from: Int, to: Int, callback: Callback?) {
        jumpCallback = callback
        val distance: Int = Level.distance(from, to)
        jumpTweener = JumpTweener(this, worldToCamera(to), (distance * 4).toFloat(), distance * 0.1f)
        jumpTweener.listener = this
        parent.add(jumpTweener)
        turnTo(from, to)
    }

    fun die() {
        sleeping = false
        play(die)
        if (emo != null) {
            emo.killAndErase()
        }
    }

    fun emitter(): Emitter {
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(this)
        return emitter
    }

    fun centerEmitter(): Emitter {
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(center())
        return emitter
    }

    fun bottomEmitter(): Emitter {
        val emitter: Emitter = GameScene.emitter()
        emitter.pos(x, y + height, width, 0)
        return emitter
    }

    fun burst(color: Int, n: Int) {
        if (visible) {
            Splash.at(center(), color, n)
        }
    }

    fun bloodBurstA(from: PointF?, damage: Int) {
        if (visible) {
            val c: PointF = center()
            val n = Math.min(9 * Math.sqrt(damage.toDouble() / ch.HT), 9.0).toInt()
            Splash.at(c, PointF.angle(from, c), 3.1415926f / 2, blood(), n)
        }
    }

    fun blood(): Int {
        return -0x450000
    }

    fun flash() {
        ga = 1f
        ba = ga
        ra = ba
        flashTime = FLASH_INTERVAL
    }

    fun add(state: State?) {
        when (state) {
            State.BURNING -> {
                burning = emitter()
                burning.pour(FlameParticle.FACTORY, 0.06f)
                if (visible) {
                    Sample.INSTANCE.play(Assets.SND_BURNING)
                }
            }
            State.LEVITATING -> {
                levitation = emitter()
                levitation.pour(Speck.factory(Speck.JET), 0.02f)
            }
            State.INVISIBLE -> PotionOfInvisibility.melt(ch)
            State.PARALYSED -> paused = true
            State.FROZEN -> {
                iceBlock = IceBlock.freeze(this)
                paused = true
            }
            State.ILLUMINATED -> GameScene.effect(TorchHalo(this).also { halo = it })
        }
    }

    fun remove(state: State?) {
        when (state) {
            State.BURNING -> if (burning != null) {
                burning.on = false
                burning = null
            }
            State.LEVITATING -> if (levitation != null) {
                levitation.on = false
                levitation = null
            }
            State.INVISIBLE -> alpha(1f)
            State.PARALYSED -> paused = false
            State.FROZEN -> {
                if (iceBlock != null) {
                    iceBlock.melt()
                    iceBlock = null
                }
                paused = false
            }
            State.ILLUMINATED -> if (halo != null) {
                halo.putOut()
            }
        }
    }

    fun update() {
        super.update()
        if (paused && listener != null) {
            listener.onComplete(curAnim)
        }
        if (flashTime > 0 && Game.elapsed.let { flashTime -= it; flashTime } <= 0) {
            resetColor()
        }
        if (burning != null) {
            burning.visible = visible
        }
        if (levitation != null) {
            levitation.visible = visible
        }
        if (iceBlock != null) {
            iceBlock.visible = visible
        }
        if (sleeping) {
            showSleep()
        } else {
            hideSleep()
        }
        if (emo != null) {
            emo.visible = visible
        }
    }

    fun showSleep() {
        if (emo is EmoIcon.Sleep) {
        } else {
            if (emo != null) {
                emo.killAndErase()
            }
            emo = Sleep(this)
        }
    }

    fun hideSleep() {
        if (emo is EmoIcon.Sleep) {
            emo.killAndErase()
            emo = null
        }
    }

    fun showAlert() {
        if (emo is EmoIcon.Alert) {
        } else {
            if (emo != null) {
                emo.killAndErase()
            }
            emo = Alert(this)
        }
    }

    fun hideAlert() {
        if (emo is EmoIcon.Alert) {
            emo.killAndErase()
            emo = null
        }
    }

    fun kill() {
        super.kill()
        if (emo != null) {
            emo.killAndErase()
            emo = null
        }
    }

    fun onComplete(tweener: Tweener) {
        if (tweener === jumpTweener) {
            if (visible && Level.water.get(ch.pos) && !ch.flying) {
                GameScene.ripple(ch.pos)
            }
            if (jumpCallback != null) {
                jumpCallback.call()
            }
        } else if (tweener === motion) {
            isMoving = false
            motion.killAndErase()
            motion = null
        }
    }

    fun onComplete(anim: Animation) {
        if (animCallback != null) {
            animCallback.call()
            animCallback = null
        } else {
            if (anim === attack) {
                idle()
                ch.onAttackComplete()
            } else if (anim === operate) {
                idle()
                ch.onOperateComplete()
            }
        }
    }

    private class JumpTweener(visual: Visual, pos: PointF, height: Float, time: Float) : Tweener(visual, time) {
        var visual: Visual
        var start: PointF
        var end: PointF
        var height: Float
        protected fun updateValues(progress: Float) {
            visual.point(PointF.inter(start, end, progress).offset(0, -height * 4 * progress * (1 - progress)))
        }

        init {
            this.visual = visual
            start = visual.point()
            end = pos
            this.height = height
        }
    }

    companion object {
        const val DEFAULT = 0xFFFFFF
        const val POSITIVE = 0x00FF00
        const val NEGATIVE = 0xFF0000
        const val WARNING = 0xFF8800
        const val NEUTRAL = 0xFFFF00
        private const val MOVE_INTERVAL = 0.1f
        private const val FLASH_INTERVAL = 0.05f
    }

    init {
        listener = this
    }
}