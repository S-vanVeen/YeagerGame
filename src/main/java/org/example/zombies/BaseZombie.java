package org.example.zombies;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.DynamicCompositeEntity;
import com.github.hanyaeger.api.entities.SceneBorderCrossingWatcher;
import com.github.hanyaeger.api.scenes.SceneBorder;
import org.example.PlayerLocation;
import org.example.scenes.GameScene;

public abstract class BaseZombie extends DynamicCompositeEntity implements SceneBorderCrossingWatcher {
    protected final PlayerLocation player;
    protected double zombieSpeed;
    protected final GameScene gameScene;
    protected int reward;
    protected int health;

    public BaseZombie(PlayerLocation player, GameScene gameScene, double speed, int reward, int health) {
        // Always start at position (0, 0)
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
    }

    protected void moveDirectlyTowardsPlayer() {
        // Get current positions
        Coordinate2D playerPos = player.getLocation();
        Coordinate2D zombiePos = getLocationInScene();

        // Calculate the vector from zombie to player
        double deltaX = playerPos.getX() - zombiePos.getX();
        double deltaY = playerPos.getY() - zombiePos.getY();

        // Calculate distance between zombie and player
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Only move if we're not already at the player's position
        if (distance > 0) {
            // Normalize the direction vector
            double dirX = deltaX / distance;
            double dirY = deltaY / distance;

            // Calculate new position (move one step toward player)
            double newX = zombiePos.getX() + dirX * zombieSpeed;
            double newY = zombiePos.getY() + dirY * zombieSpeed;

            // Update zombie position
            setAnchorLocation(new Coordinate2D(newX, newY));
        }
    }

    @Override
    public void notifyBoundaryCrossing(SceneBorder border) {
        // Reset to (0, 0) when crossing any boundary
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
}