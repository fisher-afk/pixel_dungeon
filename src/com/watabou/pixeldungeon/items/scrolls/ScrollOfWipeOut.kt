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
package com.watabou.pixeldungeon.items.scrolls

import com.watabou.noosa.audio.Sample

class ScrollOfWipeOut : Item() {
    fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_READ) {
            if (hero.buff(Blindness::class.java) != null) {
                GLog.w(TXT_BLINDED)
            } else {
                curUser = hero
                curItem = detach(hero.belongings.backpack)
                doRead()
            }
        } else {
            super.execute(hero, action)
        }
    }

    private fun doRead() {
        GameScene.flash(0xFF6644)
        Invisibility.dispel()
        for (mob in Dungeon.level.mobs.toArray(arrayOfNulls<Mob>(0))) {
            if (!Bestiary.isBoss(mob)) {
                Sample.INSTANCE.play(Assets.SND_CURSED, 0.3f, 0.3f, Random.Float(0.6f, 0.9f))
                mob.die(this)
            }
        }
        for (heap in Dungeon.level.heaps.values()) {
            when (heap.type) {
                FOR_SALE -> {
                    heap.type = Type.HEAP
                    if (Dungeon.visible.get(heap.pos)) {
                        CellEmitter.center(heap.pos).burst(Speck.factory(Speck.COIN), 2)
                    }
                }
                MIMIC -> {
                    heap.type = Type.HEAP
                    heap.sprite.link()
                    Sample.INSTANCE.play(Assets.SND_CURSED, 0.3f, 0.3f, Random.Float(0.6f, 0.9f))
                }
                else -> {
                }
            }
        }
        curUser.spend(TIME_TO_READ)
        curUser.busy()
        (curUser.sprite as HeroSprite).read()
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = true

    fun desc(): String {
        return "Read this scroll to unleash the wrath of the dungeon spirits, killing everything on the current level. " +
                "Well, almost everything. Some of the more powerful creatures may be not affected."
    }

    fun price(): Int {
        return 100 * quantity
    }

    companion object {
        private const val TXT_BLINDED = "You can't read a scroll while blinded"
        const val AC_READ = "READ"
        protected const val TIME_TO_READ = 1f
    }

    init {
        name = "Scroll of Wipe Out"
        image = ItemSpriteSheet.SCROLL_WIPE_OUT
        stackable = true
        defaultAction = AC_READ
    }
}