package cellcorp.gameofcells.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.math.MathUtils;

public class Notification {
    private String message;
    private float duration;
    private float elapsedTime;
    private Color color;
    private float alpha;
    private float yOffset; // For bounce effect

    // Visual polish additions
    private static final float PADDING = 10f;
    private static final float CORNER_RADIUS = 5f;
    private ShapeRenderer shapeRenderer;

    public Notification(String message, float duration, Color color) {
        this.message = message;
        this.duration = duration;
        this.elapsedTime = 0;
        this.color = color;
        this.alpha = 0; // Start transparent for fade-in effect
        this.yOffset = 0; // Start with no offset
        this.shapeRenderer = new ShapeRenderer(); // Initialize shape renderer
    }

    /**
     * * Updates the notification's state.
     * This method updates the elapsed time and calculates the alpha value for fade-in/out effect.
     * @param deltaTime
     * @return
    */
    public boolean update(float deltaTime) {
        elapsedTime += deltaTime;

        // Fade in/out logic
        if (elapsedTime < 0.5f) {
            alpha = Math.min(1, elapsedTime * 2); // Fade in
        } else if (elapsedTime > duration - 0.5f) {
            alpha = Math.max(0, (duration - elapsedTime) * 2); // Fade out
        } else {
            alpha = 1; // Fully visible in the middle of the duration
        }

        // Bounce effect logic
        yOffset = MathUtils.sin(elapsedTime * 10f) * 5f * alpha; // Bounce effect

        return elapsedTime >= duration; // Return true if the notification is expired
    }

    /**
     * * Renders the notification on the screen.
     * This method uses the provided SpriteBatch and BitmapFont to draw the notification message at the specified position.
     * @param batch
     * @param font
     * @param x
     * @param y
    */
    public void render(SpriteBatch batch, BitmapFont font, float x, float y) {
        // First calculate text dimensions
        font.getData().setScale(1f); // Set font scale for better visibility
        GlyphLayout layout = new GlyphLayout(font, message);

        // Draw background (using ShapeRenderer)
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Background with rounded corners
        shapeRenderer.setColor(0f, 0f, 0f, alpha * 0.7f); // Semi-transparent background
        shapeRenderer.rect(
            x - PADDING, 
            y - layout.height - PADDING/2 + yOffset, 
            layout.width + PADDING*2, 
            layout.height + PADDING, 
            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK
        );

        // Border
        shapeRenderer.setColor(color.r, color.g, color.b, alpha); // Border color
        shapeRenderer.rect(
            x - PADDING + 2, 
            y - layout.height - PADDING/2 + 2 + yOffset, 
            layout.width + PADDING * 2 - 4, 
            layout.height - PADDING - 4, 
            color, color, color, color
        );

        shapeRenderer.end();

        // Draw text
        font.setColor(1f, 1f, 1f, alpha); // White text color
        font.draw(batch, message, x, y + yOffset); // Draw the notification message
    }

    /**
     * * Get the message of the notification.
     * This message is displayed to the user when the notification is rendered.
     * @return The message of the notification.
    */
    public String getMessage() {
        return message;
    }

    /**
     * * Get the color of the notification.
     * This color is used to render the notification text.
     * @return The color of the notification.
    */
    public Color getColor() {
        return color;
    }

    /**
     * * Get the alpha value for the notification.
     * This value is between 0 and 1, where 0 is fully transparent and 1 is fully opaque.
     * @return The alpha value of the notification.
    */
    public float getAlpha() {
        return alpha;
    }

    public void dispose() {
        shapeRenderer.dispose(); // Dispose of the ShapeRenderer when done
    }
}
