package org.SurvivalOutbreak.weapons.pistol;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.Size;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.SceneBorderTouchingWatcher;
import com.github.hanyaeger.api.entities.impl.DynamicSpriteEntity;
import com.github.hanyaeger.api.scenes.SceneBorder;

public class Bullet extends DynamicSpriteEntity implements Collider, SceneBorderTouchingWatcher {

    public Bullet(Coordinate2D startLocation, Coordinate2D targetLocation) {
        super("sprites/bullet.png", startLocation, new Size(10, 10)); // Pas de sprite aan

        // Bereken richting
        double angle = calculateAngle(startLocation, targetLocation);

        // Stel snelheid en richting in
        setMotion(5, angle); // Snelheid = 5, richting berekend

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

}
