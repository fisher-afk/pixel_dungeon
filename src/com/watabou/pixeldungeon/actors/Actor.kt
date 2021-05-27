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
package com.watabou.pixeldungeon.actors

import com.watabou.pixeldungeon.Dungeon

abstract class Actor : Bundlable {
    private var time = 0f
    private var id = 0
    protected abstract fun act(): Boolean
    protected fun spend(time: Float) {
        this.time += time
    }

    protected fun postpone(time: Float) {
        if (this.time < now + time) {
            this.time = now + time
        }
    }

    protected fun cooldown(): Float {
        return time - now
    }

    protected fun diactivate() {
        time = Float.MAX_VALUE
    }

    protected fun onAdd() {}
    protected fun onRemove() {}
    fun storeInBundle(bundle: Bundle) {
        bundle.put(TIME, time)
        bundle.put(ID, id)
    }

    fun restoreFromBundle(bundle: Bundle) {
        time = bundle.getFloat(TIME)
        id = bundle.getInt(ID)
    }

    fun id(): Int {
        return if (id > 0) {
            id
        } else {
            var max = 0
            for (a in all) {
                if (a.id > max) {
                    max = a.id
                }
            }
            max + 1.also { id = it }
        }
    }

    /*protected*/
    operator fun next() {
        if (current === this) {
            current = null
        }
    }

    companion object {
        const val TICK = 1f
        private const val TIME = "time"
        private const val ID = "id"

        // **********************
        // *** Static members ***
        private val all = HashSet<Actor>()
        private var current: Actor? = null
        private val ids: SparseArray<Actor> = SparseArray<Actor>()
        private var now = 0f
        private val chars = arrayOfNulls<Char>(Level.LENGTH)
        fun clear() {
            now = 0f
            Arrays.fill(chars, null)
            all.clear()
            ids.clear()
        }

        fun fixTime() {
            if (Dungeon.hero != null && all.contains(Dungeon.hero)) {
                Statistics.duration += now
            }
            var min = Float.MAX_VALUE
            for (a in all) {
                if (a.time < min) {
                    min = a.time
                }
            }
            for (a in all) {
                a.time -= min
            }
            now = 0f
        }

        fun init() {
            addDelayed(Dungeon.hero, -Float.MIN_VALUE)
            for (mob in Dungeon.level.mobs) {
                add(mob)
            }
            for (blob in Dungeon.level.blobs.values()) {
                add(blob)
            }
            current = null
        }

        fun occupyCell(ch: Char) {
            chars[ch.pos] = ch
        }

        fun freeCell(pos: Int) {
            chars[pos] = null
        }

        fun process() {
            if (current != null) {
                return
            }
            var doNext: Boolean
            do {
                now = Float.MAX_VALUE
                current = null
                Arrays.fill(chars, null)
                for (actor in all) {
                    if (actor.time < now) {
                        now = actor.time
                        current = actor
                    }
                    if (actor is Char) {
                        val ch = actor
                        chars[ch.pos] = ch
                    }
                }
                if (current != null) {
                    if (current is Char && (current as Char?)!!.sprite.isMoving) {
                        // If it's character's turn to act, but its sprite 
                        // is moving, wait till the movement is over
                        current = null
                        break
                    }
                    doNext = current.act()
                    if (doNext && !Dungeon.hero.isAlive()) {
                        doNext = false
                        current = null
                    }
                } else {
                    doNext = false
                }
            } while (doNext)
        }

        fun add(actor: Actor) {
            add(actor, now)
        }

        fun addDelayed(actor: Actor, delay: Float) {
            add(actor, now + delay)
        }

        private fun add(actor: Actor, time: Float) {
            if (all.contains(actor)) {
                return
            }
            if (actor.id > 0) {
                ids.put(actor.id, actor)
            }
            all.add(actor)
            actor.time += time
            actor.onAdd()
            if (actor is Char) {
                val ch = actor
                chars[ch.pos] = ch
                for (buff in ch.buffs()) {
                    all.add(buff)
                    buff.onAdd()
                }
            }
        }

        fun remove(actor: Actor?) {
            if (actor != null) {
                all.remove(actor)
                actor.onRemove()
                if (actor.id > 0) {
                    ids.remove(actor.id)
                }
            }
        }

        fun findChar(pos: Int): Char? {
            return chars[pos]
        }

        fun findById(id: Int): Actor {
            return ids.get(id)
        }

        fun all(): HashSet<Actor> {
            return all
        }
    }
}