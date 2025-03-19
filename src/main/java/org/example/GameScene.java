package org.example;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.scenes.DynamicScene;

public class GameScene extends DynamicScene {
    private final SurvivalOutbreak survivalOutbreak;

    public GameScene(SurvivalOutbreak survivalOutbreak) {
        this.survivalOutbreak = survivalOutbreak;
    }

    @Override
    public void setupScene() {
        setBackgroundImage("images/startscreen_bg.jpg");
    }

    @Override
    public void setupEntities() {
        var Player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), survivalOutbreak);
        addEntity(Player);
    }
}