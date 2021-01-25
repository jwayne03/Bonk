package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;
import cat.flx.plataformes.engine.SpriteSequence;

public class Flag extends GameObject {

    public Flag(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("flag");
        this.addSpriteSequence(0, 90000);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    @Override
    public void physics(long deltaTime) {
        super.physics(deltaTime);
    }

    @Override
    public void updateCollisionRect() {
        collisionRect.set(x + 2, y + 2, x + 14, y + 14);
    }
}
