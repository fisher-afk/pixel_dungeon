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

import com.watabou.noosa.Game

class Toolbar : Component() {
    private var btnWait: Tool? = null
    private var btnSearch: Tool? = null
    private var btnInfo: Tool? = null
    private var btnInventory: Tool? = null
    private var btnQuick1: Tool? = null
    private var btnQuick2: Tool? = null
    private var pickedUp: PickedUpItem? = null
    private var lastEnabled = true
    protected fun createChildren() {
        add(object : Tool(0, 7, 20, 25) {
            protected fun onClick() {
                Dungeon.hero.rest(false)
            }

            protected fun onLongClick(): Boolean {
                Dungeon.hero.rest(true)
                return true
            }
        }.also { btnWait = it })
        add(object : Tool(20, 7, 20, 25) {
            protected fun onClick() {
                Dungeon.hero.search(true)
            }
        }.also { btnSearch = it })
        add(object : Tool(40, 7, 21, 25) {
            protected fun onClick() {
                GameScene.selectCell(informer)
            }
        }.also { btnInfo = it })
        add(object : Tool(60, 7, 23, 25) {
            private var gold: GoldIndicator? = null
            protected fun onClick() {
                GameScene.show(WndBag(Dungeon.hero.belongings.backpack, null, WndBag.Mode.ALL, null))
            }

            protected fun onLongClick(): Boolean {
                GameScene.show(WndCatalogus())
                return true
            }

            override fun createChildren() {
                super.createChildren()
                gold = GoldIndicator()
                add(gold)
            }

            override fun layout() {
                super.layout()
                gold.fill(this)
            }
        }.also { btnInventory = it })
        add(QuickslotTool(83, 7, 22, 25, true).also { btnQuick1 = it })
        add(QuickslotTool(83, 7, 22, 25, false).also { btnQuick2 = it })
        btnQuick2.visible = QuickSlot.secondaryValue != null
        add(PickedUpItem().also { pickedUp = it })
    }

    protected fun layout() {
        btnWait.setPos(x, y)
        btnSearch.setPos(btnWait.right(), y)
        btnInfo.setPos(btnSearch.right(), y)
        btnQuick1.setPos(width - btnQuick1.width(), y)
        if (btnQuick2.visible) {
            btnQuick2.setPos(btnQuick1.left() - btnQuick2.width(), y)
            btnInventory.setPos(btnQuick2.left() - btnInventory.width(), y)
        } else {
            btnInventory.setPos(btnQuick1.left() - btnInventory.width(), y)
        }
    }

    fun update() {
        super.update()
        if (lastEnabled != Dungeon.hero.ready) {
            lastEnabled = Dungeon.hero.ready
            for (tool in members) {
                if (tool is Tool) {
                    (tool as Tool).enable(lastEnabled)
                }
            }
        }
        if (!Dungeon.hero.isAlive()) {
            btnInventory!!.enable(true)
        }
    }

    fun pickup(item: Item) {
        pickedUp!!.reset(
            item,
            btnInventory.centerX(),
            btnInventory.centerY()
        )
    }

    private class Tool(x: Int, y: Int, width: Int, height: Int) : Button() {
        protected var base: Image? = null
        protected fun createChildren() {
            super.createChildren()
            base = Image(Assets.TOOLBAR)
            add(base)
        }

        protected fun layout() {
            super.layout()
            base.x = x
            base.y = y
        }

        protected fun onTouchDown() {
            base.brightness(1.4f)
        }

        protected fun onTouchUp() {
            if (active) {
                base.resetColor()
            } else {
                base.tint(BGCOLOR, 0.7f)
            }
        }

        fun enable(value: Boolean) {
            if (value != active) {
                if (value) {
                    base.resetColor()
                } else {
                    base.tint(BGCOLOR, 0.7f)
                }
                active = value
            }
        }

        companion object {
            private const val BGCOLOR = 0x7B8073
        }

        init {
            base.frame(x, y, width, height)
            width = width
            height = height
        }
    }

    private class QuickslotTool(x: Int, y: Int, width: Int, height: Int, primary: Boolean) : Tool(x, y, width, height) {
        private var slot: QuickSlot? = null
        override fun createChildren() {
            super.createChildren()
            slot = QuickSlot()
            add(slot)
        }

        override fun layout() {
            super.layout()
            slot.setRect(x + 1, y + 2, width - 2, height - 2)
        }

        override fun enable(value: Boolean) {
            slot!!.enable(value)
            super.enable(value)
        }

        init {
            if (primary) {
                slot!!.primary()
            } else {
                slot!!.secondary()
            }
        }
    }

    private class PickedUpItem : ItemSprite() {
        private var dstX = 0f
        private var dstY = 0f
        private var left = 0f
        fun reset(item: Item, dstX: Float, dstY: Float) {
            view(item.image(), item.glowing())
            visible = true
            active = visible
            this.dstX = dstX - ItemSprite.SIZE / 2
            this.dstY = dstY - ItemSprite.SIZE / 2
            left = DURATION
            x = this.dstX - DISTANCE
            y = this.dstY - DISTANCE
            alpha(1)
        }

        fun update() {
            super.update()
            if (Game.elapsed.let { left -= it; left } <= 0) {
                active = false
                visible = active
            } else {
                val p = left / DURATION
                scale.set(Math.sqrt(p.toDouble()).toFloat())
                val offset = DISTANCE * p
                x = dstX - offset
                y = dstY - offset
            }
        }

        companion object {
            private val DISTANCE: Float = DungeonTilemap.SIZE
            private const val DURATION = 0.2f
        }

        init {
            originToCenter()
            visible = false
            active = visible
        }
    }

    companion object {
        private var instance: Toolbar
        fun secondQuickslot(): Boolean {
            return instance.btnQuick2.visible
        }

        fun secondQuickslot(value: Boolean) {
            instance.btnQuick2.active = value
            instance.btnQuick2.visible = instance.btnQuick2.active
            instance.layout()
        }

        private val informer: CellSelector.Listener = object : Listener() {
            fun onSelect(cell: Int?) {
                if (cell == null) {
                    return
                }
                if (cell < 0 || cell > Level.LENGTH || !Dungeon.level.visited.get(cell) && !Dungeon.level.mapped.get(
                        cell
                    )
                ) {
                    GameScene.show(WndMessage("You don't know what is there."))
                    return
                }
                if (!Dungeon.visible.get(cell)) {
                    GameScene.show(WndInfoCell(cell))
                    return
                }
                if (cell === Dungeon.hero.pos) {
                    GameScene.show(WndHero())
                    return
                }
                val mob: Mob = Actor.findChar(cell) as Mob
                if (mob != null) {
                    GameScene.show(WndInfoMob(mob))
                    return
                }
                val heap: Heap = Dungeon.level.heaps.get(cell)
                if (heap != null && heap.type !== Heap.Type.HIDDEN) {
                    if (heap.type === Heap.Type.FOR_SALE && heap.size() === 1 && heap.peek().price() > 0) {
                        GameScene.show(WndTradeItem(heap, false))
                    } else {
                        GameScene.show(WndInfoItem(heap))
                    }
                    return
                }
                val plant: Plant = Dungeon.level.plants.get(cell)
                if (plant != null) {
                    GameScene.show(WndInfoPlant(plant))
                    return
                }
                GameScene.show(WndInfoCell(cell))
            }

            fun prompt(): String {
                return "Select a cell to examine"
            }
        }
    }

    init {
        instance = this
        height = btnInventory.height()
    }
}