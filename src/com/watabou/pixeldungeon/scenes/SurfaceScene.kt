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

import com.watabou.gltextures.Gradient

class SurfaceScene : PixelScene() {
    private var viewport: Camera? = null
    override fun create() {
        super.create()
        Music.INSTANCE.play(Assets.HAPPY, true)
        Music.INSTANCE.volume(1f)
        uiCamera.visible = false
        val w: Int = Camera.main.width
        val h: Int = Camera.main.height
        val archs = Archs()
        archs.reversed = true
        archs.setSize(w, h)
        add(archs)
        val vx: Float = align((w - SKY_WIDTH) / 2)
        val vy: Float = align((h - SKY_HEIGHT - BUTTON_HEIGHT) / 2)
        val s: Point = Camera.main.cameraToScreen(vx, vy)
        viewport = Camera(s.x, s.y, SKY_WIDTH, SKY_HEIGHT, defaultZoom)
        Camera.add(viewport)
        val window = Group()
        window.camera = viewport
        add(window)
        val dayTime: Boolean = !Dungeon.nightMode
        val sky = Sky(dayTime)
        sky.scale.set(SKY_WIDTH, SKY_HEIGHT)
        window.add(sky)
        if (!dayTime) {
            for (i in 0 until NSTARS) {
                val size: Float = Random.Float()
                val star = ColorBlock(size, size, -0x1)
                star.x = Random.Float(SKY_WIDTH) - size / 2
                star.y = Random.Float(SKY_HEIGHT) - size / 2
                star.am = size * (1 - star.y / SKY_HEIGHT)
                window.add(star)
            }
        }
        val range = (SKY_HEIGHT * 2 / 3).toFloat()
        for (i in 0 until NCLOUDS) {
            val cloud = Cloud((NCLOUDS - 1 - i) * (range / NCLOUDS) + Random.Float(range / NCLOUDS), dayTime)
            window.add(cloud)
        }
        val nPatches = (sky.width() / GrassPatch.WIDTH + 1) as Int
        for (i in 0 until nPatches * 4) {
            val patch = GrassPatch((i - 0.75f) * GrassPatch.WIDTH / 4, (SKY_HEIGHT + 1).toFloat(), dayTime)
            patch.brightness(if (dayTime) 0.7f else 0.4f)
            window.add(patch)
        }
        val a = Avatar(Dungeon.hero.heroClass)
        a.x = PixelScene.align((SKY_WIDTH - a.width) / 2)
        a.y = SKY_HEIGHT - a.height
        window.add(a)
        val pet = Pet()
        pet.bm = 1.2f
        pet.gm = pet.bm
        pet.rm = pet.gm
        pet.x = SKY_WIDTH / 2 + 2
        pet.y = SKY_HEIGHT - pet.height
        window.add(pet)
        window.add(object : TouchArea(sky) {
            protected fun onClick(touch: Touch?) {
                pet.jump()
            }
        })
        for (i in 0 until nPatches) {
            val patch = GrassPatch((i - 0.5f) * GrassPatch.WIDTH, SKY_HEIGHT.toFloat(), dayTime)
            patch.brightness(if (dayTime) 1.0f else 0.8f)
            window.add(patch)
        }
        val frame = Image(Assets.SURFACE)
        frame.frame(0, 0, FRAME_WIDTH, FRAME_HEIGHT)
        frame.x = vx - FRAME_MARGIN_X
        frame.y = vy - FRAME_MARGIN_TOP
        add(frame)
        if (dayTime) {
            a.brightness(1.2f)
            pet.brightness(1.2f)
        } else {
            frame.hardlight(0xDDEEFF)
        }
        val gameOver: RedButton = object : RedButton("Game Over") {
            protected fun onClick() {
                Game.switchScene(TitleScene::class.java)
            }
        }
        gameOver.setSize(SKY_WIDTH - FRAME_MARGIN_X * 2, BUTTON_HEIGHT)
        gameOver.setPos(frame.x + FRAME_MARGIN_X * 2, frame.y + frame.height + 4)
        add(gameOver)
        Badges.validateHappyEnd()
        fadeIn()
    }

    override fun destroy() {
        Badges.saveGlobal()
        Camera.remove(viewport)
        super.destroy()
    }

    protected fun onBackPressed() {}
    private class Sky(dayTime: Boolean) : Visual(0, 0, 1, 1) {
        private val texture: SmartTexture
        private val verticesBuffer: FloatBuffer
        fun draw() {
            super.draw()
            val script: NoosaScript = NoosaScript.get()
            texture.bind()
            script.camera(camera())
            script.uModel.valueM4(matrix)
            script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa
            )
            script.drawQuad(verticesBuffer)
        }

