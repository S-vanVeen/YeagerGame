module OutbreakSurvival {
    requires hanyaeger;
    requires java.desktop;

    exports org.example;

    opens images;
    opens audio;
    opens sprites;
    exports org.example.zombies.NormalZombie;
    exports org.example.scenes;
    exports org.example.buttons;
}