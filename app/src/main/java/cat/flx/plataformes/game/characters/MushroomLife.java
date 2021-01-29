package cat.flx.plataformes.game.characters;

import android.util.Log;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

// An crab-like enemy
public class MushroomLife extends GameObject {
    // Crab specific attributes
    private final int x0, x1;
    private int incX;

    // Constructor
    public MushroomLife(Game game, int x0, int x1, int y) {
        super(game, x0, y - 5);
        this.x0 = x0;
        this.x1 = x1;
        this.incX = 1;
        this.addTag("mushroomlife");
        this.addSpriteSequence(0, 20011);
        Log.d("xd", "MushroomLife: ");
    }

    // The crab moves horizontally between x0 and x1
    @Override
    public void physics(long deltaTime) {
        this.x += incX;
        if (x <= x0) incX = 1;
        if (x >= x1) incX = -1;
    }

    // The collision rect around the crab will consider the pincers' position
    @Override
    public void updateCollisionRect() {
        collisionRect.set(x + 2, y + 2, x + 14, y + 14);
    }

}