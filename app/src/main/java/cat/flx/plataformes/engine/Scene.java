package cat.flx.plataformes.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

abstract public class Scene {
    // Attributes
    protected Game game;
    protected ArrayList<GameObject> gameObjects;
    protected ArrayList<GameObject> newObjects;
    private HashMap<String, HashMap<String, OnContactListener>> contactListeners;   // Listeners
    Paint paintDebug;

    protected float left, top;                        // Left & top gaps
    protected float scale;                            // Current applied scale on canvas

    // Constructor
    public Scene(Game game) {
        super();
        this.game = game;
        // Empty game-object's list
        gameObjects = new ArrayList<>();
        // Empty temporary new game-object's list
        newObjects = new ArrayList<>();
        // Empty contact listeners
        this.contactListeners = new HashMap<>();
        // Debug painter
        paintDebug = new Paint();
        paintDebug.setColor(Color.GRAY);
        paintDebug.setTextSize(10);
        scale = 1.0f;                               // Draw scale
    }

    // We don't append the new object to the real list. We add it on a temporary list instead
    public void add(GameObject gameObject) {
        this.newObjects.add(gameObject);
    }

    public void setScaleForCover(float width, float height) {
        Log.d("flx", "width = " + width + " height = " + height);
        Log.d("flx", "screenWidth = " + getScreenWidth() + " screenHeight = " + getScreenHeight());
        scale = Math.max(getScreenWidth() / width, getScreenHeight() / height);
        Log.d("flx", "scale = " + scale);
        top = (getScreenHeight() - height * scale) / 2 / scale;
        left = (getScreenWidth() - width * scale) / 2 / scale;
        Log.d("flx", "top = " + top + " left = " + left);
    }
    public void setScaleForFit(float width, float height) {
        Log.d("flx", "width = " + width + " height = " + height);
        Log.d("flx", "screenWidth = " + getScreenWidth() + " screenHeight = " + getScreenHeight());
        scale = Math.min(getScreenWidth() / width, getScreenHeight() / height);
        Log.d("flx", "scale = " + scale);
        top = (getScreenHeight() - height * scale) / 2 / scale;
        left = (getScreenWidth() - width * scale) / 2 / scale;
        Log.d("flx", "top = " + top + " left = " + left);
    }

    // Useful setters & getters
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Audio getAudio() { return game.getAudio(); }
    public GameEngine getGameEngine() { return game.getGameEngine(); }
    public BitmapSet getBitmapSet() { return getGameEngine().getBitmapSet(); }
    public Context getContext() { return game.getGameEngine().getContext(); }

    protected int getScreenWidth() { return game.getScreenWidth(); }
    protected int getScreenHeight() { return game.getScreenHeight(); }

    public float getScaledWidth() { return game.getScreenWidth() / scale; }
    public float getScaledHeight() { return game.getScreenHeight() / scale; }

    // For a non-tiled scene, the full size of the scene is the size of the screen
    public int getSceneFullWidth() { return (int)getScaledWidth(); }
    public int getSceneFullHeight() { return (int)getScaledHeight(); }

    // Clears the contact listeners
    protected void clearContactListeners() {
        this.contactListeners = new HashMap<>();
    }

    // Add a contact listener between game objects based on their tags
    protected void addContactListener(String tag1, String tag2, OnContactListener listener) {
        HashMap<String, OnContactListener> tag1Listeners;
        // If tag1 is not already present, create new HashMap for it
        if (!contactListeners.containsKey(tag1)) {
            contactListeners.put(tag1, new HashMap<String, OnContactListener>());
        }
        tag1Listeners = contactListeners.get(tag1);
        if (tag1Listeners == null) return;
        // Add the listener
        tag1Listeners.put(tag2, listener);
    }

    // Physics cycle: physics, collision rectangles and collision detection
    public void physics(long deltaTime) {
        // Update physics & collision rectangles of all game objects
        for(GameObject gameObject : gameObjects) {
            gameObject.physics(deltaTime);
            gameObject.updateCollisionRect();
        }
        // Test for contacts & collisions between objects:
        // Iterate over all game objects
        for (GameObject gameObject1 : gameObjects) {
            // And their tags
            for(String tag1 : gameObject1.getTags()) {
                Rect rect1 = gameObject1.getCollisionRect();
                if (rect1 == null) continue;
                // To retrieve the listeners between him and other game objects
                HashMap<String, OnContactListener> tag1Listeners = contactListeners.get(tag1);
                if (tag1Listeners == null) continue;
                // So we search for game objects
                for (GameObject gameObject2 : gameObjects) {
                    // And their tags
                    for(String tag2 : gameObject2.getTags()) {
                        // Searching for a matching pair tag1-tag2
                        OnContactListener listener = tag1Listeners.get(tag2);
                        if (listener == null) continue;
                        Rect rect2 = gameObject2.getCollisionRect();
                        if (rect2 == null) continue;
                        // Test the collision
                        if (!rect1.intersect(rect2)) continue;
                        // And call the listener if they intersect
                        listener.onContact(tag1, gameObject1, tag2, gameObject2);
                    }
                }
            }
        }
        // Safely remove all pending "marked for deletion" game objects
        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isMarkedForDeletion()) {
                iterator.remove();
            }
        }
        // Safely add all new game-objects
        gameObjects.addAll(newObjects);
        newObjects.clear();
    }

    // Draw cycle: all game objects are drawn here
    public void draw(Canvas canvas) {

        // 1st Draw round : translated & scaled (game)
        canvas.save();
        canvas.scale(scale, scale);
        canvas.translate(left, top);
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.isUseTranslatedPixels()) continue;
            if (!gameObject.isUseScaledPixels()) continue;
            gameObject.draw(canvas);
        }
        canvas.restore();

        // 2nd Draw round : only scaled (controls)
        canvas.save();
        canvas.scale(scale, scale);
        for (GameObject gameObject : gameObjects) {
            if (gameObject.isUseTranslatedPixels()) continue;
            if (!gameObject.isUseScaledPixels()) continue;
            gameObject.draw(canvas);
        }
        canvas.restore();

        // 3rd Draw round : absolute over game (no translate, no scale)
        for (GameObject gameObject : gameObjects) {
            if (gameObject.isUseTranslatedPixels()) continue;
            if (gameObject.isUseScaledPixels()) continue;
            gameObject.draw(canvas);
        }
    }

    // Gives all the GameObjects under the specified x and y (real pixels)
    protected List<GameObject> touched(int x, int y) {
        List<GameObject> touched = new ArrayList<>();
        for(GameObject go : gameObjects) {
            if (go.intersect(x, y, scale, left, top)) {
                touched.add(go);
            }
        }
        return touched;
    }

    // Default do-nothing when touching the screen (bubbled up)
    public void onTouch(Touch touch) { }

    // User Input cycle: the user input will be analyzed here
    // This method must be overridden in real scene implementation
    abstract public void processInput();

}
