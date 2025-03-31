package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.DynamicCompositeEntity;
import com.github.hanyaeger.api.entities.SceneBorderCrossingWatcher;
import com.github.hanyaeger.api.scenes.SceneBorder;
import org.example.PlayerLocation;

public class Zombie extends DynamicCompositeEntity implements SceneBorderCrossingWatcher {
    private final PlayerLocation player;
    private static final double ZOMBIE_SPEED = 1.0; // Steps per update

    public Zombie(PlayerLocation player) {
        // Always start at position (0, 0)
        super(new Coordinate2D(0, 0));
        this.player = player;

        System.out.println("Zombie created at (0, 0)");
    }

    @Override
    protected void setupEntities() {
        var zombieSprite = new ZombieSprite(new Coordinate2D(0, 0));
        addEntity(zombieSprite);

        var hitBox = new HitBox(new Coordinate2D(0, 0));
        addEntity(hitBox);
    }

    public void executeUpdates() {
        moveDirectlyTowardsPlayer();
    }

    private void moveDirectlyTowardsPlayer() {
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
            double newX = zombiePos.getX() + dirX * ZOMBIE_SPEED;
            double newY = zombiePos.getY() + dirY * ZOMBIE_SPEED;

            // Update zombie position
            setAnchorLocation(new Coordinate2D(newX, newY));

            // Log for debugging
            boolean enableLogging = false;
            if(enableLogging) {
                System.out.println("Zombie moved to: X=" + String.format("%.2f", newX) +
                        ", Y=" + String.format("%.2f", newY));
                System.out.println("Target player: X=" + String.format("%.2f", playerPos.getX()) +
                        ", Y=" + String.format("%.2f", playerPos.getY()));
            }
        }
    }

    @Override
    public void notifyBoundaryCrossing(SceneBorder border) {
        // Reset to (0, 0) when crossing any boundary
        setAnchorLocation(new Coordinate2D(0, 0));
        System.out.println("Zombie crossed border, resetting to (0, 0)");
    }
}