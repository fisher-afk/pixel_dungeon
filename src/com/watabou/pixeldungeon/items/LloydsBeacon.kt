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

import com.watabou.noosa.Game

class LloydsBeacon : Item() {
    private var returnDepth = -1
    private var returnPos = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DEPTH, returnDepth)
        if (returnDepth != -1) {
            bundle.put(POS, returnPos)
        }
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        returnDepth = bundle.getInt(DEPTH)
        returnPos = bundle.getInt(POS)
    }

    override fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_SET)
        if (returnDepth != -1) {
            actions.add(AC_RETURN)
        }
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action === AC_SET || action === AC_RETURN) {
            if (Dungeon.bossLevel()) {
                hero.spend(TIME_TO_USE)
                GLog.w(TXT_PREVENTING)
                return
            }
            for (i in 0 until Level.NEIGHBOURS8.length) {
                if (Actor.findChar(hero.pos + Level.NEIGHBOURS8.get(i)) != null) {
                    GLog.w(TXT_CREATURES)
                    return
                }
            }
        }
        if (action === AC_SET) {
            returnDepth = Dungeon.depth
            returnPos = hero.pos
            hero.spend(TIME_TO_USE)
            hero.busy()
            hero.sprite.operate(hero.pos)
            Sample.INSTANCE.play(Assets.SND_BEACON)
            GLog.i(TXT_RETURN)
        } else if (action === AC_RETURN) {
            if (returnDepth == Dungeon.depth) {
                reset()
                WandOfBlink.appear(hero, returnPos)
                Dungeon.level.press(returnPos, hero)
                Dungeon.observe()
            } else {
                InterlevelScene.mode = InterlevelScene.Mode.RETURN
                InterlevelScene.returnDepth = returnDepth
                InterlevelScene.returnPos = returnPos
                reset()
                Game.switchScene(InterlevelScene::class.java)
            }
        } else {
            super.execute(hero, action)
        }
    }

    fun reset() {
        returnDepth = -1
    }

    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true

    override fun glowing(): Glowing? {
        return if (returnDepth != -1) WHITE else null
    }

    override fun info(): String {
        return TXT_INFO + if (returnDepth == -1) "" else Utils.format(TXT_SET, returnDepth)
    }

    companion object {
        private const val TXT_PREVENTING = "Strong magic aura of this place prevents you from using the lloyd's beacon!"
        private const val TXT_CREATURES =
            "Psychic aura of neighbouring creatures doesn't allow you to use the lloyd's beacon at this moment."
        private const val TXT_RETURN =
            "The lloyd's beacon is successfully set at your current location, now you can return here anytime."
        private const val TXT_INFO =
            "Lloyd's beacon is an intricate magic device, that allows you to return to a place you have already been."
        private const val TXT_SET = "\n\nThis beacon was set somewhere on the level %d of Pixel Dungeon."
        const val TIME_TO_USE = 1f
        const val AC_SET = "SET"
        const val AC_RETURN = "RETURN"
        private const val DEPTH = "depth"
        private const val POS = "pos"
        private val WHITE: Glowing = Glowing(0xFFFFFF)
    }

    init {
        name = "lloyd's beacon"
        image = ItemSpriteSheet.BEACON
        unique = true
    }
}