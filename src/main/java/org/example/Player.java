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
import org.example.zombies.BaseHitBox;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Player extends DynamicSpriteEntity implements KeyListener, SceneBorderTouchingWatcher, Collided, PlayerLocation {

    private SurvivalOutbreak survivalOutbreak;
    private HealthBar healthBar;
    private RoundText roundText;//?
    private Ammunition ammunition;
    private boolean isReloading = false;
    private int health = 100;
    private int round = 1;//?
    private final boolean ENABLE_LOCATION_LOGGING = false; //Kan later weg
    private final int SPRITE_ROW_UP = 0;
    private final int SPRITE_ROW_DOWN = 1;
    private final int SPRITE_ROW_LEFT = 2;
    private final int SPRITE_ROW_RIGHT = 3;
    private final int MAX_HEALTH = 500;

    private int lastDirection = SPRITE_ROW_DOWN;

    public Player(Coordinate2D location, HealthBar healthBar, RoundText roundText, SurvivalOutbreak survivalOutbreak) {
        super("sprites/sprite_sheet.png", location, new Size(25, 25), 4, 3);
        this.survivalOutbreak = survivalOutbreak;
        this.healthBar = healthBar;
        this.roundText = roundText;
        roundText.setRoundText();

        if (ENABLE_LOCATION_LOGGING) {
            System.out.println("Player initial location: X=" + location.getX() + ", Y=" + location.getY());
        }
    }

    public void setAmmunition(Ammunition ammunition) {
        this.ammunition = ammunition;
    }

    public boolean startReloading() {
        if (getSpeed() > 0) {
            System.out.println("Cannot reload while moving. Stop first!");
            return false;
        }

        if (!isReloading && ammunition != null && ammunition.getCurrentAmmo() < ammunition.getMaxAmmo()) {
            isReloading = true;
            // Hier mischien geluidje neer zetten
            System.out.println("Player is reloading...");
            return true;
        }
        return false;
    }

    public void completeReload() {
        if (isReloading) {
            isReloading = false;
            // Return to normal stance
            setCurrentFrameIndex(lastDirection * 4);
            System.out.println("Reload complete!");
        }
    }

    public void increaseMaxHealth(int amount) {
        if(health < MAX_HEALTH) {
            health += amount;
            healthBar.updateHealth(health);
            System.out.println("Player health increased to: " + health);
        }

    }

    public boolean isReloading() {
        return isReloading;
    }

    @Override
    public Coordinate2D getLocation() {
        Coordinate2D location = getLocationInScene();
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

        if (pressedKeys.contains(KeyCode.R)) {
            if (ammunition != null && !isReloading && ammunition.getCurrentAmmo() < ammunition.getMaxAmmo()) {
                startReloading();
            }
        }

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
        //kan weg later
        if (ENABLE_LOCATION_LOGGING) {
            logLocation("boundary touch");
        }
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof BaseHitBox) {
                BaseHitBox hitBox = (BaseHitBox) collider;

                if (isReloading) {
                    isReloading = false;
                }

                Coordinate2D newLocation = new Coordinate2D(
                        new Random().nextInt((int) (getSceneWidth() - getWidth())),
                        new Random().nextInt((int) (getSceneHeight() - getHeight()))
                );

                setAnchorLocation(newLocation);

                // Use the zombie's specific damage amount
                int damage = hitBox.getDamageAmount();
                health -= damage;

                // Show damage in console for debugging
                System.out.println("Player took " + damage + " damage from " +
                        hitBox.getZombieType() +
                        ". Remaining health: " + health);

                healthBar.updateHealth(health);

                if (health <= 0) {
                    survivalOutbreak.setActiveScene(3);
                }

                // Kan weg later
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