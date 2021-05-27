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

import com.watabou.noosa.Image

class QuickSlot : Button(), WndBag.Listener {
    private var itemInSlot: Item? = null
    private var slot: ItemSlot? = null
    private var crossB: Image? = null
    private var crossM: Image? = null
    private var targeting = false
    fun primary() {
        primary = this
        item(select())
    }

    fun secondary() {
        secondary = this
        item(select())
    }

    fun destroy() {
        super.destroy()
        if (this === primary) {
            primary = null
        } else {
            secondary = null
        }
        lastTarget = null
    }

    protected fun createChildren() {
        super.createChildren()
        slot = object : ItemSlot() {
            protected fun onClick() {
                if (targeting) {
                    GameScene.handleCell(lastTarget.pos)
                } else {
                    useTargeting()
                    select().execute(Dungeon.hero)
                }
            }

            protected fun onLongClick(): Boolean {
                return this@QuickSlot.onLongClick()
            }

            protected fun onTouchDown() {
                icon.lightness(0.7f)
            }

            protected fun onTouchUp() {
                icon.resetColor()
            }
        }
        add(slot)
        crossB = Icons.TARGET.get()
        crossB.visible = false
        add(crossB)
        crossM = Image()
        crossM.copy(crossB)
    }

    protected fun layout() {
        super.layout()
        slot.fill(this)
        crossB.x = PixelScene.align(x + (width - crossB.width) / 2)
        crossB.y = PixelScene.align(y + (height - crossB.height) / 2)
    }

    protected fun onClick() {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, TXT_SELECT_ITEM)
    }

    protected fun onLongClick(): Boolean {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, TXT_SELECT_ITEM)
        return true
    }

    private fun select(): Item? {
        val content = if (this === primary) primaryValue else secondaryValue
        return if (content is Item) {
            content as Item?
        } else if (content != null) {
            val item: Item = Dungeon.hero.belongings.getItem(content as Class<out Item?>?)
            if (item != null) item else Item.virtual(content as Class<out Item?>?)
        } else {
            null
        }
    }

    fun onSelect(item: Item?) {
        if (item != null) {
            if (this === primary) {
                primaryValue = if (item.stackable) item.getClass() else item
            } else {
                secondaryValue = if (item.stackable) item.getClass() else item
            }
            refresh()
        }
    }

    fun item(item: Item?) {
        slot!!.item(item)
        itemInSlot = item
        enableSlot()
    }

    fun enable(value: Boolean) {
        active = value
        if (value) {
            enableSlot()
        } else {
            slot!!.enable(false)
        }
    }

    private fun enableSlot() {
        slot!!.enable(
            itemInSlot != null && itemInSlot.quantity() > 0 &&
                    (Dungeon.hero.belongings.backpack.contains(itemInSlot) || itemInSlot.isEquipped(Dungeon.hero))
        )
    }

    private fun useTargeting() {
        targeting = lastTarget != null && lastTarget.isAlive() && Dungeon.visible.get(lastTarget.pos)
        if (targeting) {
            val pos: Int = Ballistica.cast(Dungeon.hero.pos, lastTarget.pos, false, true)
            if (pos != lastTarget.pos) {
                lastTarget = null
                targeting = false
            }
        }
        if (!targeting) {
            val n: Int = Dungeon.hero.visibleEnemies()
            for (i in 0 until n) {
                val enemy: Mob = Dungeon.hero.visibleEnemy(i)
                val pos: Int = Ballistica.cast(Dungeon.hero.pos, enemy.pos, false, true)
                if (pos == enemy.pos) {
                    lastTarget = enemy
                    targeting = true
                    break
                }
            }
        }
        if (targeting) {
            if (Actor.all().contains(lastTarget)) {
                lastTarget.sprite.parent.add(crossM)
                crossM.point(DungeonTilemap.tileToWorld(lastTarget.pos))
                crossB.visible = true
            } else {
                lastTarget = null
            }
        }
    }

    companion object {
        private const val TXT_SELECT_ITEM = "Select an item for the quickslot"
        private var primary: QuickSlot? = null
        private var secondary: QuickSlot? = null
        private var lastTarget: Char? = null
        var primaryValue: Any? = null
        var secondaryValue: Any? = null
        fun refresh() {
            if (primary != null) {
                primary!!.item(primary!!.select())
            }
            if (secondary != null) {
                secondary!!.item(secondary!!.select())
            }
        }

        fun target(item: Item?, target: Char) {
            if (target !== Dungeon.hero) {
                lastTarget = target
                HealthIndicator.instance.target(target)
            }
        }

        fun cancel() {
            if (primary != null && primary!!.targeting) {
                primary!!.crossB.visible = false
                primary!!.crossM.remove()
                primary!!.targeting = false
            }
            if (secondary != null && secondary!!.targeting) {
                secondary!!.crossB.visible = false
                secondary!!.crossM.remove()
                secondary!!.targeting = false
            }
        }

        private const val QUICKSLOT1 = "quickslot"
        private const val QUICKSLOT2 = "quickslot2"
        fun save(bundle: Bundle) {
            val stuff: Belongings = Dungeon.hero.belongings
            if (primaryValue is Class<*> &&
                stuff.getItem(primaryValue as Class<out Item?>?) != null
            ) {
                bundle.put(QUICKSLOT1, (primaryValue as Class<*>?)!!.name)
            }
            if (secondaryValue is Class<*> && stuff.getItem(secondaryValue as Class<out Item?>?) != null &&
                Toolbar.secondQuickslot()
            ) {
                bundle.put(QUICKSLOT2, (secondaryValue as Class<*>?)!!.name)
            }
        }

        fun save(bundle: Bundle, item: Item) {
            if (item === primaryValue) {
                bundle.put(QUICKSLOT1, true)
            }
            if (item === secondaryValue && Toolbar.secondQuickslot()) {
                bundle.put(QUICKSLOT2, true)
            }
        }

        fun restore(bundle: Bundle) {
            primaryValue = null
            secondaryValue = null
            var qsClass: String = bundle.getString(QUICKSLOT1)
            if (qsClass != null) {
                try {
                    primaryValue = Class.forName(qsClass)
                } catch (e: ClassNotFoundException) {
                }
            }
            qsClass = bundle.getString(QUICKSLOT2)
            if (qsClass != null) {
                try {
                    secondaryValue = Class.forName(qsClass)
                } catch (e: ClassNotFoundException) {
                }
            }
        }

        fun restore(bundle: Bundle, item: Item?) {
            if (bundle.getBoolean(QUICKSLOT1)) {
                primaryValue = item
            }
            if (bundle.getBoolean(QUICKSLOT2)) {
                secondaryValue = item
            }
        }

        fun compress() {
            if (primaryValue == null && secondaryValue != null ||
                primaryValue === secondaryValue
            ) {
                primaryValue = secondaryValue
                secondaryValue = null
            }
        }
    }
}