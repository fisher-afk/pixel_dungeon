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

import com.watabou.noosa.Camera

class Earthroot : Plant() {
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buff.affect(ch, Armor::class.java).level = ch.HT
        }
        if (Dungeon.visible.get(pos)) {
            CellEmitter.bottom(pos).start(EarthParticle.FACTORY, 0.05f, 8)
            Camera.main.shake(1, 0.4f)
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
            plantName = "Earthroot"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_EARTHROOT
            plantClass = Earthroot::class.java
            alchemyClass = PotionOfParalyticGas::class.java
        }
    }

    class Armor : Buff() {
        private var pos = 0
        private var level = 0
        fun attachTo(target: Char): Boolean {
            pos = target.pos
            return super.attachTo(target)
        }

        fun act(): Boolean {
            if (target.pos !== pos) {
                detach()
            }
            spend(STEP)
            return true
        }

        fun absorb(damage: Int): Int {
            return if (damage >= level) {
                detach()
                damage - level
            } else {
                level -= damage
                0
            }
        }

        fun level(value: Int) {
            if (level < value) {
                level = value
            }
        }

        fun icon(): Int {
            return BuffIndicator.ARMOR
        }

        override fun toString(): String {
            return "Herbal armor"
        }

        fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(POS, pos)
            bundle.put(LEVEL, level)
        }

        fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            pos = bundle.getInt(POS)
            level = bundle.getInt(LEVEL)
        }

        companion object {
            private const val STEP = 1f
            private const val POS = "pos"
            private const val LEVEL = "level"
        }
    }

    companion object {
        private const val TXT_DESC = "When a creature touches an Earthroot, its roots " +
                "create a kind of natural armor around it."
    }

    init {
        image = 5
        plantName = "Earthroot"
    }
}