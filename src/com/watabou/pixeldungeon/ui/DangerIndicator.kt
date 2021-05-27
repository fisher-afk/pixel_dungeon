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

import com.watabou.noosa.BitmapText

class DangerIndicator : Tag(0xFF4C4C) {
    private var number: BitmapText? = null
    private var icon: Image? = null
    private var enemyIndex = 0
    private var lastNumber = -1
    protected override fun createChildren() {
        super.createChildren()
        number = BitmapText(PixelScene.font1x)
        add(number)
        icon = Icons.SKULL.get()
        add(icon)
    }

    protected override fun layout() {
        super.layout()
        icon.x = right() - 10
        icon.y = y + (height - icon.height) / 2
        placeNumber()
    }

    private fun placeNumber() {
        number.x = right() - 11 - number.width()
        number.y = PixelScene.align(y + (height - number.baseLine()) / 2)
    }

    override fun update() {
        if (Dungeon.hero.isAlive()) {
            val v: Int = Dungeon.hero.visibleEnemies()
            if (v != lastNumber) {
                lastNumber = v
                if (lastNumber > 0.also { visible = it }) {
                    number.text(Integer.toString(lastNumber))
                    number.measure()
                    placeNumber()
                    flash()
                }
            }
        } else {
            visible = false
        }
        super.update()
    }

    protected fun onClick() {
        val target: Mob = Dungeon.hero.visibleEnemy(enemyIndex++)
        HealthIndicator.instance.target(if (target === HealthIndicator.instance.target()) null else target)
        Camera.main.target = null
        Camera.main.focusOn(target.sprite)
    }

    companion object {
        const val COLOR = 0xFF4C4C
    }

    init {
        setSize(24, 16)
        visible = false
    }
}