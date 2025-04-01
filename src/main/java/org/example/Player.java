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
import org.example.zombies.normalZombie.HitBox;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Player extends DynamicSpriteEntity implements KeyListener, SceneBorderTouchingWatcher, Collided, PlayerLocation {

    private SurvivalOutbreak survivalOutbreak;
    private HealthBar healthBar;
    private RoundText roundText;
    private int health = 100;
    private int round = 1;
    private static final boolean ENABLE_LOCATION_LOGGING = false; // Toggle to enable/disable logging

    public Player(Coordinate2D location, HealthBar healthBar, RoundText roundText, SurvivalOutbreak survivalOutbreak) {
        super("sprites/sprite_sheet.png", location, new Size(25, 25), 4, 3);
        this.survivalOutbreak = survivalOutbreak;
        this.healthBar = healthBar;
        this.roundText = roundText;
        roundText.setRoundText(round);

        // Log initial location
        if (ENABLE_LOCATION_LOGGING) {
            System.out.println("Player initial location: X=" + location.getX() + ", Y=" + location.getY());
        }
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

        if (pressedKeys.contains(KeyCode.W)) {
            setMotion(1.5, 180d);
            setAutoCycle(125, 0);
            moved = true;
        } else if (pressedKeys.contains(KeyCode.S)) {
            setMotion(1.5, 0d);
            setAutoCycle(125, 1);
            moved = true;
        } else if (pressedKeys.contains(KeyCode.A)) {
            setMotion(1.5, 270d);
            setAutoCycle(125, 2);
            moved = true;
        } else if (pressedKeys.contains(KeyCode.D)) {
            setMotion(1.5, 90d);
            setAutoCycle(125, 3);
            moved = true;
        } else if (pressedKeys.contains(KeyCode.ESCAPE)) {
            survivalOutbreak.setActiveScene(2);
        } else if (pressedKeys.isEmpty()) {
            setSpeed(0);
            setAutoCycle(0);
            setCurrentFrameIndex(3);
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
