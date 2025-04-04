package org.example.zombies.NormalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import org.example.scenes.GameScene;
import org.example.zombies.BaseHitBox;

public class HitBox extends BaseHitBox {

    public HitBox(final Coordinate2D initialPosition, Zombie zombie, GameScene gameScene) {
        super(initialPosition, zombie, gameScene, 20, 25, 10);
    }
}