package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestChunk {

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(
                new ApplicationListener() {
                    @Override
                    public void create() {
                    }

                    @Override
                    public void resize(int width, int height) {
                    }

                    @Override
                    public void render() {
                    }

                    @Override
                    public void pause() {
                    }

                    @Override
                    public void resume() {
                    }

                    @Override
                    public void dispose() {
                    }
                }, config
        );

        // Mock the graphics provider
        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @Test
    public void constructChunk() {
        var chunk = new Chunk(10, -20);
        assertEquals(10, chunk.row());
        assertEquals(-20, chunk.col());
    }

    @Test
    public void initialChunkDoesNotEqualChunkAfterMovingFor10Seconds() {
        var runner = GameRunner.create();

        runner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        runner.step();
        var cell = ((GamePlayScreen) runner.game.getScreen()).getCell();
        var initialChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());

        runner.setHeldDownKeys(Set.of(Input.Keys.RIGHT, Input.Keys.C));
        runner.runForSeconds(10);
        cell = ((GamePlayScreen) runner.game.getScreen()).getCell();
        var newChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        assertNotEquals(initialChunk, newChunk);
    }

    @Test
    public void testFromCoords() {
        // At time of writing, Chunk.CHUNK_LENGTH == 2000
        var chunk_at_0_0 = Chunk.fromWorldCoords(0, 0);
        assertEquals(new Chunk(0, 0), chunk_at_0_0);
        chunk_at_0_0 = Chunk.fromWorldCoords(1000, 1000);
        assertEquals(new Chunk(0, 0), chunk_at_0_0);
        chunk_at_0_0 = Chunk.fromWorldCoords(1999, 1999);
        assertEquals(new Chunk(0, 0), chunk_at_0_0);

        var chunk_at_1_1 = Chunk.fromWorldCoords(2000, 2000);
        assertEquals(new Chunk(1, 1), chunk_at_1_1);
        chunk_at_1_1 = Chunk.fromWorldCoords(2500, 2500);
        assertEquals(new Chunk(1, 1), chunk_at_1_1);

        var chunk_at_neg_1_neg_1 = Chunk.fromWorldCoords(-500, -500);
        assertEquals(new Chunk(-1, -1), chunk_at_neg_1_neg_1);
    }

    @Test
    public void testAdjacentChunks() {
        var chunk = new Chunk(0, 0);
        var expected = Set.of(
                new Chunk(-1, -1),
                new Chunk(-1, 0),
                new Chunk(-1, 1),
                new Chunk(0, -1),
                new Chunk(0, 0),
                new Chunk(0, 1),
                new Chunk(1, -1),
                new Chunk(1, 0),
                new Chunk(1, 1)
        );
        assertEquals(expected, new HashSet<>(chunk.adjacentChunks()));

        chunk = new Chunk(-1000, -1000);
        expected = Set.of(
                new Chunk(-999, -999),
                new Chunk(-999, -1000),
                new Chunk(-999, -1001),
                new Chunk(-1000, -999),
                new Chunk(-1000, -1000),
                new Chunk(-1000, -1001),
                new Chunk(-1001, -999),
                new Chunk(-1001, -1000),
                new Chunk(-1001, -1001)
        );
        assertEquals(expected, new HashSet<>(chunk.adjacentChunks()));
    }

    @Test
    public void testToRectangle() {
        // At time of writing, Chunk.CHUNK_LENGTH == 2000
        var chunk = new Chunk(0, 0);
        var expected = new Rectangle(0, 0, 2000, 2000);
        assertEquals(expected, chunk.toRectangle());

        chunk = new Chunk(1000, 0);
        expected = new Rectangle(2_000_000, 0, 2000, 2000);
        assertEquals(expected, chunk.toRectangle());
    }
}
