package org.example.zombies.BigZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;

public class BigZombieSprite extends DynamicSpriteEntity {
    public BigZombieSprite(final Coordinate2D location) {
        super("sprites/bigzombie.png", location, new Size(40, 40), 1, 2);
        setAutoCycle(250);
    }
}