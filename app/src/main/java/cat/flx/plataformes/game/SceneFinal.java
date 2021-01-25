package cat.flx.plataformes.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

import cat.flx.plataformes.R;
import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;
import cat.flx.plataformes.engine.OnContactListener;
import cat.flx.plataformes.engine.TiledScene;
import cat.flx.plataformes.engine.Touch;
import cat.flx.plataformes.game.characters.Bonk;
import cat.flx.plataformes.game.characters.Coin;
import cat.flx.plataformes.game.characters.Crab;
import cat.flx.plataformes.game.characters.Lava;
import cat.flx.plataformes.game.characters.PoisonousPlant;
import cat.flx.plataformes.game.characters.PrinPrin;
import cat.flx.plataformes.game.ui.TouchKey;

// A fully playable tiled scene
class SceneFinal extends TiledScene implements OnContactListener, GameObject.OnTouchEventListener {

    // We keep a specific reference to the player
    private final Bonk bonk;
    // Used for specific painting
    private final Paint paintScore;

    TouchKey touchKeyLeft, touchKeyRight, touchKeyJump;

    // Constructor
    SceneFinal(Game game) {
        super(game);

        // Create the main character (player)
        this.bonk = new Bonk(game, 0, 0);
        bonk.reset(0, 0);
        this.add(bonk);

        // On screen touch keys
        touchKeyLeft = new TouchKey(game, 0, (int) (getScaledHeight() - 64), 64, 64, 32, "<");
        touchKeyLeft.setUseTranslatedPixels(false);
        touchKeyLeft.setOnTouchEventListener(this, false);
        this.add(touchKeyLeft);
        touchKeyRight = new TouchKey(game, 64, (int) (getScaledHeight() - 64), 64, 64, 32, ">");
        touchKeyRight.setUseTranslatedPixels(false);
        touchKeyRight.setOnTouchEventListener(this, false);
        this.add(touchKeyRight);
        touchKeyJump = new TouchKey(game, (int) (getScaledWidth() - 64), (int) (getScaledHeight() - 64), 64, 64, 32, "^");
        touchKeyJump.setUseTranslatedPixels(false);
        touchKeyJump.setOnTouchEventListener(this, true);
        this.add(touchKeyJump);

        // Set the follow camera to the player
        this.setCamera(bonk);
        // The screen will hold 16 rows of tiles (16px height each)
        this.setScaledHeight(16 * 16);
        // Pre-loading of sound effects
        game.getAudio().loadSoundFX(new int[]{R.raw.fx_coin, R.raw.fx_die, R.raw.fx_pause});
        // Load the scene tiles from resource
        this.loadFromFile(R.raw.finalscene);
        // Add contact listeners by tag names
        this.addContactListener("bonk", "enemy", this);
        this.addContactListener("bonk", "coin", this);
        this.addContactListener("bonk", "prinprin", this);
        // Prepare the painters for drawing
        paintScore = new Paint();
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.dseg);
        paintScore.setTypeface(typeface);
        paintScore.setTextSize(14);
        paintScore.setColor(Color.WHITE);
        paintScore.setShadowLayer(2.0f, 1, 1, Color.BLACK);
    }

    // Overrides the base parser adding specific syntax for coins and crabs
    @Override
    protected GameObject parseLine(String cmd, String args) {
        // Lines beginning with "COIN"
        if (cmd.equals("COIN")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Coin(game, coinX, coinY);
        }
        // Lines beginning with "CRAB"
        if (cmd.equals("CRAB")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 3) return null;
            int crabX0 = Integer.parseInt(parts2[0].trim()) * 16;
            int crabX1 = Integer.parseInt(parts2[1].trim()) * 16;
            int crabY = Integer.parseInt(parts2[2].trim()) * 16;
            return new Crab(game, crabX0, crabX1, crabY);
        }
        // Lines beginning with "PPLANT"
        if (cmd.equals("PPLANT")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int pplantX = Integer.parseInt(parts2[0].trim()) * 16;
            int pplantY = Integer.parseInt(parts2[1].trim()) * 16;
            return new PoisonousPlant(game, pplantX, pplantY);
        }
        // Lines beginning with "PRIN"
        if (cmd.equals("PRIN")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 3) return null;
            int prinX0 = Integer.parseInt(parts2[0].trim()) * 16;
            int prinX1 = Integer.parseInt(parts2[1].trim()) * 16;
            int prinY = Integer.parseInt(parts2[2].trim()) * 16 + 2;
            return new PrinPrin(game, prinX0, prinX1, prinY);
        }
        if (cmd.equals("LAVA")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int lavaX = Integer.parseInt(parts2[0].trim()) * 16;
            int lavaY = Integer.parseInt(parts2[0].trim()) * 16;
            return new Lava(game, lavaX, lavaY);
        }
        // Test the common basic parser
        return super.parseLine(cmd, args);
    }

    @Override
    public boolean onTouch(GameObject gameObject, Touch touch) {
        if (gameObject == touchKeyLeft) {
            if (!touch.isTouching()) bonk.stopLR();
            else bonk.goLeft();
        } else if (gameObject == touchKeyRight) {
            if (!touch.isTouching()) bonk.stopLR();
            else bonk.goRight();
        } else if (gameObject == touchKeyJump) {
            bonk.jump();
        }
        return false;
    }

    @Override
    public void onTouch(Touch touch) {
        if (touch.isDown()) {                      // TOGGLE PAUSE
            if (bonk.isDead()) {
                game.loadScene(new MenuScene(game));
            } else if (game.isPaused()) game.resume();
            else game.pause();
        }
    }

    @Override
    public void processInput() {
        // Process the computer's keyboard if the game is run inside an emulator
        int keycode;
        while ((keycode = game.getGameEngine().consumeKeyTouch()) != KeyEvent.KEYCODE_UNKNOWN) {
            switch (keycode) {
                case KeyEvent.KEYCODE_Z:                    // LEFT
                    bonk.goLeft();
                    break;
                case KeyEvent.KEYCODE_X:                    // RIGHT
                    bonk.goRight();
                    break;
                case KeyEvent.KEYCODE_M:                    // JUMP
                    bonk.jump();
                    break;
                case KeyEvent.KEYCODE_P:                    // TOGGLE PAUSE
                    if (game.isPaused()) game.resume();
                    else game.pause();
                    break;
            }
        }
    }

    // Contact detection listener: A contact has been detected and must be processed
    // The object1 (based on tag1) overlapped with object2 (based on tag2)
    @Override
    public void onContact(String tag1, GameObject object1, String tag2, GameObject object2) {
        Log.d("flx", "Contact between a " + tag1 + " and " + tag2);
        // Contact between Bonk and a coin
        switch (tag2) {
            case "coin":
                this.getGame().getAudio().playSoundFX(0);
                object2.removeFromScene();
                bonk.addScore(10);
                break;
            // Contact between Bonk and an enemy
            case "enemy":
                this.getGame().getAudio().playSoundFX(1);
                object2.removeFromScene();
                bonk.die();
                break;
            // Contact between Bonk and PrinPrin
            case "prinprin":
                // TODO Change Scene
                bonk.addScore(1000);
                break;
        }
    }

    // Overrides the basic draw by adding the translucent keyboard and the score
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        touchKeyJump.setPosition((int) (getScaledWidth() - 64), (int) (getScaledHeight() - 64));

        // Score on top-right corner
        canvas.scale(getScale(), getScale());
        String score = String.format(Locale.getDefault(), "%06d", bonk.getScore());
        canvas.drawText(score, getScaledWidth() - 72, 14, paintScore);

        canvas.scale(0.5f, 0.5f);
        for (int i = 0; i < 3; i++) {
            canvas.drawBitmap(game.getBitmap(1013), i * 28, 0, null);
        }
    }
}
