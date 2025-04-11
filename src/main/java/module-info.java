module OutbreakSurvival {
    requires hanyaeger;
    requires java.desktop;

    exports org.SurvivalOutbreak;

    opens images;
    opens audio;
    opens sprites;
    exports org.SurvivalOutbreak.zombies.NormalZombie;
    exports org.SurvivalOutbreak.scenes;
    exports org.SurvivalOutbreak.buttons;
}