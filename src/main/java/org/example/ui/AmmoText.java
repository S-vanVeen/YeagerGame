package org.example.ui;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AmmoText extends TextEntity {

    public AmmoText(Coordinate2D initialLocation) {
        super(initialLocation);
        setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        setFill(Color.WHITE);
    }

    public void update(int currentAmmo, int maxAmmo) {
        setText("Ammo: " + currentAmmo + " / " + maxAmmo);

        // Change color based on ammo levels
        if (currentAmmo == 0) {
            setFill(Color.RED);
        } else if (currentAmmo <= maxAmmo * 0.25) {
            setFill(Color.ORANGE);
        } else {
            setFill(Color.WHITE);
        }
    }
}