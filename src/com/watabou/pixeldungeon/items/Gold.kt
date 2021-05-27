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

import com.watabou.noosa.audio.Sample

class Gold @JvmOverloads constructor(value: Int = 1) : Item() {
    override fun actions(hero: Hero?): ArrayList<String> {
        return ArrayList()
    }

    override fun doPickUp(hero: Hero): Boolean {
        Dungeon.gold += quantity
        Statistics.goldCollected += quantity
        Badges.validateGoldCollected()
        GameScene.pickUp(this)
        hero.sprite.showStatus(CharSprite.NEUTRAL, TXT_VALUE, quantity)
        hero.spendAndNext(TIME_TO_PICK_UP)
        Sample.INSTANCE.play(Assets.SND_GOLD, 1, 1, Random.Float(0.9f, 1.1f))
        return true
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun info(): String {
        return when (quantity) {
            0 -> TXT_COLLECT
            1 -> TXT_INFO_1
            else -> Utils.format(TXT_INFO, quantity)
        }
    }

    override fun random(): Item {
        quantity = Random.Int(20 + Dungeon.depth * 10, 40 + Dungeon.depth * 20)
        return this
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(VALUE, quantity)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        quantity = bundle.getInt(VALUE)
    }

    companion object {
        private const val TXT_COLLECT = "Collect gold coins to spend them later in a shop."
        private const val TXT_INFO = "A pile of %d gold coins. " + TXT_COLLECT
        private const val TXT_INFO_1 = "One gold coin. " + TXT_COLLECT
        private const val TXT_VALUE = "%+d"
        private const val VALUE = "value"
    }

    init {
        name = "gold"
        image = ItemSpriteSheet.GOLD
        stackable = true
    }

    init {
        this.quantity = value
    }
}