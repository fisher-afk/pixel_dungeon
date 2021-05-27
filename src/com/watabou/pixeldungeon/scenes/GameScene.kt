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
package com.watabou.pixeldungeon.scenes

import com.watabou.noosa.Camera

class GameScene : PixelScene() {
    private var water: SkinnedBlock? = null
    private var tiles: DungeonTilemap? = null
    private var fog: FogOfWar? = null
    private var hero: HeroSprite? = null
    private var log: GameLog? = null
    private var busy: BusyIndicator? = null
    private var terrain: Group? = null
    private var ripples: Group? = null
    private var plants: Group? = null
    private var heaps: Group? = null
    private var mobs: Group? = null
    private var emitters: Group? = null
    private var effects: Group? = null
    private var gases: Group? = null
    private var spells: Group? = null
    private var statuses: Group? = null
    private var emoicons: Group? = null
    private var toolbar: Toolbar? = null
    private var prompt: Toast? = null
    override fun create() {
        Music.INSTANCE.play(Assets.TUNE, true)
        Music.INSTANCE.volume(1f)
        PixelDungeon.lastClass(Dungeon.hero.heroClass.ordinal())
        super.create()
        Camera.main.zoom(defaultZoom + PixelDungeon.zoom())
        scene = this
        terrain = Group()
        add(terrain)
        water = SkinnedBlock(
            Level.WIDTH * DungeonTilemap.SIZE,
            Level.HEIGHT * DungeonTilemap.SIZE,
            Dungeon.level.waterTex()
        )
        terrain.add(water)
        ripples = Group()
        terrain.add(ripples)
        tiles = DungeonTilemap()
        terrain.add(tiles)
        Dungeon.level.addVisuals(this)
        plants = Group()
        add(plants)
        var size: Int = Dungeon.level.plants.size()
        for (i in 0 until size) {
            addPlantSprite(Dungeon.level.plants.valueAt(i))
        }
        heaps = Group()
        add(heaps)
        size = Dungeon.level.heaps.size()
        for (i in 0 until size) {
            addHeapSprite(Dungeon.level.heaps.valueAt(i))
        }
        emitters = Group()
        effects = Group()
        emoicons = Group()
        mobs = Group()
        add(mobs)
        for (mob in Dungeon.level.mobs) {
            addMobSprite(mob)
            if (Statistics.amuletObtained) {
                mob.beckon(Dungeon.hero.pos)
            }
        }
        add(emitters)
        add(effects)
        gases = Group()
        add(gases)
        for (blob in Dungeon.level.blobs.values()) {
            blob.emitter = null
            addBlobSprite(blob)
        }
        fog = FogOfWar(Level.WIDTH, Level.HEIGHT)
        fog.updateVisibility(Dungeon.visible, Dungeon.level.visited, Dungeon.level.mapped)
        add(fog)
        brightness(PixelDungeon.brightness())
        spells = Group()
        add(spells)
        statuses = Group()
        add(statuses)
        add(emoicons)
        hero = HeroSprite()
        hero.place(Dungeon.hero.pos)
        hero.updateArmor()
        mobs.add(hero)
        add(HealthIndicator())
        add(CellSelector(tiles).also { cellSelector = it })
        val sb = StatusPane()
        sb.camera = uiCamera
        sb.setSize(uiCamera.width, 0)
        add(sb)
        toolbar = Toolbar()
        toolbar.camera = uiCamera
        toolbar.setRect(0, uiCamera.height - toolbar.height(), uiCamera.width, toolbar.height())
        add(toolbar)
        val attack = AttackIndicator()
        attack.camera = uiCamera
        attack.setPos(
            uiCamera.width - attack.width(),
            toolbar.top() - attack.height()
        )
        add(attack)
        log = GameLog()
        log.camera = uiCamera
        log.setRect(0, toolbar.top(), attack.left(), 0)
        add(log)
        busy = BusyIndicator()
        busy.camera = uiCamera
        busy.x = 1
        busy.y = sb.bottom() + 1
        add(busy)
        when (InterlevelScene.mode) {
            RESURRECT -> {
                WandOfBlink.appear(Dungeon.hero, Dungeon.level.entrance)
                Flare(8, 32).color(0xFFFF66, true).show(hero, 2f)
            }
            RETURN -> WandOfBlink.appear(Dungeon.hero, Dungeon.hero.pos)
            FALL -> Chasm.heroLand()
            DESCEND -> {
                when (Dungeon.depth) {
                    1 -> WndStory.showChapter(WndStory.ID_SEWERS)
                    6 -> WndStory.showChapter(WndStory.ID_PRISON)
                    11 -> WndStory.showChapter(WndStory.ID_CAVES)
                    16 -> WndStory.showChapter(WndStory.ID_METROPOLIS)
                    22 -> WndStory.showChapter(WndStory.ID_HALLS)
                }
                if (Dungeon.hero.isAlive() && Dungeon.depth !== 22) {
                    Badges.validateNoKilling()
                }
            }
            else -> {
            }
        }
        val dropped: ArrayList<Item> = Dungeon.droppedItems.get(Dungeon.depth)
        if (dropped != null) {
            for (item in dropped) {
                val pos: Int = Dungeon.level.randomRespawnCell()
                if (item is Potion) {
                    (item as Potion).shatter(pos)
                } else if (item is Plant.Seed) {
                    Dungeon.level.plant(item as Plant.Seed, pos)
                } else {
                    Dungeon.level.drop(item, pos)
                }
            }
            Dungeon.droppedItems.remove(Dungeon.depth)
        }
        Camera.main.target = hero
        if (InterlevelScene.mode !== InterlevelScene.Mode.NONE) {
            if (Dungeon.depth < Statistics.deepestFloor) {
                GLog.h(TXT_WELCOME_BACK, Dungeon.depth)
            } else {
                GLog.h(TXT_WELCOME, Dungeon.depth)
                Sample.INSTANCE.play(Assets.SND_DESCEND)
            }
            when (Dungeon.level.feeling) {
                CHASM -> GLog.w(TXT_CHASM)
                WATER -> GLog.w(TXT_WATER)
                GRASS -> GLog.w(TXT_GRASS)
                else -> {
                }
            }
            if (Dungeon.level is RegularLevel &&
                (Dungeon.level as RegularLevel).secretDoors > Random.IntRange(3, 4)
            ) {
                GLog.w(TXT_SECRETS)
            }
            if (Dungeon.nightMode && !Dungeon.bossLevel()) {
                GLog.w(TXT_NIGHT_MODE)
            }
            InterlevelScene.mode = InterlevelScene.Mode.NONE
            fadeIn()
        }
    }

