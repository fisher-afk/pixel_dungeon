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
package com.watabou.pixeldungeon.ui

import com.watabou.noosa.Image

enum class Icons {
    SKULL, BUSY, COMPASS, PREFS, WARNING, TARGET, WATA, WARRIOR, MAGE, ROGUE, HUNTRESS, CLOSE, DEPTH, SLEEP, ALERT, SUPPORT, SUPPORTED, BACKPACK, SEED_POUCH, SCROLL_HOLDER, WAND_HOLSTER, KEYRING, CHECKED, UNCHECKED, EXIT, CHALLENGE_OFF, CHALLENGE_ON, RESUME;

    fun get(): Image {
        return get(this)
    }

    companion object {
        operator fun get(type: Icons?): Image {
            val icon = Image(Assets.ICONS)
            when (type) {
                SKULL -> icon.frame(icon.texture.uvRect(0, 0, 8, 8))
                BUSY -> icon.frame(icon.texture.uvRect(8, 0, 16, 8))
                COMPASS -> icon.frame(icon.texture.uvRect(0, 8, 7, 13))
                PREFS -> icon.frame(icon.texture.uvRect(30, 0, 46, 16))
                WARNING -> icon.frame(icon.texture.uvRect(46, 0, 58, 12))
                TARGET -> icon.frame(icon.texture.uvRect(0, 13, 16, 29))
                WATA -> icon.frame(icon.texture.uvRect(30, 16, 45, 26))
                WARRIOR -> icon.frame(icon.texture.uvRect(0, 29, 16, 45))
                MAGE -> icon.frame(icon.texture.uvRect(16, 29, 32, 45))
                ROGUE -> icon.frame(icon.texture.uvRect(32, 29, 48, 45))
                HUNTRESS -> icon.frame(icon.texture.uvRect(48, 29, 64, 45))
                CLOSE -> icon.frame(icon.texture.uvRect(0, 45, 13, 58))
                DEPTH -> icon.frame(icon.texture.uvRect(45, 12, 54, 20))
                SLEEP -> icon.frame(icon.texture.uvRect(13, 45, 22, 53))
                ALERT -> icon.frame(icon.texture.uvRect(22, 45, 30, 53))
                SUPPORT -> icon.frame(icon.texture.uvRect(30, 45, 46, 61))
                SUPPORTED -> icon.frame(icon.texture.uvRect(46, 45, 62, 61))
                BACKPACK -> icon.frame(icon.texture.uvRect(58, 0, 68, 10))
                SCROLL_HOLDER -> icon.frame(icon.texture.uvRect(68, 0, 78, 10))
                SEED_POUCH -> icon.frame(icon.texture.uvRect(78, 0, 88, 10))
                WAND_HOLSTER -> icon.frame(icon.texture.uvRect(88, 0, 98, 10))
                KEYRING -> icon.frame(icon.texture.uvRect(64, 29, 74, 39))
                CHECKED -> icon.frame(icon.texture.uvRect(54, 12, 66, 24))
                UNCHECKED -> icon.frame(icon.texture.uvRect(66, 12, 78, 24))
                EXIT -> icon.frame(icon.texture.uvRect(98, 0, 114, 16))
                CHALLENGE_OFF -> icon.frame(icon.texture.uvRect(78, 16, 102, 40))
                CHALLENGE_ON -> icon.frame(icon.texture.uvRect(102, 16, 126, 40))
                RESUME -> icon.frame(icon.texture.uvRect(114, 0, 126, 11))
            }
            return icon
        }

        operator fun get(cl: HeroClass?): Image? {
            return when (cl) {
                HeroClass.WARRIOR -> get(WARRIOR)
                HeroClass.MAGE -> get(MAGE)
                HeroClass.ROGUE -> get(ROGUE)
                HeroClass.HUNTRESS -> get(HUNTRESS)
                else -> null
            }
        }
    }
}