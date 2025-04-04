package org.example;

import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.YaegerGame;
import org.example.scenes.EndScene;
import org.example.scenes.GameScene;
import org.example.scenes.PauseScene;
import org.example.scenes.TitleScene;

public class SurvivalOutbreak extends YaegerGame
{
    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void setupGame() {
        setGameTitle("Survival Outbreak");
        setSize(new Size(1200, 600));
    }

    @Override
    public void setupScenes() {
        addScene(0, new TitleScene(this));
        addScene(1, new GameScene(this));
        addScene(2, new PauseScene(this));
        addScene(3, new EndScene(this));
    }
}
