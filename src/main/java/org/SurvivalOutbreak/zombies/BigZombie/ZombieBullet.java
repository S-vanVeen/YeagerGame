package org.SurvivalOutbreak.zombies.BigZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.Collided;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.SceneBorderTouchingWatcher;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;
import com.github.hanyaeger.api.scenes.SceneBorder;
import org.SurvivalOutbreak.Player;

import java.util.List;

public class ZombieBullet extends DynamicSpriteEntity implements Collider, SceneBorderTouchingWatcher, Collided {
    private final int BULLET_DAMAGE = 15;

    public ZombieBullet(Coordinate2D startLocation, Coordinate2D targetLocation) {
        super("sprites/bullet.png", startLocation, new Size(10, 10));

        double angle = calculateAngle(startLocation, targetLocation);
        setMotion(3, angle);

        playSound();
    }

    private double calculateAngle(Coordinate2D start, Coordinate2D target) {
        double deltaX = target.getX() - start.getX();
        double deltaY = target.getY() - start.getY();

        return Math.toDegrees(Math.atan2(-deltaY, deltaX)) + 90;
    }

    private void playSound() {
        var shootSound = new com.github.hanyaeger.api.media.SoundClip("audio/schietGeluid.wav");
        shootSound.play();
    }

    @Override
    public void notifyBoundaryTouching(SceneBorder sceneBorder) {
        this.remove();
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof Player) {
                Player player = (Player) collider;
                System.out.println("Zombie bullet hit player!");
                //werkt niet geen tijd meer
                this.remove();
            }
        }
    }
}