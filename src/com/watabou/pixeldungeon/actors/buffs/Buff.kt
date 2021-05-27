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
package com.watabou.pixeldungeon.actors.buffs

import com.watabou.pixeldungeon.actors.Actor

class Buff : Actor() {
    var target: Char? = null
    fun attachTo(target: Char): Boolean {
        if (target.immunities().contains(javaClass)) {
            return false
        }
        this.target = target
        target.add(this)
        return true
    }

    fun detach() {
        target.remove(this)
    }

    override fun act(): Boolean {
        diactivate()
        return true
    }

    fun icon(): Int {
        return BuffIndicator.NONE
    }

    companion object {
        fun <T : Buff?> append(target: Char, buffClass: Class<T>): T? {
            return try {
                val buff = buffClass.newInstance()
                buff.attachTo(target)
                buff
            } catch (e: Exception) {
                null
            }
        }

        fun <T : FlavourBuff?> append(target: Char, buffClass: Class<T>, duration: Float): T {
            val buff: T = append(target, buffClass)
            buff!!.spend(duration)
            return buff
        }

        fun <T : Buff?> affect(target: Char, buffClass: Class<T>): T {
            val buff: T = target.buff(buffClass)
            return buff ?: append(target, buffClass)
        }

        fun <T : FlavourBuff?> affect(target: Char, buffClass: Class<T>, duration: Float): T {
            val buff = affect(target, buffClass)
            buff!!.spend(duration)
            return buff
        }

        fun <T : FlavourBuff?> prolong(target: Char, buffClass: Class<T>, duration: Float): T {
            val buff = affect(target, buffClass)
            buff!!.postpone(duration)
            return buff
        }

        fun detach(buff: Buff?) {
            buff?.detach()
        }

        fun detach(target: Char, cl: Class<out Buff?>?) {
            detach(target.buff(cl))
        }
    }
}