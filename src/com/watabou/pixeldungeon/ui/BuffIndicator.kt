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
package com.watabou.pixeldungeon.ui

import com.watabou.gltextures.SmartTexture

class BuffIndicator(private val ch: Char) : Component() {
    private var texture: SmartTexture? = null
    private var film: TextureFilm? = null
    private var icons: SparseArray<Image> = SparseArray<Image>()
    fun destroy() {
        super.destroy()
        if (this === heroInstance) {
            heroInstance = null
        }
    }

    protected fun createChildren() {
        texture = TextureCache.get(Assets.BUFFS_SMALL)
        film = TextureFilm(texture, SIZE, SIZE)
    }

    protected fun layout() {
        clear()
        val newIcons: SparseArray<Image> = SparseArray<Image>()
        for (buff in ch.buffs()) {
            val icon: Int = buff.icon()
            if (icon != NONE) {
                val img = Image(texture)
                img.frame(film.get(icon))
                img.x = x + members.size() * (SIZE + 2)
                img.y = y
                add(img)
                newIcons.put(icon, img)
            }
        }
        for (key in icons.keyArray()) {
            if (newIcons.get(key) == null) {
                val icon: Image = icons.get(key)
                icon.origin.set(SIZE / 2)
                add(icon)
                add(object : AlphaTweener(icon, 0, 0.6f) {
                    protected fun updateValues(progress: Float) {
                        super.updateValues(progress)
                        image.scale.set(1 + 5 * progress)
                    }
                })
            }
        }
        icons = newIcons
    }

    companion object {
        const val NONE = -1
        const val MIND_VISION = 0
        const val LEVITATION = 1
        const val FIRE = 2
        const val POISON = 3
        const val PARALYSIS = 4
        const val HUNGER = 5
        const val STARVATION = 6
        const val SLOW = 7
        const val OOZE = 8
        const val AMOK = 9
        const val TERROR = 10
        const val ROOTS = 11
        const val INVISIBLE = 12
        const val SHADOWS = 13
        const val WEAKNESS = 14
        const val FROST = 15
        const val BLINDNESS = 16
        const val COMBO = 17
        const val FURY = 18
        const val HEALING = 19
        const val ARMOR = 20
        const val HEART = 21
        const val LIGHT = 22
        const val CRIPPLE = 23
        const val BARKSKIN = 24
        const val IMMUNITY = 25
        const val BLEEDING = 26
        const val MARK = 27
        const val DEFERRED = 28
        const val VERTIGO = 29
        const val RAGE = 30
        const val SACRIFICE = 31
        const val SIZE = 7
        private var heroInstance: BuffIndicator? = null
        fun refreshHero() {
            if (heroInstance != null) {
                heroInstance!!.layout()
            }
        }
    }

    init {
        if (ch === Dungeon.hero) {
            heroInstance = this
        }
    }
}