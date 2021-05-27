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

import com.watabou.input.Touchscreen.Touch

class StatusPane : Component() {
    private var shield: NinePatch? = null
    private var avatar: Image? = null
    private var blood: Emitter? = null
    private var lastTier = 0
    private var hp: Image? = null
    private var exp: Image? = null
    private var lastLvl = -1
    private var lastKeys = -1
    private var level: BitmapText? = null
    private var depth: BitmapText? = null
    private var keys: BitmapText? = null
    private var danger: DangerIndicator? = null
    private var loot: LootIndicator? = null
    private var resume: ResumeButton? = null
    private var buffs: BuffIndicator? = null
    private var compass: Compass? = null
    private var btnMenu: MenuButton? = null
    protected fun createChildren() {
        shield = NinePatch(Assets.STATUS, 80, 0, 30 + 18, 0)
        add(shield)
        add(object : TouchArea(0, 1, 30, 30) {
            protected fun onClick(touch: Touch?) {
                val sprite: Image = Dungeon.hero.sprite
                if (!sprite.isVisible()) {
                    Camera.main.focusOn(sprite)
                }
                GameScene.show(WndHero())
            }
        })
        btnMenu = MenuButton()
        add(btnMenu)
        avatar = HeroSprite.avatar(Dungeon.hero.heroClass, lastTier)
        add(avatar)
        blood = BitmaskEmitter(avatar)
        blood.pour(BloodParticle.FACTORY, 0.3f)
        blood.autoKill = false
        blood.on = false
        add(blood)
        compass = Compass(Dungeon.level.exit)
        add(compass)
        hp = Image(Assets.HP_BAR)
        add(hp)
        exp = Image(Assets.XP_BAR)
        add(exp)
        level = BitmapText(PixelScene.font1x)
        level.hardlight(0xFFEBA4)
        add(level)
        depth = BitmapText(Integer.toString(Dungeon.depth), PixelScene.font1x)
        depth.hardlight(0xCACFC2)
        depth.measure()
        add(depth)
        Dungeon.hero.belongings.countIronKeys()
        keys = BitmapText(PixelScene.font1x)
        keys.hardlight(0xCACFC2)
        add(keys)
        danger = DangerIndicator()
        add(danger)
        loot = LootIndicator()
        add(loot)
        resume = ResumeButton()
        add(resume)
        buffs = BuffIndicator(Dungeon.hero)
        add(buffs)
    }

    protected fun layout() {
        height = 32
        shield.size(width, shield.height)
        avatar.x = PixelScene.align(camera(), shield.x + 15 - avatar.width / 2)
        avatar.y = PixelScene.align(camera(), shield.y + 16 - avatar.height / 2)
        compass.x = avatar.x + avatar.width / 2 - compass.origin.x
        compass.y = avatar.y + avatar.height / 2 - compass.origin.y
        hp.x = 30
        hp.y = 3
        depth.x = width - 24 - depth.width() - 18
        depth.y = 6
        keys.y = 6
        layoutTags()
        buffs.setPos(32, 11)
        btnMenu.setPos(width - btnMenu.width(), 1)
    }

    private fun layoutTags() {
        var pos = 18f
        if (tagDanger) {
            danger.setPos(width - danger.width(), pos)
            pos = danger.bottom() + 1
        }
        if (tagLoot) {
            loot.setPos(width - loot.width(), pos)
            pos = loot.bottom() + 1
        }
        if (tagResume) {
            resume.setPos(width - resume.width(), pos)
        }
    }

    private var tagDanger = false
    private var tagLoot = false
    private var tagResume = false
    fun update() {
        super.update()
        if (tagDanger != danger.visible || tagLoot != loot.visible || tagResume != resume.visible) {
            tagDanger = danger.visible
            tagLoot = loot.visible
            tagResume = resume.visible
            layoutTags()
        }
        val health: Float = Dungeon.hero.HP as Float / Dungeon.hero.HT
        if (health == 0f) {
            avatar.tint(0x000000, 0.6f)
            blood.on = false
        } else if (health < 0.25f) {
            avatar.tint(0xcc0000, 0.4f)
            blood.on = true
        } else {
            avatar.resetColor()
            blood.on = false
        }
        hp.scale.x = health
        exp.scale.x = width / exp.width * Dungeon.hero.exp / Dungeon.hero.maxExp()
        if (Dungeon.hero.lvl !== lastLvl) {
            if (lastLvl != -1) {
                val emitter: Emitter = recycle(Emitter::class.java) as Emitter
                emitter.revive()
                emitter.pos(27, 27)
                emitter.burst(Speck.factory(Speck.STAR), 12)
            }
            lastLvl = Dungeon.hero.lvl
            level.text(Integer.toString(lastLvl))
            level.measure()
            level.x = PixelScene.align(27.5f - level.width() / 2)
            level.y = PixelScene.align(28.0f - level.baseLine() / 2)
        }
        val k: Int = IronKey.curDepthQuantity
        if (k != lastKeys) {
            lastKeys = k
            keys.text(Integer.toString(lastKeys))
            keys.measure()
            keys.x = width - 8 - keys.width() - 18
        }
        val tier: Int = Dungeon.hero.tier()
        if (tier != lastTier) {
            lastTier = tier
            avatar.copy(HeroSprite.avatar(Dungeon.hero.heroClass, tier))
        }
    }

    private class MenuButton : Button() {
        private var image: Image? = null
        protected fun createChildren() {
            super.createChildren()
            image = Image(Assets.STATUS, 114, 3, 12, 11)
            add(image)
        }

        protected fun layout() {
            super.layout()
            image.x = x + 2
            image.y = y + 2
        }

        protected fun onTouchDown() {
            image.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK)
        }

        protected fun onTouchUp() {
            image.resetColor()
        }

        protected fun onClick() {
            GameScene.show(WndGame())
        }

        init {
            width = image.width + 4
            height = image.height + 4
        }
    }
}