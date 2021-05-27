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

class DewVial : Item() {
    private var volume = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(VOLUME, volume)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        volume = bundle.getInt(VOLUME)
    }

    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        if (volume > 0) {
            actions.add(AC_DRINK)
        }
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_DRINK) {
            if (volume > 0) {
                val value = Math.ceil(
                    Math.pow(
                        volume.toDouble(),
                        POW
                    ) / NUM * hero.HT
                ).toInt()
                val effect = Math.min(hero.HT - hero.HP, value)
                if (effect > 0) {
                    hero.HP += effect
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), if (volume > 5) 2 else 1)
                    hero.sprite.showStatus(CharSprite.POSITIVE, TXT_VALUE, effect)
                }
                volume = 0
                hero.spend(TIME_TO_DRINK)
                hero.busy()
                Sample.INSTANCE.play(Assets.SND_DRINK)
                hero.sprite.operate(hero.pos)
                updateQuickslot()
            } else {
                GLog.w(TXT_EMPTY)
            }
        } else {
            super.execute(hero, action)
        }
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    val isFull: Boolean
        get() = volume >= MAX_VOLUME

    fun collectDew(dew: Dewdrop) {
        GLog.i(TXT_COLLECTED)
        volume += dew.quantity
        if (volume >= MAX_VOLUME) {
            volume = MAX_VOLUME
            GLog.p(TXT_FULL)
        }
        updateQuickslot()
    }

    fun fill() {
        volume = MAX_VOLUME
        updateQuickslot()
    }

    override fun glowing(): Glowing? {
        return if (isFull) WHITE else null
    }

    override fun status(): String {
        return Utils.format(TXT_STATUS, volume, MAX_VOLUME)
    }

    override fun info(): String {
        return "You can store excess dew in this tiny vessel for drinking it later. " +
                "If the vial is full, in a moment of deadly peril the dew will be " +
                "consumed automatically."
    }

    override fun toString(): String {
        return super.toString() + " (" + status() + ")"
    }

    companion object {
        private const val MAX_VOLUME = 10
        private const val AC_DRINK = "DRINK"
        private const val TIME_TO_DRINK = 1f
        private const val TXT_VALUE = "%+dHP"
        private const val TXT_STATUS = "%d/%d"
        private const val TXT_AUTO_DRINK = "The dew vial was emptied to heal your wounds."
        private const val TXT_COLLECTED = "You collected a dewdrop into your dew vial."
        private const val TXT_FULL = "Your dew vial is full!"
        private const val TXT_EMPTY = "Your dew vial is empty!"
        private const val VOLUME = "volume"
        private const val NUM = 20.0
        private val POW = Math.log10(NUM)
        fun autoDrink(hero: Hero) {
            val vial: DewVial = hero.belongings.getItem(DewVial::class.java)
            if (vial != null && vial.isFull) {
                vial.execute(hero)
                hero.sprite.emitter().start(ShaftParticle.FACTORY, 0.2f, 3)
                GLog.w(TXT_AUTO_DRINK)
            }
        }

        private val WHITE: Glowing = Glowing(0xFFFFCC)
    }

    init {
        name = "dew vial"
        image = ItemSpriteSheet.VIAL
        defaultAction = AC_DRINK
        unique = true
    }
}