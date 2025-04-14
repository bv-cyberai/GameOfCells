package cellcorp.gameofcells.hud;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class Bar extends BaseDrawable {
    private static final float WIDTH = 600;
    private static final float HEIGHT = 40;
    private static final float FONT_SCALE = 0.3f;

    private final AssetManager assetManager;
    private final ShapeRenderer shapeRenderer;
    private final String text;
    private final Color color;

    private float fillPercent;

    /**
     * @param graphicsProvider Graphics provider
     * @param text             Text to display in middle of bar
     * @param color            Color of bar
     */
    public Bar(GraphicsProvider graphicsProvider, AssetManager assetManager, String text, Color color) {
        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.assetManager = assetManager;
        this.text = text;
        this.color = color;

        setMinWidth(WIDTH);
        setMinHeight(HEIGHT);
    }

    private GlyphLayout layout(AssetManager assetManager, String text) {
        // GlyphLayout uses the font scale at construction to calculate width, height.
        // So, we have to set it to whatever we'll be using in draw
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var scaleX = font.getScaleX();
        var scaleY = font.getScaleY();
        font.getData().setScale(FONT_SCALE);
        var layout = new GlyphLayout(font, text);
        font.getData().setScale(scaleX, scaleY);
        return layout;
    }

    /**
     * Set the percent that this bar is filled.
     */
    public void setFillPercent(float fillPercent) {
        this.fillPercent = fillPercent;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        var layout = layout(assetManager, text);

        // Starts with batch in-progress. Stop it for shape renderer, then restart.
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setColor(color);
        batch.setColor(color);

        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var scaleX = font.getScaleX();
        var scaleY = font.getScaleY();
        font.getData().setScale(FONT_SCALE);
        var fillWidth = fillPercent * width;

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(x, y, fillWidth, height);
        shapeRenderer.end();

        var textX = x + width / 2 - layout.width / 2;
        var textY = y + height / 2 + layout.height / 2;
        batch.begin();
        font.draw(batch, text, textX, textY);
        font.getData().setScale(scaleX, scaleY);
    }
}
