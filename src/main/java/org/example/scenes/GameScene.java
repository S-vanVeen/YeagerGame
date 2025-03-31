package org.example.scenes;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.scenes.DynamicScene;
import com.github.hanyaeger.api.TimerContainer;
import com.github.hanyaeger.api.Timer;
import org.example.Player;
import org.example.SurvivalOutbreak;
import org.example.ui.HealthBar;
import org.example.ui.RoundText;
import org.example.zombies.normalZombie.Zombie;

public class GameScene extends DynamicScene implements TimerContainer {
    private final SurvivalOutbreak survivalOutbreak;
    private Zombie zombie;
    private Player player;

    public GameScene(SurvivalOutbreak survivalOutbreak) {
        this.survivalOutbreak = survivalOutbreak;
    }

    @Override
    public void setupScene() {
        setBackgroundImage("images/startscreen_bg.jpg");
    }

    @Override
    public void setupEntities() {
        HealthBar healthBar = new HealthBar(new Coordinate2D(50, getHeight() - 50), 100, 100, 10);
        addEntity(healthBar);
        addEntity(healthBar.getHealthText());


        RoundText roundText = new RoundText(new Coordinate2D(50, 50));
        addEntity(roundText);

        // Create player and add to scene
        player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), healthBar, roundText,survivalOutbreak);
        addEntity(player);

        // Create zombie with player location
        zombie = new Zombie(player);
        addEntity(zombie);
    }

    @Override
    public void setupTimers() {
        // Add timer to update zombie movement every 100ms
        addTimer(new ZombieUpdater(10));
    }

    // Inner class to handle zombie updates
    private class ZombieUpdater extends Timer {

        public ZombieUpdater(long intervalInMs) {
            super(intervalInMs);
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            if (zombie != null) {
                zombie.executeUpdates();
            }
        }
    }
}