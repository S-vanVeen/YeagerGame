package org.example.zombies.BigZombie;

import com.github.hanyaeger.api.Coordinate2D;
import org.example.PlayerLocation;
import org.example.scenes.GameScene;
import org.example.zombies.BaseZombie;

public class BigZombie extends BaseZombie {
    private static final double DEFAULT_SPEED = 0.7;  // Slower than normal zombie
    private static final int DEFAULT_REWARD = 100;    // Higher reward than normal zombie
    private static final int DEFAULT_HEALTH = 3;      // More health than normal zombie

    public BigZombie(PlayerLocation player, GameScene gameScene) {
        super(player, gameScene, DEFAULT_SPEED, DEFAULT_REWARD, DEFAULT_HEALTH);
        System.out.println("BigZombie created at (0, 0)");
    }

    @Override
    protected void setupEntities() {
        var zombieSprite = new BigZombieSprite(new Coordinate2D(0, 0));
        addEntity(zombieSprite);

        var hitBox = new HitBox(new Coordinate2D(0, 0), this, gameScene);
        addEntity(hitBox);
    }
}