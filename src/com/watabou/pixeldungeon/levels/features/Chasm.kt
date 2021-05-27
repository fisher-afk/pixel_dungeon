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
package com.watabou.pixeldungeon.levels.features

import com.watabou.noosa.Camera

object Chasm {
    private const val TXT_CHASM = "Chasm"
    private const val TXT_YES = "Yes, I know what I'm doing"
    private const val TXT_NO = "No, I changed my mind"
    private const val TXT_JUMP = "Do you really want to jump into the chasm? You can probably die."
    var jumpConfirmed = false
    fun heroJump(hero: Hero) {
        GameScene.show(
            object : WndOptions(TXT_CHASM, TXT_JUMP, TXT_YES, TXT_NO) {
                protected fun onSelect(index: Int) {
                    if (index == 0) {
                        jumpConfirmed = true
                        hero.resume()
                    }
                }
            }
        )
    }

    fun heroFall(pos: Int) {
        jumpConfirmed = false
        Sample.INSTANCE.play(Assets.SND_FALLING)
        if (Dungeon.hero.isAlive()) {
            Dungeon.hero.interrupt()
            InterlevelScene.mode = InterlevelScene.Mode.FALL
            if (Dungeon.level is RegularLevel) {
                val room: Room = (Dungeon.level as RegularLevel).room(pos)
                InterlevelScene.fallIntoPit = room != null && room.type === Room.Type.WEAK_FLOOR
            } else {
                InterlevelScene.fallIntoPit = false
            }
            Game.switchScene(InterlevelScene::class.java)
        } else {
            Dungeon.hero.sprite.visible = false
        }
    }

    fun heroLand() {
        val hero: Hero = Dungeon.hero
        hero.sprite.burst(hero.sprite.blood(), 10)
        Camera.main.shake(4, 0.2f)
        Buff.prolong(hero, Cripple::class.java, Cripple.DURATION)
        hero.damage(Random.IntRange(hero.HT / 3, hero.HT / 2), object : Doom() {
            fun onDeath() {
                Badges.validateDeathFromFalling()
                Dungeon.fail(Utils.format(ResultDescriptions.FALL, Dungeon.depth))
                GLog.n("You fell to death...")
            }
        })
    }

    fun mobFall(mob: Mob) {
        mob.destroy()
        (mob.sprite as MobSprite).fall()
    }
}