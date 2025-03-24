package org.example.zombies.normalZombie;

import com.github.hanyaeger.api.Coordinate2D;
import com.github.hanyaeger.api.entities.DynamicCompositeEntity;
import com.github.hanyaeger.api.entities.SceneBorderCrossingWatcher;
import com.github.hanyaeger.api.scenes.SceneBorder;

import java.util.Random;

public class Zombie extends DynamicCompositeEntity implements SceneBorderCrossingWatcher {
    public Zombie(Coordinate2D initialLocation) {
        super(initialLocation);
        setMotion(1, 90d);
    }

    @Override
    protected void setupEntities() {
        var ZombieSprite = new ZombieSprite(new Coordinate2D(0, 0));
        addEntity(ZombieSprite);

        var HitBox = new HitBox(new Coordinate2D(0, 0));
        addEntity(HitBox);
    }

    @Override
    public void notifyBoundaryCrossing(SceneBorder border){
        setAnchorLocation(new Coordinate2D(-20, new Random().nextInt((int) getSceneHeight() - 25))); // - grootte van sprite
    }
}
