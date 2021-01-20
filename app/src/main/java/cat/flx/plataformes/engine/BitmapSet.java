package cat.flx.plataformes.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// This class holds the Bitmaps and animations used by the game and game objects
public class BitmapSet {

    private final Context context;
    private SparseArray<Bitmap> bitmaps;                    // all the bitmaps
    private SparseArray<SpriteSequence> spriteSequences;    // all the sprite sequences

    // Constructor loads the resources and fills the two holders
    BitmapSet(Context context) {
        this.context = context;
        this.emptyBitmapSet();
    }

    public void emptyBitmapSet() {
        bitmaps = new SparseArray<>();
        spriteSequences = new SparseArray<>();
    }

    // Retrieve a Bitmap by index
    Bitmap getBitmap(int index) { return bitmaps.get(index); }

    // Gets a clone of the specified sprite sequence by index
    public SpriteSequence getSpriteSequence(int index) {
        SpriteSequence sequence = spriteSequences.get(index);
        if (sequence == null) return null;
        return new SpriteSequence(sequence);
    }

    // Method for loading the sprites from sprite PNG and sprite info TXT
    public void addSprites(int resource, int resourceInfo) {
        boolean loadingAnimations = false;

        // Load the sprites and tiles from res/raw
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        Bitmap bitmapsBMP = BitmapFactory.decodeResource(context.getResources(), resource, opts);
        // Prepping the transformations for image rotation
        Matrix rot1 = new Matrix();     // no-rotation
        Matrix rot2 = new Matrix();
        rot2.setScale(-1, 1);           // flip horizontal
        // Load the sprite's and tile's definition file
        InputStream in = context.getResources().openRawResource(resourceInfo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.toUpperCase().startsWith("SPRIT")) {
                    loadingAnimations = false;
                    continue;
                }
                if (line.toUpperCase().startsWith("ANIMA")) {
                    loadingAnimations = true;
                    continue;
                }
                if (line.contains("//")) {              // Remove comments
                    String[] commentSplit = line.split("//");
                    line = commentSplit[0];
                }
                line = line.trim();
                String[] firstSplit = line.split(":");  // id:def from line
                if (firstSplit.length != 2) continue;
                int id = Integer.parseInt(firstSplit[0].trim());
                String def = firstSplit[1].trim();
                String[] secondSplit = def.split(",");
                if (!loadingAnimations) {               // This must be a "SPRITE"
                    if (secondSplit.length != 5) continue;
                    int x = Integer.parseInt(secondSplit[0].trim());
                    int y = Integer.parseInt(secondSplit[1].trim());
                    int w = Integer.parseInt(secondSplit[2].trim());
                    int h = Integer.parseInt(secondSplit[3].trim());
                    int r = Integer.parseInt(secondSplit[4].trim());
                    Matrix m = (r == 1) ? rot2 : rot1;
                    // Get the portion of the original Bitmap and store it in the array
                    Bitmap bitmap = Bitmap.createBitmap(bitmapsBMP, x, y, w, h, m, true);
                    bitmaps.put(id, bitmap);
                    Log.d("flx","Added sprite #" + id);
                }
                else {                                  // This must be an "SEQUENCE"
                    SpriteSequence spriteSequence = new SpriteSequence(this);
                    spriteSequences.put(id, spriteSequence);
                    for(String spec : secondSplit) {
                        if (spec.contains("x")) {
                            String[] thirdSplit = spec.split("x");
                            if (thirdSplit.length != 2) continue;
                            int bitmapIndex = Integer.parseInt(thirdSplit[0].trim());
                            int number = Integer.parseInt(thirdSplit[1].trim());
                            for(int i = 0; i < number; i++) {
                                spriteSequence.addSprite(bitmapIndex);
                            }
                        }
                        else {
                            int bitmapIndex = Integer.parseInt(spec.trim());
                            spriteSequence.addSprite(bitmapIndex);
                        }
                    }
                    Log.d("flx","Added animation #" + id);
                }
            }
            reader.close();
        }
        catch (Exception ignored) { }
        finally {
            try { reader.close(); } catch (Exception ignored) { }
        }

        // Release the resources of the original Bitmap. It's needed no more in the app
        bitmapsBMP.recycle();
    }

}

