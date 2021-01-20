package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.TiledScene;

public class Bullet extends Enemy {
    private double vx, vy;

    public Bullet(Game game, int x, int y) {
        super(game, x, y);
        this.vx = Math.random() * 10 - 5;
        this.vy = -Math.random() * 10;
        this.addTag("bullet");
        this.addSpriteSequence(0, 5000);
    }

    // The crab moves horizontally between x0 and x1
    @Override public void physics(long deltaTime) {
        x += vx;
        y += vy;
        vy++;
        TiledScene scene = (TiledScene) game.getScene();
        if (y > scene.getSceneFullHeight()) {
            this.removeFromScene();
        }
    }

    // The collision rect around the crab will consider the pincers' position
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 7, y + 7);
    }
}
