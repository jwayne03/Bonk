package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;
import cat.flx.plataformes.engine.SpriteSequence;

public class PrinPrin extends GameObject {
    // PrinPrin specific attributes
    private final int x0, x1;
    private int incX;

    // Constructor
    public PrinPrin(Game game, int x0, int x1, int y) {
        super(game, x0, y);
        this.x0 = x0;
        this.x1 = x1;
        this.incX = 1;
        this.addTag("prinprin");
        this.addSpriteSequence(0, 4105);    // WALKING RIGHT
        this.addSpriteSequence(1, 4104);    // WALKING LEFT
    }

    // PrinPrin moves horizontally between x0 and x1
    @Override public void physics(long deltaTime) {
        this.x += incX;
        if (x <= x0) {
            incX = 1;
            this.changeState(0);
        }
        if (x >= x1) {
            incX = -1;
            this.changeState(1);
        }
    }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 16, y + 30);
    }
}