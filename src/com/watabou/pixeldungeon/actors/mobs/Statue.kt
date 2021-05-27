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

class Statue : Mob() {
    private var weapon: Weapon? = null
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(WEAPON, weapon)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        weapon = bundle.get(WEAPON) as Weapon
    }

    protected override fun act(): Boolean {
        if (Dungeon.visible.get(pos)) {
            Journal.add(Journal.Feature.STATUE)
        }
        return super.act()
    }

    fun damageRoll(): Int {
        return Random.NormalIntRange(weapon.min(), weapon.max())
    }

    fun attackSkill(target: Char?): Int {
        return ((9 + Dungeon.depth) * weapon.ACU) as Int
    }

    protected override fun attackDelay(): Float {
        return weapon.DLY
    }

    fun dr(): Int {
        return Dungeon.depth
    }

    override fun damage(dmg: Int, src: Any?) {
        if (state === PASSIVE) {
            state = HUNTING
        }
        super.damage(dmg, src)
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        weapon.proc(this, enemy, damage)
        return damage
    }

    override fun beckon(cell: Int) {
        // Do nothing
    }

    override fun die(cause: Any?) {
        Dungeon.level.drop(weapon, pos).sprite.drop()
        super.die(cause)
    }

    override fun destroy() {
        Journal.remove(Journal.Feature.STATUE)
        super.destroy()
    }

    override fun reset(): Boolean {
        state = PASSIVE
        return true
    }

    override fun description(): String {
        return "You would think that it's just another ugly statue of this dungeon, but its red glowing eyes give itself away. " +
                "While the statue itself is made of stone, the _" + weapon.name() + "_, it's wielding, looks real."
    }

    companion object {
        private const val WEAPON = "weapon"
        private val RESISTANCES = HashSet<Class<*>>()
        private val IMMUNITIES = HashSet<Class<*>>()

        init {
            RESISTANCES.add(ToxicGas::class.java)
            RESISTANCES.add(Poison::class.java)
            RESISTANCES.add(Death::class.java)
            RESISTANCES.add(ScrollOfPsionicBlast::class.java)
            IMMUNITIES.add(Leech::class.java)
        }
    }

    fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }

    fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }

    init {
        name = "animated statue"
        spriteClass = StatueSprite::class.java
        EXP = 0
        state = PASSIVE
    }

    init {
        do {
            weapon = Generator.random(Generator.Category.WEAPON) as Weapon
        } while (weapon !is MeleeWeapon || weapon.level() < 0)
        weapon.identify()
        weapon.enchant()
        HT = 15 + Dungeon.depth * 5
        HP = HT
        defenseSkill = 4 + Dungeon.depth
    }
}