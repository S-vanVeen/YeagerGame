package org.example.zombies;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.Collided;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import javafx.scene.paint.Color;
import org.example.scenes.GameScene;
import org.example.weapons.pistol.Bullet;
import java.util.List;

public abstract class BaseHitBox extends RectangleEntity implements Collider, Collided {

    protected final BaseZombie zombie;
    protected final GameScene gameScene;
    protected final int damageAmount;

    protected BaseHitBox(final Coordinate2D initialPosition, BaseZombie zombie, GameScene gameScene,
                         double width, double height, int damageAmount) {
        super(initialPosition);
        this.zombie = zombie;
        this.gameScene = gameScene;
        this.damageAmount = damageAmount;
        setWidth(width);
        setHeight(height);
        setFill(Color.TRANSPARENT);
    }

    public int getDamageAmount() {
        return damageAmount;
    }

    public String getZombieType() {
        return zombie.getClass().getSimpleName();
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof Bullet) {
                Bullet bullet = (Bullet) collider;
                System.out.println(zombie.getClass().getSimpleName() + " hit with bullet!");
                bullet.remove();
                zombie.takeDamage(1);
            }
        }
    }
}