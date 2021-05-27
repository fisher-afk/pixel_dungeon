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

import com.watabou.pixeldungeon.actors.hero.HeroClass.storeInBundle
import com.watabou.pixeldungeon.actors.hero.HeroClass.title
import com.watabou.pixeldungeon.actors.hero.HeroClass.masteryBadge
import com.watabou.pixeldungeon.actors.hero.HeroClass.spritesheet
import com.watabou.pixeldungeon.actors.hero.HeroClass.perks
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.ui.Toast
import kotlin.jvm.JvmOverloads
import java.util.ArrayList
import com.watabou.pixeldungeon.ui.Toolbar
import java.lang.ClassNotFoundException
import com.watabou.pixeldungeon.ui.BadgesList.ListItem
import java.lang.Exception
import java.lang.StringBuilder
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import com.watabou.pixeldungeon.items.Item
import java.util.Collections
import java.util.Comparator
import java.io.IOException
import kotlin.Throws
import java.util.Arrays
import android.util.Log
import java.util.Locale
import GamesInProgress.Info
import com.watabou.pixeldungeon.actors.mobs.Spinner
import com.watabou.pixeldungeon.actors.blobs.Blob
import android.util.SparseArray
import Graph.Node
import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import com.watabou.pixeldungeon.levels.HallsLevel.Stream
import kotlin.jvm.Synchronized
import android.content.Intent
import android.net.Uri
import java.nio.FloatBuffer
import java.lang.Thread
import java.io.FileNotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.annotation.SuppressLint
import java.nio.ShortBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.util.FloatMath
import android.graphics.RectF
import com.watabou.pixeldungeon.windows.WndTabbed.Tab
import com.watabou.pixeldungeon.windows.WndJournal.ListItem
import com.watabou.pixeldungeon.windows.WndCatalogus.ListItem
import java.io.OutputStream
import java.io.InputStream
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.content.pm.ActivityInfo
import java.lang.Runnable
import android.view.View
import com.watabou.pixeldungeon.GamesInProgress.Info

