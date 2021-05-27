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

class ItemSlot() : Button() {
    protected var icon: ItemSprite? = null
    protected var topLeft: BitmapText? = null
    protected var topRight: BitmapText? = null
    protected var bottomRight: BitmapText? = null

    constructor(item: Item?) : this() {
        item(item)
    }

    protected fun createChildren() {
        super.createChildren()
        icon = ItemSprite()
        add(icon)
        topLeft = BitmapText(PixelScene.font1x)
        add(topLeft)
        topRight = BitmapText(PixelScene.font1x)
        add(topRight)
        bottomRight = BitmapText(PixelScene.font1x)
        add(bottomRight)
    }

    protected fun layout() {
        super.layout()
        icon.x = x + (width - icon.width) / 2
        icon.y = y + (height - icon.height) / 2
        if (topLeft != null) {
            topLeft.x = x
            topLeft.y = y
        }
        if (topRight != null) {
            topRight.x = x + (width - topRight.width())
            topRight.y = y
        }
        if (bottomRight != null) {
            bottomRight.x = x + (width - bottomRight.width())
            bottomRight.y = y + (height - bottomRight.height())
        }
    }

    fun item(item: Item?) {
        if (item == null) {
            active = false
            bottomRight.visible = false
            topRight.visible = bottomRight.visible
            topLeft.visible = topRight.visible
            icon.visible = topLeft.visible
        } else {
            active = true
            bottomRight.visible = true
            topRight.visible = bottomRight.visible
            topLeft.visible = topRight.visible
            icon.visible = topLeft.visible
            icon.view(item.image(), item.glowing())
            topLeft.text(item.status())
            val isArmor = item is Armor
            val isWeapon = item is Weapon
            if (isArmor || isWeapon) {
                if (item.levelKnown || isWeapon && item !is MeleeWeapon) {
                    val str: Int = if (isArmor) (item as Armor?).STR else (item as Weapon?).STR
                    topRight.text(Utils.format(TXT_STRENGTH, str))
                    if (str > Dungeon.hero.STR()) {
                        topRight.hardlight(DEGRADED)
                    } else {
                        topRight.resetColor()
                    }
                } else {
                    topRight.text(
                        Utils.format(
                            TXT_TYPICAL_STR,
                            if (isArmor) (item as Armor?).typicalSTR() else (item as MeleeWeapon?).typicalSTR()
                        )
                    )
                    topRight.hardlight(WARNING)
                }
                topRight.measure()
            } else {
                topRight.text(null)
            }
            val level: Int = item.visiblyUpgraded()
            if (level != 0 || item.cursed && item.cursedKnown) {
                bottomRight.text(if (item.levelKnown) Utils.format(TXT_LEVEL, level) else TXT_CURSED)
                bottomRight.measure()
                bottomRight.hardlight(if (level > 0) if (item.isBroken()) WARNING else UPGRADED else DEGRADED)
            } else {
                bottomRight.text(null)
            }
            layout()
        }
    }

    fun enable(value: Boolean) {
        active = value
        val alpha = if (value) ENABLED else DISABLED
        icon.alpha(alpha)
        topLeft.alpha(alpha)
        topRight.alpha(alpha)
        bottomRight.alpha(alpha)
    }

    fun showParams(value: Boolean) {
        if (value) {
            add(topRight)
            add(bottomRight)
        } else {
            remove(topRight)
            remove(bottomRight)
        }
    }

    companion object {
        const val DEGRADED = 0xFF4444
        const val UPGRADED = 0x44FF44
        const val WARNING = 0xFF8800
        private const val ENABLED = 1.0f
        private const val DISABLED = 0.3f
        private const val TXT_STRENGTH = ":%d"
        private const val TXT_TYPICAL_STR = "%d?"
        private const val TXT_LEVEL = "%+d"
        private const val TXT_CURSED = "" //"-";

        // Special "virtual items"
        val CHEST: Item = object : Item() {
            fun image(): Int {
                return ItemSpriteSheet.this
            }
        }
        val LOCKED_CHEST: Item = object : Item() {
            fun image(): Int {
                return ItemSpriteSheet.this
            }
        }
        val TOMB: Item = object : Item() {
            fun image(): Int {
                return ItemSpriteSheet.this
            }
        }
        val SKELETON: Item = object : Item() {
            fun image(): Int {
                return ItemSpriteSheet.BONES
            }
        }
    }
}