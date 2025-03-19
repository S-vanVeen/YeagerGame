package org.example;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.Newtonian;
import com.github.hanyaeger.api.entities.SceneBorderTouchingWatcher;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;
import com.github.hanyaeger.api.scenes.SceneBorder;
import com.github.hanyaeger.api.userinput.KeyListener;
import javafx.scene.input.KeyCode;

import java.util.Set;

public class Player extends DynamicSpriteEntity implements KeyListener, SceneBorderTouchingWatcher, Newtonian {

    private SurvivalOutbreak survivalOutbreak;

    public Player(Coordinate2D location, SurvivalOutbreak survivalOutbreak) {
        super("sprites/Player_idle_front.png", location, new Size(25,25), 1, 1);

        this.survivalOutbreak = survivalOutbreak;

        setGravityConstant(0.0);
        setFrictionConstant(0.0);
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
        } else if(pressedKeys.contains(KeyCode.S)){
            setMotion(1,0d);
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

}