package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Particles Class
 * <p>
 * Controls the Rendering of the Particles displayed on the screen
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

public class Particles {
    private static final int NUM_PARTICLES = 100; // Number of particles
    private final Array<Particle> particles;
    private final Texture whitePixelTexture;
    private final Random random;

    public Particles(Texture whitePixelTexture) {
        this.whitePixelTexture = whitePixelTexture;
        this.random = new Random();
        this.particles = new Array<>();

        // Initialize particles
        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles.add(new Particle(
                random.nextFloat() * 1200, // Random x position (adjust to your screen width)
                random.nextFloat() * 800,  // Random y position (adjust to your screen height)
                random.nextFloat() * 2 + 1, // Random size between 1 and 3
                random.nextFloat() * 0.5f + 0.1f, // Random speed between 0.1 and 0.6
                whitePixelTexture // Pass the white pixel texture to the particle
            ));
        }
    }

    public void update(float delta, float worldWidth, float worldHeight) {
        for (Particle particle : particles) {
            particle.update(delta, worldWidth, worldHeight);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Particle particle : particles) {
            particle.draw(batch);
        }
    }

    public void dispose() {
        whitePixelTexture.dispose();
    }

    // Inner class for individual particles
    private static class Particle {
        private float x, y;
        private final float size;
        private final float speed;
        private final Texture texture;

        public Particle(float x, float y, float size, float speed, Texture texture) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.texture = texture;
        }

        public void update(float delta, float worldWidth, float worldHeight) {
            y -= speed; // Move particles downward
            if (y < 0) { // Reset particle position if it goes off-screen
                y = worldHeight;
                x = (float) Math.random() * worldWidth;
            }
        }

        public void draw(SpriteBatch batch) {
            batch.setColor(0.5f, 0.5f, 0.8f, 0.5f); // Light blue with transparency
            batch.draw(texture, x, y, size, size); // Draw the particle
        }
    }

    /**
     * Checks if the particles are active
     *
     * @return true if particles are active, false otherwise
     */
    public boolean isActive() {
        return !particles.isEmpty();
    }

    /**
     * Get the white pixel texture.
     *
     * @return The white pixel texture used for rendering particles.
     */
    public Texture getWhitePixelTexture() {
        return whitePixelTexture;
    }

    /**
     * Get the number of particles.
     *
     * @return The number of particles in the system.
     */
    public int getParticleCount() {
        return particles.size;
    }
}
