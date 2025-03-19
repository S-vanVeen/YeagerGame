package org.example;

import com.github.hanyaeger.api.scenes.StaticScene;

public class TitleScene extends StaticScene {
    private final OutbreakSurvival outbreakSurvival;

    public TitleScene(OutbreakSurvival outbreakSurvival) {
        this.outbreakSurvival = outbreakSurvival;
    }

    @Override
    public void setupScene() {
        setBackgroundAudioVolume(0.05);
        setBackgroundAudio("audio/zombie_sound.mp3");
        setBackgroundImage("images/startscreen_bg.jpg");
    }

    @Override
    public void setupEntities() {

    }
}
