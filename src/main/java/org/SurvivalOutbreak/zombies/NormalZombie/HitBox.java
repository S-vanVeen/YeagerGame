package org.SurvivalOutbreak.zombies.NormalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import org.SurvivalOutbreak.scenes.GameScene;
import org.SurvivalOutbreak.zombies.BaseHitBox;

public class HitBox extends BaseHitBox {

    public HitBox(final Coordinate2D initialPosition, Zombie zombie, GameScene gameScene) {
        super(initialPosition, zombie, gameScene, 20, 25, 10);
    }
}