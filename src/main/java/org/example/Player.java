package org.example;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.Collided;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.SceneBorderTouchingWatcher;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;
import com.github.hanyaeger.api.scenes.SceneBorder;
import com.github.hanyaeger.api.userinput.KeyListener;
import javafx.scene.input.KeyCode;
import org.example.ui.HealthBar;
import org.example.ui.RoundText;
import org.example.weapons.Ammunition;
import org.example.zombies.NormalZombie.HitBox;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Player extends DynamicSpriteEntity implements KeyListener, SceneBorderTouchingWatcher, Collided, PlayerLocation {

    private SurvivalOutbreak survivalOutbreak;
    private HealthBar healthBar;
    private RoundText roundText;
    private Ammunition ammunition; // Reference to the ammunition system
    private boolean isReloading = false; // Tracks if player is currently reloading
    private int health = 100;
    private int round = 1;
    private static final boolean ENABLE_LOCATION_LOGGING = false; // Toggle to enable/disable logging

    // Sprite indices for animation
    private static final int SPRITE_ROW_UP = 0;
    private static final int SPRITE_ROW_DOWN = 1;
    private static final int SPRITE_ROW_LEFT = 2;
    private static final int SPRITE_ROW_RIGHT = 3;
   // private static final int SPRITE_RELOAD = 9; // Additional sprite for reload animation (if available)

    private int lastDirection = SPRITE_ROW_DOWN; // Track last movement direction

    public Player(Coordinate2D location, HealthBar healthBar, RoundText roundText, SurvivalOutbreak survivalOutbreak) {
        super("sprites/sprite_sheet.png", location, new Size(25, 25), 4, 3);
        this.survivalOutbreak = survivalOutbreak;
        this.healthBar = healthBar;
        this.roundText = roundText;
        roundText.setRoundText();

        // Log initial location
        if (ENABLE_LOCATION_LOGGING) {
            System.out.println("Player initial location: X=" + location.getX() + ", Y=" + location.getY());
        }
    }

    /**
     * Set the ammunition system for this player
     * @param ammunition The ammunition system to use
     */
    public void setAmmunition(Ammunition ammunition) {
        this.ammunition = ammunition;
    }

    /**
     * Start the reloading animation
     * @return true if reload started successfully, false otherwise
     */
    public boolean startReloading() {
        // Check if player is standing still (not moving) by checking speed
        if (getSpeed() > 0) {
            System.out.println("Cannot reload while moving. Stop first!");
            return false;
        }

        if (!isReloading && ammunition != null && ammunition.getCurrentAmmo() < ammunition.getMaxAmmo()) {
            isReloading = true;
            // If you have a reloading sprite/animation, set it here
            // setCurrentFrameIndex(SPRITE_RELOAD);
            System.out.println("Player is reloading...");
            return true;
        }
        return false;
    }

    /**
     * Complete the reload process
     */
    public void completeReload() {
        if (isReloading) {
            isReloading = false;
            // Return to normal stance
            setCurrentFrameIndex(lastDirection * 4);
            System.out.println("Reload complete!");
        }
    }

    public void increaseMaxHealth(int amount) {
        health += amount;
        healthBar.updateHealth(health);
        System.out.println("Player health increased to: " + health);
    }

    /**
     * Check if player is currently reloading
     * @return true if reloading, false otherwise
     */
    public boolean isReloading() {
        return isReloading;
    }

    @Override
    public Coordinate2D getLocation() {
        Coordinate2D location = getLocationInScene();

        // Log the location when it's requested (typically by the zombie)
        if (ENABLE_LOCATION_LOGGING) {
            System.out.println("Player location (requested): X=" +
                    String.format("%.2f", location.getX()) + ", Y=" +
                    String.format("%.2f", location.getY()));
        }

        return location;
    }

    @Override
    public void onPressedKeysChange(Set<KeyCode> pressedKeys) {
        boolean moved = false;

        // Don't respond to movement keys if reloading
        if (!isReloading) {
            if (pressedKeys.contains(KeyCode.W)) {
                setMotion(1.5, 180d);
                setAutoCycle(125, SPRITE_ROW_UP);
                lastDirection = SPRITE_ROW_UP;
                moved = true;
            } else if (pressedKeys.contains(KeyCode.S)) {
                setMotion(1.5, 0d);
                setAutoCycle(125, SPRITE_ROW_DOWN);
                lastDirection = SPRITE_ROW_DOWN;
                moved = true;
            } else if (pressedKeys.contains(KeyCode.A)) {
                setMotion(1.5, 270d);
                setAutoCycle(125, SPRITE_ROW_LEFT);
                lastDirection = SPRITE_ROW_LEFT;
                moved = true;
            } else if (pressedKeys.contains(KeyCode.D)) {
                setMotion(1.5, 90d);
                setAutoCycle(125, SPRITE_ROW_RIGHT);
                lastDirection = SPRITE_ROW_RIGHT;
                moved = true;
            } else if (pressedKeys.contains(KeyCode.ESCAPE)) {
                survivalOutbreak.setActiveScene(2);
            } else if (pressedKeys.isEmpty()) {
                setSpeed(0);
                setAutoCycle(0);
                setCurrentFrameIndex(lastDirection * 4); // Stand still facing last direction
            }
        }

        // Handle reload key press - only works when standing still
        if (pressedKeys.contains(KeyCode.R)) {
            if (ammunition != null && !isReloading && ammunition.getCurrentAmmo() < ammunition.getMaxAmmo()) {
                // Will only reload if player is standing still (checked inside startReloading)
                startReloading();
            }
        }

        // If no keys pressed, make sure player stops completely
        if (pressedKeys.isEmpty() && !isReloading) {
            setSpeed(0);
            setAutoCycle(0);
            setCurrentFrameIndex(lastDirection * 4); // Stand still facing last direction
        }

        if (moved && ENABLE_LOCATION_LOGGING) {
            logLocation("key press");
        }

        if (pressedKeys.contains(KeyCode.L) && ENABLE_LOCATION_LOGGING) {
            logLocation("manual check");
        }
    }

    @Override
    public void notifyBoundaryTouching(SceneBorder border) {
        setSpeed(0);

        switch (border) {
            case TOP:
                setAnchorLocationY(1);
                break;
            case BOTTOM:
                setAnchorLocationY(getSceneHeight() - getHeight() - 1);
                break;
            case LEFT:
                setAnchorLocationX(1);
                break;
            case RIGHT:
                setAnchorLocationX(getSceneWidth() - getWidth() - 1);
            default:
                break;
        }

        // Log position change when hitting boundary
        if (ENABLE_LOCATION_LOGGING) {
            logLocation("boundary touch");
        }
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof HitBox) {
                // Cancel reloading if hit by zombie
                if (isReloading) {
                    isReloading = false;
                }

                Coordinate2D newLocation = new Coordinate2D(
                        new Random().nextInt((int) (getSceneWidth() - getWidth())),
                        new Random().nextInt((int) (getSceneHeight() - getHeight()))
                );

                setAnchorLocation(newLocation);

                health -= 10;
                healthBar.updateHealth(health);

                if (health <= 0) {
                    survivalOutbreak.setActiveScene(3);
                }

                // Log position change after collision
                if (ENABLE_LOCATION_LOGGING) {
                    logLocation("collision");
                }
            }
        }
    }

    private void logLocation(String reason) {
        Coordinate2D location = getLocationInScene();
        System.out.println("Player location (" + reason + "): X=" +
                String.format("%.2f", location.getX()) + ", Y=" +
                String.format("%.2f", location.getY()));
    }
}