    override fun destroy() {
        scene = null
        Badges.saveGlobal()
        super.destroy()
    }

    @Synchronized
    fun pause() {
        try {
            Dungeon.saveAll()
            Badges.saveGlobal()
        } catch (e: IOException) {
            //
        }
    }

    @Synchronized
    fun update() {
        if (Dungeon.hero == null) {
            return
        }
        super.update()
        water.offset(0, -5 * Game.elapsed)
        Actor.process()
        if (Dungeon.hero.ready && !Dungeon.hero.paralysed) {
            log.newLine()
        }
        cellSelector!!.enabled = Dungeon.hero.ready
    }

    protected fun onBackPressed() {
        if (!cancel()) {
            add(WndGame())
        }
    }

    protected fun onMenuPressed() {
        if (Dungeon.hero.ready) {
            selectItem(null, WndBag.Mode.ALL, null)
        }
    }

    fun brightness(value: Boolean) {
        tiles.bm = if (value) 1.5f else 1.0f
        tiles.gm = tiles.bm
        tiles.rm = tiles.gm
        water.bm = tiles.rm
        water.gm = water.bm
        water.rm = water.gm
        if (value) {
            fog.am = +2f
            fog.aa = -1f
        } else {
            fog.am = +1f
            fog.aa = 0f
        }
    }