object Assets {
    const val ARCS_BG = "arcs1.png"
    const val ARCS_FG = "arcs2.png"
    const val DASHBOARD = "dashboard.png"
    const val BANNERS = "banners.png"
    const val BADGES = "badges.png"
    const val LOCKED = "locked_badge.png"
    const val AMULET = "amulet.png"
    const val CHROME = "chrome.png"
    const val ICONS = "icons.png"
    const val STATUS = "status_pane.png"
    const val HP_BAR = "hp_bar.png"
    const val XP_BAR = "exp_bar.png"
    const val TOOLBAR = "toolbar.png"
    const val SHADOW = "shadow.png"
    const val WARRIOR = "warrior.png"
    const val MAGE = "mage.png"
    const val ROGUE = "rogue.png"
    const val HUNTRESS = "ranger.png"
    const val AVATARS = "avatars.png"
    const val PET = "pet.png"
    const val SURFACE = "surface.png"
    const val FIREBALL = "fireball.png"
    const val SPECKS = "specks.png"
    const val EFFECTS = "effects.png"
    const val RAT = "rat.png"
    const val GNOLL = "gnoll.png"
    const val CRAB = "crab.png"
    const val GOO = "goo.png"
    const val SWARM = "swarm.png"
    const val SKELETON = "skeleton.png"
    const val SHAMAN = "shaman.png"
    const val THIEF = "thief.png"
    const val TENGU = "tengu.png"
    const val SHEEP = "sheep.png"
    const val KEEPER = "shopkeeper.png"
    const val BAT = "bat.png"
    const val BRUTE = "brute.png"
    const val SPINNER = "spinner.png"
    const val DM300 = "dm300.png"
    const val WRAITH = "wraith.png"
    const val ELEMENTAL = "elemental.png"
    const val MONK = "monk.png"
    const val WARLOCK = "warlock.png"
    const val GOLEM = "golem.png"
    const val UNDEAD = "undead.png"
    const val KING = "king.png"
    const val STATUE = "statue.png"
    const val PIRANHA = "piranha.png"
    const val EYE = "eye.png"
    const val SUCCUBUS = "succubus.png"
    const val SCORPIO = "scorpio.png"
    const val ROTTING = "rotting_fist.png"
    const val BURNING = "burning_fist.png"
    const val YOG = "yog.png"
    const val LARVA = "larva.png"
    const val GHOST = "ghost.png"
    const val MAKER = "wandmaker.png"
    const val TROLL = "blacksmith.png"
    const val IMP = "demon.png"
    const val RATKING = "ratking.png"
    const val BEE = "bee.png"
    const val MIMIC = "mimic.png"
    const val ITEMS = "items.png"
    const val PLANTS = "plants.png"
    const val TILES_SEWERS = "tiles0.png"
    const val TILES_PRISON = "tiles1.png"
    const val TILES_CAVES = "tiles2.png"
    const val TILES_CITY = "tiles3.png"
    const val TILES_HALLS = "tiles4.png"
    const val WATER_SEWERS = "water0.png"
    const val WATER_PRISON = "water1.png"
    const val WATER_CAVES = "water2.png"
    const val WATER_CITY = "water3.png"
    const val WATER_HALLS = "water4.png"
    const val BUFFS_SMALL = "buffs.png"
    const val BUFFS_LARGE = "large_buffs.png"
    const val SPELL_ICONS = "spell_icons.png"
    const val FONTS1X = "font1x.png"
    const val FONTS15X = "font15x.png"
    const val FONTS2X = "font2x.png"
    const val FONTS25X = "font25x.png"
    const val FONTS3X = "font3x.png"
    const val THEME = "theme.mp3"
    const val TUNE = "game.mp3"
    const val HAPPY = "surface.mp3"
    const val SND_CLICK = "snd_click.mp3"
    const val SND_BADGE = "snd_badge.mp3"
    const val SND_GOLD = "snd_gold.mp3"
    const val SND_OPEN = "snd_door_open.mp3"
    const val SND_UNLOCK = "snd_unlock.mp3"
    const val SND_ITEM = "snd_item.mp3"
    const val SND_DEWDROP = "snd_dewdrop.mp3"
    const val SND_HIT = "snd_hit.mp3"
    const val SND_MISS = "snd_miss.mp3"
    const val SND_STEP = "snd_step.mp3"
    const val SND_WATER = "snd_water.mp3"
    const val SND_DESCEND = "snd_descend.mp3"
    const val SND_EAT = "snd_eat.mp3"
    const val SND_READ = "snd_read.mp3"
    const val SND_LULLABY = "snd_lullaby.mp3"
    const val SND_DRINK = "snd_drink.mp3"
    const val SND_SHATTER = "snd_shatter.mp3"
    const val SND_ZAP = "snd_zap.mp3"
    const val SND_LIGHTNING = "snd_lightning.mp3"
    const val SND_LEVELUP = "snd_levelup.mp3"
    const val SND_DEATH = "snd_death.mp3"
    const val SND_CHALLENGE = "snd_challenge.mp3"
    const val SND_CURSED = "snd_cursed.mp3"
    const val SND_TRAP = "snd_trap.mp3"
    const val SND_EVOKE = "snd_evoke.mp3"
    const val SND_TOMB = "snd_tomb.mp3"
    const val SND_ALERT = "snd_alert.mp3"
    const val SND_MELD = "snd_meld.mp3"
    const val SND_BOSS = "snd_boss.mp3"
    const val SND_BLAST = "snd_blast.mp3"
    const val SND_PLANT = "snd_plant.mp3"
    const val SND_RAY = "snd_ray.mp3"
    const val SND_BEACON = "snd_beacon.mp3"
    const val SND_TELEPORT = "snd_teleport.mp3"
    const val SND_CHARMS = "snd_charms.mp3"
    const val SND_MASTERY = "snd_mastery.mp3"
    const val SND_PUFF = "snd_puff.mp3"
    const val SND_ROCKS = "snd_rocks.mp3"
    const val SND_BURNING = "snd_burning.mp3"
    const val SND_FALLING = "snd_falling.mp3"
    const val SND_GHOST = "snd_ghost.mp3"
    const val SND_SECRET = "snd_secret.mp3"
    const val SND_BONES = "snd_bones.mp3"
    const val SND_BEE = "snd_bee.mp3"
    const val SND_DEGRADE = "snd_degrade.mp3"
    const val SND_MIMIC = "snd_mimic.mp3"
}