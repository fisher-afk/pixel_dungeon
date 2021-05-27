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

import com.watabou.noosa.audio.Sample

class Plant : Bundlable {
    var plantName: String? = null
    var image = 0
    var pos = 0
    var sprite: PlantSprite? = null
    fun activate(ch: Char?) {
        if (ch is Hero && (ch as Hero?).subClass === HeroSubClass.WARDEN) {
            Buff.affect(ch, Barkskin::class.java).level(ch.HT / 3)
        }
        wither()
    }

    fun wither() {
        Dungeon.level.uproot(pos)
        sprite.kill()
        if (Dungeon.visible.get(pos)) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6)
        }
        if (Dungeon.hero.subClass === HeroSubClass.WARDEN) {
            if (Random.Int(5) === 0) {
                Dungeon.level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop()
            }
            if (Random.Int(5) === 0) {
                Dungeon.level.drop(Dewdrop(), pos).sprite.drop()
            }
        }
    }

    fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
    }

    fun desc(): String? {
        return null
    }

    class Seed : Item() {
        protected var plantClass: Class<out Plant>? = null
        protected var plantName: String? = null
        var alchemyClass: Class<out Item?>? = null
        fun actions(hero: Hero?): ArrayList<String> {
            val actions: ArrayList<String> = super.actions(hero)
            actions.add(AC_PLANT)
            return actions
        }

        protected fun onThrow(cell: Int) {
            if (Dungeon.level.map.get(cell) === Terrain.ALCHEMY || Level.pit.get(cell)) {
                super.onThrow(cell)
            } else {
                Dungeon.level.plant(this, cell)
            }
        }

        fun execute(hero: Hero, action: String) {
            if (action == AC_PLANT) {
                hero.spend(TIME_TO_PLANT)
                hero.busy()
                (detach(hero.belongings.backpack) as Seed).onThrow(hero.pos)
                hero.sprite.operate(hero.pos)
            } else {
                super.execute(hero, action)
            }
        }

        fun couch(pos: Int): Plant? {
            return try {
                if (Dungeon.visible.get(pos)) {
                    Sample.INSTANCE.play(Assets.SND_PLANT)
                }
                val plant = plantClass!!.newInstance()
                plant.pos = pos
                plant
            } catch (e: Exception) {
                null
            }
        }

        val isUpgradable: Boolean
            get() = false
        val isIdentified: Boolean
            get() = true

        fun price(): Int {
            return 10 * quantity
        }

        fun info(): String {
            return java.lang.String.format(TXT_INFO, Utils.indefinite(plantName), desc())
        }

        companion object {
            const val AC_PLANT = "PLANT"
            private const val TXT_INFO = "Throw this seed to the place where you want to grow %s.\n\n%s"
            private const val TIME_TO_PLANT = 1f
        }

        init {
            stackable = true
            defaultAction = AC_THROW
        }
    }

    companion object {
        private const val POS = "pos"
    }
}