    private fun addHeapSprite(heap: Heap) {
        heap.sprite = heaps.recycle(ItemSprite::class.java) as ItemSprite
        val sprite: ItemSprite = heap.sprite
        sprite.revive()
        sprite.link(heap)
        heaps.add(sprite)
    }

    private fun addDiscardedSprite(heap: Heap) {
        heap.sprite = heaps.recycle(DiscardedItemSprite::class.java) as DiscardedItemSprite
        heap.sprite.revive()
        heap.sprite.link(heap)
        heaps.add(heap.sprite)
    }

    private fun addPlantSprite(plant: Plant) {
        (plants.recycle(PlantSprite::class.java) as PlantSprite?. also { plant.sprite = it }).reset(plant)
    }

    private fun addBlobSprite(gas: Blob) {
        if (gas.emitter == null) {
            gases.add(BlobEmitter(gas))
        }
    }

    private fun addMobSprite(mob: Mob) {
        val sprite: CharSprite = mob.sprite()
        sprite.visible = Dungeon.visible.get(mob.pos)
        mobs.add(sprite)
        sprite.link(mob)
    }

    private fun prompt(text: String?) {
        if (prompt != null) {
            prompt.killAndErase()
            prompt = null
        }
        if (text != null) {
            prompt = object : Toast(text) {
                protected fun onClose() {
                    cancel()
                }
            }
            prompt.camera = uiCamera
            prompt.setPos((uiCamera.width - prompt.width()) / 2, uiCamera.height - 60)
            add(prompt)
        }
    }

    private fun showBanner(banner: Banner) {
        banner.camera = uiCamera
        banner.x = align(uiCamera, (uiCamera.width - banner.width) / 2)
        banner.y = align(uiCamera, (uiCamera.height - banner.height) / 3)
        add(banner)
    }

