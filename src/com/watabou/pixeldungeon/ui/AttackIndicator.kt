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

import com.watabou.pixeldungeon.Dungeon

class AttackIndicator : Tag(DangerIndicator.COLOR) {
    private var sprite: CharSprite? = null
    private val candidates: ArrayList<Mob?> = ArrayList<Mob?>()
    protected override fun createChildren() {
        super.createChildren()
    }

    protected override fun layout() {
        super.layout()
        if (sprite != null) {
            sprite.x = x + (width - sprite.width()) / 2
            sprite.y = y + (height - sprite.height()) / 2
            PixelScene.align(sprite)
        }
    }

    override fun update() {
        super.update()
        if (Dungeon.hero.isAlive()) {
            if (!Dungeon.hero.ready) {
                enable(false)
            }
        } else {
            visible(false)
            enable(false)
        }
    }

    private fun checkEnemies() {
        val heroPos: Int = Dungeon.hero.pos
        candidates.clear()
        val v: Int = Dungeon.hero.visibleEnemies()
        for (i in 0 until v) {
            val mob: Mob = Dungeon.hero.visibleEnemy(i)
            if (Level.adjacent(heroPos, mob.pos)) {
                candidates.add(mob)
            }
        }
        if (!candidates.contains(lastTarget)) {
            if (candidates.isEmpty()) {
                lastTarget = null
            } else {
                lastTarget = Random.element(candidates)
                updateImage()
                flash()
            }
        } else {
            if (!bg.visible) {
                flash()
            }
        }
        visible(lastTarget != null)
        enable(bg.visible)
    }

    private fun updateImage() {
        if (sprite != null) {
            sprite.killAndErase()
            sprite = null
        }
        try {
            sprite = lastTarget.spriteClass.newInstance()
            sprite.idle()
            sprite.paused = true
            add(sprite)
            sprite.x = x + (width - sprite.width()) / 2 + 1
            sprite.y = y + (height - sprite.height()) / 2
            PixelScene.align(sprite)
        } catch (e: Exception) {
        }
    }

    private var enabled = true
    private fun enable(value: Boolean) {
        enabled = value
        if (sprite != null) {
            sprite.alpha(if (value) ENABLED else DISABLED)
        }
    }

    private fun visible(value: Boolean) {
        bg.visible = value
        if (sprite != null) {
            sprite.visible = value
        }
    }

    protected fun onClick() {
        if (enabled) {
            Dungeon.hero.handle(lastTarget.pos)
        }
    }

    companion object {
        private const val ENABLED = 1.0f
        private const val DISABLED = 0.3f
        private var instance: AttackIndicator
        private var lastTarget: Mob? = null
        fun target(target: Char?) {
            lastTarget = target as Mob?
            instance.updateImage()
            HealthIndicator.instance.target(target)
        }

        fun updateState() {
            instance.checkEnemies()
        }
    }

    init {
        instance = this
        setSize(24, 24)
        visible(false)
        enable(false)
    }
}