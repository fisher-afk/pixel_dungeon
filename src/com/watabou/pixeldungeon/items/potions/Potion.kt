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

import com.watabou.noosa.audio.Sample

class Potion : Item() {
    private val color: String
    fun actions(hero: Hero?): ArrayList<String> {
        val actions: ArrayList<String> = super.actions(hero)
        actions.add(AC_DRINK)
        return actions
    }

    fun execute(hero: Hero, action: String) {
        if (action == AC_DRINK) {
            if (isKnown && (this is PotionOfLiquidFlame ||
                        this is PotionOfToxicGas ||
                        this is PotionOfParalyticGas)
            ) {
                GameScene.show(
                    object : WndOptions(TXT_HARMFUL, TXT_R_U_SURE_DRINK, TXT_YES, TXT_NO) {
                        protected fun onSelect(index: Int) {
                            if (index == 0) {
                                drink(hero)
                            }
                        }
                    }
                )
            } else {
                drink(hero)
            }
        } else {
            super.execute(hero, action)
        }
    }

    fun doThrow(hero: Hero?) {
        if (isKnown && (this is PotionOfExperience ||
                    this is PotionOfHealing ||
                    this is PotionOfLevitation ||
                    this is PotionOfMindVision ||
                    this is PotionOfStrength ||
                    this is PotionOfInvisibility ||
                    this is PotionOfMight)
        ) {
            GameScene.show(
                object : WndOptions(TXT_BENEFICIAL, TXT_R_U_SURE_THROW, TXT_YES, TXT_NO) {
                    protected fun onSelect(index: Int) {
                        if (index == 0) {
                            super@Potion.doThrow(hero)
                        }
                    }
                }
            )
        } else {
            super.doThrow(hero)
        }
    }

    protected fun drink(hero: Hero) {
        detach(hero.belongings.backpack)
        hero.spend(TIME_TO_DRINK)
        hero.busy()
        onThrow(hero.pos)
        Sample.INSTANCE.play(Assets.SND_DRINK)
        hero.sprite.operate(hero.pos)
    }

    protected fun onThrow(cell: Int) {
        if (Dungeon.hero.pos === cell) {
            apply(Dungeon.hero)
        } else if (Dungeon.level.map.get(cell) === Terrain.WELL || Level.pit.get(cell)) {
            super.onThrow(cell)
        } else {
            shatter(cell)
        }
    }

    protected fun apply(hero: Hero) {
        shatter(hero.pos)
    }

    fun shatter(cell: Int) {
        if (Dungeon.visible.get(cell)) {
            GLog.i("The flask shatters and " + color() + " liquid splashes harmlessly")
            Sample.INSTANCE.play(Assets.SND_SHATTER)
            splash(cell)
        }
    }

    val isKnown: Boolean
        get() = handler.isKnown(this)

    fun setKnown() {
        if (!isKnown) {
            handler.know(this)
        }
        Badges.validateAllPotionsIdentified()
    }

    fun identify(): Item {
        setKnown()
        return this
    }

    protected fun color(): String {
        return color
    }

    fun name(): String {
        return if (isKnown) name else "$color potion"
    }

    fun info(): String {
        return if (isKnown) desc() else "This flask contains a swirling " + color + " liquid. " +
                "Who knows what it will do when drunk or thrown?"
    }

    val isIdentified: Boolean
        get() = isKnown
    val isUpgradable: Boolean
        get() = false

    protected fun splash(cell: Int) {
        val color: Int = ItemSprite.pick(image, 8, 10)
        Splash.at(cell, color, 5)
    }

    fun price(): Int {
        return 20 * quantity
    }

    companion object {
        const val AC_DRINK = "DRINK"
        private const val TXT_HARMFUL = "Harmful potion!"
        private const val TXT_BENEFICIAL = "Beneficial potion"
        private const val TXT_YES = "Yes, I know what I'm doing"
        private const val TXT_NO = "No, I changed my mind"
        private const val TXT_R_U_SURE_DRINK =
            "Are you sure you want to drink it? In most cases you should throw such potions at your enemies."
        private const val TXT_R_U_SURE_THROW =
            "Are you sure you want to throw it? In most cases it makes sense to drink it."
        private const val TIME_TO_DRINK = 1f
        private val potions = arrayOf<Class<*>>(
            PotionOfHealing::class.java,
            PotionOfExperience::class.java,
            PotionOfToxicGas::class.java,
            PotionOfLiquidFlame::class.java,
            PotionOfStrength::class.java,
            PotionOfParalyticGas::class.java,
            PotionOfLevitation::class.java,
            PotionOfMindVision::class.java,
            PotionOfPurity::class.java,
            PotionOfInvisibility::class.java,
            PotionOfMight::class.java,
            PotionOfFrost::class.java
        )
        private val colors = arrayOf(
            "turquoise", "crimson", "azure", "jade", "golden", "magenta",
            "charcoal", "ivory", "amber", "bistre", "indigo", "silver"
        )
        private val images = arrayOf<Int>(
            ItemSpriteSheet.POTION_TURQUOISE,
            ItemSpriteSheet.POTION_CRIMSON,
            ItemSpriteSheet.POTION_AZURE,
            ItemSpriteSheet.POTION_JADE,
            ItemSpriteSheet.POTION_GOLDEN,
            ItemSpriteSheet.POTION_MAGENTA,
            ItemSpriteSheet.POTION_CHARCOAL,
            ItemSpriteSheet.POTION_IVORY,
            ItemSpriteSheet.POTION_AMBER,
            ItemSpriteSheet.POTION_BISTRE,
            ItemSpriteSheet.POTION_INDIGO,
            ItemSpriteSheet.POTION_SILVER
        )
        private var handler: ItemStatusHandler<Potion>? = null
        fun initColors() {
            handler = ItemStatusHandler<Potion>(potions as Array<Class<out Potion?>>, colors, images)
        }

        fun save(bundle: Bundle?) {
            handler.save(bundle)
        }

        fun restore(bundle: Bundle?) {
            handler = ItemStatusHandler<Potion>(potions as Array<Class<out Potion?>>, colors, images, bundle)
        }

        val known: HashSet<Class<out Potion>>
            get() = handler.known()
        val unknown: HashSet<Class<out Potion>>
            get() = handler.unknown()

        fun allKnown(): Boolean {
            return handler.known().size() === potions.size
        }
    }

    init {
        stackable = true
        defaultAction = AC_DRINK
    }

    init {
        image = handler.image(this)
        color = handler.label(this)
    }
}