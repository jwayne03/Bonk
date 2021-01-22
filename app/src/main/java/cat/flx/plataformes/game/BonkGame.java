package cat.flx.plataformes.game;

import cat.flx.plataformes.R;
import cat.flx.plataformes.engine.BitmapSet;
import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameEngine;

// This game is a Game instance
public class BonkGame extends Game {

    // Constructor
    BonkGame(GameEngine gameEngine) {
        super(gameEngine);

        // Load the bitmap set for this game
        BitmapSet bitmapSet = getGameEngine().getBitmapSet();
        bitmapSet.addSprites(R.raw.bm_tiles, R.raw.bm_tiles_info);
        bitmapSet.addSprites(R.raw.bm_bonk, R.raw.bm_bonk_info);
        bitmapSet.addSprites(R.raw.bm_coin, R.raw.bm_coin_info);
        bitmapSet.addSprites(R.raw.bm_crab, R.raw.bm_crab_info);
        bitmapSet.addSprites(R.raw.bm_prinprin, R.raw.bm_prinprin_info);
        bitmapSet.addSprites(R.raw.bm_bullet, R.raw.bm_bullet_info);
        bitmapSet.addSprites(R.raw.bm_tileslava, R.raw.bm_lava_info);
    }

    // Method to be called when the game is first started
    @Override
    public void start() {
        // When the game is loaded, the Scene01 is presented to the user
        MenuScene scene = new MenuScene(this);
        this.loadScene(scene);
        // Background music
        getAudio().loadMusic(R.raw.so_music);
    }

    // Method to be called when the game is being closed
    @Override
    public void stop() {
        // Nothing special for now
    }

    // Method to be called when the game returns from pause
    @Override
    public void resume() {
        super.resume();
        getAudio().startMusic();
    }

    // Method to be called when the game goes to pause
    @Override
    public void pause() {
        super.pause();
        getAudio().stopMusic();
    }

}
