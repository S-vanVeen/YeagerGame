package org.example.scenes;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.scenes.DynamicScene;
import org.example.Player;
import org.example.SurvivalOutbreak;
import org.example.ui.HealthText;
import org.example.ui.RoundText;
import org.example.zombies.normalZombie.Zombie;

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
        RoundText roundText = new RoundText(new Coordinate2D(25, 25));
        addEntity(roundText);

        HealthText healthText = new HealthText(new Coordinate2D(50, getHeight() - 50));
        addEntity(healthText);

        var Player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), roundText,healthText, survivalOutbreak);
        addEntity(Player);

        Zombie Zombie = new Zombie(new Coordinate2D(getWidth(), getHeight()));
        addEntity(Zombie);
    }
}