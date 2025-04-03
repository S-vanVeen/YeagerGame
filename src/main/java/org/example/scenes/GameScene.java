package org.example.scenes;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.scenes.DynamicScene;
import com.github.hanyaeger.api.TimerContainer;
import com.github.hanyaeger.api.Timer;
import com.github.hanyaeger.api.userinput.MouseButtonPressedListener;
import javafx.scene.input.MouseButton;
import org.example.Player;
import org.example.SurvivalOutbreak;
import org.example.ui.AmmoText;
import org.example.ui.Cash;
import org.example.ui.CountdownTimer;
import org.example.ui.HealthBar;
import org.example.ui.RoundText;
import org.example.weapons.Ammunition;
import org.example.weapons.pistol.Bullet;
import org.example.zombies.BaseZombie;
import org.example.zombies.BigZombie.BigZombie;
import org.example.zombies.NormalZombie.Zombie;
import org.example.ui.PurchaseOption;
import org.example.ui.PurchaseOption.PurchaseType;

import java.util.ArrayList;
import java.util.Random;

public class GameScene extends DynamicScene implements TimerContainer, MouseButtonPressedListener {
    private final SurvivalOutbreak survivalOutbreak;
    private RoundText roundText;
    private CountdownTimer countdownTimer;
    private Player player;
    private Cash cash;
    private Ammunition ammunition;
    private final ArrayList<BaseZombie> zombies = new ArrayList<>(); // Changed to BaseZombie
    private int maxZombies = 10;
    private static final int ROUND_DELAY_SECONDS = 30;
    private static final int STARTING_AMMO = 12;
    private static final int MAX_AMMO = 24;
    private static final int RELOAD_TIME_MS = 1000; // 2 seconds to reload
    private final Random random = new Random();
    private PurchaseOption healthUpgradeOption;
    private PurchaseOption ammoUpgradeOption;
    private PurchaseOption ammoRefillOption;
    private static final int HEALTH_UPGRADE_AMOUNT = 25;
    private static final int AMMO_UPGRADE_AMOUNT = 24;

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

        // Create ammo system and display
        ammunition = new Ammunition(STARTING_AMMO, MAX_AMMO);
        AmmoText ammoText = new AmmoText(new Coordinate2D(350, getHeight() - 60));
        ammunition.setAmmoText(ammoText);
        addEntity(ammoText);

        attachMouseButtonPressedListener();
        // No need to attach key listener here - the Player entity handles that

        roundText = new RoundText(new Coordinate2D(50, 50));
        roundText.setRoundText();
        addEntity(roundText);

        // Create countdown timer in center of screen, but hide it initially
        countdownTimer = new CountdownTimer(new Coordinate2D(getWidth() / 2 - 150, 25), ROUND_DELAY_SECONDS);
        countdownTimer.setOpacity(0); // Hide initially
        addEntity(countdownTimer);

        player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), healthBar, roundText, survivalOutbreak);
        player.setAmmunition(ammunition); // Connect player to ammunition system
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
        addTimer(new ReloadTimer(RELOAD_TIME_MS)); // Handle reloading
    }

    @Override
    public void onMouseButtonPressed(MouseButton button, Coordinate2D coordinate2D) {
        if (button == MouseButton.PRIMARY && !player.isReloading()) {
            if (ammunition.getCurrentAmmo() > 0) {
                System.out.println("Mouse clicked at: X=" + coordinate2D.getX() + ", Y=" + coordinate2D.getY());
                Bullet bullet = new Bullet(player.getLocation(), coordinate2D);
                addEntity(bullet);
                ammunition.useAmmo();
            } else {
                System.out.println("Out of ammo! Press R to reload.");
                // Auto-trigger reload when trying to shoot with no ammo, but only if standing still
                if (player.getSpeed() == 0) {
                    player.startReloading();
                } else {
                    System.out.println("Stop moving to reload!");
                }
            }
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
                showPurchaseOptions(); // Show purchase options when countdown starts

                // Reload ammunition at the end of each round
                ammunition.reload();

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
                    hidePurchaseOptions(); // Hide purchase options
                    startNextRound();
                    countdownActive = false;
                    countdownTimer.setOpacity(0); // Hide the countdown timer
                    roundReadyToStart = false; // Reset round status
                }
            }
        }
    }

    private class ReloadTimer extends Timer {
        private int reloadDuration;
        private int reloadProgress = 0;

        public ReloadTimer(long intervalInMs) {
            super(50); // Update every 50ms for smooth reload
            this.reloadDuration = (int)(intervalInMs / 50); // Convert to timer ticks
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            // Check if player is reloading
            if (player.isReloading()) {
                reloadProgress++;

                // Reload complete
                if (reloadProgress >= reloadDuration) {
                    ammunition.reload();
                    player.completeReload();
                    reloadProgress = 0;
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
    private void showPurchaseOptions() {
        // Position on the right side of the screen
        double rightSideX = getWidth() - 400; // 300 pixels from the right edge

        // Create purchase options if they don't exist yet
        if (healthUpgradeOption == null) {
            healthUpgradeOption = new PurchaseOption(
                    new Coordinate2D(rightSideX, 150),
                    PurchaseType.HEALTH_UPGRADE,
                    this::processPurchase
            );
            ammoUpgradeOption = new PurchaseOption(
                    new Coordinate2D(rightSideX, 200),
                    PurchaseType.AMMO_UPGRADE,
                    this::processPurchase
            );
            ammoRefillOption = new PurchaseOption(
                    new Coordinate2D(rightSideX, 250),
                    PurchaseType.AMMO_REFILL,
                    this::processPurchase
            );
        }

        // Add entities to the scene
        addEntity(healthUpgradeOption);
        addEntity(ammoUpgradeOption);
        addEntity(ammoRefillOption);
    }

    /**
     * Hides purchase options
     */
    private void hidePurchaseOptions() {
        if (healthUpgradeOption != null) {
            healthUpgradeOption.remove();
            ammoUpgradeOption.remove();
            ammoRefillOption.remove();
        }
    }

    /**
     * Processes a purchase when a purchase option is clicked
     */
    private void processPurchase(PurchaseType type, int cost) {
        // Check if player can afford the purchase
        if (cash.getAmount() >= cost) {
            cash.increase(-cost); // Deduct cost

            switch (type) {
                case HEALTH_UPGRADE:
                    // Increase player's max health
                    player.increaseMaxHealth(HEALTH_UPGRADE_AMOUNT);
                    System.out.println("Health upgraded! +25 max health");
                    break;

                case AMMO_UPGRADE:
                    // Increase max ammo capacity
                    ammunition.increaseMaxAmmo(AMMO_UPGRADE_AMOUNT);
                    System.out.println("Ammo capacity upgraded! +24 max ammo");
                    break;

                case AMMO_REFILL:
                    // Refill ammo to max capacity
                    ammunition.reload();
                    System.out.println("Ammo refilled to maximum!");
                    break;
            }
        } else {
            System.out.println("Not enough cash! Need $" + cost);
        }
    }

}