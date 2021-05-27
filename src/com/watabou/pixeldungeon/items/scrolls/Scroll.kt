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

import com.watabou.pixeldungeon.Badges

abstract class Scroll : Item() {
    private val rune: String
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

    protected abstract fun doRead()
    protected fun readAnimation() {
        curUser.spend(TIME_TO_READ)
        curUser.busy()
        (curUser.sprite as HeroSprite).read()
    }

    val isKnown: Boolean
        get() = handler.isKnown(this)

    fun setKnown() {
        if (!isKnown) {
            handler.know(this)
        }
        Badges.validateAllScrollsIdentified()
    }

    fun identify(): Item {
        setKnown()
        return super.identify()
    }

    fun name(): String {
        return if (isKnown) name else "scroll \"$rune\""
    }

    fun info(): String {
        return if (isKnown) desc() else "This parchment is covered with indecipherable writing, and bears a title " +
                "of rune " + rune + ". Who knows what it will do when read aloud?"
    }

    val isUpgradable: Boolean
        get() = false
    val isIdentified: Boolean
        get() = isKnown

    fun price(): Int {
        return 15 * quantity
    }

    companion object {
        private const val TXT_BLINDED = "You can't read a scroll while blinded"
        const val AC_READ = "READ"
        protected const val TIME_TO_READ = 1f
        private val scrolls = arrayOf<Class<*>>(
            ScrollOfIdentify::class.java,
            ScrollOfMagicMapping::class.java,
            ScrollOfRecharging::class.java,
            ScrollOfRemoveCurse::class.java,
            ScrollOfTeleportation::class.java,
            ScrollOfChallenge::class.java,
            ScrollOfTerror::class.java,
            ScrollOfLullaby::class.java,
            ScrollOfPsionicBlast::class.java,
            ScrollOfMirrorImage::class.java,
            ScrollOfUpgrade::class.java,
            ScrollOfEnchantment::class.java
        )
        private val runes = arrayOf(
            "KAUNAN",
            "SOWILO",
            "LAGUZ",
            "YNGVI",
            "GYFU",
            "RAIDO",
            "ISAZ",
            "MANNAZ",
            "NAUDIZ",
            "BERKANAN",
            "ODAL",
            "TIWAZ"
        )
        private val images = arrayOf<Int>(
            ItemSpriteSheet.SCROLL_KAUNAN,
            ItemSpriteSheet.SCROLL_SOWILO,
            ItemSpriteSheet.SCROLL_LAGUZ,
            ItemSpriteSheet.SCROLL_YNGVI,
            ItemSpriteSheet.SCROLL_GYFU,
            ItemSpriteSheet.SCROLL_RAIDO,
            ItemSpriteSheet.SCROLL_ISAZ,
            ItemSpriteSheet.SCROLL_MANNAZ,
            ItemSpriteSheet.SCROLL_NAUDIZ,
            ItemSpriteSheet.SCROLL_BERKANAN,
            ItemSpriteSheet.SCROLL_ODAL,
            ItemSpriteSheet.SCROLL_TIWAZ
        )
        private var handler: ItemStatusHandler<Scroll>? = null
        fun initLabels() {
            handler = ItemStatusHandler<Scroll>(scrolls as Array<Class<out Scroll?>>, runes, images)
        }

        fun save(bundle: Bundle?) {
            handler.save(bundle)
        }

        fun restore(bundle: Bundle?) {
            handler = ItemStatusHandler<Scroll>(scrolls as Array<Class<out Scroll?>>, runes, images, bundle)
        }

        val known: HashSet<Class<out Scroll>>
            get() = handler.known()
        val unknown: HashSet<Class<out Scroll>>
            get() = handler.unknown()

        fun allKnown(): Boolean {
            return handler.known().size() === scrolls.size
        }
    }

    init {
        stackable = true
        defaultAction = AC_READ
    }

    init {
        image = handler.image(this)
        rune = handler.label(this)
    }
}