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

import com.watabou.noosa.Camera

abstract class Char : Actor() {
    var pos = 0
    var sprite: CharSprite? = null
    var name = "mob"
    var HT = 0
    var HP = 0
    protected var baseSpeed = 1f
    var paralysed = false
    var rooted = false
    var flying = false
    var invisible = 0
    var viewDistance = 8
    private val buffs: HashSet<Buff> = HashSet<Buff>()
    protected override fun act(): Boolean {
        Dungeon.level.updateFieldOfView(this)
        return false
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(POS, pos)
        bundle.put(TAG_HP, HP)
        bundle.put(TAG_HT, HT)
        bundle.put(BUFFS, buffs)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        pos = bundle.getInt(POS)
        HP = bundle.getInt(TAG_HP)
        HT = bundle.getInt(TAG_HT)
        for (b in bundle.getCollection(BUFFS)) {
            if (b != null) {
                (b as Buff).attachTo(this)
            }
        }
    }

    fun attack(enemy: Char): Boolean {
        val visibleFight = Dungeon.visible.get(pos) || Dungeon.visible.get(enemy.pos)
        return if (hit(this, enemy, false)) {
            if (visibleFight) {
                GLog.i(TXT_HIT, name, enemy.name)
            }

            // FIXME
            val dr =
                if (this is Hero && (this as Hero).rangedWeapon != null && (this as Hero).subClass === HeroSubClass.SNIPER) 0 else Random.IntRange(
                    0,
                    enemy.dr()
                )
            val dmg = damageRoll()
            var effectiveDamage = Math.max(dmg - dr, 0)
            effectiveDamage = attackProc(enemy, effectiveDamage)
            effectiveDamage = enemy.defenseProc(this, effectiveDamage)
            enemy.damage(effectiveDamage, this)
            if (visibleFight) {
                Sample.INSTANCE.play(Assets.SND_HIT, 1, 1, Random.Float(0.8f, 1.25f))
            }
            if (enemy === Dungeon.hero) {
                Dungeon.hero.interrupt()
                if (effectiveDamage > enemy.HT / 4) {
                    Camera.main.shake(GameMath.gate(1, effectiveDamage / (enemy.HT / 4), 5), 0.3f)
                }
            }
            enemy.sprite.bloodBurstA(sprite.center(), effectiveDamage)
            enemy.sprite.flash()
            if (!enemy.isAlive && visibleFight) {
                if (enemy === Dungeon.hero) {
                    if (Dungeon.hero.killerGlyph != null) {

                        // FIXME
                        //	Dungeon.fail( Utils.format( ResultDescriptions.GLYPH, Dungeon.hero.killerGlyph.name(), Dungeon.depth ) );
                        //	GLog.n( TXT_KILL, Dungeon.hero.killerGlyph.name() );
                    } else {
                        if (Bestiary.isBoss(this)) {
                            Dungeon.fail(Utils.format(ResultDescriptions.BOSS, name, Dungeon.depth))
                        } else {
                            Dungeon.fail(
                                Utils.format(
                                    ResultDescriptions.MOB,
                                    Utils.indefinite(name), Dungeon.depth
                                )
                            )
                        }
                        GLog.n(TXT_KILL, name)
                    }
                } else {
                    GLog.i(TXT_DEFEAT, name, enemy.name)
                }
            }
            true
        } else {
            if (visibleFight) {
                val defense = enemy.defenseVerb()
                enemy.sprite.showStatus(CharSprite.NEUTRAL, defense)
                if (this === Dungeon.hero) {
                    GLog.i(TXT_YOU_MISSED, enemy.name, defense)
                } else {
                    GLog.i(TXT_SMB_MISSED, enemy.name, defense, name)
                }
                Sample.INSTANCE.play(Assets.SND_MISS)
            }
            false
        }
    }

    fun attackSkill(target: Char?): Int {
        return 0
    }

    fun defenseSkill(enemy: Char?): Int {
        return 0
    }

    fun defenseVerb(): String {
        return "dodged"
    }

    fun dr(): Int {
        return 0
    }

    fun damageRoll(): Int {
        return 1
    }

