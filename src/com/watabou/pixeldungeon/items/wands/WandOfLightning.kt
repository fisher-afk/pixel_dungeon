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
package com.watabou.pixeldungeon.items.wands

import com.watabou.noosa.Camera

class WandOfLightning : Wand() {
    private val affected = ArrayList<Char>()
    private val points = IntArray(20)
    private var nPoints = 0
    protected override fun onZap(cell: Int) {
        // Everything is processed in fx() method
        if (!curUser.isAlive()) {
            Dungeon.fail(Utils.format(ResultDescriptions.WAND, name, Dungeon.depth))
            GLog.n("You killed yourself with your own Wand of Lightning...")
        }
    }

    private fun hit(ch: Char, damage: Int) {
        if (damage < 1) {
            return
        }
        if (ch === Dungeon.hero) {
            Camera.main.shake(2, 0.3f)
        }
        affected.add(ch)
        ch.damage(if (Level.water.get(ch.pos) && !ch.flying) (damage * 2) else damage, LightningTrap.LIGHTNING)
        ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3)
        ch.sprite.flash()
        points[nPoints++] = ch.pos
        val ns = HashSet<Char>()
        for (i in 0 until Level.NEIGHBOURS8.length) {
            val n: Char = Actor.findChar(ch.pos + Level.NEIGHBOURS8.get(i))
            if (n != null && !affected.contains(n)) {
                ns.add(n)
            }
        }
        if (ns.size > 0) {
            hit(Random.element(ns), Random.Int(damage / 2, damage))
        }
    }

    protected override fun fx(cell: Int, callback: Callback?) {
        nPoints = 0
        points[nPoints++] = Dungeon.hero.pos
        val ch: Char = Actor.findChar(cell)
        if (ch != null) {
            affected.clear()
            val lvl: Int = power()
            hit(ch, Random.Int(5 + lvl / 2, 10 + lvl))
        } else {
            points[nPoints++] = cell
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3)
        }
        curUser.sprite.parent.add(Lightning(points, nPoints, callback))
    }

    fun desc(): String {
        return "This wand conjures forth deadly arcs of electricity, which deal damage " +
                "to several creatures standing close to each other."
    }

    init {
        name = "Wand of Lightning"
    }
}