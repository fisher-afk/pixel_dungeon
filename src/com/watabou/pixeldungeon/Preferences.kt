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

import com.watabou.noosa.Game

internal enum class Preferences {
    INSTANCE;

    private var prefs: SharedPreferences? = null
    private fun get(): SharedPreferences? {
        if (prefs == null) {
            prefs = Game.instance.getPreferences(Game.MODE_PRIVATE)
        }
        return prefs
    }

    fun getInt(key: String?, defValue: Int): Int {
        return get().getInt(key, defValue)
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return get().getBoolean(key, defValue)
    }

    fun getString(key: String?, defValue: String?): String {
        return get().getString(key, defValue)
    }

    fun put(key: String?, value: Int) {
        get().edit().putInt(key, value).commit()
    }

    fun put(key: String?, value: Boolean) {
        get().edit().putBoolean(key, value).commit()
    }

    fun put(key: String?, value: String?) {
        get().edit().putString(key, value).commit()
    }

    companion object {
        const val KEY_LANDSCAPE = "landscape"
        const val KEY_IMMERSIVE = "immersive"
        const val KEY_GOOGLE_PLAY = "google_play"
        const val KEY_SCALE_UP = "scaleup"
        const val KEY_MUSIC = "music"
        const val KEY_SOUND_FX = "soundfx"
        const val KEY_ZOOM = "zoom"
        const val KEY_LAST_CLASS = "last_class"
        const val KEY_CHALLENGES = "challenges"
        const val KEY_DONATED = "donated"
        const val KEY_INTRO = "intro"
        const val KEY_BRIGHTNESS = "brightness"
    }
}