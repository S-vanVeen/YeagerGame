package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.Collided;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import javafx.scene.paint.Color;
import org.example.scenes.GameScene;
import org.example.weapons.pistol.Bullet;
import java.util.List;

public class HitBox extends RectangleEntity implements Collider, Collided {

    private final Zombie zombie; // Referentie naar de zombie
    private final GameScene gameScene; // Verwijzing naar de GameScene

    // Pas de constructor aan zodat je ook de GameScene meegeeft
    protected HitBox(final Coordinate2D initialPosition, Zombie zombie, GameScene gameScene) {
        super(initialPosition);
        this.zombie = zombie;
        this.gameScene = gameScene;
        setWidth(20);
        setHeight(25);
        setFill(Color.TRANSPARENT);
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof Bullet) {
                Bullet bullet = (Bullet) collider;

                System.out.println("Zombie geraakt met schot!");

                bullet.remove(); // Verwijder de kogel uit de game
                zombie.remove(); // Verwijder de zombie uit de game
                gameScene.removeZombie(zombie); // Verwijder de zombie uit de lijst
            }
        }
    }
}
