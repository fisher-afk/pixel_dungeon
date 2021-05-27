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
package com.watabou.pixeldungeon.actors.blobs

import com.watabou.noosa.audio.Sample

class WaterOfAwareness : WellWater() {
    protected override fun affectHero(hero: Hero): Boolean {
        Sample.INSTANCE.play(Assets.SND_DRINK)
        emitter.parent.add(Identification(DungeonTilemap.tileCenterToWorld(pos)))
        hero.belongings.observe()
        for (i in 0 until Level.LENGTH) {
            val terr: Int = Dungeon.level.map.get(i)
            if (Terrain.flags.get(terr) and Terrain.SECRET !== 0) {
                Level.set(i, Terrain.discover(terr))
                GameScene.updateMap(i)
                if (Dungeon.visible.get(i)) {
                    GameScene.discoverTile(i, terr)
                }
            }
        }
        Buff.affect(hero, Awareness::class.java, Awareness.DURATION)
        Dungeon.observe()
        Dungeon.hero.interrupt()
        GLog.p(TXT_PROCCED)
        Journal.remove(Feature.WELL_OF_AWARENESS)
        return true
    }

    protected override fun affectItem(item: Item): Item? {
        return if (item.isIdentified()) {
            null
        } else {
            item.identify()
            Badges.validateItemLevelAquired(item)
            emitter.parent.add(Identification(DungeonTilemap.tileCenterToWorld(pos)))
            Journal.remove(Feature.WELL_OF_AWARENESS)
            item
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.QUESTION), 0.3f)
    }

    override fun tileDesc(): String {
        return "Power of knowledge radiates from the water of this well. " +
                "Take a sip from it to reveal all secrets of equipped items."
    }

    companion object {
        private const val TXT_PROCCED = "As you take a sip, you feel the knowledge pours into your mind. " +
                "Now you know everything about your equipped items. Also you sense " +
                "all items on the level and know all its secrets."
    }
}