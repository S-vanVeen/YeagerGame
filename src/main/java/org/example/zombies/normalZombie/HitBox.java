package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.Collider;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import javafx.scene.paint.Color;

public class HitBox extends RectangleEntity implements Collider {

        protected HitBox(final Coordinate2D initialPosition) {
            super(initialPosition);
            setWidth(20);
            setHeight(25);
            setFill(Color.TRANSPARENT);
        }
    }