    companion object {
        private const val TXT_WELCOME = "Welcome to the level %d of Pixel Dungeon!"
        private const val TXT_WELCOME_BACK = "Welcome back to the level %d of Pixel Dungeon!"
        private const val TXT_NIGHT_MODE = "Be cautious, since the dungeon is even more dangerous at night!"
        private const val TXT_CHASM = "Your steps echo across the dungeon."
        private const val TXT_WATER = "You hear the water splashing around you."
        private const val TXT_GRASS = "The smell of vegetation is thick in the air."
        private const val TXT_SECRETS = "The atmosphere hints that this floor hides many secrets."
        var scene: GameScene? = null
        private var cellSelector: CellSelector? = null

        // -------------------------------------------------------
        fun add(plant: Plant) {
            if (scene != null) {
                scene!!.addPlantSprite(plant)
            }
        }

        fun add(gas: Blob) {
            Actor.add(gas)
            if (scene != null) {
                scene!!.addBlobSprite(gas)
            }
        }

        fun add(heap: Heap) {
            if (scene != null) {
                scene!!.addHeapSprite(heap)
            }
        }

        fun discard(heap: Heap) {
            if (scene != null) {
                scene!!.addDiscardedSprite(heap)
            }
        }

        fun add(mob: Mob) {
            Dungeon.level.mobs.add(mob)
            Actor.add(mob)
            Actor.occupyCell(mob)
            scene!!.addMobSprite(mob)
        }

        fun add(mob: Mob, delay: Float) {
            Dungeon.level.mobs.add(mob)
            Actor.addDelayed(mob, delay)
            Actor.occupyCell(mob)
            scene!!.addMobSprite(mob)
        }

        fun add(icon: EmoIcon?) {
            scene!!.emoicons.add(icon)
        }

        fun effect(effect: Visual?) {
            scene!!.effects.add(effect)
        }

        fun ripple(pos: Int): Ripple {
            val ripple: Ripple = scene!!.ripples.recycle(Ripple::class.java) as Ripple
            ripple.reset(pos)
            return ripple
        }

        fun spellSprite(): SpellSprite {
            return scene!!.spells.recycle(SpellSprite::class.java) as SpellSprite
        }

        fun emitter(): Emitter? {
            return if (scene != null) {
                val emitter: Emitter = scene!!.emitters.recycle(
                    Emitter::class.java
                ) as Emitter
                emitter.revive()
                emitter
            } else {
                null
            }
        }

        fun status(): FloatingText? {
            return if (scene != null) scene!!.statuses.recycle(
                FloatingText::class.java
            ) as FloatingText else null
        }

        fun pickUp(item: Item?) {
            scene!!.toolbar.pickup(item)
        }

        fun updateMap() {
            if (scene != null) {
                scene!!.tiles.updated.set(0, 0, Level.WIDTH, Level.HEIGHT)
            }
        }

        fun updateMap(cell: Int) {
            if (scene != null) {
                scene!!.tiles.updated.union(cell % Level.WIDTH, cell / Level.WIDTH)
            }
        }

        fun discoverTile(pos: Int, oldValue: Int) {
            if (scene != null) {
                scene!!.tiles.discover(pos, oldValue)
            }
        }

        fun show(wnd: Window?) {
            cancelCellSelector()
            scene.add(wnd)
        }

        fun afterObserve() {
            if (scene != null) {
                scene!!.fog.updateVisibility(Dungeon.visible, Dungeon.level.visited, Dungeon.level.mapped)
                for (mob in Dungeon.level.mobs) {
                    mob.sprite.visible = Dungeon.visible.get(mob.pos)
                }
            }
        }

        fun flash(color: Int) {
            scene.fadeIn(-0x1000000 or color, true)
        }

        fun gameOver() {
            val gameOver = Banner(BannerSprites.get(BannerSprites.Type.GAME_OVER))
            gameOver.show(0x000000, 1f)
            scene!!.showBanner(gameOver)
            Sample.INSTANCE.play(Assets.SND_DEATH)
        }

        fun bossSlain() {
            if (Dungeon.hero.isAlive()) {
                val bossSlain = Banner(BannerSprites.get(BannerSprites.Type.BOSS_SLAIN))
                bossSlain.show(0xFFFFFF, 0.3f, 5f)
                scene!!.showBanner(bossSlain)
                Sample.INSTANCE.play(Assets.SND_BOSS)
            }
        }

        fun handleCell(cell: Int) {
            cellSelector!!.select(cell)
        }

        fun selectCell(listener: CellSelector.Listener) {
            cellSelector!!.listener = listener
            scene!!.prompt(listener.prompt())
        }

        private fun cancelCellSelector(): Boolean {
            return if (cellSelector!!.listener != null && cellSelector!!.listener !== defaultCellListener) {
                cellSelector!!.cancel()
                true
            } else {
                false
            }
        }

        fun selectItem(listener: WndBag.Listener?, mode: WndBag.Mode, title: String?): WndBag {
            cancelCellSelector()
            val wnd: WndBag = if (mode === Mode.SEED) WndBag.seedPouch(listener, mode, title) else WndBag.lastBag(
                listener,
                mode,
                title
            )
            scene.add(wnd)
            return wnd
        }

        fun cancel(): Boolean {
            return if (Dungeon.hero.curAction != null || Dungeon.hero.restoreHealth) {
                Dungeon.hero.curAction = null
                Dungeon.hero.restoreHealth = false
                true
            } else {
                cancelCellSelector()
            }
        }

        fun ready() {
            selectCell(defaultCellListener)
            QuickSlot.cancel()
        }

        private val defaultCellListener: CellSelector.Listener = object : Listener() {
            fun onSelect(cell: Int?) {
                if (Dungeon.hero.handle(cell)) {
                    Dungeon.hero.next()
                }
            }

            fun prompt(): String? {
                return null
            }
        }
    }
}