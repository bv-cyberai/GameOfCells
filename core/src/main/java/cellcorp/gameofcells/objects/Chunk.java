package cellcorp.gameofcells.objects;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A chunk of world space -- like chunks in minecraft.
 * Used for spawning / despawning logic.
 * This is just a value class, it has no state or behavior.
 * Any two chunks with the same `row()` and `col()` are equal.
 */
public final class Chunk {
    /**
     * Length of chunks. Ideally, should be at least as large as the larger of (VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT),
     * to prevent objects popping in within player view.
     */
    public static final int CHUNK_LENGTH = 1600;

    private final int row;
    private final int col;

    public Chunk(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Get the chunk containing the given world coordinates.
     */
    public static Chunk fromWorldCoords(float x, float y) {
        return new Chunk(
                (int) Math.floor(x / (float) CHUNK_LENGTH),
                (int) Math.floor(y / (float) CHUNK_LENGTH)
        );
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Chunk) obj;
        return this.row == that.row &&
                this.col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Chunk[" +
                "row=" + row + ", " +
                "col=" + col + ']';
    }

    /**
     * Get the rectangle this chunk covers in the world.
     */
    public Rectangle toRectangle() {
        return new Rectangle(
                row * CHUNK_LENGTH,
                col * CHUNK_LENGTH,
                CHUNK_LENGTH,
                CHUNK_LENGTH
        );
    }

    /**
     * Get a list of chunks in a 3x3 grid around this chunk, including this chunk.
     */
    public List<Chunk> adjacentChunks() {
        var adjacentChunks = new ArrayList<Chunk>();
        for (int row = this.row() - 1; row <= this.row() + 1; row++) {
            for (int col = this.col() - 1; col <= this.col() + 1; col++) {
                adjacentChunks.add(new Chunk(row, col));
            }
        }
        return adjacentChunks;
    }
}