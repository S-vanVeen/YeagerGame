package org.SurvivalOutbreak.ui;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CountdownTimer extends TextEntity {
    private int secondsLeft;

    public CountdownTimer(Coordinate2D initialLocation, int totalSeconds) {
        super(initialLocation);

        this.secondsLeft = totalSeconds;

        setFont(Font.font("impact", FontWeight.BOLD, 30));
        setFill(Color.RED);
        updateText();
    }

    public void tick() {
        if (secondsLeft > 0) {
            secondsLeft--;
            updateText();
        }
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void reset(int seconds) {
        secondsLeft = seconds;
        updateText();
    }

    private void updateText() {
        setText("Seconden tot volgende ronde: " + secondsLeft);
    }
}