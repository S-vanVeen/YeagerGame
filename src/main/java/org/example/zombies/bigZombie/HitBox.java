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

    private final BigZombie zombie; // Reference to the BigZombie
    private final GameScene gameScene; // Reference to the GameScene

    protected HitBox(final Coordinate2D initialPosition, BigZombie zombie, GameScene gameScene) {
        super(initialPosition);
        this.zombie = zombie;
        this.gameScene = gameScene;
        setWidth(35);  // Larger hitbox
        setHeight(40); // Taller hitbox
        setFill(Color.TRANSPARENT);
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof Bullet) {
                Bullet bullet = (Bullet) collider;

                System.out.println("BigZombie hit with bullet!");

                // Remove the bullet
                bullet.remove();

                // Apply damage to the zombie (will handle death internally if health <= 0)
                zombie.takeDamage(1);
            }
        }
    }
}