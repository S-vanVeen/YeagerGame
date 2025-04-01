package org.example.scenes;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.scenes.DynamicScene;
import com.github.hanyaeger.api.TimerContainer;
import com.github.hanyaeger.api.Timer;
import com.github.hanyaeger.api.userinput.MouseButtonPressedListener;
import javafx.scene.input.MouseButton;
import org.example.Player;
import org.example.SurvivalOutbreak;
import org.example.ui.HealthBar;
import org.example.ui.RoundText;
import org.example.weapons.pistol.Bullet;
import org.example.zombies.normalZombie.Zombie;

import java.util.ArrayList;

public class GameScene extends DynamicScene implements TimerContainer, MouseButtonPressedListener {
    private final SurvivalOutbreak survivalOutbreak;
    private Zombie zombie;
    private Player player;

    // ArrayList to store bullets
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    public GameScene(SurvivalOutbreak survivalOutbreak) {
        this.survivalOutbreak = survivalOutbreak;
    }

    private final ArrayList<Zombie> zombies = new ArrayList<>();
    private final int maxZombies = 10;
    private final Coordinate2D[] spawnPoints = {
            new Coordinate2D(100, 100),
            new Coordinate2D(500, 200),
            new Coordinate2D(300, 400),
            new Coordinate2D(700, 50),
            new Coordinate2D(50, 500)
    };


    @Override
    public void setupScene() {
        setBackgroundImage("images/startscreen_bg.jpg");
    }

    @Override
    public void setupEntities() {
        HealthBar healthBar = new HealthBar(new Coordinate2D(50, getHeight() - 50), 100, 100, 10);
        addEntity(healthBar);
        addEntity(healthBar.getHealthText());

        attachMouseButtonPressedListener();

        RoundText roundText = new RoundText(new Coordinate2D(50, 50));
        addEntity(roundText);

        // Create player and add to scene
        player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), healthBar, roundText, survivalOutbreak);
        addEntity(player);

        // Spawn zombies
        for (int i = 0; i < maxZombies; i++) {
            Coordinate2D spawnPoint;

            if (i < spawnPoints.length) {
                spawnPoint = spawnPoints[i]; // Gebruik vaste spawnpunten als ze beschikbaar zijn
            } else {
                spawnPoint = new Coordinate2D(Math.random() * getWidth(), Math.random() * getHeight()); // Willekeurige locatie
            }

            Zombie zombie = new Zombie(player);
            zombie.setAnchorLocation(spawnPoint);
            zombies.add(zombie);
            addEntity(zombie);
        }
    }



    @Override
    public void setupTimers() {
        // Add timer to update zombie movement every 100ms
        addTimer(new ZombieUpdater(10));
    }

    @Override
    public void onMouseButtonPressed(MouseButton button, Coordinate2D coordinate2D) {
        if (button == MouseButton.PRIMARY) { // Left mouse button
            System.out.println("Mouse clicked at: X=" + coordinate2D.getX() + ", Y=" + coordinate2D.getY());

            Bullet bullet = new Bullet(player.getLocation(), coordinate2D);
            bullets.add(bullet);
            addEntity(bullet);
        }
    }

    // Inner class to handle zombie updates
    private class ZombieUpdater extends Timer {

        public ZombieUpdater(long intervalInMs) {
            super(intervalInMs);
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            for (Zombie zombie : zombies) {
                zombie.executeUpdates();
            }
        }
    }
}
