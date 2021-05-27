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
package com.watabou.pixeldungeon.plants

import com.watabou.pixeldungeon.Dungeon

class Sungrass : Plant() {
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buff.affect(ch, Health::class.java)
        }
        if (Dungeon.visible.get(pos)) {
            CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)
        }
    }

    override fun desc(): String {
        return TXT_DESC
    }

    class Seed : Plant.Seed() {
        fun desc(): String {
            return TXT_DESC
        }

        init {
            plantName = "Sungrass"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_SUNGRASS
            plantClass = Sungrass::class.java
            alchemyClass = PotionOfHealing::class.java
        }
    }

    class Health : Buff() {
        private var pos = 0
        fun attachTo(target: Char): Boolean {
            pos = target.pos
            return super.attachTo(target)
        }

        fun act(): Boolean {
            if (target.pos !== pos || target.HP >= target.HT) {
                detach()
            } else {
                target.HP = Math.min(target.HT, target.HP + target.HT / 10)
                target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
            }
            spend(STEP)
            return true
        }

        fun icon(): Int {
            return BuffIndicator.HEALING
        }

        override fun toString(): String {
            return "Herbal healing"
        }

        fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(POS, pos)
        }

        fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            pos = bundle.getInt(POS)
        }

        companion object {
            private const val STEP = 5f
            private const val POS = "pos"
        }
    }

    companion object {
        private const val TXT_DESC = "Sungrass is renowned for its sap's healing properties."
    }

    init {
        image = 4
        plantName = "Sungrass"
    }
}