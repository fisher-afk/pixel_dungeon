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
package com.watabou.pixeldungeon.items.wands

import com.watabou.noosa.audio.Sample

class WandOfMagicMissile : Wand() {
    private var disenchantEquipped = false
    override fun actions(hero: Hero): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        if (level() > 0) {
            actions.add(AC_DISENCHANT)
        }
        return actions
    }

    protected override fun onZap(cell: Int) {
        val ch: Char = Actor.findChar(cell)
        if (ch != null) {
            val level: Int = power()
            ch.damage(Random.Int(1, 6 + level * 2), this)
            ch.sprite.burst(-0x663301, level / 2 + 2)
            if (ch === curUser && !ch.isAlive()) {
                Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
                GLog.n("You killed yourself with your own Wand of Magic Missile...")
            }
        }
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_DISENCHANT) {
            if (hero.belongings.weapon === this) {
                disenchantEquipped = true
                hero.belongings.weapon = null
                updateQuickslot()
            } else {
                disenchantEquipped = false
                detach(hero.belongings.backpack)
            }
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WAND, TXT_SELECT_WAND)
        } else {
            super.execute(hero, action)
        }
    }

    protected override val isKnown: Boolean
        protected get() = true

    override fun setKnown() {}
    protected override fun initialCharges(): Int {
        return 3
    }

    fun desc(): String {
        return "This wand launches missiles of pure magical energy, dealing moderate damage to a target creature."
    }

    private val itemSelector: WndBag.Listener = object : Listener() {
        fun onSelect(item: Item?) {
            if (item != null) {
                Sample.INSTANCE.play(Assets.SND_EVOKE)
                ScrollOfUpgrade.upgrade(curUser)
                evoke(curUser)
                GLog.w(TXT_DISENCHANTED, item.name())
                item.upgrade()
                curUser.spendAndNext(TIME_TO_DISENCHANT)
                Badges.validateItemLevelAquired(item)
            } else {
                if (disenchantEquipped) {
                    curUser.belongings.weapon = this@WandOfMagicMissile
                    this@WandOfMagicMissile.updateQuickslot()
                } else {
                    collect(curUser.belongings.backpack)
                }
            }
        }
    }

    companion object {
        const val AC_DISENCHANT = "DISENCHANT"
        private const val TXT_SELECT_WAND = "Select a wand to upgrade"
        private const val TXT_DISENCHANTED =
            "you disenchanted the Wand of Magic Missile and used its essence to upgrade your %s"
        private const val TIME_TO_DISENCHANT = 2f
    }

    init {
        name = "Wand of Magic Missile"
        image = ItemSpriteSheet.WAND_MAGIC_MISSILE
    }
}