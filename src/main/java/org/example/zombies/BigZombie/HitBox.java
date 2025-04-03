package org.example.zombies.BigZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.Collided;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import javafx.scene.paint.Color;
import org.example.scenes.GameScene;
import org.example.weapons.pistol.Bullet;
import java.util.List;

public class HitBox extends RectangleEntity implements Collider, Collided {

    private final BigZombie zombie;
    private final GameScene gameScene;

    protected HitBox(final Coordinate2D initialPosition, BigZombie zombie, GameScene gameScene) {
        super(initialPosition);
        this.zombie = zombie;
        this.gameScene = gameScene;
        setWidth(35);
        setHeight(40);
        setFill(Color.TRANSPARENT);
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof Bullet) {
                Bullet bullet = (Bullet) collider;
                System.out.println("BigZombie hit with bullet!");
                bullet.remove();
                zombie.takeDamage(1);
            }
        }
    }
}