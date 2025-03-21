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
import org.example.entities.Text.HealthText;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class Player extends DynamicSpriteEntity implements KeyListener, SceneBorderTouchingWatcher, Collided {

    private SurvivalOutbreak survivalOutbreak;
    private HealthText healthText;
    private int health = 100;

    public Player(Coordinate2D location, HealthText healthText, SurvivalOutbreak survivalOutbreak) {
        super("sprites/Player_idle_front.png", location, new Size(25,25), 1, 1);
        this.survivalOutbreak = survivalOutbreak;
        this.healthText = healthText;
        healthText.setHealthText(health);

    }

    @Override
    public void onPressedKeysChange(Set<KeyCode> pressedKeys){
        if(pressedKeys.contains(KeyCode.A)){
            setMotion(1,270d);
            setCurrentFrameIndex(0);
        } else if(pressedKeys.contains(KeyCode.D)){
            setMotion(1,90d);
            setCurrentFrameIndex(1);
        } else if(pressedKeys.contains(KeyCode.W)){
            setMotion(1,180d);
        } else if(pressedKeys.contains(KeyCode.S)) {
            setMotion(1, 0d);
        } else if(pressedKeys.contains(KeyCode.ESCAPE)){
            survivalOutbreak.setActiveScene(2);
        } else if (pressedKeys.isEmpty()) {
            setSpeed(0);
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
    }

    @Override
    public void onCollision(List<Collider> collidingObject){
        setAnchorLocation(new Coordinate2D(
                new Random().nextInt((int)(getSceneWidth()-getWidth())),
                new Random().nextInt((int)(getSceneHeight()-getHeight())))
        );

        health -= 10;
        healthText.setHealthText(health);

        if(health <= 0){
            survivalOutbreak.setActiveScene(3);
        }
    }
}