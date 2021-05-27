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
package com.watabou.pixeldungeon.utils

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

object BArray {
    fun and(a: BooleanArray, b: BooleanArray, result: BooleanArray?): BooleanArray {
        var result = result
        val length = a.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = a[i] && b[i]
        }
        return result
    }

    fun or(a: BooleanArray, b: BooleanArray, result: BooleanArray?): BooleanArray {
        var result = result
        val length = a.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = a[i] || b[i]
        }
        return result
    }

    fun not(a: BooleanArray, result: BooleanArray?): BooleanArray {
        var result = result
        val length = a.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = !a[i]
        }
        return result
    }

    fun `is`(a: IntArray, result: BooleanArray?, v1: Int): BooleanArray {
        var result = result
        val length = a.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = a[i] == v1
        }
        return result
    }

    fun isOneOf(a: IntArray, result: BooleanArray?, vararg v: Int): BooleanArray {
        var result = result
        val length = a.size
        val nv = v.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = false
            for (j in 0 until nv) {
                if (a[i] == v[j]) {
                    result[i] = true
                    break
                }
            }
        }
        return result
    }

    fun isNot(a: IntArray, result: BooleanArray?, v1: Int): BooleanArray {
        var result = result
        val length = a.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = a[i] != v1
        }
        return result
    }

    fun isNotOneOf(a: IntArray, result: BooleanArray?, vararg v: Int): BooleanArray {
        var result = result
        val length = a.size
        val nv = v.size
        if (result == null) {
            result = BooleanArray(length)
        }
        for (i in 0 until length) {
            result[i] = true
            for (j in 0 until nv) {
                if (a[i] == v[j]) {
                    result[i] = false
                    break
                }
            }
        }
        return result
    }
}