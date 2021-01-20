package cat.flx.plataformes.game.ui;

import android.graphics.Canvas;
import android.graphics.Color;

import cat.flx.plataformes.engine.Game;
import cat.flx.plataformes.engine.ui.Button;

public class TouchKey extends Button {

    public TouchKey(Game game, int x, int y, int w, int h, float fontSize, String text) {
        super(game, x, y, w, h, fontSize, text);
        paint.setColor(Color.argb(20, 0, 0, 0));
        paintText.setColor(Color.GRAY);
        this.setText(text);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + w, y + h, paint);
        canvas.drawText(text, tx, ty, paintText);
    }

}
