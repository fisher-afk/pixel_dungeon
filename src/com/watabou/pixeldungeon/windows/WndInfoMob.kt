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

import com.watabou.noosa.BitmapText

class WndInfoMob(mob: Mob) : WndTitledMessage(MobTitle(mob), desc(mob)) {
    private class MobTitle(mob: Mob) : Component() {
        private val image: CharSprite
        private val name: BitmapText
        private val health: HealthBar
        private val buffs: BuffIndicator
        protected fun layout() {
            image.x = 0
            image.y = Math.max(0, name.height() + GAP + health.height() - image.height)
            name.x = image.width + GAP
            name.y = image.height - health.height() - GAP - name.baseLine()
            val w: Float = width - image.width - GAP
            health.setRect(image.width + GAP, image.height - health.height(), w, health.height())
            buffs.setPos(
                name.x + name.width() + GAP,
                name.y + name.baseLine() - BuffIndicator.SIZE
            )
            height = health.bottom()
        }

        companion object {
            private const val GAP = 2
        }

        init {
            name = PixelScene.createText(Utils.capitalize(mob.name), 9)
            name.hardlight(TITLE_COLOR)
            name.measure()
            add(name)
            image = mob.sprite()
            add(image)
            health = HealthBar()
            health.level(mob.HP as Float / mob.HT)
            add(health)
            buffs = BuffIndicator(mob)
            add(buffs)
        }
    }

    companion object {
        private fun desc(mob: Mob): String {
            val builder: StringBuilder = StringBuilder(mob.description())
            builder.append(
                """
    
    
    ${mob.state.status().toString()}.
    """.trimIndent()
            )
            return builder.toString()
        }
    }
}