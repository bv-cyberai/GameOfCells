package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.providers.GraphicsProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Collection;
import java.util.List;

/**
 * MinimapRenderer Class
 * <p>
 * Controls the Rendering by the minimap. Draws the minimap
 * based on the Glucose and zones in the chunk.
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class MinimapRenderer {
    // Chosen to roughly existing minimap size and location.
    private static final int VIEW_RECT_WIDTH = 1333;
    private static final int VIEW_RECT_HEIGHT = 800;
    private final float minimapWidth, minimapHeight;
    private final float margin = 20f;

    private final float visibleWorldWidth = 2000f;
    private final float visibleWorldHeight = 2000f;

    private ShapeRenderer shapeRenderer;
    private final FitViewport viewport;

    public MinimapRenderer(GraphicsProvider graphicsProvider, float worldWidth, float worldHeight, float minimapWidth, float minimapHeight, OrthographicCamera camera) {
        this.minimapWidth = minimapWidth;
        this.minimapHeight = minimapHeight;
        // Needed for `ScissorStack` clipping to work
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
    }

    // Renders the minimap
    // Takes the screen width and height, player coordinates, and collections of acid zones, basic zones, and glucose objects
    // The minimap is drawn at the bottom left corner of the screen with a margin
    // The player is represented by a white square in the center of the minimap
    public void render(float screenWidth, float screenHeight, float playerX, float playerY,
                       Collection<Zone> acidZones, Collection<Zone> basicZones,
                       List<Glucose> glucoseList) {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }

        float x = margin;
        float y = margin;

        viewport.update((int) screenWidth, (int) screenHeight);
        viewport.apply(true);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw the minimap background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(x, y, minimapWidth, minimapHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Acid Zones
        for (Zone zone : acidZones) {
            shapeRenderer.setColor(1f, 0.5f, 0.8f, 0.6f);
            drawZoneRelative(zone.x(), zone.y(), zone.getZoneRadius(), x, y, playerX, playerY);
        }

        // Basic Zones
        for (Zone zone : basicZones) {
            shapeRenderer.setColor(0.3f, 0.6f, 1f, 0.6f);
            drawZoneRelative(zone.x(), zone.y(), zone.getZoneRadius(), x, y, playerX, playerY);
        }
        // Glucose
        for (Glucose glucose : glucoseList) {
            shapeRenderer.setColor(1f, 1f, 0.3f, 0.6f);
            drawRelativeObject(glucose.getX(), glucose.getY(), 2f, x, y, playerX, playerY);
        }

        // Player (centered)
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(
                x + minimapWidth / 2f - 2f,
                y + minimapHeight / 2f - 2f,
                4f, 4f
        );

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // Draws a relative object on the minimap
    // Takes the world coordinates of the object and converts them to minimap coordinates
    // The object is drawn at the specified offset from the minimap center
    // The player coordinates are used to determine the relative position of the object
    private void drawRelativeObject(float worldX, float worldY, float size, float offsetX, float offsetY, float playerX, float playerY) {
        float dx = worldX - playerX;
        float dy = worldY - playerY;

        if (Math.abs(dx) > visibleWorldWidth / 2f || Math.abs(dy) > visibleWorldHeight / 2f) return;

        float relX = (dx / visibleWorldWidth) * minimapWidth;
        float relY = (dy / visibleWorldHeight) * minimapHeight;

        float drawX = offsetX + minimapWidth / 2f + relX;
        float drawY = offsetY + minimapHeight / 2f + relY;

        shapeRenderer.circle(drawX, drawY, size);
    }

    // Draws a zone on the minimap
    // Takes the world coordinates of the zone and converts them to minimap coordinates
    // The zone is drawn at the specified offset from the minimap center
    // The player coordinates are used to determine the relative position of the zone
    private void drawZoneRelative(float worldX, float worldY, float worldRadius, float offsetX, float offsetY, float playerX, float playerY) {
        float dx = worldX - playerX;
        float dy = worldY - playerY;

        float visibleHalfW = visibleWorldWidth / 2f;
        float visibleHalfH = visibleWorldHeight / 2f;

        // Skip zones too far away
        if (Math.abs(dx) > visibleHalfW + worldRadius || Math.abs(dy) > visibleHalfH + worldRadius)
            return;

        // (world_coords -> minimap_coords) scaling factors
        float scaleX = minimapWidth / visibleWorldWidth;
        float scaleY = minimapHeight / visibleWorldHeight;

        float dxMinimap = dx * scaleX;
        float dyMinimap = dy * scaleY;

        // Coords of the zone within the minimap
        float zoneXMinimap = (minimapWidth / 2) + dxMinimap;
        float zoneYMinimap = (minimapHeight / 2) + dyMinimap;

        // Coords of the zone within the viewport
        float zoneXViewport = offsetX + zoneXMinimap;
        float zoneYViewport = offsetY + zoneYMinimap;

        float scaledRadiusX = (worldRadius / visibleWorldWidth) * minimapWidth;
        float scaledRadiusY = (worldRadius / visibleWorldHeight) * minimapHeight;

        float scaledWidth = scaledRadiusX * 2;
        float scaledHeight = scaledRadiusY * 2;

        // For some reason, `ShapeRenderer.ellipse` draws the ellipse with `x` and `y` as the bottom-left of the ellipse
        float zoneLeftViewport = zoneXViewport - scaledRadiusX;
        float zoneBottomViewport = zoneYViewport - scaledRadiusY;

        // Create a clipping area with `ScissorStack`
        // See: https://libgdx.com/wiki/graphics/2d/clipping-with-the-use-of-scissorstack
        var scissors = new Rectangle();
        var clipBounds = new Rectangle(offsetX, offsetY, minimapWidth, minimapHeight);
        ScissorStack.calculateScissors(viewport.getCamera(), shapeRenderer.getTransformMatrix(), clipBounds, scissors);

        shapeRenderer.flush();
        if (ScissorStack.pushScissors(scissors)) {
            shapeRenderer.ellipse(zoneLeftViewport, zoneBottomViewport, scaledWidth, scaledHeight);
            shapeRenderer.flush();
            ScissorStack.popScissors();
        }
    }
}
