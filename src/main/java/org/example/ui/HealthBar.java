package org.example.ui;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.RectangleEntity;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;

public class HealthBar extends RectangleEntity {
    private TextEntity healthText;
    private double maxHealth;
    private double currentHealth;
    private double barWidth;

    public HealthBar(Coordinate2D initialLocation, double maxHealth, double barWidth, double barHeight) {
        super(initialLocation);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.barWidth = barWidth;

        // Instellen van de balk (rechthoek)
        setWidth(barWidth);
        setHeight(barHeight);
        setFill(Color.GREEN);

        // Gezondheidstekst boven de balk
        healthText = new TextEntity(new Coordinate2D(initialLocation.getX(), initialLocation.getY() - 20));
        healthText.setFont(Font.font("Roboto", FontWeight.NORMAL, 15));
        healthText.setFill(Color.WHITE);
        updateHealthText();
    }

    public void updateHealth(double newHealth) {
        this.currentHealth = newHealth;
        updateHealthBar();
        updateHealthText();
    }

    private void updateHealthBar() {
        double healthPercentage = currentHealth / maxHealth;
        setWidth(barWidth * healthPercentage);
        if (healthPercentage > 0.6) {
            setFill(Color.GREEN);
        } else if (healthPercentage > 0.3) {
            setFill(Color.ORANGE);
        } else {
            setFill(Color.RED);
        }
    }

    private void updateHealthText() {
        healthText.setText("Health: " + (int) currentHealth);
    }

    public TextEntity getHealthText() {
        return healthText;
    }
}
