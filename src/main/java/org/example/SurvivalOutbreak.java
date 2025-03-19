package org.example;

import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.YaegerGame;

public class SurvivalOutbreak extends YaegerGame
{
    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void setupGame() {
        setGameTitle("Survival Outbreak");
        setSize(new Size(1920, 1080));
    }

    @Override
    public void setupScenes() {
        addScene(0, new TitleScene(this));
        addScene(1, new GameScene());
    }
}
