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
package com.watabou.pixeldungeon

import android.os.Build
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.actors.blobs.Foliage
import com.watabou.pixeldungeon.actors.blobs.WaterOfHealth
import com.watabou.pixeldungeon.actors.buffs.Shadows
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.FetidRat
import com.watabou.pixeldungeon.actors.mobs.npcs.MirrorImage
import com.watabou.pixeldungeon.actors.mobs.npcs.Shopkeeper
import com.watabou.pixeldungeon.items.quest.DriedRose
import com.watabou.pixeldungeon.items.rings.RingOfElements
import com.watabou.pixeldungeon.items.rings.RingOfMending
import com.watabou.pixeldungeon.items.rings.RingOfPower
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.items.wands.WandOfReach
import com.watabou.pixeldungeon.items.weapon.enchantments.Shock
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang
import com.watabou.pixeldungeon.plants.Dreamweed
import com.watabou.pixeldungeon.plants.Rotberry
import com.watabou.pixeldungeon.scenes.TitleScene

class PixelDungeon : Game(TitleScene::class.java) {
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateImmersiveMode()
        val metrics = DisplayMetrics()
        instance.getWindowManager().getDefaultDisplay().getMetrics(metrics)
        val landscape: Boolean = metrics.widthPixels > metrics.heightPixels
        if (Preferences.INSTANCE.getBoolean(Preferences.KEY_LANDSCAPE, false) !== landscape) {
            landscape(!landscape)
        }
        Music.INSTANCE.enable(music())
        Sample.INSTANCE.enable(soundFx())
        Sample.INSTANCE.load(
            Assets.SND_CLICK,
            Assets.SND_BADGE,
            Assets.SND_GOLD,
            Assets.SND_DESCEND,
            Assets.SND_STEP,
            Assets.SND_WATER,
            Assets.SND_OPEN,
            Assets.SND_UNLOCK,
            Assets.SND_ITEM,
            Assets.SND_DEWDROP,
            Assets.SND_HIT,
            Assets.SND_MISS,
            Assets.SND_EAT,
            Assets.SND_READ,
            Assets.SND_LULLABY,
            Assets.SND_DRINK,
            Assets.SND_SHATTER,
            Assets.SND_ZAP,
            Assets.SND_LIGHTNING,
            Assets.SND_LEVELUP,
            Assets.SND_DEATH,
            Assets.SND_CHALLENGE,
            Assets.SND_CURSED,
            Assets.SND_EVOKE,
            Assets.SND_TRAP,
            Assets.SND_TOMB,
            Assets.SND_ALERT,
            Assets.SND_MELD,
            Assets.SND_BOSS,
            Assets.SND_BLAST,
            Assets.SND_PLANT,
            Assets.SND_RAY,
            Assets.SND_BEACON,
            Assets.SND_TELEPORT,
            Assets.SND_CHARMS,
            Assets.SND_MASTERY,
            Assets.SND_PUFF,
            Assets.SND_ROCKS,
            Assets.SND_BURNING,
            Assets.SND_FALLING,
            Assets.SND_GHOST,
            Assets.SND_SECRET,
            Assets.SND_BONES,
            Assets.SND_BEE,
            Assets.SND_DEGRADE,
            Assets.SND_MIMIC
        )
    }

    fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateImmersiveMode()
        }
    }

    fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        if (immersiveModeChanged) {
            requestedReset = true
            immersiveModeChanged = false
        }
    }

    companion object {
        fun switchNoFade(c: Class<out PixelScene?>?) {
            PixelScene.noFade = true
            switchScene(c)
        }

        /*
	 * ---> Prefernces
	 */
        fun landscape(value: Boolean) {
            Game.instance.setRequestedOrientation(if (value) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            Preferences.INSTANCE.put(Preferences.KEY_LANDSCAPE, value)
        }

        fun landscape(): Boolean {
            return width > height
        }

        // *** IMMERSIVE MODE ****
        private var immersiveModeChanged = false
        @SuppressLint("NewApi")
        fun immerse(value: Boolean) {
            Preferences.INSTANCE.put(Preferences.KEY_IMMERSIVE, value)
            instance.runOnUiThread(Runnable {
                updateImmersiveMode()
                immersiveModeChanged = true
            })
        }

        @SuppressLint("NewApi")
        fun updateImmersiveMode() {
            if (Build.VERSION.SDK_INT >= 19) {
                try {
                    // Sometime NullPointerException happens here
                    instance.getWindow().getDecorView().setSystemUiVisibility(
                        if (immersed()) View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY else 0
                    )
                } catch (e: Exception) {
                    reportException(e)
                }
            }
        }

        fun immersed(): Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_IMMERSIVE, false)
        }

        // *****************************
        fun scaleUp(value: Boolean) {
            Preferences.INSTANCE.put(Preferences.KEY_SCALE_UP, value)
            switchScene(TitleScene::class.java)
        }

        fun scaleUp(): Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_SCALE_UP, true)
        }

        fun zoom(value: Int) {
            Preferences.INSTANCE.put(Preferences.KEY_ZOOM, value)
        }

        fun zoom(): Int {
            return Preferences.INSTANCE.getInt(Preferences.KEY_ZOOM, 0)
        }

        fun music(value: Boolean) {
            Music.INSTANCE.enable(value)
            Preferences.INSTANCE.put(Preferences.KEY_MUSIC, value)
        }

        fun music(): Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_MUSIC, true)
        }

        fun soundFx(value: Boolean) {
            Sample.INSTANCE.enable(value)
            Preferences.INSTANCE.put(Preferences.KEY_SOUND_FX, value)
        }

        fun soundFx(): Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_SOUND_FX, true)
        }

        fun brightness(value: Boolean) {
            Preferences.INSTANCE.put(Preferences.KEY_BRIGHTNESS, value)
            if (scene() is GameScene) {
                (scene() as GameScene).brightness(value)
            }
        }

        fun brightness(): Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_BRIGHTNESS, false)
        }

        fun donated(value: String?) {
            Preferences.INSTANCE.put(Preferences.KEY_DONATED, value)
        }

        fun donated(): String {
            return Preferences.INSTANCE.getString(Preferences.KEY_DONATED, "")
        }

        fun lastClass(value: Int) {
            Preferences.INSTANCE.put(Preferences.KEY_LAST_CLASS, value)
        }

        fun lastClass(): Int {
            return Preferences.INSTANCE.getInt(Preferences.KEY_LAST_CLASS, 0)
        }

        fun challenges(value: Int) {
            Preferences.INSTANCE.put(Preferences.KEY_CHALLENGES, value)
        }

        fun challenges(): Int {
            return Preferences.INSTANCE.getInt(Preferences.KEY_CHALLENGES, 0)
        }

        fun intro(value: Boolean) {
            Preferences.INSTANCE.put(Preferences.KEY_INTRO, value)
        }

        fun intro(): Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_INTRO, true)
        }

        /*
	 * <--- Preferences
	 */
        fun reportException(tr: Throwable?) {
            Log.e("PD", Log.getStackTraceString(tr))
        }
    }

    init {
        com.watabou.utils.Bundle.addAlias(
            ScrollOfUpgrade::class.java,
            "com.watabou.pixeldungeon.items.scrolls.ScrollOfEnhancement"
        )
        com.watabou.utils.Bundle.addAlias(
            WaterOfHealth::class.java,
            "com.watabou.pixeldungeon.actors.blobs.Light"
        )
        com.watabou.utils.Bundle.addAlias(
            RingOfMending::class.java,
            "com.watabou.pixeldungeon.items.rings.RingOfRejuvenation"
        )
        com.watabou.utils.Bundle.addAlias(
            WandOfReach::class.java,
            "com.watabou.pixeldungeon.items.wands.WandOfTelekenesis"
        )
        com.watabou.utils.Bundle.addAlias(
            Foliage::class.java,
            "com.watabou.pixeldungeon.actors.blobs.Blooming"
        )
        com.watabou.utils.Bundle.addAlias(
            Shadows::class.java,
            "com.watabou.pixeldungeon.actors.buffs.Rejuvenation"
        )
        com.watabou.utils.Bundle.addAlias(
            ScrollOfPsionicBlast::class.java,
            "com.watabou.pixeldungeon.items.scrolls.ScrollOfNuclearBlast"
        )
        com.watabou.utils.Bundle.addAlias(
            Hero::class.java,
            "com.watabou.pixeldungeon.actors.Hero"
        )
        com.watabou.utils.Bundle.addAlias(
            Shopkeeper::class.java,
            "com.watabou.pixeldungeon.actors.mobs.Shopkeeper"
        )
        // 1.6.1
        com.watabou.utils.Bundle.addAlias(
            DriedRose::class.java,
            "com.watabou.pixeldungeon.items.DriedRose"
        )
        com.watabou.utils.Bundle.addAlias(
            MirrorImage::class.java,
            "com.watabou.pixeldungeon.items.scrolls.ScrollOfMirrorImage\$MirrorImage"
        )
        // 1.6.4
        com.watabou.utils.Bundle.addAlias(
            RingOfElements::class.java,
            "com.watabou.pixeldungeon.items.rings.RingOfCleansing"
        )
        com.watabou.utils.Bundle.addAlias(
            RingOfElements::class.java,
            "com.watabou.pixeldungeon.items.rings.RingOfResistance"
        )
        com.watabou.utils.Bundle.addAlias(
            Boomerang::class.java,
            "com.watabou.pixeldungeon.items.weapon.missiles.RangersBoomerang"
        )
        com.watabou.utils.Bundle.addAlias(
            RingOfPower::class.java,
            "com.watabou.pixeldungeon.items.rings.RingOfEnergy"
        )
        // 1.7.2
        com.watabou.utils.Bundle.addAlias(
            Dreamweed::class.java,
            "com.watabou.pixeldungeon.plants.Blindweed"
        )
        com.watabou.utils.Bundle.addAlias(
            Dreamweed.Seed::class.java,
            "com.watabou.pixeldungeon.plants.Blindweed\$Seed"
        )
        // 1.7.4
        com.watabou.utils.Bundle.addAlias(
            Shock::class.java,
            "com.watabou.pixeldungeon.items.weapon.enchantments.Piercing"
        )
        com.watabou.utils.Bundle.addAlias(
            Shock::class.java,
            "com.watabou.pixeldungeon.items.weapon.enchantments.Swing"
        )
        com.watabou.utils.Bundle.addAlias(
            ScrollOfEnchantment::class.java,
            "com.watabou.pixeldungeon.items.scrolls.ScrollOfWeaponUpgrade"
        )
        // 1.7.5
        com.watabou.utils.Bundle.addAlias(
            ScrollOfEnchantment::class.java,
            "com.watabou.pixeldungeon.items.Stylus"
        )
        // 1.8.0
        com.watabou.utils.Bundle.addAlias(
            FetidRat::class.java,
            "com.watabou.pixeldungeon.actors.mobs.npcs.Ghost\$FetidRat"
        )
        com.watabou.utils.Bundle.addAlias(
            Rotberry::class.java,
            "com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker\$Rotberry"
        )
        com.watabou.utils.Bundle.addAlias(
            Rotberry.Seed::class.java,
            "com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker\$Rotberry\$Seed"
        )
        // 1.9.0
        com.watabou.utils.Bundle.addAlias(
            WandOfReach::class.java,
            "com.watabou.pixeldungeon.items.wands.WandOfTelekinesis"
        )
    }
}