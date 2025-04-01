package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.Collided;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import javafx.scene.paint.Color;
import org.example.weapons.pistol.Bullet;
import java.util.List;

public class HitBox extends RectangleEntity implements Collider, Collided {

    private final Zombie zombie; // Referentie naar de zombie

    protected HitBox(final Coordinate2D initialPosition, Zombie zombie) {
        super(initialPosition);
        this.zombie = zombie; // Bewaar de referentie
        setWidth(20);
        setHeight(25);
        setFill(Color.TRANSPARENT);
    }

    @Override
    public void onCollision(List<Collider> colliders) {
        for (Collider collider : colliders) {
            if (collider instanceof Bullet) {
                System.out.println("Zombie geraakt met schot!");
                zombie.remove(); // Verwijder de zombie
            }
        }
    }
}
