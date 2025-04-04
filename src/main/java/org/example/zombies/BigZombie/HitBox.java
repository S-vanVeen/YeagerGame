package org.example.zombies.BigZombie;

import com.github.hanyaeger.api.Coordinate2D;
import org.example.scenes.GameScene;
import org.example.zombies.BaseHitBox;

public class HitBox extends BaseHitBox {

    public HitBox(final Coordinate2D initialPosition, BigZombie zombie, GameScene gameScene) {
        super(initialPosition, zombie, gameScene,35, 40, 25);
    }
}