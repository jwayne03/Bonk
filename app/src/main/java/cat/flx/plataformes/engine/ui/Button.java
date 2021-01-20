package cat.flx.plataformes.engine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.GameObject;

public class Button extends GameObject {
    protected int w, h, tx, ty;
    protected String text;
    protected Paint paint;
    protected Paint paintText;

    public Button(Game game, int x, int y, int w, int h, float fontSize, String text) {
        super(game, x, y);
        this.w = w;
        this.h = h;
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paintText = new Paint(paint);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(fontSize);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
        this.setText(text);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        this.setText(this.text);
    }

    public void setText(String text) {
        this.text = text;
        float cx = x + w / 2.0f;
        float cy = y + h / 2.0f;
        Rect r = new Rect();
        paintText.getTextBounds(text, 0, text.length(), r);
        this.tx = (int) (cx - r.exactCenterX());
        this.ty = (int) (cy - r.exactCenterY());
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(x + 2, y + 2, x + w, y + h, paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(x, y, x + w - 2, y + h - 2, paint);
        paintText.setColor(Color.WHITE);
        canvas.drawText(text, tx + 1, ty + 1, paintText);
        paintText.setColor(Color.BLACK);
        canvas.drawText(text, tx, ty, paintText);
    }

    // Sets the size of the touch / collision detector
    @Override
    public void updateCollisionRect() {
        collisionRect.set(x, y, x + w, y + h);
    }

}
