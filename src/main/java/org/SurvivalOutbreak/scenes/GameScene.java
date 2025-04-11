package org.SurvivalOutbreak.scenes;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.YaegerEntity;
import com.github.hanyaeger.api.scenes.DynamicScene;
import com.github.hanyaeger.api.TimerContainer;
import com.github.hanyaeger.api.Timer;
import com.github.hanyaeger.api.userinput.MouseButtonPressedListener;
import javafx.scene.input.MouseButton;
import org.SurvivalOutbreak.Player;
import org.SurvivalOutbreak.SurvivalOutbreak;
import org.SurvivalOutbreak.ui.AmmoText;
import org.SurvivalOutbreak.ui.Cash;
import org.SurvivalOutbreak.ui.CountdownTimer;
import org.SurvivalOutbreak.ui.HealthBar;
import org.SurvivalOutbreak.ui.RoundText;
import org.SurvivalOutbreak.weapons.Ammunition;
import org.SurvivalOutbreak.weapons.pistol.Bullet;
import org.SurvivalOutbreak.zombies.BaseZombie;
import org.SurvivalOutbreak.zombies.BigZombie.BigZombie;
import org.SurvivalOutbreak.zombies.NormalZombie.Zombie;
import org.SurvivalOutbreak.ui.PurchaseOption;
import org.SurvivalOutbreak.ui.PurchaseOption.PurchaseType;

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
    private int startZombies = 5;
    private final double ZOMBIE_SPAWN_FACTOR = 1.5;
    private final int ROUND_DELAY_SECONDS = 10;
    private final int STARTING_AMMO = 12;
    private int maxAmmo = 24;
    private final int RELOAD_TIME_MS = 1000;
    private final Random random = new Random();
    private PurchaseOption healthUpgradeOption;
    private PurchaseOption ammoUpgradeOption;
    //private PurchaseOption ammoRefillOption;
    private final int HEALTH_UPGRADE_AMOUNT = 10;
    private final int AMMO_UPGRADE_AMOUNT = 2;
    private final int MAX_ACTIVE_ZOMBIES = 50;
    private int zombiesToSpawn = 0;
    private boolean allZombiesSpawned = false;
    private final double ZOMBIE_ATTRIBUTE_SCALING_FACTOR = 1.05;


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
        setBackgroundAudioVolume(0.15);
        setBackgroundAudio("audio/zombieGeluid.m4a");
        setBackgroundImage("images/achtergrond.jpg");
    }

    @Override
    public void setupEntities() {
        HealthBar healthBar = new HealthBar(new Coordinate2D(50, getHeight() - 50), 100, 100, 10);
        addEntity(healthBar);
        addEntity(healthBar.getHealthText());

        cash = new Cash(new Coordinate2D(200, getHeight() - 60));
        addEntity(cash.getCashText());

        ammunition = new Ammunition(STARTING_AMMO, maxAmmo);
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
        zombiesToSpawn = startZombies;
        allZombiesSpawned = false;
        int initialSpawnCount = Math.min(zombiesToSpawn, MAX_ACTIVE_ZOMBIES);
        for (int i = 0; i < initialSpawnCount; i++) {
            spawnSingleZombie();
            zombiesToSpawn--;
        }
        if (zombiesToSpawn <= 0) {
            allZombiesSpawned = true;
        }
    }

    private double getZombieScalingFactor() {
        int currentRound = roundText.getRound();
        return Math.pow(ZOMBIE_ATTRIBUTE_SCALING_FACTOR, currentRound - 1);
    }

    private void spawnSingleZombie() {
        if (zombies.size() >= MAX_ACTIVE_ZOMBIES) {
            return;
        }

        Coordinate2D spawnPoint;
        int index = zombies.size() % spawnPoints.length;
        spawnPoint = spawnPoints[index];

        double offsetX = random.nextDouble() * 100 - 50; // -50 to +50
        double offsetY = random.nextDouble() * 100 - 50; // -50 to +50
        spawnPoint = new Coordinate2D(spawnPoint.getX() + offsetX, spawnPoint.getY() + offsetY);

        double scalingFactor = getZombieScalingFactor();

        BaseZombie zombie;
        if (random.nextDouble() < 0.2) {
            double scaledSpeed = 0.7 * scalingFactor;  // DEFAULT_SPEED
            int scaledReward = (int)(100 * scalingFactor); // DEFAULT_REWARD
            int scaledHealth = (int)Math.ceil(3 * scalingFactor); // DEFAULT_HEALTH

            zombie = new BigZombie(player, this, scaledSpeed, scaledReward, scaledHealth);
        } else {
            double scaledSpeed = 1.0 * scalingFactor; // DEFAULT_SPEED
            int scaledReward = (int)(50 * scalingFactor); // DEFAULT_REWARD
            int scaledHealth = (int)Math.ceil(1 * scalingFactor); // DEFAULT_HEALTH

            zombie = new Zombie(player, this, scaledSpeed, scaledReward, scaledHealth);
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
                // automatisch herladen waneer je zonder ammo probeert te schieten
                if (player.getSpeed() == 0) {
                    player.startReloading();
                } else {
                    System.out.println("Stop moving to reload!");
                }
            }
        }
    }
    private void playZombieSound() {
        var shootSound = new com.github.hanyaeger.api.media.SoundClip("audio/zombieDood.wav");
        shootSound.play();
    }

    public void addEntityToScene(YaegerEntity entity) {
        addEntity(entity);
    }

    public void removeZombie(BaseZombie zombie) {
        playZombieSound();
        zombies.remove(zombie);

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
        startZombies = (int) (startZombies * ZOMBIE_SPAWN_FACTOR);

        double scalingFactor = getZombieScalingFactor();
        System.out.println("Round " + roundText.getRound() +
                " started with " + startZombies +
                " zombies! Zombie attributes scaled by factor: " +
                String.format("%.2f", scalingFactor));

        prepareZombiesForRound();
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
        }

        addEntity(healthUpgradeOption);
        addEntity(ammoUpgradeOption);
    }

    private void hidePurchaseOptions() {
        if (healthUpgradeOption != null) {
            healthUpgradeOption.remove();
            ammoUpgradeOption.remove();
        }
    }

    private void processPurchase(PurchaseType type, int cost) {
        if (cash.getAmount() >= cost) {
            cash.increase(-cost);

            switch (type) {
                case HEALTH_UPGRADE:
                    player.increaseMaxHealth(HEALTH_UPGRADE_AMOUNT);
                    System.out.println("Health upgraded! +" + HEALTH_UPGRADE_AMOUNT + " max health");
                    break;

                case AMMO_UPGRADE:
                    ammunition.increaseMaxAmmo(AMMO_UPGRADE_AMOUNT);
                    System.out.println("Ammo capacity upgraded! +" + AMMO_UPGRADE_AMOUNT +" max ammo");
                    break;

            }
        } else {
            System.out.println("Not enough cash! Need $" + cost);
        }
    }
}