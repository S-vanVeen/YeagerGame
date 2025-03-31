package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;

public class ZombieSprite extends DynamicSpriteEntity {
    public ZombieSprite(final Coordinate2D location) {
        super("sprites/normal_zombie.png", location, new Size(25, 25), 1, 1);
//        setAutoCycle(25);
        
    }
}
