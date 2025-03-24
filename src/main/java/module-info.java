module OutbreakSurvival {
    requires hanyaeger;
    requires java.desktop;

    exports org.example;

    opens images;
    opens audio;
    opens sprites;
    exports org.example.zombies.normalZombie;
    exports org.example.scenes;
    exports org.example.buttons;
}