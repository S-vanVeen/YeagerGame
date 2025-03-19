package org.example;

import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.YaegerGame;

public class OutbreakSurvival extends YaegerGame
{
    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void setupGame() {
        setGameTitle("Outbreak Survival");
        setSize(new Size(1920, 1080));
    }

    @Override
    public void setupScenes() {
        addScene(0, new TitleScene(this));
    }
}
