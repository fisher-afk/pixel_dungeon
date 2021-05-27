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
package com.watabou.pixeldungeon.actors.hero

import com.watabou.noosa.Camera

class Hero : Char() {
    var heroClass = HeroClass.ROGUE
    var subClass: HeroSubClass? = HeroSubClass.NONE
    private var attackSkill = 10
    private var defenseSkill = 5
    var ready = false
    var curAction: HeroAction? = null
    var lastAction: HeroAction? = null
    private var enemy: Char? = null
    var killerGlyph: Armor.Glyph? = null
    private var theKey: Item? = null
    var restoreHealth = false
    var rangedWeapon: MissileWeapon? = null
    var belongings: Belongings
    var STR: Int
    var weakened = false
    var awareness: Float
    var lvl = 1
    var exp = 0
    private var visibleEnemies: ArrayList<Mob>
    fun STR(): Int {
        return if (weakened) STR - 2 else STR
    }

    fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        heroClass.storeInBundle(bundle)
        subClass!!.storeInBundle(bundle)
        bundle.put(ATTACK, attackSkill)
        bundle.put(DEFENSE, defenseSkill)
        bundle.put(STRENGTH, STR)
        bundle.put(LEVEL, lvl)
        bundle.put(EXPERIENCE, exp)
        belongings.storeInBundle(bundle)
    }

    fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        heroClass = HeroClass.restoreInBundle(bundle)
        subClass = HeroSubClass.restoreInBundle(bundle)
        attackSkill = bundle.getInt(ATTACK)
        defenseSkill = bundle.getInt(DEFENSE)
        STR = bundle.getInt(STRENGTH)
        updateAwareness()
        lvl = bundle.getInt(LEVEL)
        exp = bundle.getInt(EXPERIENCE)
        belongings.restoreFromBundle(bundle)
    }

    fun className(): String {
        return if (subClass == null || subClass === HeroSubClass.NONE) heroClass.title() else subClass.title()!!
    }

    fun live() {
        Buff.affect(this, Regeneration::class.java)
        Buff.affect(this, Hunger::class.java)
    }

    fun tier(): Int {
        return if (belongings.armor == null) 0 else belongings.armor.tier
    }

    fun shoot(enemy: Char?, wep: MissileWeapon?): Boolean {
        rangedWeapon = wep
        val result: Boolean = attack(enemy)
        rangedWeapon = null
        return result
    }

    fun attackSkill(target: Char): Int {
        var bonus = 0
        for (buff in buffs(RingOfAccuracy.Accuracy::class.java)) {
            bonus += (buff as RingOfAccuracy.Accuracy).level
        }
        var accuracy: Float = if (bonus == 0) 1 else Math.pow(1.4, bonus.toDouble()).toFloat()
        if (rangedWeapon != null && Level.distance(pos, target.pos) === 1) {
            accuracy *= 0.5f
        }
        val wep: KindOfWeapon = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        return if (wep != null) {
            (attackSkill * accuracy * wep.acuracyFactor(this)) as Int
        } else {
            (attackSkill * accuracy).toInt()
        }
    }

    fun defenseSkill(enemy: Char?): Int {
        var bonus = 0
        for (buff in buffs(RingOfEvasion.Evasion::class.java)) {
            bonus += (buff as RingOfEvasion.Evasion).level
        }
        var evasion: Float = if (bonus == 0) 1 else Math.pow(1.2, bonus.toDouble()).toFloat()
        if (paralysed) {
            evasion /= 2f
        }
        val aEnc = if (belongings.armor != null) belongings.armor.STR - STR() else 0
        return if (aEnc > 0) {
            (defenseSkill * evasion / Math.pow(1.5, aEnc.toDouble())).toInt()
        } else {
            if (heroClass === HeroClass.ROGUE) {
                if (curAction != null && subClass === HeroSubClass.FREERUNNER && !isStarving) {
                    evasion *= 2f
                }
                ((defenseSkill - aEnc) * evasion).toInt()
            } else {
                (defenseSkill * evasion).toInt()
            }
        }
    }

    fun dr(): Int {
        var dr = if (belongings.armor != null) Math.max(belongings.armor.DR(), 0) else 0
        val barkskin: Barkskin = buff(Barkskin::class.java)
        if (barkskin != null) {
            dr += barkskin.level()
        }
        return dr
    }

    fun damageRoll(): Int {
        val wep: KindOfWeapon = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        val dmg: Int
        dmg = if (wep != null) {
            wep.damageRoll(this)
        } else {
            if (STR() > 10) Random.IntRange(1, STR() - 9) else 1
        }
        return if (buff(Fury::class.java) != null) (dmg * 1.5f).toInt() else dmg
    }

    fun speed(): Float {
        val aEnc = if (belongings.armor != null) belongings.armor.STR - STR() else 0
        return if (aEnc > 0) {
            (super.speed() * Math.pow(1.3, -aEnc.toDouble()))
        } else {
            val speed: Float = super.speed()
            if ((sprite as HeroSprite).sprint(subClass === HeroSubClass.FREERUNNER && !isStarving)) 1.6f * speed else speed
        }
    }

    fun attackDelay(): Float {
        val wep: KindOfWeapon = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        return if (wep != null) {
            wep.speedFactor(this)
        } else {
            1f
        }
    }

    fun spend(time: Float) {
        var hasteLevel = 0
        for (buff in buffs(RingOfHaste.Haste::class.java)) {
            hasteLevel += (buff as RingOfHaste.Haste).level
        }
        super.spend(if (hasteLevel == 0) time else (time * Math.pow(1.1, -hasteLevel.toDouble())).toFloat())
    }

    fun spendAndNext(time: Float) {
        busy()
        spend(time)
        next()
    }

    fun act(): Boolean {
        super.act()
        if (paralysed) {
            curAction = null
            spendAndNext(TICK)
            return false
        }
        checkVisibleMobs()
        AttackIndicator.updateState()
        if (curAction == null) {
            if (restoreHealth) {
                restoreHealth = if (isStarving || HP >= HT) {
                    false
                } else {
                    spend(TIME_TO_REST)
                    next()
                    return false
                }
            }
            ready()
            return false
        } else {
            restoreHealth = false
            ready = false
            if (curAction is HeroAction.Move) {
                return actMove(curAction as HeroAction.Move)
            } else if (curAction is HeroAction.Interact) {
                return actInteract(curAction as HeroAction.Interact)
            } else if (curAction is HeroAction.Buy) {
                return actBuy(curAction as HeroAction.Buy)
            } else if (curAction is HeroAction.PickUp) {
                return actPickUp(curAction as HeroAction.PickUp)
            } else if (curAction is HeroAction.OpenChest) {
                return actOpenChest(curAction as HeroAction.OpenChest)
            } else if (curAction is HeroAction.Unlock) {
                return actUnlock(curAction as HeroAction.Unlock)
            } else if (curAction is HeroAction.Descend) {
                return actDescend(curAction as HeroAction.Descend)
            } else if (curAction is HeroAction.Ascend) {
                return actAscend(curAction as HeroAction.Ascend)
            } else if (curAction is HeroAction.Attack) {
                return actAttack(curAction as HeroAction.Attack)
            } else if (curAction is HeroAction.Cook) {
                return actCook(curAction as HeroAction.Cook)
            }
        }
        return false
    }

    fun busy() {
        ready = false
    }

    private fun ready() {
        sprite.idle()
        curAction = null
        ready = true
        GameScene.ready()
    }

    fun interrupt() {
        if (isAlive() && curAction != null && curAction.dst !== pos) {
            lastAction = curAction
        }
        curAction = null
    }

    fun resume() {
        curAction = lastAction
        lastAction = null
        act()
    }

    private fun actMove(action: HeroAction.Move): Boolean {
        return if (getCloser(action.dst)) {
            true
        } else {
            if (Dungeon.level.map.get(pos) === Terrain.SIGN) {
                Sign.read(pos)
            }
            ready()
            false
        }
    }

    private fun actInteract(action: HeroAction.Interact): Boolean {
        val npc: NPC = action.npc
        return if (Level.adjacent(pos, npc.pos)) {
            ready()
            sprite.turnTo(pos, npc.pos)
            npc.interact()
            false
        } else {
            if (Level.fieldOfView.get(npc.pos) && getCloser(npc.pos)) {
                true
            } else {
                ready()
                false
            }
        }
    }

    private fun actBuy(action: HeroAction.Buy): Boolean {
        val dst: Int = action.dst
        return if (pos === dst || Level.adjacent(pos, dst)) {
            ready()
            val heap: Heap = Dungeon.level.heaps.get(dst)
            if (heap != null && heap.type === Type.FOR_SALE && heap.size() === 1) {
                GameScene.show(WndTradeItem(heap, true))
            }
            false
        } else if (getCloser(dst)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actCook(action: HeroAction.Cook): Boolean {
        val dst: Int = action.dst
        return if (Dungeon.visible.get(dst)) {
            ready()
            AlchemyPot.operate(this, dst)
            false
        } else if (getCloser(dst)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actPickUp(action: HeroAction.PickUp): Boolean {
        val dst: Int = action.dst
        return if (pos === dst) {
            val heap: Heap = Dungeon.level.heaps.get(pos)
            if (heap != null) {
                val item: Item = heap.pickUp()
                if (item.doPickUp(this)) {
                    if (item is Dewdrop) {
                        // Do nothing
                    } else {
                        val important =
                            (item is ScrollOfUpgrade || item is ScrollOfEnchantment) && (item as Scroll).isKnown() ||
                                    (item is PotionOfStrength || item is PotionOfMight) && (item as Potion).isKnown()
                        if (important) {
                            GLog.p(TXT_YOU_NOW_HAVE, item.name())
                        } else {
                            GLog.i(TXT_YOU_NOW_HAVE, item.name())
                        }
                    }
                    if (!heap.isEmpty()) {
                        GLog.i(TXT_SOMETHING_ELSE)
                    }
                    curAction = null
                } else {
                    Dungeon.level.drop(item, pos).sprite.drop()
                    ready()
                }
            } else {
                ready()
            }
            false
        } else if (getCloser(dst)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actOpenChest(action: HeroAction.OpenChest): Boolean {
        val dst: Int = action.dst
        return if (Level.adjacent(pos, dst) || pos === dst) {
            val heap: Heap = Dungeon.level.heaps.get(dst)
            if (heap != null && heap.type !== Type.HEAP && heap.type !== Type.FOR_SALE) {
                theKey = null
                if (heap.type === Type.LOCKED_CHEST || heap.type === Type.CRYSTAL_CHEST) {
                    theKey = belongings.getKey(GoldenKey::class.java, Dungeon.depth)
                    if (theKey == null) {
                        GLog.w(TXT_LOCKED_CHEST)
                        ready()
                        return false
                    }
                }
                when (heap.type) {
                    TOMB -> {
                        Sample.INSTANCE.play(Assets.SND_TOMB)
                        Camera.main.shake(1, 0.5f)
                    }
                    SKELETON -> {
                    }
                    else -> Sample.INSTANCE.play(Assets.SND_UNLOCK)
                }
                spend(Key.TIME_TO_UNLOCK)
                sprite.operate(dst)
            } else {
                ready()
            }
            false
        } else if (getCloser(dst)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actUnlock(action: HeroAction.Unlock): Boolean {
        val doorCell: Int = action.dst
        return if (Level.adjacent(pos, doorCell)) {
            theKey = null
            val door: Int = Dungeon.level.map.get(doorCell)
            if (door == Terrain.LOCKED_DOOR) {
                theKey = belongings.getKey(IronKey::class.java, Dungeon.depth)
            } else if (door == Terrain.LOCKED_EXIT) {
                theKey = belongings.getKey(SkeletonKey::class.java, Dungeon.depth)
            }
            if (theKey != null) {
                spend(Key.TIME_TO_UNLOCK)
                sprite.operate(doorCell)
                Sample.INSTANCE.play(Assets.SND_UNLOCK)
            } else {
                GLog.w(TXT_LOCKED_DOOR)
                ready()
            }
            false
        } else if (getCloser(doorCell)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actDescend(action: HeroAction.Descend): Boolean {
        val stairs: Int = action.dst
        return if (pos === stairs && pos === Dungeon.level.exit) {
            curAction = null
            val hunger: Hunger = buff(Hunger::class.java)
            if (hunger != null && !hunger.isStarving()) {
                hunger.satisfy(-Hunger.STARVING / 10)
            }
            InterlevelScene.mode = InterlevelScene.Mode.DESCEND
            Game.switchScene(InterlevelScene::class.java)
            false
        } else if (getCloser(stairs)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actAscend(action: HeroAction.Ascend): Boolean {
        val stairs: Int = action.dst
        return if (pos === stairs && pos === Dungeon.level.entrance) {
            if (Dungeon.depth === 1) {
                if (belongings.getItem(Amulet::class.java) == null) {
                    GameScene.show(WndMessage(TXT_LEAVE))
                    ready()
                } else {
                    Dungeon.win(ResultDescriptions.WIN)
                    Dungeon.deleteGame(Dungeon.hero.heroClass, true)
                    Game.switchScene(SurfaceScene::class.java)
                }
            } else {
                curAction = null
                val hunger: Hunger = buff(Hunger::class.java)
                if (hunger != null && !hunger.isStarving()) {
                    hunger.satisfy(-Hunger.STARVING / 10)
                }
                InterlevelScene.mode = InterlevelScene.Mode.ASCEND
                Game.switchScene(InterlevelScene::class.java)
            }
            false
        } else if (getCloser(stairs)) {
            true
        } else {
            ready()
            false
        }
    }

    private fun actAttack(action: HeroAction.Attack): Boolean {
        enemy = action.target
        return if (Level.adjacent(pos, enemy.pos) && enemy.isAlive() && !isCharmedBy(enemy)) {
            spend(attackDelay())
            sprite.attack(enemy.pos)
            false
        } else {
            if (Level.fieldOfView.get(enemy.pos) && getCloser(enemy.pos)) {
                true
            } else {
                ready()
                false
            }
        }
    }

    fun rest(tillHealthy: Boolean) {
        spendAndNext(TIME_TO_REST)
        if (!tillHealthy) {
            sprite.showStatus(CharSprite.DEFAULT, TXT_WAIT)
        }
        restoreHealth = tillHealthy
    }

    fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        val wep: KindOfWeapon = if (rangedWeapon != null) rangedWeapon else belongings.weapon
        if (wep != null) {
            wep.proc(this, enemy, damage)
            when (subClass) {
                GLADIATOR -> if (wep is MeleeWeapon) {
                    damage += Buff.affect(this, Combo::class.java).hit(enemy, damage)
                }
                BATTLEMAGE -> {
                    if (wep is Wand) {
                        val wand: Wand = wep as Wand
                        if (wand.curCharges >= wand.maxCharges) {
                            wand.use()
                        } else if (damage > 0) {
                            wand.curCharges++
                            wand.updateQuickslot()
                            ScrollOfRecharging.charge(this)
                        }
                        damage += wand.curCharges
                    }
                    if (rangedWeapon != null) {
                        Buff.prolong(this, SnipersMark::class.java, attackDelay() * 1.1f).`object` = enemy.id()
                    }
                }
                SNIPER -> if (rangedWeapon != null) {
                    Buff.prolong(this, SnipersMark::class.java, attackDelay() * 1.1f).`object` = enemy.id()
                }
                else -> {
                }
            }
        }
        return damage
    }

    fun defenseProc(enemy: Char, damage: Int): Int {
        var damage = damage
        val thorns: RingOfThorns.Thorns = buff(RingOfThorns.Thorns::class.java)
        if (thorns != null) {
            val dmg: Int = Random.IntRange(0, damage)
            if (dmg > 0) {
                enemy.damage(dmg, thorns)
            }
        }
        val armor: Earthroot.Armor = buff(Earthroot.Armor::class.java)
        if (armor != null) {
            damage = armor.absorb(damage)
        }
        if (belongings.armor != null) {
            damage = belongings.armor.proc(enemy, this, damage)
        }
        return damage
    }

    fun damage(dmg: Int, src: Any?) {
        restoreHealth = false
        super.damage(dmg, src)
        if (subClass === HeroSubClass.BERSERKER && 0 < HP && HP <= HT * Fury.LEVEL) {
            Buff.affect(this, Fury::class.java)
        }
    }

    private fun checkVisibleMobs() {
        val visible: ArrayList<Mob> = ArrayList<Mob>()
        var newMob = false
        for (m in Dungeon.level.mobs) {
            if (Level.fieldOfView.get(m.pos) && m.hostile) {
                visible.add(m)
                if (!visibleEnemies.contains(m)) {
                    newMob = true
                }
            }
        }
        if (newMob) {
            interrupt()
            restoreHealth = false
        }
        visibleEnemies = visible
    }

    fun visibleEnemies(): Int {
        return visibleEnemies.size
    }

    fun visibleEnemy(index: Int): Mob {
        return visibleEnemies[index % visibleEnemies.size]
    }

    private fun getCloser(target: Int): Boolean {
        if (rooted) {
            Camera.main.shake(1, 1f)
            return false
        }
        var step = -1
        if (Level.adjacent(pos, target)) {
            if (Actor.findChar(target) == null) {
                if (Level.pit.get(target) && !flying && !Chasm.jumpConfirmed) {
                    Chasm.heroJump(this)
                    interrupt()
                    return false
                }
                if (Level.passable.get(target) || Level.avoid.get(target)) {
                    step = target
                }
            }
        } else {
            val len: Int = Level.LENGTH
            val p: BooleanArray = Level.passable
            val v: BooleanArray = Dungeon.level.visited
            val m: BooleanArray = Dungeon.level.mapped
            val passable = BooleanArray(len)
            for (i in 0 until len) {
                passable[i] = p[i] && (v[i] || m[i])
            }
            step = Dungeon.findPath(this, pos, target, passable, Level.fieldOfView)
        }
        return if (step != -1) {
            val oldPos: Int = pos
            move(step)
            sprite.move(oldPos, pos)
            spend(1 / speed())
            true
        } else {
            false
        }
    }

    fun handle(cell: Int): Boolean {
        if (cell == -1) {
            return false
        }
        var ch: Char?
        var heap: Heap
        if (Dungeon.level.map.get(cell) === Terrain.ALCHEMY && cell != pos) {
            curAction = Cook(cell)
        } else if (Level.fieldOfView.get(cell) && Actor.findChar(cell).also { ch = it } is Mob) {
            if (ch is NPC) {
                curAction = Interact(ch as NPC?)
            } else {
                curAction = Attack(ch)
            }
        } else if (Level.fieldOfView.get(cell) && Dungeon.level.heaps.get(cell)
                .also { heap = it } != null && heap.type !== Heap.Type.HIDDEN
        ) {
            when (heap.type) {
                HEAP -> curAction = PickUp(cell)
                FOR_SALE -> curAction = if (heap.size() === 1 && heap.peek().price() > 0) Buy(cell) else PickUp(cell)
                else -> curAction = OpenChest(cell)
            }
        } else if (Dungeon.level.map.get(cell) === Terrain.LOCKED_DOOR || Dungeon.level.map.get(cell) === Terrain.LOCKED_EXIT) {
            curAction = Unlock(cell)
        } else if (cell == Dungeon.level.exit) {
            curAction = Descend(cell)
        } else if (cell == Dungeon.level.entrance) {
            curAction = Ascend(cell)
        } else {
            curAction = Move(cell)
            lastAction = null
        }
        return act()
    }

    fun earnExp(exp: Int) {
        this.exp += exp
        var levelUp = false
        while (this.exp >= maxExp()) {
            this.exp -= maxExp()
            lvl++
            HT += 5
            HP += 5
            attackSkill++
            defenseSkill++
            if (lvl < 10) {
                updateAwareness()
            }
            levelUp = true
        }
        if (levelUp) {
            GLog.p(TXT_NEW_LEVEL, lvl)
            sprite.showStatus(CharSprite.POSITIVE, TXT_LEVEL_UP)
            Sample.INSTANCE.play(Assets.SND_LEVELUP)
            Badges.validateLevelReached()
        }
        if (subClass === HeroSubClass.WARLOCK) {
            val value: Int = Math.min(HT - HP, 1 + (Dungeon.depth - 1) / 5)
            if (value > 0) {
                HP += value
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 1)
            }
            (buff(Hunger::class.java) as Hunger).satisfy(10)
        }
    }

    fun maxExp(): Int {
        return 5 + lvl * 5
    }

    fun updateAwareness() {
        awareness = (1 - Math.pow(
            if (heroClass === HeroClass.ROGUE) 0.85 else 0.90,
            (1 + Math.min(lvl, 9)) * 0.5
        )).toFloat()
    }

    val isStarving: Boolean
        get() = (buff(Hunger::class.java) as Hunger).isStarving()

    fun add(buff: Buff?) {
        super.add(buff)
        if (sprite != null) {
            if (buff is Burning) {
                GLog.w("You catch fire!")
                interrupt()
            } else if (buff is Paralysis) {
                GLog.w("You are paralysed!")
                interrupt()
            } else if (buff is Poison) {
                GLog.w("You are poisoned!")
                interrupt()
            } else if (buff is Ooze) {
                GLog.w("Caustic ooze eats your flesh. Wash away it!")
            } else if (buff is Roots) {
                GLog.w("You can't move!")
            } else if (buff is Weakness) {
                GLog.w("You feel weakened!")
            } else if (buff is Blindness) {
                GLog.w("You are blinded!")
            } else if (buff is Fury) {
                GLog.w("You become furious!")
                sprite.showStatus(CharSprite.POSITIVE, "furious")
            } else if (buff is Charm) {
                GLog.w("You are charmed!")
            } else if (buff is Cripple) {
                GLog.w("You are crippled!")
            } else if (buff is Bleeding) {
                GLog.w("You are bleeding!")
            } else if (buff is Vertigo) {
                GLog.w("Everything is spinning around you!")
                interrupt()
            } else if (buff is Light) {
                sprite.add(CharSprite.State.ILLUMINATED)
            }
        }
        BuffIndicator.refreshHero()
    }

    fun remove(buff: Buff?) {
        super.remove(buff)
        if (buff is Light) {
            sprite.remove(CharSprite.State.ILLUMINATED)
        }
        BuffIndicator.refreshHero()
    }

    fun stealth(): Int {
        var stealth: Int = super.stealth()
        for (buff in buffs(RingOfShadows.Shadows::class.java)) {
            stealth += (buff as RingOfShadows.Shadows).level
        }
        return stealth
    }

    fun die(cause: Any?) {
        curAction = null
        DewVial.autoDrink(this)
        if (isAlive()) {
            Flare(8, 32).color(0xFFFF66, true).show(sprite, 2f)
            return
        }
        Actor.fixTime()
        super.die(cause)
        val ankh: Ankh = belongings.getItem(Ankh::class.java) as Ankh
        if (ankh == null) {
            reallyDie(cause)
        } else {
            Dungeon.deleteGame(Dungeon.hero.heroClass, false)
            GameScene.show(WndResurrect(ankh, cause))
        }
    }

    fun move(step: Int) {
        super.move(step)
        if (!flying) {
            if (Level.water.get(pos)) {
                Sample.INSTANCE.play(Assets.SND_WATER, 1, 1, Random.Float(0.8f, 1.25f))
            } else {
                Sample.INSTANCE.play(Assets.SND_STEP)
            }
            Dungeon.level.press(pos, this)
        }
    }

    fun onMotionComplete() {
        Dungeon.observe()
        search(false)
        super.onMotionComplete()
    }

    fun onAttackComplete() {
        AttackIndicator.target(enemy)
        attack(enemy)
        curAction = null
        Invisibility.dispel()
        super.onAttackComplete()
    }

    fun onOperateComplete() {
        if (curAction is HeroAction.Unlock) {
            if (theKey != null) {
                theKey.detach(belongings.backpack)
                theKey = null
            }
            val doorCell: Int = (curAction as HeroAction.Unlock).dst
            val door: Int = Dungeon.level.map.get(doorCell)
            Level.set(doorCell, if (door == Terrain.LOCKED_DOOR) Terrain.DOOR else Terrain.UNLOCKED_EXIT)
            GameScene.updateMap(doorCell)
        } else if (curAction is HeroAction.OpenChest) {
            if (theKey != null) {
                theKey.detach(belongings.backpack)
                theKey = null
            }
            val heap: Heap = Dungeon.level.heaps.get((curAction as HeroAction.OpenChest).dst)
            if (heap.type === Type.SKELETON) {
                Sample.INSTANCE.play(Assets.SND_BONES)
            }
            heap.open(this)
        }
        curAction = null
        super.onOperateComplete()
    }

    fun search(intentional: Boolean): Boolean {
        var smthFound = false
        var positive = 0
        var negative = 0
        for (buff in buffs(RingOfDetection.Detection::class.java)) {
            val bonus: Int = (buff as RingOfDetection.Detection).level
            if (bonus > positive) {
                positive = bonus
            } else if (bonus < 0) {
                negative += bonus
            }
        }
        var distance = 1 + positive + negative
        var level = if (intentional) 2 * awareness - awareness * awareness else awareness
        if (distance <= 0) {
            level /= (2 - distance).toFloat()
            distance = 1
        }
        val cx: Int = pos % Level.WIDTH
        val cy: Int = pos / Level.WIDTH
        var ax = cx - distance
        if (ax < 0) {
            ax = 0
        }
        var bx = cx + distance
        if (bx >= Level.WIDTH) {
            bx = Level.WIDTH - 1
        }
        var ay = cy - distance
        if (ay < 0) {
            ay = 0
        }
        var by = cy + distance
        if (by >= Level.HEIGHT) {
            by = Level.HEIGHT - 1
        }
        for (y in ay..by) {
            var x = ax
            var p: Int = ax + y * Level.WIDTH
            while (x <= bx) {
                if (Dungeon.visible.get(p)) {
                    if (intentional) {
                        sprite.parent.addToBack(CheckedCell(p))
                    }
                    if (Level.secret.get(p) && (intentional || Random.Float() < level)) {
                        val oldValue: Int = Dungeon.level.map.get(p)
                        GameScene.discoverTile(p, oldValue)
                        Level.set(p, Terrain.discover(oldValue))
                        GameScene.updateMap(p)
                        ScrollOfMagicMapping.discover(p)
                        smthFound = true
                    }
                    if (intentional) {
                        val heap: Heap = Dungeon.level.heaps.get(p)
                        if (heap != null && heap.type === Type.HIDDEN) {
                            heap.open(this)
                            smthFound = true
                        }
                    }
                }
                x++
                p++
            }
        }
        if (intentional) {
            sprite.showStatus(CharSprite.DEFAULT, TXT_SEARCH)
            sprite.operate(pos)
            if (smthFound) {
                spendAndNext(if (Random.Float() < level) TIME_TO_SEARCH else TIME_TO_SEARCH * 2)
            } else {
                spendAndNext(TIME_TO_SEARCH)
            }
        }
        if (smthFound) {
            GLog.w(TXT_NOTICED_SMTH)
            Sample.INSTANCE.play(Assets.SND_SECRET)
            interrupt()
        }
        return smthFound
    }

    fun resurrect(resetLevel: Int) {
        HP = HT
        Dungeon.gold = 0
        exp = 0
        belongings.resurrect(resetLevel)
        live()
    }

    fun resistances(): HashSet<Class<*>> {
        val r: RingOfElements.Resistance = buff(RingOfElements.Resistance::class.java)
        return if (r == null) super.resistances() else r.resistances()
    }

    fun immunities(): HashSet<Class<*>> {
        val buff: GasesImmunity = buff(GasesImmunity::class.java)
        return if (buff == null) super.immunities() else GasesImmunity.IMMUNITIES
    }

    operator fun next() {
        super.next()
    }

    interface Doom {
        fun onDeath()
    }

    companion object {
        private const val TXT_LEAVE = "One does not simply leave Pixel Dungeon."
        private const val TXT_LEVEL_UP = "level up!"
        private const val TXT_NEW_LEVEL = "Welcome to level %d! Now you are healthier and more focused. " +
                "It's easier for you to hit enemies and dodge their attacks."
        const val TXT_YOU_NOW_HAVE = "You now have %s"
        private const val TXT_SOMETHING_ELSE = "There is something else here"
        private const val TXT_LOCKED_CHEST = "This chest is locked and you don't have matching key"
        private const val TXT_LOCKED_DOOR = "You don't have a matching key"
        private const val TXT_NOTICED_SMTH = "You noticed something"
        private const val TXT_WAIT = "..."
        private const val TXT_SEARCH = "search"
        const val STARTING_STR = 10
        private const val TIME_TO_REST = 1f
        private const val TIME_TO_SEARCH = 2f
        private const val ATTACK = "attackSkill"
        private const val DEFENSE = "defenseSkill"
        private const val STRENGTH = "STR"
        private const val LEVEL = "lvl"
        private const val EXPERIENCE = "exp"
        fun preview(info: Info, bundle: Bundle) {
            info.level = bundle.getInt(LEVEL)
        }

        fun reallyDie(cause: Any?) {
            val length: Int = Level.LENGTH
            val map: IntArray = Dungeon.level.map
            val visited: BooleanArray = Dungeon.level.visited
            val discoverable: BooleanArray = Level.discoverable
            for (i in 0 until length) {
                val terr = map[i]
                if (discoverable[i]) {
                    visited[i] = true
                    if (Terrain.flags.get(terr) and Terrain.SECRET !== 0) {
                        Level.set(i, Terrain.discover(terr))
                        GameScene.updateMap(i)
                    }
                }
            }
            Bones.leave()
            Dungeon.observe()
            Dungeon.hero.belongings.identify()
            val pos: Int = Dungeon.hero.pos
            val passable = ArrayList<Int>()
            for (ofs in Level.NEIGHBOURS8) {
                val cell = pos + ofs
                if ((Level.passable.get(cell) || Level.avoid.get(cell)) && Dungeon.level.heaps.get(cell) == null) {
                    passable.add(cell)
                }
            }
            Collections.shuffle(passable)
            val items: ArrayList<Item> = ArrayList<Item>(Dungeon.hero.belongings.backpack.items)
            for (cell in passable) {
                if (items.isEmpty()) {
                    break
                }
                val item: Item = Random.element(items)
                Dungeon.level.drop(item, cell).sprite.drop(pos)
                items.remove(item)
            }
            GameScene.gameOver()
            if (cause is Doom) {
                cause.onDeath()
            }
            Dungeon.deleteGame(Dungeon.hero.heroClass, true)
        }
    }

    init {
        name = "you"
        HT = 20
        HP = HT
        STR = STARTING_STR
        awareness = 0.1f
        belongings = Belongings(this)
        visibleEnemies = ArrayList<Mob>()
    }
}