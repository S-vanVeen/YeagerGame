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
import org.example.ui.HealthText;
import org.example.ui.RoundText;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class Player extends DynamicSpriteEntity implements KeyListener, SceneBorderTouchingWatcher, Collided, PlayerLocation {

    private SurvivalOutbreak survivalOutbreak;
    private HealthText healthText;
    private RoundText roundText;
    private int health = 100;
    private int round = 1;
    private static final boolean ENABLE_LOCATION_LOGGING = true; // Toggle to enable/disable logging

    public Player(Coordinate2D location, HealthText healthText, RoundText roundText, SurvivalOutbreak survivalOutbreak) {
        super("sprites/Player_idle_front.png", location, new Size(25,25), 1, 1);
        this.survivalOutbreak = survivalOutbreak;
        this.healthText = healthText;
        this.roundText = roundText;
        healthText.setHealthText(health);
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
    public void onPressedKeysChange(Set<KeyCode> pressedKeys){
        boolean moved = false;

        if(pressedKeys.contains(KeyCode.A)){
            setMotion(2,270d);
            setCurrentFrameIndex(0);
            moved = true;
        } else if(pressedKeys.contains(KeyCode.D)){
            setMotion(2,90d);
            setCurrentFrameIndex(1);
            moved = true;
        } else if(pressedKeys.contains(KeyCode.W)){
            setMotion(2,180d);
            moved = true;
        } else if(pressedKeys.contains(KeyCode.S)) {
            setMotion(2, 0d);
            moved = true;
        } else if(pressedKeys.contains(KeyCode.ESCAPE)){
            survivalOutbreak.setActiveScene(2);
        } else if (pressedKeys.isEmpty()) {
            setSpeed(0);
        }

        // Log when player starts moving
        if (moved && ENABLE_LOCATION_LOGGING) {
            logLocation("key press");
        }

        // Debug key for manually printing location (press L)
        if (pressedKeys.contains(KeyCode.L) && ENABLE_LOCATION_LOGGING) {
            logLocation("manual check");
        }
    }

    @Override
    public void notifyBoundaryTouching(SceneBorder border){
        setSpeed(0);

        switch(border){
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
    public void onCollision(List<Collider> collidingObject){
        Coordinate2D newLocation = new Coordinate2D(
                new Random().nextInt((int)(getSceneWidth()-getWidth())),
                new Random().nextInt((int)(getSceneHeight()-getHeight()))
        );

        setAnchorLocation(newLocation);

        health -= 10;
        healthText.setHealthText(health);

        if(health <= 0){
            survivalOutbreak.setActiveScene(3);
        }

        // Log position change after collision
        if (ENABLE_LOCATION_LOGGING) {
            logLocation("collision");
        }
    }

    private void logLocation(String reason) {
        Coordinate2D location = getLocationInScene();
        System.out.println("Player location (" + reason + "): X=" +
                String.format("%.2f", location.getX()) + ", Y=" +
                String.format("%.2f", location.getY()));
    }
}