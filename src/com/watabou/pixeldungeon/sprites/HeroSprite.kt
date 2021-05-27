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

import com.watabou.gltextures.SmartTexture

class HeroSprite : CharSprite() {
    private var fly: Animation? = null
    private var read: Animation? = null
    fun updateArmor() {
        val film = TextureFilm(tiers(), (ch as Hero).tier(), FRAME_WIDTH, FRAME_HEIGHT)
        idle = Animation(1, true)
        idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(RUN_FRAMERATE, true)
        run.frames(film, 2, 3, 4, 5, 6, 7)
        die = Animation(20, false)
        die.frames(film, 8, 9, 10, 11, 12, 11)
        attack = Animation(15, false)
        attack.frames(film, 13, 14, 15, 0)
        zap = attack.clone()
        operate = Animation(8, false)
        operate.frames(film, 16, 17, 16, 17)
        fly = Animation(1, true)
        fly.frames(film, 18)
        read = Animation(20, false)
        read.frames(film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19)
    }

    override fun place(p: Int) {
        super.place(p)
        Camera.main.target = this
    }

    override fun move(from: Int, to: Int) {
        super.move(from, to)
        if (ch.flying) {
            play(fly)
        }
        Camera.main.target = this
    }

    override fun jump(from: Int, to: Int, callback: Callback?) {
        super.jump(from, to, callback)
        play(fly)
    }

    fun read() {
        animCallback = object : Callback() {
            fun call() {
                idle()
                ch.onOperateComplete()
            }
        }
        play(read)
    }

    override fun update() {
        sleeping = (ch as Hero).restoreHealth
        super.update()
    }

    fun sprint(on: Boolean): Boolean {
        run.delay = if (on) 0.625f / RUN_FRAMERATE else 1f / RUN_FRAMERATE
        return on
    }

    companion object {
        private const val FRAME_WIDTH = 12
        private const val FRAME_HEIGHT = 15
        private const val RUN_FRAMERATE = 20
        private var tiers: TextureFilm? = null
        fun tiers(): TextureFilm? {
            if (tiers == null) {
                val texture: SmartTexture = TextureCache.get(Assets.ROGUE)
                tiers = TextureFilm(texture, texture.width, FRAME_HEIGHT)
            }
            return tiers
        }

        fun avatar(cl: HeroClass, armorTier: Int): Image {
            val patch: RectF = tiers().get(armorTier)
            val avatar = Image(cl.spritesheet())
            val frame: RectF = avatar.texture.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT)
            frame.offset(patch.left, patch.top)
            avatar.frame(frame)
            return avatar
        }
    }

    init {
        link(Dungeon.hero)
        texture(Dungeon.hero.heroClass.spritesheet())
        updateArmor()
        idle()
    }
}