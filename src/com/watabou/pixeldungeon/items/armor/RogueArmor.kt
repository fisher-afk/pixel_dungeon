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
package com.watabou.pixeldungeon.items.armor

import com.watabou.noosa.audio.Sample

class RogueArmor : ClassArmor() {
    override fun special(): String {
        return AC_SPECIAL
    }

    override fun doSpecial() {
        GameScene.selectCell(teleporter)
    }

    override fun doEquip(hero: Hero): Boolean {
        return if (hero.heroClass === HeroClass.ROGUE) {
            super.doEquip(hero)
        } else {
            GLog.w(TXT_NOT_ROGUE)
            false
        }
    }

    override fun desc(): String {
        return "Wearing this dark garb, a rogue can perform a trick, that is called \"smoke bomb\" " +
                "(though no real explosives are used): he blinds enemies who could see him and jumps aside."
    }

    companion object {
        private const val TXT_FOV = "You can only jump to an empty location in your field of view"
        private const val TXT_NOT_ROGUE = "Only rogues can use this armor!"
        private const val AC_SPECIAL = "SMOKE BOMB"
        protected var teleporter: CellSelector.Listener = object : Listener() {
            fun onSelect(target: Int?) {
                if (target != null) {
                    if (!Level.fieldOfView.get(target) ||
                        !(Level.passable.get(target) || Level.avoid.get(target)) || Actor.findChar(target) != null
                    ) {
                        GLog.w(TXT_FOV)
                        return
                    }
                    curUser.HP -= curUser.HP / 3
                    for (mob in Dungeon.level.mobs) {
                        if (Level.fieldOfView.get(mob.pos)) {
                            Buff.prolong(mob, Blindness::class.java, 2)
                            mob.state = mob.WANDERING
                            mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4)
                        }
                    }
                    WandOfBlink.appear(curUser, target)
                    CellEmitter.get(target).burst(Speck.factory(Speck.WOOL), 10)
                    Sample.INSTANCE.play(Assets.SND_PUFF)
                    Dungeon.level.press(target, curUser)
                    Dungeon.observe()
                    curUser.spendAndNext(Actor.TICK)
                }
            }

            fun prompt(): String {
                return "Choose a location to jump to"
            }
        }
    }

    init {
        name = "rogue garb"
        image = ItemSpriteSheet.ARMOR_ROGUE
    }
}