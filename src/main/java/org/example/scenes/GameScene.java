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
    private RoundText roundText;
    private Player player;
    private final ArrayList<Zombie> zombies = new ArrayList<>();
    private int maxZombies = 10;

    private final Coordinate2D[] spawnPoints = {
            new Coordinate2D(100, 100),
            new Coordinate2D(500, 200),
            new Coordinate2D(300, 400)
    };

    private boolean roundReadyToStart = false; // Dit wordt gebruikt om aan te geven of de nieuwe ronde moet beginnen.

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

        attachMouseButtonPressedListener();

        roundText = new RoundText(new Coordinate2D(50, 50)); // Hier moet roundText een instantie zijn van RoundText
        addEntity(roundText);

        player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), healthBar, roundText, survivalOutbreak);
        addEntity(player);

        spawnZombies();
    }

    private void spawnZombies() {
        zombies.clear();
        for (int i = 0; i < maxZombies; i++) {
            Coordinate2D spawnPoint = (i < spawnPoints.length) ? spawnPoints[i] : new Coordinate2D(Math.random() * getWidth(), Math.random() * getHeight());
            Zombie zombie = new Zombie(player, this);
            zombie.setAnchorLocation(spawnPoint);
            zombies.add(zombie);
            addEntity(zombie);
        }
    }

    @Override
    public void setupTimers() {
        addTimer(new ZombieUpdater(10)); // Update zombies every 10 ms
        addTimer(new RoundManager(30000)); // 30 seconds timer for a new round (after all zombies are killed)
    }

    @Override
    public void onMouseButtonPressed(MouseButton button, Coordinate2D coordinate2D) {
        if (button == MouseButton.PRIMARY) {
            System.out.println("Mouse clicked at: X=" + coordinate2D.getX() + ", Y=" + coordinate2D.getY());
            Bullet bullet = new Bullet(player.getLocation(), coordinate2D);
            addEntity(bullet);
        }
    }

    public void removeZombie(Zombie zombie) {
        zombies.remove(zombie);
    }

    private class ZombieUpdater extends Timer {
        public ZombieUpdater(long intervalInMs) {
            super(intervalInMs);
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            if (zombies.isEmpty() && !roundReadyToStart) {
                // Alle zombies zijn verslagen, we stellen de ronde als klaar om te starten in
                roundReadyToStart = true;
                System.out.println("Alle zombies zijn verslagen, wacht 30 seconden voor de nieuwe ronde.");
            } else {
                for (Zombie zombie : zombies) {
                    zombie.executeUpdates(); // Perform updates for each zombie
                }
            }
        }
    }

    private class RoundManager extends Timer {
        public RoundManager(long intervalInMs) {
            super(intervalInMs);
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            if (roundReadyToStart) {
                // Wacht 60 seconden na het verslaan van alle zombies om de nieuwe ronde te starten
                startNextRound();
                roundReadyToStart = false; // Reset de ronde status
            }
        }
    }

    private void startNextRound() {
        roundText.verhoogRonde();
        roundText.setRoundText();
        maxZombies = (int) (maxZombies * 1.5);
        spawnZombies();
        System.out.println("Ronde " + roundText.getRound() + " gestart met " + maxZombies + " zombies!");
    }
}
