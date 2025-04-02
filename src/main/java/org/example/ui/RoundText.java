package org.example.ui;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RoundText extends TextEntity {
    private int round = 1;
    public RoundText(Coordinate2D initialLocation){
        super(initialLocation);

        setFont(Font.font("Roboto", FontWeight.NORMAL, 15));
        setFill(Color.WHITE);
    }

    public void verhoogRonde() {
        round++;
    }

    public void volgendeRondeTimer() {
        setText("Seconden tot volgende ronde");
    }

    public void setRoundText(){
        setText("Ronde: " + round);
    }

    public int getRound() {
        return round;
    }
}
