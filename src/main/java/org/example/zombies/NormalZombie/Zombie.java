package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import org.example.PlayerLocation;
import org.example.scenes.GameScene;
import org.example.zombies.BaseZombie;

public class Zombie extends BaseZombie {
    private static final double DEFAULT_SPEED = 1.0;
    private static final int DEFAULT_REWARD = 50;
    private static final int DEFAULT_HEALTH = 1;

    public Zombie(PlayerLocation player, GameScene gameScene) {
        super(player, gameScene, DEFAULT_SPEED, DEFAULT_REWARD, DEFAULT_HEALTH);
        System.out.println("Zombie created at (0, 0)");
    }

    @Override
    protected void setupEntities() {
        var zombieSprite = new ZombieSprite(new Coordinate2D(0, 0));
        addEntity(zombieSprite);

        var hitBox = new HitBox(new Coordinate2D(0, 0), this, gameScene);
        addEntity(hitBox);
    }
}