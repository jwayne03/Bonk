package cat.flx.plataformes.game.characters;

import android.graphics.Rect;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;
import cat.flx.plataformes.engine.Scene;
import cat.flx.plataformes.engine.TiledScene;

// The main character (player)
public class Bonk extends GameObject {
    // Bonk specific attributes
    private int vx;         // vel-X is 1 or 2 (boosted velocity)
    private int vy;
    private boolean isJumping;
    private int score;
    private int bonklife;

    // Useful constants
    private static final int MAX_VELOCITY = 4;
    private static final int JUMP_VELOCITY = -8;
    private static final int PAD_LEFT = 2;
    private static final int PAD_TOP = 0;
    private static final int COL_WIDTH = 20;
    private static final int COL_HEIGHT = 32;


    private final static int STATE_STANDING_FRONT = 1000;
    private final static int STATE_WALKING_LEFT = 1001;
    private final static int STATE_WALKING_RIGHT = 1002;
    private final static int STATE_DEAD = 1003;
    private final static int STATE_JUMPING_LEFT = 1004;
    private final static int STATE_JUMPING_RIGHT = 1005;
    private final static int STATE_FALLING_LEFT = 1006;
    private final static int STATE_FALLING_RIGHT = 1007;
    private final static int STATE_JUMPING_FRONT = 1008;

    // State change matrix depending on movement direction
    private static final int[] NEW_STATES = {
            STATE_JUMPING_LEFT, STATE_JUMPING_FRONT, STATE_JUMPING_RIGHT,
            STATE_WALKING_LEFT, STATE_STANDING_FRONT, STATE_WALKING_RIGHT,
            STATE_FALLING_LEFT, STATE_JUMPING_FRONT, STATE_FALLING_RIGHT
    };

    // Constructor
    public Bonk(Game game, int x, int y) {
        super(game, x, y);
        this.reset(x, y);
        this.addTag("bonk");
        for (int i = 1000; i <= 1012; i++) {
            this.addSpriteSequence(i, i); // The first 0-10 states are indexed animations 0-10
        }
        this.bonklife = 3;
    }

    public int getBonklife() {
        return bonklife;
    }

    public void setBonklife(int bonklife) {
        this.bonklife = bonklife;
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    // Score related
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    // Reset Bonk to a known position
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.vx = 2;
        this.state = STATE_STANDING_FRONT;
    }

    // Dying is exactly state 3
    public boolean isDead() {
        return (state == STATE_DEAD);
    }

    // And kill him is exactly change its state to 3
    public void die() {
        this.bonklife--;
        changeState(STATE_DEAD);
    }

    // User input helper methods
    private boolean left, right, jump;

    public void goLeft() {
        left = true;
        right = false;
    }

    public void goRight() {
        left = false;
        right = true;
    }

    public void stopLR() {
        left = right = false;
    }

    public void jump() {
        jump = true;
    }

    private void clearJump() {
        jump = false;
    }

    private boolean isLeft() {
        return left;
    }

    private boolean isRight() {
        return right;
    }

    private boolean isJump() {
        return jump;
    }

    @Override
    public void physics(long deltaTime) {
        // If died, no physics
        if (state == STATE_DEAD) return;

        // Analyze user input
        int vx = 0;
        if (this.isLeft()) vx = -this.vx;
        else if (this.isRight()) vx = this.vx;
        if (this.isJump()) {
            if (!isJumping) {       // Avoid double jumps
                vy = JUMP_VELOCITY;
                isJumping = true;
            }
            this.clearJump();
        }

        // Apply physics and tests to scene walls and grounds (only if it's in a TiledScene scene)
        Scene scene = game.getScene();
        if (!(scene instanceof TiledScene)) return;
        TiledScene tiledScene = (TiledScene) scene;
        // 1) detect wall to right
        int newX = x + vx;
        int newY = y;
        if (vx > 0) {
            int col = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int r1 = (newY + PAD_TOP) / 16;
            int r2 = (newY + PAD_TOP + COL_HEIGHT - 1) / 16;
            for (int row = r1; row <= r2; row++) {
                if (tiledScene.isWall(row, col)) {
                    newX = col * 16 - PAD_LEFT - COL_WIDTH - 1;
                    break;
                }
            }
        }
        // 2) detect wall to left
        if (vx < 0) {
            int col = (newX + PAD_LEFT) / 16;
            int r1 = (newY + PAD_TOP) / 16;
            int r2 = (newY + PAD_TOP + COL_HEIGHT - 1) / 16;
            for (int row = r1; row <= r2; row++) {
                if (tiledScene.isWall(row, col)) {
                    newX = (col + 1) * 16 - PAD_LEFT;
                    break;
                }
            }
        }
        // 3) detect ground
        // physics (try fall and detect ground)
        vy++;
        if (vy > MAX_VELOCITY) vy = MAX_VELOCITY;
        newY = y + vy;
        if (vy >= 0) {
            int c1 = (newX + PAD_LEFT) / 16;
            int c2 = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int row = (newY + PAD_TOP + COL_HEIGHT) / 16;
            for (int col = c1; col <= c2; col++) {
                if (tiledScene.isGround(row, col)) {
                    newY = row * 16 - PAD_TOP - COL_HEIGHT;
                    vy = 0;
                    isJumping = false;
                    break;
                }
            }
        }
        // 4) detect ceiling
        if (vy < 0) {
            int c1 = (newX + PAD_LEFT) / 16;
            int c2 = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int row = (newY + PAD_TOP) / 16;
            for (int col = c1; col <= c2; col++) {
                if (tiledScene.isWall(row, col)) {
                    newY = (row + 1) * 16 - PAD_TOP;
                    vy = 0;
                    break;
                }
            }
        }

        // Apply resulting physics
        x = newX;
        y = newY;

        // Apply screen limits
        x = Math.max(x, -PAD_LEFT);
        x = Math.min(x, tiledScene.getSceneFullWidth() - COL_WIDTH);
        y = Math.min(y, tiledScene.getSceneFullHeight() - COL_HEIGHT);

        // Decide the out state
        int c = (vx < 0) ? 0 : ((vx == 0) ? 1 : 2);
        int r = (vy < 0) ? 0 : ((vy == 0) ? 1 : 2);
        changeState(NEW_STATES[r * 3 + c]);
    }

    // The collision rect is only valid while alive
    @Override
    public Rect getCollisionRect() {
        return (state == STATE_DEAD) ? null : collisionRect;
    }

    // Updates the collision rect around the character
    @Override
    public void updateCollisionRect() {
        collisionRect.set(
                x + PAD_LEFT,
                y + PAD_TOP,
                x + PAD_LEFT + COL_WIDTH,
                y + PAD_TOP + COL_HEIGHT
        );
    }
}
