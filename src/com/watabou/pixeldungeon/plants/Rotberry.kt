package com.watabou.pixeldungeon.plants

import com.watabou.noosa.audio.Sample

class Rotberry : Plant() {
    override fun activate(ch: Char?) {
        super.activate(ch)
        GameScene.add(Blob.seed(pos, 100, ToxicGas::class.java))
        Dungeon.level.drop(Seed(), pos).sprite.drop()
        if (ch != null) {
            Buff.prolong(ch, Roots::class.java, Roots.TICK * 3)
        }
    }

    override fun desc(): String {
        return TXT_DESC
    }

    class Seed : Plant.Seed() {
        fun collect(container: Bag?): Boolean {
            return if (super.collect(container)) {
                if (Dungeon.level != null) {
                    for (mob in Dungeon.level.mobs) {
                        mob.beckon(Dungeon.hero.pos)
                    }
                    GLog.w("The seed emits a roar that echoes throughout the dungeon!")
                    CellEmitter.center(Dungeon.hero.pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3)
                    Sample.INSTANCE.play(Assets.SND_CHALLENGE)
                }
                true
            } else {
                false
            }
        }

        fun desc(): String {
            return TXT_DESC
        }

        init {
            plantName = "Rotberry"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_ROTBERRY
            plantClass = Rotberry::class.java
            alchemyClass = PotionOfStrength::class.java
        }
    }

    companion object {
        private const val TXT_DESC = "Berries of this shrub taste like sweet, sweet death."
    }

    init {
        image = 7
        plantName = "Rotberry"
    }
}