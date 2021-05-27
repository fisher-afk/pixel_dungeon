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
package com.watabou.pixeldungeon.actors.blobs

import com.watabou.pixeldungeon.Journal

class WaterOfTransmutation : WellWater() {
    protected override fun affectItem(item: Item?): Item? {
        var item: Item? = item
        item = if (item is MeleeWeapon) {
            changeWeapon(item as MeleeWeapon?)
        } else if (item is Scroll) {
            changeScroll(item as Scroll?)
        } else if (item is Potion) {
            changePotion(item as Potion?)
        } else if (item is Ring) {
            changeRing(item as Ring?)
        } else if (item is Wand) {
            changeWand(item as Wand?)
        } else if (item is Plant.Seed) {
            changeSeed(item as Plant.Seed?)
        } else {
            null
        }
        if (item != null) {
            Journal.remove(Feature.WELL_OF_TRANSMUTATION)
        }
        return item
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0)
    }

    private fun changeWeapon(w: MeleeWeapon?): MeleeWeapon? {
        var n: MeleeWeapon? = null
        if (w is Knuckles) {
            n = Dagger()
        } else if (w is Dagger) {
            n = Knuckles()
        } else if (w is Spear) {
            n = Quarterstaff()
        } else if (w is Quarterstaff) {
            n = Spear()
        } else if (w is Sword) {
            n = Mace()
        } else if (w is Mace) {
            n = Sword()
        } else if (w is Longsword) {
            n = BattleAxe()
        } else if (w is BattleAxe) {
            n = Longsword()
        } else if (w is Glaive) {
            n = WarHammer()
        } else if (w is WarHammer) {
            n = Glaive()
        }
        return if (n != null) {
            val level: Int = w.level()
            if (level > 0) {
                n.upgrade(level)
            } else if (level < 0) {
                n.degrade(-level)
            }
            if (w.isEnchanted()) {
                n.enchant()
            }
            n.levelKnown = w.levelKnown
            n.cursedKnown = w.cursedKnown
            n.cursed = w.cursed
            Journal.remove(Feature.WELL_OF_TRANSMUTATION)
            n
        } else {
            null
        }
    }

    private fun changeRing(r: Ring?): Ring {
        var n: Ring
        do {
            n = Generator.random(Category.RING) as Ring
        } while (n.getClass() === r.getClass())
        n.level(0)
        val level: Int = r.level()
        if (level > 0) {
            n.upgrade(level)
        } else if (level < 0) {
            n.degrade(-level)
        }
        n.levelKnown = r.levelKnown
        n.cursedKnown = r.cursedKnown
        n.cursed = r.cursed
        return n
    }

    private fun changeWand(w: Wand?): Wand {
        var n: Wand
        do {
            n = Generator.random(Category.WAND) as Wand
        } while (n.getClass() === w.getClass())
        n.level(0)
        n.upgrade(w.level())
        n.levelKnown = w.levelKnown
        n.cursedKnown = w.cursedKnown
        n.cursed = w.cursed
        return n
    }

    private fun changeSeed(s: Plant.Seed?): Plant.Seed {
        var n: Plant.Seed
        do {
            n = Generator.random(Category.SEED) as Plant.Seed
        } while (n.getClass() === s.getClass())
        return n
    }

    private fun changeScroll(s: Scroll?): Scroll {
        return if (s is ScrollOfUpgrade) {
            ScrollOfEnchantment()
        } else if (s is ScrollOfEnchantment) {
            ScrollOfUpgrade()
        } else {
            var n: Scroll
            do {
                n = Generator.random(Category.SCROLL) as Scroll
            } while (n.getClass() === s.getClass())
            n
        }
    }

    private fun changePotion(p: Potion?): Potion {
        return if (p is PotionOfStrength) {
            PotionOfMight()
        } else if (p is PotionOfMight) {
            PotionOfStrength()
        } else {
            var n: Potion
            do {
                n = Generator.random(Category.POTION) as Potion
            } while (n.getClass() === p.getClass())
            n
        }
    }

    override fun tileDesc(): String {
        return "Power of change radiates from the water of this well. " +
                "Throw an item into the well to turn it into something else."
    }
}