        companion object {
            private val day = intArrayOf(-0xbb7701, -0x331101)
            private val night = intArrayOf(-0xffeeab, -0xcca680)
        }

        init {
            texture = Gradient(if (dayTime) day else night)
            val vertices = FloatArray(16)
            verticesBuffer = Quad.create()
            vertices[2] = 0.25f
            vertices[6] = 0.25f
            vertices[10] = 0.75f
            vertices[14] = 0.75f
            vertices[3] = 0
            vertices[7] = 1
            vertices[11] = 1
            vertices[15] = 0
            vertices[0] = 0
            vertices[1] = 0
            vertices[4] = 1
            vertices[5] = 0
            vertices[8] = 1
            vertices[9] = 1
            vertices[12] = 0
            vertices[13] = 1
            verticesBuffer.position(0)
            verticesBuffer.put(vertices)
        }
    }

    private class Cloud(y: Float, dayTime: Boolean) : Image(Assets.SURFACE) {
        fun update() {
            super.update()
            if (speed.x > 0 && x > SKY_WIDTH) {
                x = -width()
            } else if (speed.x < 0 && x < -width()) {
                x = SKY_WIDTH
            }
        }

        companion object {
            private var lastIndex = -1
        }

        init {
            var index: Int
            do {
                index = Random.Int(3)
            } while (index == lastIndex)
            when (index) {
                0 -> frame(88, 0, 49, 20)
                1 -> frame(88, 20, 49, 22)
                2 -> frame(88, 42, 50, 18)
            }
            lastIndex = index
            y = y
            scale.set(1 - y / SKY_HEIGHT)
            x = Random.Float(SKY_WIDTH + width()) - width()
            speed.x = scale.x * if (dayTime) +8 else -8
            if (dayTime) {
                tint(0xCCEEFF, 1 - scale.y)
            } else {
                bm = +3.0f
                gm = bm
                rm = gm
                ba = -2.1f
                ga = ba
                ra = ga
            }
        }
    }

    private class Avatar(cl: HeroClass) : Image(Assets.AVATARS) {
        companion object {
            private const val WIDTH = 24
            private const val HEIGHT = 28
        }

        init {
            frame(TextureFilm(texture, WIDTH, HEIGHT).get(cl.ordinal))
        }
    }

    private class Pet : MovieClip(Assets.PET), MovieClip.Listener {
        private val idle: Animation
        private val jump: Animation
        fun jump() {
            play(jump)
        }

        fun onComplete(anim: Animation) {
            if (anim === jump) {
                play(idle)
            }
        }

        init {
            val frames = TextureFilm(texture, 16, 16)
            idle = Animation(2, true)
            idle.frames(frames, 0, 0, 0, 0, 0, 0, 1)
            jump = Animation(10, false)
            jump.frames(frames, 2, 3, 4, 5, 6)
            listener = this
            play(idle)
        }
    }

    private class GrassPatch(tx: Float, ty: Float, forward: Boolean) : Image(Assets.SURFACE) {
        private val tx: Float
        private val ty: Float
        private var a: Double = Random.Float(5)
        private var angle = 0.0
        private val forward: Boolean
        fun update() {
            super.update()
            a += Random.Float(Game.elapsed * 5)
            angle = (2 + Math.cos(a)) * if (forward) +0.2 else -0.2
            scale.y = Math.cos(angle).toFloat()
            x = tx + Math.tan(angle).toFloat() * width
            y = ty - scale.y * height
        }

        protected fun updateMatrix() {
            super.updateMatrix()
            Matrix.skewX(matrix, (angle / Matrix.G2RAD) as Float)
        }

        companion object {
            const val WIDTH = 16
            const val HEIGHT = 14
        }

        init {
            frame(88 + Random.Int(4) * WIDTH, 60, WIDTH, HEIGHT)
            this.tx = tx
            this.ty = ty
            this.forward = forward
        }
    }

    companion object {
        private const val FRAME_WIDTH = 88
        private const val FRAME_HEIGHT = 125
        private const val FRAME_MARGIN_TOP = 9
        private const val FRAME_MARGIN_X = 4
        private const val BUTTON_HEIGHT = 20
        private const val SKY_WIDTH = 80
        private const val SKY_HEIGHT = 112
        private const val NSTARS = 100
        private const val NCLOUDS = 5
    }
}