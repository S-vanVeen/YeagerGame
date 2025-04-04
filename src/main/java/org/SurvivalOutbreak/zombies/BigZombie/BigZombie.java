package org.SurvivalOutbreak.zombies.BigZombie;

import com.github.hanyaeger.api.Coordinate2D;
import java.util.Random;
import org.SurvivalOutbreak.PlayerLocation;
import org.SurvivalOutbreak.scenes.GameScene;
import org.SurvivalOutbreak.zombies.BigZombie.ZombieBullet;
import org.SurvivalOutbreak.zombies.BaseZombie;

public class BigZombie extends BaseZombie {

    private final double SHOOTING_RANGE = 300.0;
    private  final int SHOOT_PROBABILITY = 40;
    private final Random random = new Random();

    public BigZombie(PlayerLocation player, GameScene gameScene, double speed, int reward, int health) {
        super(player, gameScene, speed, reward, health);
        this.attackCooldown = 2000;
        System.out.println("BigZombie created at (0, 0) with stats - Speed: " +
                String.format("%.2f", speed) +
                ", Reward: " + reward +
                ", Health: " + health);
    }

    @Override
    protected void setupEntities() {
        var zombieSprite = new BigZombieSprite(new Coordinate2D(0, 0));
        addEntity(zombieSprite);

        var hitBox = new HitBox(new Coordinate2D(0, 0), this, gameScene);
        addEntity(hitBox);
    }

    @Override
    protected boolean attackMethod() {
        double distance = getDistanceToPlayer();
        if (distance <= SHOOTING_RANGE) {
            if (random.nextInt(100) < SHOOT_PROBABILITY) {
                shootAtPlayer();
                return true;
            }
        }

        return false;
    }

    private void shootAtPlayer() {
        Coordinate2D zombieLocation = getLocationInScene();
        Coordinate2D playerLocation = player.getLocation();

        System.out.println("BigZombie shooting at player from distance: " +
                String.format("%.2f", getDistanceToPlayer()));

        ZombieBullet bullet = new ZombieBullet(zombieLocation, playerLocation);
        gameScene.addEntityToScene(bullet);
    }
}