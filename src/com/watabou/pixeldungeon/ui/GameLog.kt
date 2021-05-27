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

import com.watabou.noosa.BitmapTextMultiline
import java.util.regex.Pattern

class GameLog : Component(), Signal.Listener<String?> {
    private var lastEntry: BitmapTextMultiline? = null
    private var lastColor = 0
    private fun recreateLines() {
        for (entry in entries) {
            lastEntry = PixelScene.createMultiline(entry.text, 6)
            lastEntry.hardlight(entry.color.also { lastColor = it })
            add(lastEntry)
        }
    }

    fun newLine() {
        lastEntry = null
    }

    fun onSignal(text: String) {
        var text = text
        var color: Int = CharSprite.DEFAULT
        if (text.startsWith(GLog.POSITIVE)) {
            text = text.substring(GLog.POSITIVE.length())
            color = CharSprite.POSITIVE
        } else if (text.startsWith(GLog.NEGATIVE)) {
            text = text.substring(GLog.NEGATIVE.length())
            color = CharSprite.NEGATIVE
        } else if (text.startsWith(GLog.WARNING)) {
            text = text.substring(GLog.WARNING.length())
            color = CharSprite.WARNING
        } else if (text.startsWith(GLog.HIGHLIGHT)) {
            text = text.substring(GLog.HIGHLIGHT.length())
            color = CharSprite.NEUTRAL
        }
        text = Utils.capitalize(text).toString() +
                if (PUNCTUATION.matcher(text).matches()) "" else "."
        if (lastEntry != null && color == lastColor && lastEntry.nLines < MAX_LINES) {
            val lastMessage: String = lastEntry.text()
            lastEntry.text(if (lastMessage.length == 0) text else "$lastMessage $text")
            lastEntry.measure()
            entries[entries.size - 1].text = lastEntry.text()
        } else {
            lastEntry = PixelScene.createMultiline(text, 6)
            lastEntry.hardlight(color)
            lastColor = color
            add(lastEntry)
            entries.add(Entry(text, color))
        }
        if (length > 0) {
            var nLines: Int
            do {
                nLines = 0
                for (i in 0 until length) {
                    nLines += (members.get(i) as BitmapTextMultiline).nLines
                }
                if (nLines > MAX_LINES) {
                    remove(members.get(0))
                    entries.removeAt(0)
                }
            } while (nLines > MAX_LINES)
            if (entries.isEmpty()) {
                lastEntry = null
            }
        }
        layout()
    }

    protected fun layout() {
        var pos: Float = y
        for (i in length - 1 downTo 0) {
            val entry: BitmapTextMultiline = members.get(i) as BitmapTextMultiline
            entry.maxWidth = width as Int
            entry.measure()
            entry.x = x
            entry.y = pos - entry.height()
            pos -= entry.height()
        }
    }

    fun destroy() {
        GLog.update.remove(this)
        super.destroy()
    }

    private class Entry(var text: String, var color: Int)
    companion object {
        private const val MAX_LINES = 3
        private val PUNCTUATION = Pattern.compile(".*[.,;?! ]$")
        private val entries = ArrayList<Entry>()
        fun wipe() {
            entries.clear()
        }
    }

    init {
        GLog.update.add(this)
        recreateLines()
    }
}