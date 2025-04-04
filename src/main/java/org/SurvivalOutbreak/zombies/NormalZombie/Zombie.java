package org.SurvivalOutbreak.zombies.NormalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import org.SurvivalOutbreak.PlayerLocation;
import org.SurvivalOutbreak.scenes.GameScene;
import org.SurvivalOutbreak.zombies.BaseZombie;

public class Zombie extends BaseZombie {
    private final double ATTACK_RANGE = 15.0;

    public Zombie(PlayerLocation player, GameScene gameScene, double speed, int reward, int health) {
        super(player, gameScene, speed, reward, health);
        this.attackCooldown = 1500;
        System.out.println("Zombie created at (0, 0) with stats - Speed: " +
                String.format("%.2f", speed) +
                ", Reward: " + reward +
                ", Health: " + health);
    }

    @Override
    protected void setupEntities() {
        var zombieSprite = new ZombieSprite(new Coordinate2D(0, 0));
        addEntity(zombieSprite);

        var hitBox = new HitBox(new Coordinate2D(0, 0), this, gameScene);
        addEntity(hitBox);
    }

    @Override
    protected boolean attackMethod() {

        double distance = getDistanceToPlayer();

        if (distance <= ATTACK_RANGE) {
            System.out.println("Zombie attempting to attack player at distance: " +
                    String.format("%.2f", distance));
            return true;
        }

        return false;
    }
}