    fun attackProc(enemy: Char?, damage: Int): Int {
        return damage
    }

    fun defenseProc(enemy: Char?, damage: Int): Int {
        return damage
    }

    fun speed(): Float {
        return if (buff<T?>(Cripple::class.java) == null) baseSpeed else baseSpeed * 0.5f
    }

    fun damage(dmg: Int, src: Any) {
        var dmg = dmg
        if (HP <= 0) {
            return
        }
        Buff.detach(this, Frost::class.java)
        val srcClass: Class<*> = src.javaClass
        if (immunities().contains(srcClass)) {
            dmg = 0
        } else if (resistances().contains(srcClass)) {
            dmg = Random.IntRange(0, dmg)
        }
        if (buff<T?>(Paralysis::class.java) != null) {
            if (Random.Int(dmg) >= Random.Int(HP)) {
                Buff.detach(this, Paralysis::class.java)
                if (Dungeon.visible.get(pos)) {
                    GLog.i(TXT_OUT_OF_PARALYSIS, name)
                }
            }
        }
        HP -= dmg
        if (dmg > 0 || src is Char) {
            sprite.showStatus(
                if (HP > HT / 2) CharSprite.WARNING else CharSprite.NEGATIVE,
                Integer.toString(dmg)
            )
        }
        if (HP <= 0) {
            die(src)
        }
    }

    fun destroy() {
        HP = 0
        Actor.remove(this)
        Actor.freeCell(pos)
    }

    fun die(src: Any?) {
        destroy()
        sprite.die()
    }

    val isAlive: Boolean
        get() = HP > 0

    protected override fun spend(time: Float) {
        var timeScale = 1f
        if (buff<T?>(Slow::class.java) != null) {
            timeScale *= 0.5f
        }
        if (buff<T?>(Speed::class.java) != null) {
            timeScale *= 2.0f
        }
        super.spend(time / timeScale)
    }

    fun buffs(): HashSet<Buff> {
        return buffs
    }

    fun <T : Buff?> buffs(c: Class<T?>): HashSet<T> {
        val filtered = HashSet<T>()
        for (b in buffs) {
            if (c.isInstance(b)) {
                filtered.add(b as T)
            }
        }
        return filtered
    }

    fun <T : Buff?> buff(c: Class<T>): T? {
        for (b in buffs) {
            if (c.isInstance(b)) {
                return b
            }
        }
        return null
    }

    fun isCharmedBy(ch: Char): Boolean {
        val chID: Int = ch.id()
        for (b in buffs) {
            if (b is Charm && (b as Charm).`object` === chID) {
                return true
            }
        }
        return false
    }

