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
package com.watabou.pixeldungeon.items.armor.glyphs

import com.watabou.pixeldungeon.Badges

class Viscosity : Glyph() {
    fun proc(armor: Armor, attacker: Char?, defender: Char, damage: Int): Int {
        if (damage == 0) {
            return 0
        }
        val level = Math.max(0, armor.effectiveLevel())
        return if (Random.Int(level + 7) >= 6) {
            var debuff: DeferedDamage =
                defender.buff(DeferedDamage::class.java)
            if (debuff == null) {
                debuff = DeferedDamage()
                debuff.attachTo(defender)
            }
            debuff.prolong(damage)
            defender.sprite.showStatus(CharSprite.WARNING, "deferred %d", damage)
            0
        } else {
            damage
        }
    }

    fun name(weaponName: String?): String {
        return String.format(TXT_VISCOSITY, weaponName)
    }

    fun glowing(): Glowing {
        return PURPLE
    }

    class DeferedDamage : Buff() {
        protected var damage = 0
        fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(DAMAGE, damage)
        }

        fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            damage = bundle.getInt(DAMAGE)
        }

        fun attachTo(target: Char?): Boolean {
            return if (super.attachTo(target)) {
                postpone(TICK)
                true
            } else {
                false
            }
        }

        fun prolong(damage: Int) {
            this.damage += damage
        }

        fun icon(): Int {
            return BuffIndicator.DEFERRED
        }

        override fun toString(): String {
            return Utils.format("Defered damage (%d)", damage)
        }

        fun act(): Boolean {
            if (target.isAlive()) {
                target.damage(1, this)
                if (target === Dungeon.hero && !target.isAlive()) {
                    // FIXME
                    Dungeon.fail(Utils.format(ResultDescriptions.GLYPH, "enchantment of viscosity", Dungeon.depth))
                    GLog.n("The enchantment of viscosity killed you...")
                    Badges.validateDeathFromGlyph()
                }
                spend(TICK)
                if (--damage <= 0) {
                    detach()
                }
            } else {
                detach()
            }
            return true
        }

        companion object {
            private const val DAMAGE = "damage"
        }
    }

    companion object {
        private const val TXT_VISCOSITY = "%s of viscosity"
        private val PURPLE: ItemSprite.Glowing = Glowing(0x8844CC)
    }
}