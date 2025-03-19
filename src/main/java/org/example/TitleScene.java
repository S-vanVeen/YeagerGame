package org.example;

import com.github.hanyaeger.api.AnchorPoint;
import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import com.github.hanyaeger.api.scenes.StaticScene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TitleScene extends StaticScene {
    private final SurvivalOutbreak survivalOutbreak;

    public TitleScene(SurvivalOutbreak survivalOutbreak) {
        this.survivalOutbreak = survivalOutbreak;
    }

    @Override
    public void setupScene() {
        setBackgroundAudioVolume(0.05);
        setBackgroundAudio("audio/zombie_sound.mp3");
        setBackgroundImage("images/startscreen_bg.jpg");
    }

    @Override
    public void setupEntities() {


        var titleSceneText = new TextEntity(
                new Coordinate2D(getWidth() / 2, getHeight() / 4),
                "Survival Outbreak"
        );
        titleSceneText.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        titleSceneText.setFill(Color.RED);
        titleSceneText.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        addEntity(titleSceneText);

        var titleSceneSubText = new TextEntity(
                new Coordinate2D(getWidth() / 2, getHeight() / 4 + 75),
                "The last survivor on earth"
        );
        titleSceneSubText.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        titleSceneSubText.setFill(Color.WHITE);
        titleSceneSubText.setFont(Font.font("Impact", FontWeight.BOLD, 40));
        addEntity(titleSceneSubText);

        var StartKnop = new StartKnop(survivalOutbreak, new Coordinate2D(getWidth() / 2, getHeight() / 2 + 75));
        StartKnop.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        addEntity(StartKnop);
    }
}