    fun add(buff: Buff) {
        buffs.add(buff)
        Actor.add(buff)
        if (sprite != null) {
            if (buff is Poison) {
                CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 5)
                sprite.showStatus(CharSprite.NEGATIVE, "poisoned")
            } else if (buff is Amok) {
                sprite.showStatus(CharSprite.NEGATIVE, "amok")
            } else if (buff is Slow) {
                sprite.showStatus(CharSprite.NEGATIVE, "slowed")
            } else if (buff is MindVision) {
                sprite.showStatus(CharSprite.POSITIVE, "mind")
                sprite.showStatus(CharSprite.POSITIVE, "vision")
            } else if (buff is Paralysis) {
                sprite.add(CharSprite.State.PARALYSED)
                sprite.showStatus(CharSprite.NEGATIVE, "paralysed")
            } else if (buff is Terror) {
                sprite.showStatus(CharSprite.NEGATIVE, "frightened")
            } else if (buff is Roots) {
                sprite.showStatus(CharSprite.NEGATIVE, "rooted")
            } else if (buff is Cripple) {
                sprite.showStatus(CharSprite.NEGATIVE, "crippled")
            } else if (buff is Bleeding) {
                sprite.showStatus(CharSprite.NEGATIVE, "bleeding")
            } else if (buff is Vertigo) {
                sprite.showStatus(CharSprite.NEGATIVE, "dizzy")
            } else if (buff is Sleep) {
                sprite.idle()
            } else if (buff is Burning) {
                sprite.add(CharSprite.State.BURNING)
            } else if (buff is Levitation) {
                sprite.add(CharSprite.State.LEVITATING)
            } else if (buff is Frost) {
                sprite.add(CharSprite.State.FROZEN)
            } else if (buff is Invisibility) {
                if (buff !is Shadows) {
                    sprite.showStatus(CharSprite.POSITIVE, "invisible")
                }
                sprite.add(CharSprite.State.INVISIBLE)
            }
        }
    }

    fun remove(buff: Buff) {
        buffs.remove(buff)
        Actor.remove(buff)
        if (buff is Burning) {
            sprite.remove(CharSprite.State.BURNING)
        } else if (buff is Levitation) {
            sprite.remove(CharSprite.State.LEVITATING)
        } else if (buff is Invisibility && invisible <= 0) {
            sprite.remove(CharSprite.State.INVISIBLE)
        } else if (buff is Paralysis) {
            sprite.remove(CharSprite.State.PARALYSED)
        } else if (buff is Frost) {
            sprite.remove(CharSprite.State.FROZEN)
        }
    }

    fun remove(buffClass: Class<out Buff?>) {
        for (buff in buffs(buffClass)) {
            remove(buff)
        }
    }

    protected override fun onRemove() {
        for (buff in buffs.toArray(arrayOfNulls<Buff>(0))) {
            buff.detach()
        }
    }

    fun updateSpriteState() {
        for (buff in buffs) {
            if (buff is Burning) {
                sprite.add(CharSprite.State.BURNING)
            } else if (buff is Levitation) {
                sprite.add(CharSprite.State.LEVITATING)
            } else if (buff is Invisibility) {
                sprite.add(CharSprite.State.INVISIBLE)
            } else if (buff is Paralysis) {
                sprite.add(CharSprite.State.PARALYSED)
            } else if (buff is Frost) {
                sprite.add(CharSprite.State.FROZEN)
            } else if (buff is Light) {
                sprite.add(CharSprite.State.ILLUMINATED)
            }
        }
    }

    fun stealth(): Int {
        return 0
    }

    fun move(step: Int) {
        var step = step
        if (Level.adjacent(step, pos) && buff<T?>(Vertigo::class.java) != null) {
            step = pos + Level.NEIGHBOURS8.get(Random.Int(8))
            if (!(Level.passable.get(step) || Level.avoid.get(step)) || Actor.findChar(step) != null) {
                return
            }
        }
        if (Dungeon.level.map.get(pos) === Terrain.OPEN_DOOR) {
            Door.leave(pos)
        }
        pos = step
        if (flying && Dungeon.level.map.get(pos) === Terrain.DOOR) {
            Door.enter(pos)
        }
        if (this !== Dungeon.hero) {
            sprite.visible = Dungeon.visible.get(pos)
        }
    }

    fun distance(other: Char): Int {
        return Level.distance(pos, other.pos)
    }

    fun onMotionComplete() {
        next()
    }

    fun onAttackComplete() {
        next()
    }

    fun onOperateComplete() {
        next()
    }

    fun resistances(): HashSet<Class<*>> {
        return EMPTY
    }

    fun immunities(): HashSet<Class<*>> {
        return EMPTY
    }

    companion object {
        protected const val TXT_HIT = "%s hit %s"
        protected const val TXT_KILL = "%s killed you..."
        protected const val TXT_DEFEAT = "%s defeated %s"
        private const val TXT_YOU_MISSED = "%s %s your attack"
        private const val TXT_SMB_MISSED = "%s %s %s's attack"
        private const val TXT_OUT_OF_PARALYSIS = "The pain snapped %s out of paralysis"
        private const val POS = "pos"
        private const val TAG_HP = "HP"
        private const val TAG_HT = "HT"
        private const val BUFFS = "buffs"
        fun hit(attacker: Char, defender: Char, magic: Boolean): Boolean {
            val acuRoll: Float = Random.Float(attacker.attackSkill(defender))
            val defRoll: Float = Random.Float(defender.defenseSkill(attacker))
            return (if (magic) acuRoll * 2 else acuRoll) >= defRoll
        }

        private val EMPTY = HashSet<Class<*>>()
    }
}