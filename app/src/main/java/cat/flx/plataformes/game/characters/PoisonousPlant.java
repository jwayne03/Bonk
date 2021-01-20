package cat.flx.plataformes.game.characters;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.SpriteSequence;

public class PoisonousPlant extends Enemy {

    // Constructor
    public PoisonousPlant(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("plant");
        this.addTag("poisonous");
        this.addSpriteSequence(0, 15);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    // A coin doesn't move
    @Override
    public void physics(long deltaTime) {
    }

    // The collision rect around the coin
    @Override
    public void updateCollisionRect() {
        collisionRect.set(
                x + 3, y,
                x + 14, y + 16);
    }
}