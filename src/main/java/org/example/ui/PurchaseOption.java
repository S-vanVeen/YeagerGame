package org.example.ui;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import com.github.hanyaeger.api.userinput.MouseButtonPressedListener;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PurchaseOption extends TextEntity implements MouseButtonPressedListener {
    private final PurchaseType purchaseType;
    private final PurchaseHandler purchaseHandler;

    public enum PurchaseType {
        HEALTH_UPGRADE("Buy Health Upgrade ($50): +25 Health", 50),
        AMMO_UPGRADE("Buy Ammo Upgrade ($75): +24 Max Ammo", 75),
        AMMO_REFILL("Buy Ammo ($25): Refill to Max", 25);

        private final String displayText;
        private final int cost;

        PurchaseType(String displayText, int cost) {
            this.displayText = displayText;
            this.cost = cost;
        }

        public String getDisplayText() {
            return displayText;
        }

        public int getCost() {
            return cost;
        }
    }

    public interface PurchaseHandler {
        void handlePurchase(PurchaseType type, int cost);
    }

    public PurchaseOption(Coordinate2D location, PurchaseType purchaseType, PurchaseHandler purchaseHandler) {
        super(location);
        this.purchaseType = purchaseType;
        this.purchaseHandler = purchaseHandler;

        setText(purchaseType.getDisplayText());
        setFont(Font.font("Roboto", FontWeight.BOLD, 18));
        setFill(Color.YELLOW);

        // Ensure click events are captured
        attachMouseButtonPressedListener();
    }

    @Override
    public void onMouseButtonPressed(MouseButton mouseButton, Coordinate2D coordinate2D) {
        if (mouseButton == MouseButton.PRIMARY) {
            purchaseHandler.handlePurchase(purchaseType, purchaseType.getCost());
        }
    }
}