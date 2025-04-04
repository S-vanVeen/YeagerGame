package org.example.scenes;

import com.github.hanyaeger.api.AnchorPoint;
import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import com.github.hanyaeger.api.scenes.StaticScene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.buttons.PlayAgainButton;
import org.example.buttons.QuitButton;
import org.example.SurvivalOutbreak;
import org.example.ui.Cash;

public class EndScene extends StaticScene {
    private final SurvivalOutbreak survivalOutbreak;
    private Cash score;

    public EndScene(SurvivalOutbreak survivalOutbreak) {
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


        var EndSceneText = new TextEntity(
                new Coordinate2D(getWidth() / 2, getHeight() / 4),
                "Game Over"
        );
        EndSceneText.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        EndSceneText.setFill(Color.RED);
        EndSceneText.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        addEntity(EndSceneText);

        var EndSceneSubText = new TextEntity(
                new Coordinate2D(getWidth() / 2, getHeight() / 4 + 75),
                "Score: "
        );
        EndSceneSubText.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        EndSceneSubText.setFill(Color.WHITE);
        EndSceneSubText.setFont(Font.font("Impact", FontWeight.BOLD, 40));
        addEntity(EndSceneSubText);

        var PlayAgainButton = new PlayAgainButton(survivalOutbreak, new Coordinate2D(getWidth() / 2, getHeight() / 2 + 75));
        PlayAgainButton.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        addEntity(PlayAgainButton);

        var QuitButton = new QuitButton(survivalOutbreak, new Coordinate2D(getWidth() / 2, getHeight() / 2 + 125));
        QuitButton.setAnchorPoint(AnchorPoint.CENTER_CENTER);
        addEntity(QuitButton);
    }
}
