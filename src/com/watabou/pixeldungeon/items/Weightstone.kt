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
package com.watabou.pixeldungeon.items

import com.watabou.noosa.BitmapTextMultiline

class Weightstone : Item() {
    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_APPLY)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === AC_APPLY) {
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON)
        } else {
            super.execute(hero, action)
        }
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    private fun apply(weapon: Weapon, forSpeed: Boolean) {
        detach(curUser.belongings.backpack)
        weapon.fix()
        if (forSpeed) {
            weapon.imbue = Weapon.Imbue.SPEED
            GLog.p(TXT_FAST, weapon.name())
        } else {
            weapon.imbue = Weapon.Imbue.ACCURACY
            GLog.p(TXT_ACCURATE, weapon.name())
        }
        curUser.sprite.operate(curUser.pos)
        Sample.INSTANCE.play(Assets.SND_MISS)
        curUser.spend(TIME_TO_APPLY)
        curUser.busy()
    }

    override fun price(): Int {
        return 40 * quantity
    }

    override fun info(): String {
        return "Using a weightstone, you can balance your melee weapon to increase its speed or accuracy."
    }

    private val itemSelector: WndBag.Listener = object : Listener() {
        fun onSelect(item: Item?) {
            if (item != null) {
                GameScene.show(WndBalance(item as Weapon?))
            }
        }
    }

    inner class WndBalance(weapon: Weapon) : Window() {
        protected fun onSelect(index: Int) {}

        companion object {
            private const val TXT_CHOICE = "How would you like to balance your %s?"
            private const val TXT_SPEED = "For speed"
            private const val TXT_ACCURACY = "For accuracy"
            private const val TXT_CANCEL = "Never mind"
            private const val WIDTH = 120
            private const val MARGIN = 2
            private const val BUTTON_WIDTH = WIDTH - MARGIN * 2
            private const val BUTTON_HEIGHT = 20
        }

        init {
            val titlebar = IconTitle(weapon)
            titlebar.setRect(0, 0, Companion.WIDTH, 0)
            add(titlebar)
            val tfMesage: BitmapTextMultiline =
                PixelScene.createMultiline(Utils.format(Companion.TXT_CHOICE, weapon.name()), 8)
            tfMesage.maxWidth = Companion.WIDTH - Companion.MARGIN * 2
            tfMesage.measure()
            tfMesage.x = Companion.MARGIN
            tfMesage.y = titlebar.bottom() + Companion.MARGIN
            add(tfMesage)
            var pos: Float = tfMesage.y + tfMesage.height()
            if (weapon.imbue !== Weapon.Imbue.SPEED) {
                val btnSpeed: RedButton = object : RedButton(Companion.TXT_SPEED) {
                    protected fun onClick() {
                        hide()
                        this@Weightstone.apply(weapon, true)
                    }
                }
                btnSpeed.setRect(
                    Companion.MARGIN,
                    pos + Companion.MARGIN,
                    Companion.BUTTON_WIDTH,
                    Companion.BUTTON_HEIGHT
                )
                add(btnSpeed)
                pos = btnSpeed.bottom()
            }
            if (weapon.imbue !== Weapon.Imbue.ACCURACY) {
                val btnAccuracy: RedButton = object : RedButton(Companion.TXT_ACCURACY) {
                    protected fun onClick() {
                        hide()
                        this@Weightstone.apply(weapon, false)
                    }
                }
                btnAccuracy.setRect(
                    Companion.MARGIN,
                    pos + Companion.MARGIN,
                    Companion.BUTTON_WIDTH,
                    Companion.BUTTON_HEIGHT
                )
                add(btnAccuracy)
                pos = btnAccuracy.bottom()
            }
            val btnCancel: RedButton = object : RedButton(Companion.TXT_CANCEL) {
                protected fun onClick() {
                    hide()
                }
            }
            btnCancel.setRect(Companion.MARGIN, pos + Companion.MARGIN, Companion.BUTTON_WIDTH, Companion.BUTTON_HEIGHT)
            add(btnCancel)
            resize(Companion.WIDTH, btnCancel.bottom() as Int + Companion.MARGIN)
        }
    }

    companion object {
        private const val TXT_SELECT_WEAPON = "Select a weapon to balance"
        private const val TXT_FAST = "you balanced your %s to make it faster"
        private const val TXT_ACCURATE = "you balanced your %s to make it more accurate"
        private const val TIME_TO_APPLY = 2f
        private const val AC_APPLY = "APPLY"
    }

    init {
        name = "weightstone"
        image = ItemSpriteSheet.WEIGHT
        stackable = true
    }
}