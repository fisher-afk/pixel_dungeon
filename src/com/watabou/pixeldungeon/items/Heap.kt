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
package com.watabou.pixeldungeon.items

import com.watabou.noosa.audio.Sample

class Heap : Bundlable {
    enum class Type {
        HEAP, FOR_SALE, CHEST, LOCKED_CHEST, CRYSTAL_CHEST, TOMB, SKELETON, MIMIC, HIDDEN
    }

    var type = Type.HEAP
    var pos = 0
    var sprite: ItemSprite? = null
    var items: LinkedList<Item>? = LinkedList<Item>()
    fun image(): Int {
        return when (type) {
            Type.HEAP, Type.FOR_SALE -> if (size() > 0) items.peek()
                .image() else 0
            Type.CHEST, Type.MIMIC -> ItemSpriteSheet.CHEST
            Type.LOCKED_CHEST -> ItemSpriteSheet.LOCKED_CHEST
            Type.CRYSTAL_CHEST -> ItemSpriteSheet.CRYSTAL_CHEST
            Type.TOMB -> ItemSpriteSheet.TOMB
            Type.SKELETON -> ItemSpriteSheet.BONES
            Type.HIDDEN -> ItemSpriteSheet.HIDDEN
            else -> 0
        }
    }

    fun glowing(): ItemSprite.Glowing? {
        return if ((type == Type.HEAP || type == Type.FOR_SALE) && items.size > 0) items.peek().glowing() else null
    }

    fun open(hero: Hero) {
        when (type) {
            Type.MIMIC -> if (Mimic.spawnAt(pos, items) != null) {
                GLog.n(TXT_MIMIC)
                destroy()
            } else {
                type = Type.CHEST
            }
            Type.TOMB -> Wraith.spawnAround(hero.pos)
            Type.SKELETON -> {
                CellEmitter.center(pos).start(Speck.factory(Speck.RATTLE), 0.1f, 3)
                for (item in items) {
                    if (item.cursed) {
                        if (Wraith.spawnAt(pos) == null) {
                            hero.sprite.emitter().burst(ShadowParticle.CURSE, 6)
                            hero.damage(hero.HP / 2, this)
                        }
                        Sample.INSTANCE.play(Assets.SND_CURSED)
                        break
                    }
                }
            }
            Type.HIDDEN -> {
                sprite.alpha(0)
                sprite.parent.add(AlphaTweener(sprite, 1, FADE_TIME))
            }
            else -> {
            }
        }
        if (type != Type.MIMIC) {
            type = Type.HEAP
            sprite.link()
            sprite.drop()
        }
    }

    fun size(): Int {
        return items.size
    }

    fun pickUp(): Item {
        val item: Item = items.removeFirst()
        if (items.isEmpty()) {
            destroy()
        } else if (sprite != null) {
            sprite.view(image(), glowing())
        }
        return item
    }

    fun peek(): Item {
        return items.peek()
    }

    fun drop(item: Item) {
        var item: Item = item
        if (item.stackable) {
            val c: Class<*> = item.getClass()
            for (i in items) {
                if (i.getClass() === c) {
                    i.quantity += item.quantity
                    item = i
                    break
                }
            }
            items.remove(item)
        }
        if (item is Dewdrop) {
            items.add(item)
        } else {
            items.addFirst(item)
        }
        if (sprite != null) {
            sprite.view(image(), glowing())
        }
    }

    fun replace(a: Item?, b: Item?) {
        val index: Int = items.indexOf(a)
        if (index != -1) {
            items.removeAt(index)
            items.add(index, b)
        }
    }

    fun burn() {
        if (type == Type.MIMIC) {
            val m: Mimic = Mimic.spawnAt(pos, items)
            if (m != null) {
                Buff.affect(m, Burning::class.java).reignite(m)
                m.sprite.emitter().burst(FlameParticle.FACTORY, 5)
                destroy()
            }
        }
        if (type != Type.HEAP) {
            return
        }
        var burnt = false
        var evaporated = false
        for (item in items.toTypedArray()) {
            if (item is Scroll) {
                items.remove(item)
                burnt = true
            } else if (item is Dewdrop) {
                items.remove(item)
                evaporated = true
            } else if (item is MysteryMeat) {
                replace(item, ChargrilledMeat.cook(item as MysteryMeat))
                burnt = true
            }
        }
        if (burnt || evaporated) {
            if (Dungeon.visible.get(pos)) {
                if (burnt) {
                    burnFX(pos)
                } else {
                    evaporateFX(pos)
                }
            }
            if (isEmpty) {
                destroy()
            } else if (sprite != null) {
                sprite.view(image(), glowing())
            }
        }
    }

    fun freeze() {
        if (type == Type.MIMIC) {
            val m: Mimic = Mimic.spawnAt(pos, items)
            if (m != null) {
                Buff.prolong(m, Frost::class.java, Frost.duration(m) * Random.Float(1.0f, 1.5f))
                destroy()
            }
        }
        if (type != Type.HEAP) {
            return
        }
        var frozen = false
        for (item in items.toTypedArray()) {
            if (item is MysteryMeat) {
                replace(item, FrozenCarpaccio.cook(item as MysteryMeat))
                frozen = true
            }
        }
        if (frozen) {
            if (isEmpty) {
                destroy()
            } else if (sprite != null) {
                sprite.view(image(), glowing())
            }
        }
    }

    fun transmute(): Item? {
        CellEmitter.get(pos).burst(Speck.factory(Speck.BUBBLE), 3)
        Splash.at(pos, 0xFFFFFF, 3)
        val chances = FloatArray(items.size)
        var count = 0
        var index = 0
        for (item in items) {
            if (item is Seed) {
                count += item.quantity
                chances[index++] = item.quantity.toFloat()
            } else {
                count = 0
                break
            }
        }
        return if (count >= SEEDS_TO_POTION) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6)
            Sample.INSTANCE.play(Assets.SND_PUFF)
            if (Random.Int(count) === 0) {
                CellEmitter.center(pos).burst(Speck.factory(Speck.EVOKE), 3)
                destroy()
                Statistics.potionsCooked++
                Badges.validatePotionsCooked()
                Generator.random(Generator.Category.POTION)
            } else {
                val proto: Seed = items.get(Random.chances(chances)) as Seed
                val itemClass: Class<out Item?> = proto.alchemyClass
                destroy()
                Statistics.potionsCooked++
                Badges.validatePotionsCooked()
                if (itemClass == null) {
                    Generator.random(Generator.Category.POTION)
                } else {
                    try {
                        itemClass.newInstance()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } else {
            null
        }
    }

    val isEmpty: Boolean
        get() = items == null || items.size == 0

    fun destroy() {
        Dungeon.level.heaps.remove(pos)
        if (sprite != null) {
            sprite.kill()
        }
        items.clear()
        items = null
    }

    fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
        type = Type.valueOf(bundle.getString(TYPE))
        items = LinkedList<Item>(bundle.getCollection(ITEMS) as Collection<Item?>)
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
        bundle.put(TYPE, type.toString())
        bundle.put(ITEMS, items)
    }

    companion object {
        private const val TXT_MIMIC = "This is a mimic!"
        private const val SEEDS_TO_POTION = 3
        private const val FADE_TIME = 0.6f
        fun burnFX(pos: Int) {
            CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
            Sample.INSTANCE.play(Assets.SND_BURNING)
        }

        fun evaporateFX(pos: Int) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 5)
        }

        private const val POS = "pos"
        private const val TYPE = "type"
        private const val ITEMS = "items"
    }
}