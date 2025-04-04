package org.SurvivalOutbreak.zombies;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.DynamicCompositeEntity;
import com.github.hanyaeger.api.entities.SceneBorderCrossingWatcher;
import com.github.hanyaeger.api.scenes.SceneBorder;
import org.SurvivalOutbreak.PlayerLocation;
import org.SurvivalOutbreak.scenes.GameScene;

public abstract class BaseZombie extends DynamicCompositeEntity implements SceneBorderCrossingWatcher {
    protected final PlayerLocation player;
    protected double zombieSpeed;
    protected final GameScene gameScene;
    protected int reward;
    protected int health;
    protected long lastAttackTime = 0;
    protected long attackCooldown = 2000;

    public BaseZombie(PlayerLocation player, GameScene gameScene, double speed, int reward, int health) {
        super(new Coordinate2D(0, 0));
        this.player = player;
        this.gameScene = gameScene;
        this.zombieSpeed = speed;
        this.reward = reward;
        this.health = health;
    }

    @Override
    protected abstract void setupEntities();

    public void executeUpdates() {
        moveDirectlyTowardsPlayer();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime > attackCooldown) {
            attackMethod();
            lastAttackTime = currentTime;
        }
    }

    protected void moveDirectlyTowardsPlayer() {
        Coordinate2D playerPos = player.getLocation();
        Coordinate2D zombiePos = getLocationInScene();

        double deltaX = playerPos.getX() - zombiePos.getX();
        double deltaY = playerPos.getY() - zombiePos.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            double dirX = deltaX / distance;
            double dirY = deltaY / distance;

            double newX = zombiePos.getX() + dirX * zombieSpeed;
            double newY = zombiePos.getY() + dirY * zombieSpeed;
            setAnchorLocation(new Coordinate2D(newX, newY));
        }
    }

    protected abstract boolean attackMethod();

    // Get distance to player
    protected double getDistanceToPlayer() {
        Coordinate2D playerPos = player.getLocation();
        Coordinate2D zombiePos = getLocationInScene();

        double deltaX = playerPos.getX() - zombiePos.getX();
        double deltaY = playerPos.getY() - zombiePos.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public void notifyBoundaryCrossing(SceneBorder border) {
        setAnchorLocation(new Coordinate2D(0, 0));
        System.out.println(getClass().getSimpleName() + " crossed border, resetting to (0, 0)");
    }

    public int getReward() {
        return reward;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            handleDeath();
        }
    }

    protected void handleDeath() {
        remove();
        gameScene.removeZombie(this);
        gameScene.addCash(reward);
    }

    // For zombies that can shoot
    protected Coordinate2D getDirectionToPlayer() {
        Coordinate2D playerPos = player.getLocation();
        Coordinate2D zombiePos = getLocationInScene();
        return new Coordinate2D(playerPos.getX(), playerPos.getY());
    }
}