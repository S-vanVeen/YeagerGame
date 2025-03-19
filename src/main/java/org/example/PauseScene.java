package org.example;

import com.github.hanyaeger.api.AnchorPoint;
import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import com.github.hanyaeger.api.scenes.StaticScene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PauseScene extends StaticScene {
    private final SurvivalOutbreak survivalOutbreak;

    public PauseScene(SurvivalOutbreak survivalOutbreak) {
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


        var PauseSceneText = new TextEntity(
                new Coordinate2D(getWidth() / 2, getHeight() / 4),
                "Gepauzeerd"
        );
        PauseSceneText.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        PauseSceneText.setFill(Color.WHITE);
        PauseSceneText.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        addEntity(PauseSceneText);

        var PauseSceneSubText = new TextEntity(
                new Coordinate2D(getWidth() / 2, getHeight() / 4 + 75),
                "Score: 23530"
        );
        PauseSceneSubText.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        PauseSceneSubText.setFill(Color.WHITE);
        PauseSceneSubText.setFont(Font.font("Impact", FontWeight.BOLD, 40));
        addEntity(PauseSceneSubText);

        var ResumeButton = new ResumeButton(survivalOutbreak, new Coordinate2D(getWidth() / 2, getHeight() / 2 + 75));
        ResumeButton.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        addEntity(ResumeButton);
    }
}
