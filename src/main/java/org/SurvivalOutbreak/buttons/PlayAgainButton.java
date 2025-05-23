package org.SurvivalOutbreak.buttons;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.impl.TextEntity;
import com.github.hanyaeger.api.userinput.MouseButtonPressedListener;
import com.github.hanyaeger.api.userinput.MouseEnterListener;
import com.github.hanyaeger.api.userinput.MouseExitListener;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.SurvivalOutbreak.SurvivalOutbreak;

public class PlayAgainButton extends TextEntity implements MouseButtonPressedListener, MouseEnterListener, MouseExitListener {
    private final SurvivalOutbreak survivalOutbreak;

    public PlayAgainButton(SurvivalOutbreak survivalOutbreak, Coordinate2D initialLocation) {
        super(initialLocation,"Begin Opnieuw");
        this.survivalOutbreak = survivalOutbreak;
        setFill(Color.WHITE);
        setFont(Font.font("Rockwell Extra Bold", FontWeight.SEMI_BOLD, 30));
    }

    @Override
    public void onMouseButtonPressed(MouseButton mouseButton, Coordinate2D coordinate2D) {
        survivalOutbreak.setActiveScene(1);
    }

    @Override
    public void onMouseEntered(){
        setFill(Color.RED);
        setCursor(Cursor.HAND);
    }

    @Override
    public void onMouseExited(){
        setFill(Color.WHITE);
        setCursor(Cursor.DEFAULT);
    }
}
