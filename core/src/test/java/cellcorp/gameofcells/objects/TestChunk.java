package cellcorp.gameofcells.objects;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.Input;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestChunk {
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
        var cell = ((GamePlayScreen)runner.game.getScreen()).getCell();
        var initialChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());

        runner.setHeldDownKeys(Set.of(Input.Keys.RIGHT, Input.Keys.C));
        runner.runForSeconds(10);
        cell = ((GamePlayScreen)runner.game.getScreen()).getCell();
        var newChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        assertNotEquals(initialChunk, newChunk);
    }
}
