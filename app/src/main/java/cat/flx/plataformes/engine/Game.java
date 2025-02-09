package cat.flx.plataformes.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;


// This class is the base for a generic game
public class Game {

    private final GameEngine gameEngine;
    private Scene scene;                // The current scene
    private boolean paused = true;      // True if the game is paused

    // Constructor (bidirectional relationship)
    public Game(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        gameEngine.setGame(this);
    }

    // Useful getters and helpers
    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public Audio getAudio() {
        return gameEngine.getAudio();
    }

    public Scene getScene() {
        return scene;
    }

    public BitmapSet getBitmapSet() {
        return gameEngine.getBitmapSet();
    }

    public Bitmap getBitmap(int index) {
        return getBitmapSet().getBitmap(index);
    }

    public SpriteSequence getSpriteSequence(int index) {
        return getBitmapSet().getSpriteSequence(index);
    }

    public int getScreenWidth() {
        return gameEngine.getScreenWidth();
    }

    public int getScreenHeight() {
        return gameEngine.getScreenHeight();
    }

    public boolean isPaused() {
        return paused;
    }

    // Methods to be called by the game engine on start, stop, resume and pause
    public void start() {
    }

    public void stop() {
    }

    public void resume() {
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    // Sets the current scene
    public void loadScene(Scene scene) {
        this.scene = scene;
    }

    // Process input from user
    void processInput() {
        if (scene == null) return;

        Touch touch;
        while ((touch = gameEngine.consumeTouch()) != null) {
            // Gets the list of GameObject under the touch point
            List<GameObject> objects = scene.touched(touch.getX(), touch.getY());
            boolean bubbleUp = true;
            for (GameObject gameObject : objects) {
                // Make the action and control the propagation (return false to avoid bubble up)
                bubbleUp = gameObject.touched(touch);
                if (!bubbleUp) break;
            }
            if (bubbleUp) scene.onTouch(touch);
        }
        scene.processInput();
    }

    // The physics cycle (if not paused)
    void physics(long deltaTime) {
        if (scene == null) return;
        if (paused) return;
        scene.physics(deltaTime);
    }

    // The drawing cycle
    void draw(Canvas canvas) {
        if (scene == null) return;
        scene.draw(canvas);
    }

}
