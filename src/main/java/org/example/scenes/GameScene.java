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
    private final ArrayList<BaseZombie> zombies = new ArrayList<>();
    private int maxZombies = 10;
    private int ROUND_DELAY_SECONDS = 30;
    private int STARTING_AMMO = 12;
    private int MAX_AMMO = 24;
    private int RELOAD_TIME_MS = 1000;
    private final Random random = new Random();
    private PurchaseOption healthUpgradeOption;
    private PurchaseOption ammoUpgradeOption;
    private PurchaseOption ammoRefillOption;
    private int HEALTH_UPGRADE_AMOUNT = 25;
    private int AMMO_UPGRADE_AMOUNT = 24;
    private final int MAX_ACTIVE_ZOMBIES = 50;
    private int zombiesToSpawn = 0;
    private boolean allZombiesSpawned = false;


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

        cash = new Cash(new Coordinate2D(200, getHeight() - 60));
        addEntity(cash.getCashText());

        ammunition = new Ammunition(STARTING_AMMO, MAX_AMMO);
        AmmoText ammoText = new AmmoText(new Coordinate2D(350, getHeight() - 60));
        ammunition.setAmmoText(ammoText);
        addEntity(ammoText);

        attachMouseButtonPressedListener();

        roundText = new RoundText(new Coordinate2D(50, 50));
        roundText.setRoundText();
        addEntity(roundText);

        countdownTimer = new CountdownTimer(new Coordinate2D(getWidth() / 2 - 150, 25), ROUND_DELAY_SECONDS);
        countdownTimer.setOpacity(0); // Hide initially
        addEntity(countdownTimer);

        player = new Player(new Coordinate2D(getWidth() / 2, getHeight() / 2), healthBar, roundText, survivalOutbreak);
        player.setAmmunition(ammunition); // Connect player to ammunition system
        addEntity(player);

        prepareZombiesForRound();
    }

    private void prepareZombiesForRound() {
        zombies.clear();
        zombiesToSpawn = maxZombies;
        allZombiesSpawned = false;

        // Spawn initial zombies up to the maximum allowed
        int initialSpawnCount = Math.min(zombiesToSpawn, MAX_ACTIVE_ZOMBIES);
        for (int i = 0; i < initialSpawnCount; i++) {
            spawnSingleZombie();
            zombiesToSpawn--;
        }

        // If we've spawned all zombies for this round
        if (zombiesToSpawn <= 0) {
            allZombiesSpawned = true;
        }
    }

    private void spawnSingleZombie() {
        if (zombies.size() >= MAX_ACTIVE_ZOMBIES) {
            return; // Do not spawn if we're at max capacity
        }

        // Pick a spawn point
        Coordinate2D spawnPoint;
        int index = zombies.size() % spawnPoints.length;
        spawnPoint = spawnPoints[index];

        // With some randomization so they don't all spawn on the exact same point
        double offsetX = random.nextDouble() * 100 - 50; // -50 to +50
        double offsetY = random.nextDouble() * 100 - 50; // -50 to +50
        spawnPoint = new Coordinate2D(spawnPoint.getX() + offsetX, spawnPoint.getY() + offsetY);

        // Create zombie based on probability
        BaseZombie zombie;
        if (random.nextDouble() < 0.2) { // 20% chance for BigZombie
            zombie = new BigZombie(player, this);
        } else {
            zombie = new Zombie(player, this);
        }

        zombie.setAnchorLocation(spawnPoint);
        zombies.add(zombie);
        addEntity(zombie);
    }

    @Override
    public void setupTimers() {
        addTimer(new ZombieUpdater(10)); // Update zombies elke 10ms
        addTimer(new RoundManager(1000)); // Checkt status van ronde en voor de countdown
        addTimer(new ReloadTimer(RELOAD_TIME_MS));
        addTimer(new ZombieSpawner(500)); // Check every half second if we can spawn more zombies
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
                // automatisch herladen waneer je zonder ammo probeert te shcieten
                if (player.getSpeed() == 0) {
                    player.startReloading();
                } else {
                    System.out.println("Stop moving to reload!");
                }
            }
        }
    }

    public void removeZombie(BaseZombie zombie) {
        zombies.remove(zombie);

        // When a zombie is removed and there are more to spawn, spawn a new one
        if (!allZombiesSpawned && zombiesToSpawn > 0 && zombies.size() < MAX_ACTIVE_ZOMBIES) {
            spawnSingleZombie();
            zombiesToSpawn--;

            if (zombiesToSpawn <= 0) {
                allZombiesSpawned = true;
            }
        }
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
            if (zombies.isEmpty() && allZombiesSpawned && !roundReadyToStart && !countdownActive) {
                roundReadyToStart = true;
                countdownActive = true;
                countdownTimer.reset(ROUND_DELAY_SECONDS);
                countdownTimer.setOpacity(1);
                showPurchaseOptions();

                ammunition.reload();

                System.out.println("All zombies defeated. Waiting " + ROUND_DELAY_SECONDS + " seconds for the next round.");
            } else if (!zombies.isEmpty()) {
                for (BaseZombie zombie : zombies) {
                    zombie.executeUpdates();
                }
            }
        }
    }

    private class ZombieSpawner extends Timer {
        public ZombieSpawner(long intervalInMs) {
            super(intervalInMs);
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            // If there are zombies to spawn and we're below the maximum active zombies
            if (!allZombiesSpawned && zombiesToSpawn > 0 && zombies.size() < MAX_ACTIVE_ZOMBIES) {
                spawnSingleZombie();
                zombiesToSpawn--;

                if (zombiesToSpawn <= 0) {
                    allZombiesSpawned = true;
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
                countdownTimer.tick(); // elke seconde

                if (countdownTimer.getSecondsLeft() <= 0) {
                    hidePurchaseOptions();
                    startNextRound();
                    countdownActive = false;
                    countdownTimer.setOpacity(0);
                    roundReadyToStart = false;
                }
            }
        }
    }

    private class ReloadTimer extends Timer {
        private int reloadDuration;
        private int reloadProgress = 0;

        public ReloadTimer(long intervalInMs) {
            super(50); //
            this.reloadDuration = (int)(intervalInMs / 50); // Convert to timer ticks
        }

        @Override
        public void onAnimationUpdate(long timestamp) {
            if (player.isReloading()) {
                reloadProgress++;

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
        prepareZombiesForRound();
        System.out.println("Round " + roundText.getRound() + " started with " + maxZombies + " zombies!");
    }
    private void showPurchaseOptions() {
        double rightSideX = getWidth() - 400;

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

        addEntity(healthUpgradeOption);
        addEntity(ammoUpgradeOption);
        addEntity(ammoRefillOption);
    }

    private void hidePurchaseOptions() {
        if (healthUpgradeOption != null) {
            healthUpgradeOption.remove();
            ammoUpgradeOption.remove();
            ammoRefillOption.remove();
        }
    }

    private void processPurchase(PurchaseType type, int cost) {
        if (cash.getAmount() >= cost) {
            cash.increase(-cost);

            switch (type) {
                case HEALTH_UPGRADE:
                    player.increaseMaxHealth(HEALTH_UPGRADE_AMOUNT);
                    System.out.println("Health upgraded! +25 max health");
                    break;

                case AMMO_UPGRADE:
                    ammunition.increaseMaxAmmo(AMMO_UPGRADE_AMOUNT);
                    System.out.println("Ammo capacity upgraded! +24 max ammo");
                    break;

                case AMMO_REFILL:
                    ammunition.reload();
                    System.out.println("Ammo refilled to maximum!");
                    break;
            }
        } else {
            System.out.println("Not enough cash! Need $" + cost);
        }
    }
}