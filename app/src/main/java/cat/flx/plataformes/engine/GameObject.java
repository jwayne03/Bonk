package cat.flx.plataformes.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

// A generic, common, basic Game Object
public class GameObject {
    // Common attributes
    protected Game game;
    protected boolean useTranslatedPixels = true;
    protected boolean useScaledPixels = true;
    protected int x, y;                     // (X,Y) position
    protected int state;                    // Current State in animation
    protected Rect collisionRect;           // Collision rectangle for contact listeners
    private final List<String> tags;        // List of String tags associated with this object
    private boolean markedForDeletion;      // Is this object to be deleted from scene?
    private final Paint collisionRectPaint; // For debugging (collision rectangle on screen)
    // Defined animations on this game object
    private final SparseArray<SpriteSequence> animations;

    // Constructor
    public GameObject(Game game, int x, int y) {
        this.game = game;
        this.markedForDeletion = false;
        this.x = x;
        this.y = y;
        this.animations = new SparseArray<>();
        this.state = 0;
        this.collisionRect = new Rect();
        this.tags = new ArrayList<>();
        this.collisionRectPaint = new Paint();
        this.collisionRectPaint.setStyle(Paint.Style.STROKE);
        this.collisionRectPaint.setColor(Color.YELLOW);
    }

    // Useful getters & setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public int getState() { return state; }
    public Rect getCollisionRect() { return collisionRect; }
    public boolean isUseTranslatedPixels()  { return useTranslatedPixels; }
    public void setUseTranslatedPixels(boolean useTranslatedPixels) { this.useTranslatedPixels = useTranslatedPixels; }
    public boolean isUseScaledPixels() { return useScaledPixels; }
    public void setUseScaledPixels(boolean useScaledPixels) { this.useScaledPixels = useScaledPixels; }

    // Marked for deletion
    boolean isMarkedForDeletion() { return markedForDeletion; }
    public void removeFromScene() { this.markedForDeletion = true; }

    // Tags
    public boolean hasTag(String tag) { return tags.contains(tag); }
    protected void addTag(String tag) { tags.add(tag); }
    public void removeTag(String tag) { tags.remove(tag); }

    // The scene needs this getter
    List<String> getTags() { return tags; }

    // Sprite sequence definition
    protected void addSpriteSequence(int state, int spriteSequenceIndex) {
        SpriteSequence sequence = game.getSpriteSequence(spriteSequenceIndex);
        this.animations.put(state, sequence);
    }
    // Get current sprite sequence
    protected SpriteSequence getCurrentSpriteSequence() {
        return this.animations.get(state, null);
    }

    // Change state method
    public void changeState(int state) {
        if (this.state == state) return;
        this.state = state;
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        if (spriteSequence == null) return;
        spriteSequence.reset();
    }

    // Physics implementation
    public void physics(long deltaTime) { }

    // Update collision rectangle on need
    public void updateCollisionRect() { }

    public boolean intersect(float x, float y, float scale, float left, float top) {
        if (isUseScaledPixels()) { x /= scale; y /= scale; }
        if (isUseTranslatedPixels()) { x -= left; y -= top; }
        if (this.getCollisionRect() == null) return false;
        return this.getCollisionRect().contains((int)x, (int)y);
    }

    // Draw the current sprite animation based on the state
    public void draw(Canvas canvas) {
        try {
            SpriteSequence spriteSequence = getCurrentSpriteSequence();
            if (spriteSequence == null) return;
            spriteSequence.drawSprite(canvas, x, y);

            // If debug mode, paint the collision rectangle
            if (!game.getGameEngine().getDebugMode()) return;
            if (collisionRect != null) canvas.drawRect(collisionRect, collisionRectPaint);
        }
        catch (Exception ignored) { }
    }

    private OnTouchEventListener listener;
    private boolean onlyOnDown = true;
    public void setOnTouchEventListener(OnTouchEventListener listener, boolean onlyOnDown) {
        this.listener = listener;
        this.onlyOnDown = onlyOnDown;
    }

    public interface OnTouchEventListener {
        boolean onTouch(GameObject gameObject, Touch touch);
    }

    public boolean touched(Touch touch) {
        if (onlyOnDown && !touch.isDown()) return true;
        if (listener != null) return listener.onTouch(this, touch);
        return true;
    }

}
