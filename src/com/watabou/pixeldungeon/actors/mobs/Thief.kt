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
package com.watabou.pixeldungeon.actors.mobs

import com.watabou.pixeldungeon.Dungeon

class Thief : Mob() {
    var item: Item? = null
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEM, item)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        item = bundle.get(ITEM) as Item
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(1, 7)
    }

    protected override fun attackDelay(): Float {
        return 0.5f
    }

    override fun die(cause: Any?) {
        super.die(cause)
        if (item != null) {
            Dungeon.level.drop(item, pos).sprite.drop()
        }
    }

    fun attackSkill(target: Char?): Int {
        return 12
    }

    fun dr(): Int {
        return 3
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        if (item == null && enemy is Hero && steal(enemy as Hero)) {
            state = FLEEING
        }
        return damage
    }

    fun defenseProc(enemy: Char?, damage: Int): Int {
        if (state === FLEEING) {
            Dungeon.level.drop(Gold(), pos).sprite.drop()
        }
        return damage
    }

    protected fun steal(hero: Hero): Boolean {
        val item: Item = hero.belongings.randomUnequipped()
        return if (item != null) {
            GLog.w(TXT_STOLE, this.name, item.name())
            item.detachAll(hero.belongings.backpack)
            this.item = item
            true
        } else {
            false
        }
    }

    override fun description(): String {
        var desc = "Deeper levels of the dungeon have always been a hiding place for all kinds of criminals. " +
                "Not all of them could keep a clear mind during their extended periods so far from daylight. Long ago, " +
                "these crazy thieves and bandits have forgotten who they are and why they steal."
        if (item != null) {
            desc += java.lang.String.format(TXT_CARRIES, Utils.capitalize(this.name), item.name())
        }
        return desc
    }

    private inner class Fleeing : Mob.Fleeing() {
        protected override fun nowhereToRun() {
            if (buff(Terror::class.java) == null) {
                sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE)
                state = HUNTING
            } else {
                super.nowhereToRun()
            }
        }
    }

    companion object {
        protected const val TXT_STOLE = "%s stole %s from you!"
        protected const val TXT_CARRIES = "\n\n%s is carrying a _%s_. Stolen obviously."
        private const val ITEM = "item"
    }

    init {
        name = "crazy thief"
        spriteClass = ThiefSprite::class.java
        HT = 20
        HP = HT
        defenseSkill = 12
        EXP = 5
        maxLvl = 10
        loot = RingOfHaggler::class.java
        lootChance = 0.01f
        FLEEING = Fleeing()
    }
}