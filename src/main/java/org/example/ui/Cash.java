package org.example.ui;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Cash {
    private int amount;
    private final CashText cashText;

    public Cash(Coordinate2D location) {
        this.amount = 0;
        this.cashText = new CashText(location);
        updateCashText();
    }

    public void increase(int amount) {
        this.amount += amount;
        updateCashText();
    }


    public int getAmount() {
        return amount;
    }

    private void updateCashText() {
        cashText.setText("Cash: $" + amount);
    }

    public CashText getCashText() {
        return cashText;
    }

    public static class CashText extends TextEntity {
        public CashText(Coordinate2D initialLocation) {
            super(initialLocation);
            setFont(Font.font("Roboto", FontWeight.BOLD, 20));
            setFill(Color.GREEN);
        }
    }
}