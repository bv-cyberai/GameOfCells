package cellcorp.gameofcells.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.Collection;


import java.util.List;

public class MinimapRenderer {
    private final float minimapWidth, minimapHeight;
    private final float margin = 20f;

    private final float visibleWorldWidth = 2000f;
    private final float visibleWorldHeight = 2000f;

    private final ShapeRenderer shapeRenderer;

    public MinimapRenderer(float worldWidth, float worldHeight, float minimapWidth, float minimapHeight, OrthographicCamera camera) {
        this.minimapWidth = minimapWidth;
        this.minimapHeight = minimapHeight;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(float screenWidth, float screenHeight, float playerX, float playerY, 
                        Collection<Zone> acidZones, Collection<Zone> basicZones, 
                        List<Glucose> glucoseList) {
        float x = margin;
        float y = margin;

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
            4f, 4f);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

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

    private void drawZoneRelative(float worldX, float worldY, float worldRadius, float offsetX, float offsetY, float playerX, float playerY) {
        float dx = worldX - playerX;
        float dy = worldY - playerY;
    
        float visibleHalfW = visibleWorldWidth / 2f;
        float visibleHalfH = visibleWorldHeight / 2f;
    
        // Skip zones too far away
        if (Math.abs(dx) > visibleHalfW + worldRadius || Math.abs(dy) > visibleHalfH + worldRadius)
            return;
    
        // Scale world-space to minimap-space
        float px = offsetX + minimapWidth / 2f + (dx / visibleWorldWidth) * minimapWidth;
        float py = offsetY + minimapHeight / 2f + (dy / visibleWorldHeight) * minimapHeight;
    
        float scaledRadiusX = (worldRadius / visibleWorldWidth) * minimapWidth;
        float scaledRadiusY = (worldRadius / visibleWorldHeight) * minimapHeight;
    
        // Make sure it's visible
        scaledRadiusX = Math.max(scaledRadiusX, 2f);
        scaledRadiusY = Math.max(scaledRadiusY, 2f);
    
        // Calculate the bounds of the minimap
        float minimapLeft = offsetX;
        float minimapRight = offsetX + minimapWidth;
        float minimapBottom = offsetY;
        float minimapTop = offsetY + minimapHeight;
    
        // Calculate the bounds of the zone
        float zoneLeft = px - scaledRadiusX;
        float zoneRight = px + scaledRadiusX;
        float zoneBottom = py - scaledRadiusY;
        float zoneTop = py + scaledRadiusY;
    
        // Clip the zone to the minimap bounds
        float clippedLeft = Math.max(zoneLeft, minimapLeft);
        float clippedRight = Math.min(zoneRight, minimapRight);
        float clippedBottom = Math.max(zoneBottom, minimapBottom);
        float clippedTop = Math.min(zoneTop, minimapTop);
    
        // Only draw if the zone is still visible after clipping
        if (clippedLeft < clippedRight && clippedBottom < clippedTop) {
            // Adjust the position and size to account for clipping
            float clippedPx = (clippedLeft + clippedRight) / 2f;
            float clippedPy = (clippedBottom + clippedTop) / 2f;
            float clippedRadiusX = (clippedRight - clippedLeft) / 2f;
            float clippedRadiusY = (clippedTop - clippedBottom) / 2f;
    
            shapeRenderer.ellipse(
                clippedPx - clippedRadiusX,
                clippedPy - clippedRadiusY,
                clippedRadiusX * 2f,
                clippedRadiusY * 2f
            );
        }
    }
}
