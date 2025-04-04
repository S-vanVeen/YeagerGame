package org.SurvivalOutbreak.zombies.NormalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;

public class ZombieSprite extends DynamicSpriteEntity {
    public ZombieSprite(final Coordinate2D location) {
        super("sprites/zombie_spritesheet.png", location, new Size(25, 25), 1, 2);
        setAutoCycle(250);
    }
}