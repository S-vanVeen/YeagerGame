package org.example.scenes;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.scenes.DynamicScene;
import com.github.hanyaeger.api.TimerContainer;
import com.github.hanyaeger.api.Timer;
import com.github.hanyaeger.api.userinput.MouseButtonPressedListener;
import javafx.scene.input.MouseButton;
import org.example.Player;
import org.example.SurvivalOutbreak;
import org.example.ui.Cash;
import org.example.ui.CountdownTimer;
import org.example.ui.HealthBar;
import org.example.ui.RoundText;
import org.example.weapons.pistol.Bullet;
import org.example.zombies.BaseZombie;
import org.example.zombies.BigZombie.BigZombie;
import org.example.zombies.NormalZombie.Zombie;

import java.util.ArrayList;
import java.util.Random;

public class GameScene extends DynamicScene implements TimerContainer, MouseButtonPressedListener {
    private final SurvivalOutbreak survivalOutbreak;
    private RoundText roundText;
    private CountdownTimer countdownTimer;
    private Player player;
    private Cash cash;
    private final ArrayList<BaseZombie> zombies = new ArrayList<>(); // Changed to BaseZombie
    private int maxZombies = 10;
    private static final int ROUND_DELAY_SECONDS = 30;
    private final Random random = new Random();

    private final Coordinate2D[] spawnPoints = {
            new Coordinate2D(100, 100),
            new Coordinate2D(500, 200),
            new Coordinate2D(300, 400)
    };

    private boolean roundReadyToStart = false;
    private boolean countdownActive = false;

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

        // Create Cash display to the right of the health bar
        cash = new Cash(new Coordinate2D(200, getHeight() - 60));
        addEntity(cash.getCashText());

        attachMouseButtonPressedListener();

        roundText = new RoundText(new Coordinate2D(50, 50));
        roundText.setRoundText();
        addEntity(roundText);

        // Create countdown timer in center of screen, but hide it initially
        countdownTimer = new CountdownTimer(new Coordinate2D(getWidth() / 2 - 150, 25), ROUND_DELAY_SECONDS);
        countdownTimer.setOpacity(0); // Hide initially
        addEntity(countdownTimer);

        player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), healthBar, roundText, survivalOutbreak);
        addEntity(player);

        spawnZombies();
    }

    private void spawnZombies() {
        zombies.clear();
        for (int i = 0; i < maxZombies; i++) {
            Coordinate2D spawnPoint = (i < spawnPoints.length) ?
                    spawnPoints[i] :
                    new Coordinate2D(Math.random() * getWidth(), Math.random() * getHeight());

            // Decide what type of zombie to spawn (20% chance for BigZombie)
            BaseZombie zombie;
            if (random.nextDouble() < 0.2) {
                zombie = new BigZombie(player, this);
            } else {
                zombie = new Zombie(player, this);
            }

            zombie.setAnchorLocation(spawnPoint);
            zombies.add(zombie);
            addEntity(zombie);
        }
    }

    @Override
    public void setupTimers() {
        addTimer(new ZombieUpdater(10)); // Update zombies every 10 ms
        addTimer(new RoundManager(1000)); // Check round status every second and handle countdown
    }

    @Override
    public void onMouseButtonPressed(MouseButton button, Coordinate2D coordinate2D) {
        if (button == MouseButton.PRIMARY) {
            System.out.println("Mouse clicked at: X=" + coordinate2D.getX() + ", Y=" + coordinate2D.getY());
            Bullet bullet = new Bullet(player.getLocation(), coordinate2D);
            addEntity(bullet);
        }
    }

    // Updated to accept BaseZombie instead of just Zombie
    public void removeZombie(BaseZombie zombie) {
        zombies.remove(zombie);
    }

    public void addCash(int amount) {
        cash.increase(amount);
        System.out.println("Cash increased by " + amount + ". New total: $" + cash.getAmount());
    }

    private class ZombieUpdater extends Timer {
        public ZombieUpdater(long intervalInMs) {
            super(intervalInMs);
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            if (zombies.isEmpty() && !roundReadyToStart && !countdownActive) {
                // All zombies are defeated, start the countdown
                roundReadyToStart = true;
                countdownActive = true;
                countdownTimer.reset(ROUND_DELAY_SECONDS);
                countdownTimer.setOpacity(1); // Show the countdown timer
                System.out.println("All zombies defeated. Waiting " + ROUND_DELAY_SECONDS + " seconds for the next round.");
            } else if (!zombies.isEmpty()) {
                for (BaseZombie zombie : zombies) {
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
            if (countdownActive) {
                countdownTimer.tick(); // Update countdown every second

                if (countdownTimer.getSecondsLeft() <= 0) {
                    // Countdown finished, start the next round
                    startNextRound();
                    countdownActive = false;
                    countdownTimer.setOpacity(0); // Hide the countdown timer
                    roundReadyToStart = false; // Reset round status
                }
            }
        }
    }

    private void startNextRound() {
        roundText.verhoogRonde();
        roundText.setRoundText();
        maxZombies = (int) (maxZombies * 1.5);
        spawnZombies();
        System.out.println("Round " + roundText.getRound() + " started with " + maxZombies + " zombies!");
    }
}