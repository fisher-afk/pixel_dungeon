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

import com.watabou.utils.Bundle

class ItemStatusHandler<T : Item?> {
    private var items: Array<Class<out T>>
    private var images: HashMap<Class<out T>, Int>
    private var labels: HashMap<Class<out T>, String>
    private var known: HashSet<Class<out T>>

    constructor(items: Array<Class<out T>>, allLabels: Array<String?>, allImages: Array<Int?>) {
        this.items = items
        images = HashMap()
        labels = HashMap()
        known = HashSet()
        val labelsLeft: ArrayList<String> = ArrayList<String>(Arrays.asList<String>(*allLabels))
        val imagesLeft: ArrayList<Int> = ArrayList<Int>(Arrays.asList<Int>(*allImages))
        for (i in items.indices) {
            val item = items[i]
            val index: Int = Random.Int(labelsLeft.size)
            labels[item] = labelsLeft[index]
            labelsLeft.removeAt(index)
            images[item] = imagesLeft[index]
            imagesLeft.removeAt(index)
        }
    }

    constructor(items: Array<Class<out T>>, labels: Array<String>, images: Array<Int>, bundle: Bundle) {
        this.items = items
        this.images = HashMap()
        this.labels = HashMap()
        known = HashSet()
        restore(bundle, labels, images)
    }

    fun save(bundle: Bundle) {
        for (i in items.indices) {
            val itemName = items[i].toString()
            bundle.put(itemName + PFX_IMAGE, images[items[i]])
            bundle.put(itemName + PFX_LABEL, labels[items[i]])
            bundle.put(
                itemName + PFX_KNOWN, known.contains(
                    items[i]
                )
            )
        }
    }

    private fun restore(bundle: Bundle, allLabels: Array<String>, allImages: Array<Int>) {
        val labelsLeft: ArrayList<String> = ArrayList<String>(Arrays.asList<String>(*allLabels))
        val imagesLeft: ArrayList<Int> = ArrayList<Int>(Arrays.asList<Int>(*allImages))
        for (i in items.indices) {
            val item = items[i]
            val itemName = item.toString()
            if (bundle.contains(itemName + PFX_LABEL)) {
                val label: String = bundle.getString(itemName + PFX_LABEL)
                labels[item] = label
                labelsLeft.remove(label)
                val image: Int = bundle.getInt(itemName + PFX_IMAGE)
                images[item] = image
                imagesLeft.remove(image)
                if (bundle.getBoolean(itemName + PFX_KNOWN)) {
                    known.add(item)
                }
            } else {
                val index: Int = Random.Int(labelsLeft.size)
                labels[item] = labelsLeft[index]
                labelsLeft.removeAt(index)
                images[item] = imagesLeft[index]
                imagesLeft.removeAt(index)
            }
        }
    }

    fun image(item: T): Int {
        return images[item.getClass()]!!
    }

    fun label(item: T): String? {
        return labels[item.getClass()]
    }

    fun isKnown(item: T): Boolean {
        return known.contains(item.getClass())
    }

    fun know(item: T) {
        known.add(item.getClass() as Class<out T>)
        if (known.size == items.size - 1) {
            for (i in items.indices) {
                if (!known.contains(items[i])) {
                    known.add(items[i])
                    break
                }
            }
        }
    }

    fun known(): HashSet<Class<out T>> {
        return known
    }

    fun unknown(): HashSet<Class<out T>> {
        val result = HashSet<Class<out T>>()
        for (i in items) {
            if (!known.contains(i)) {
                result.add(i)
            }
        }
        return result
    }

    companion object {
        private const val PFX_IMAGE = "_image"
        private const val PFX_LABEL = "_label"
        private const val PFX_KNOWN = "_known"
    }
}