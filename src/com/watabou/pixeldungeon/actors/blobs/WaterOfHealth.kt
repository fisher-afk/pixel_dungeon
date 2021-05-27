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

import com.watabou.noosa.audio.Sample

class WaterOfHealth : WellWater() {
    protected override fun affectHero(hero: Hero): Boolean {
        Sample.INSTANCE.play(Assets.SND_DRINK)
        PotionOfHealing.heal(hero)
        hero.belongings.uncurseEquipped()
        (hero.buff(Hunger::class.java) as Hunger).satisfy(Hunger.STARVING)
        CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)
        Dungeon.hero.interrupt()
        GLog.p(TXT_PROCCED)
        Journal.remove(Feature.WELL_OF_HEALTH)
        return true
    }

    protected override fun affectItem(item: Item): Item? {
        if (item is DewVial && !(item as DewVial).isFull()) {
            (item as DewVial).fill()
            Journal.remove(Feature.WELL_OF_HEALTH)
            return item
        }
        return null
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.HEALING), 0.5f, 0)
    }

    override fun tileDesc(): String {
        return "Power of health radiates from the water of this well. " +
                "Take a sip from it to heal your wounds and satisfy hunger."
    }

    companion object {
        private const val TXT_PROCCED = "As you take a sip, you feel your wounds heal completely."
    }
}