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
package com.watabou.pixeldungeon.windows

import com.watabou.input.Touchscreen.Touch

class WndStory(text: String?) : Window(0, 0, Chrome.get(Chrome.Type.SCROLL)) {
    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 6
        private const val bgR = 0.77f
        private const val bgG = 0.73f
        private const val bgB = 0.62f
        const val ID_SEWERS = 0
        const val ID_PRISON = 1
        const val ID_CAVES = 2
        const val ID_METROPOLIS = 3
        const val ID_HALLS = 4
        private val CHAPTERS: SparseArray<String> = SparseArray<String>()
        fun showChapter(id: Int) {
            if (Dungeon.chapters.contains(id)) {
                return
            }
            val text: String = CHAPTERS.get(id)
            if (text != null) {
                val wnd = WndStory(text)
                if (0.6f.also { wnd.delay = it } > 0) {
                    wnd.tf.visible = false
                    wnd.chrome.visible = wnd.tf.visible
                    wnd.shadow.visible = wnd.chrome.visible
                }
                Game.scene().add(wnd)
                Dungeon.chapters.add(id)
            }
        }

        init {
            CHAPTERS.put(
                ID_SEWERS,
                "The Dungeon lies right beneath the City, its upper levels actually constitute the City's sewer system. " +
                        "Being nominally a part of the City, these levels are not that dangerous. No one will call it a safe place, " +
                        "but at least you won't need to deal with evil magic here."
            )
            CHAPTERS.put(
                ID_PRISON,
                "Many years ago an underground prison was built here for the most dangerous criminals. At the time it seemed " +
                        "like a very clever idea, because this place indeed was very hard to escape. But soon dark miasma started to permeate " +
                        "from below, driving prisoners and guards insane. In the end the prison was abandoned, though some convicts " +
                        "were left locked up here."
            )
            CHAPTERS.put(
                ID_CAVES,
                "The caves, which stretch down under the abandoned prison, are sparcely populated. They lie too deep to be exploited " +
                        "by the City and they are too poor in minerals to interest the dwarves. In the past there was a trade outpost " +
                        "somewhere here on the route between these two states, but it has perished since the decline of Dwarven Metropolis. " +
                        "Only omnipresent gnolls and subterranean animals dwell here now."
            )
            CHAPTERS.put(
                ID_METROPOLIS,
                "Dwarven Metropolis was once the greatest of dwarven city-states. In its heyday the mechanized army of dwarves " +
                        "has successfully repelled the invasion of the old god and his demon army. But it is said, that the returning warriors " +
                        "have brought seeds of corruption with them, and that victory was the beginning of the end for the underground kingdom."
            )
            CHAPTERS.put(
                ID_HALLS,
                """
                    In the past these levels were the outskirts of Metropolis. After the costly victory in the war with the old god dwarves were too weakened to clear them of remaining demons. Gradually demons have tightened their grip on this place and now it's called Demon Halls.
                    
                    Very few adventurers have ever descended this far...
                    """.trimIndent()
            )
        }
    }

    private val tf: BitmapTextMultiline
    private var delay = 0f
    fun update() {
        super.update()
        if (delay > 0 && Game.elapsed.let { delay -= it; delay } <= 0) {
            tf.visible = true
            chrome.visible = tf.visible
            shadow.visible = chrome.visible
        }
    }

    init {
        tf = PixelScene.createMultiline(text, 7)
        tf.maxWidth = WIDTH - MARGIN * 2
        tf.measure()
        tf.ra = bgR
        tf.ga = bgG
        tf.ba = bgB
        tf.rm = -bgR
        tf.gm = -bgG
        tf.bm = -bgB
        tf.x = MARGIN
        add(tf)
        add(object : TouchArea(chrome) {
            protected fun onClick(touch: Touch?) {
                hide()
            }
        })
        resize((tf.width() + MARGIN * 2) as Int, Math.min(tf.height(), 180))
    }
}