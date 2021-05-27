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
package com.watabou.pixeldungeon.items.potions

import com.watabou.pixeldungeon.Badges

class PotionOfMight : PotionOfStrength() {
    protected override fun apply(hero: Hero) {
        setKnown()
        hero.STR++
        hero.HT += 5
        hero.HP += 5
        hero.sprite.showStatus(CharSprite.POSITIVE, "+1 str, +5 ht")
        GLog.p("Newfound strength surges through your body.")
        Badges.validateStrengthAttained()
    }

    override fun desc(): String {
        return "This powerful liquid will course through your muscles, permanently " +
                "increasing your strength by one point and health by five points."
    }

    override fun price(): Int {
        return if (isKnown()) 200 * quantity else super.price()
    }

    init {
        name = "Potion of Might"
    }
}