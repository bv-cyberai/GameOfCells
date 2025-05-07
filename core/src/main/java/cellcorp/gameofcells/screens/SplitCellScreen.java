package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.Util;
import cellcorp.gameofcells.objects.Cell;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static cellcorp.gameofcells.screens.SplitCellScreen.State.*;

/**
 * Screen that plays the cell splitting animation.
 */
public class SplitCellScreen implements GameOfCellsScreen {
    private static final float START_DURATION = 1;
    private static final float ZOOM_IN_DURATION = 3;
    private static final float SPLIT_DURATION = 4;
    private static final float ROTATE_DURATION = 1;
    private static final float SWIM_DURATION = 5;
    private static final float ZOOM_OUT_DURATION = 3;

    private static final float MINIMUM_ZOOM = 1;
    private static final float MAXIMUM_ZOOM = 1.3f;

    /**
     * Maximum distance for each cell to move in the split phase
     */
    private static final float MAXIMUM_SPLIT_OFFSET = 200;
    private static final float VERTICAL_WIGGLE = 5;

    private static final float SWIM_ANGLE_DEGREES = -60;
    private static final float SWIM_DISTANCE = 800;
    private static final float SWIM_WIGGLE_FREQUENCY = 15;
    private static final float SWIM_WIGGLE_MAGNITUDE = 15;

    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;

    private final Main game;
    private final GamePlayScreen gamePlayScreen;
    private final Cell cell;
    private final Cell childCell;

    /**
     * x-position of the cell when this screen is created.
     */
    private final float startX;
    /**
     * y-position of the cell when this screen is created.
     */
    private final float startY;
    private final float startAngle;
    private final float swimStartX;

    private SplitCellScreen.State state = ZOOM_IN;
    private float timerSeconds = 0;

    public SplitCellScreen(Main game, GamePlayScreen gamePlayScreen) {
        this.game = game;
        this.gamePlayScreen = gamePlayScreen;
        this.cell = this.gamePlayScreen.getCell();
        this.childCell = new Cell(gamePlayScreen.getCell());
        this.startX = cell.getX();
        this.startY = cell.getY();
        this.startAngle = cell.getCellRotation();
        this.swimStartX = startX + MAXIMUM_SPLIT_OFFSET;

        this.camera = this.gamePlayScreen.getCamera();
        this.viewport = this.gamePlayScreen.getViewport();
        this.spriteBatch = this.gamePlayScreen.getSpriteBatch();
        this.shapeRenderer = this.gamePlayScreen.getShapeRenderer();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float deltaTimeSeconds) {
        handleInput(deltaTimeSeconds);
        update(deltaTimeSeconds);
        draw();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {

    }

    @Override
    public void update(float deltaTimeSeconds) {
        updateTimer(deltaTimeSeconds);
        if (state == SPLIT) {
            var splitProgress = timerSeconds / SPLIT_DURATION;
            splitProgress = Util.smoothStep(0, 1, splitProgress);
            var xOffset = MathUtils.lerp(0, MAXIMUM_SPLIT_OFFSET, splitProgress);
            var yOffset = MathUtils.sin(splitProgress * 2 * (float) Math.PI) * VERTICAL_WIGGLE;

            cell.getCircle().x = startX - xOffset;
            cell.getCircle().y = startY - yOffset;
            childCell.getCircle().x = startX + xOffset;
            childCell.getCircle().y = startY + yOffset;
        } else if (state == ROTATE) {
            var rotateProgress = timerSeconds / ROTATE_DURATION;
            rotateProgress = Util.smoothStep(0, 1, rotateProgress);
            var angle = MathUtils.lerpAngleDeg(startAngle, SWIM_ANGLE_DEGREES, rotateProgress);
            childCell.setCellRotation(angle);
            childCell.updateFlagellum(deltaTimeSeconds);
        } else if (state == SWIM) {
            var swimProgress = timerSeconds / SWIM_DURATION;
            var swimDistance = MathUtils.lerp(0, SWIM_DISTANCE, swimProgress);
            var wiggle = MathUtils.sin(swimProgress * SWIM_WIGGLE_FREQUENCY) * SWIM_WIGGLE_MAGNITUDE;
            var swimVector = new Vector2(wiggle, swimDistance);
            swimVector = swimVector.rotateDeg(SWIM_ANGLE_DEGREES);

            childCell.getCircle().x = swimStartX + swimVector.x;
            childCell.getCircle().y = startY + swimVector.y;
        }
    }

    private void updateFlagellum() {
        var flagellumVectors = childCell.getFlagellumVectors();
        flagellumVectors.clear();

        //calculate new sin wave positions.
        for (int y = 0; y < cell.getFlagellumLength(); y++) {
            float flagX = (float) (50 * Math.sin((y * 0.05f + timerSeconds)));
            flagellumVectors.add(new Vector2(flagX, y - childCell.getCircle().radius - cell.getFlagellumLength())); // <-shifts flagella down, stupid calc, but it works
        }

        // Rotate the flagellum
        for (int i = 0; i < flagellumVectors.size; i++) {
            Vector2 vector = flagellumVectors.get(i);
            vector.rotateDeg(cell.getCellRotation());
        }
    }

    private void updateTimer(float deltaTimeSeconds) {
        if (isFinished(deltaTimeSeconds)) {
            game.setScreen(gamePlayScreen);
            gamePlayScreen.getGameLoaderSaver().saveState();
            return;
        }
        timerSeconds += deltaTimeSeconds;
        if (timerSeconds > duration(state)) {
            state = nextState(state);
            timerSeconds = 0;
        }
    }

    /**
     * Determine whether the animation has finished.
     */
    private boolean isFinished(float deltaTimeSeconds) {
        return state == ZOOM_OUT && timerSeconds + deltaTimeSeconds > ZOOM_OUT_DURATION;
    }

    private float duration(State state) {
        if (state == START) {
            return START_DURATION;
        } else if (state == ZOOM_IN) {
            return ZOOM_IN_DURATION;
        } else if (state == SPLIT) {
            return SPLIT_DURATION;
        } else if (state == ROTATE) {
            return ROTATE_DURATION;
        } else if (state == SWIM) {
            return SWIM_DURATION;
        } else {
            // ZOOM_OUT
            return ZOOM_OUT_DURATION;
        }
    }

    private State nextState(State state) {
        if (state == START) {
            return ZOOM_IN;
        } else if (state == ZOOM_IN) {
            return SPLIT;
        } else if (state == SPLIT) {
            return ROTATE;
        } else if (state == ROTATE) {
            return SWIM;
        } else {
            // SWIM || ZOOM_OUT
            return ZOOM_OUT;
        }
    }

    @Override
    public void draw() {
        setCameraZoom();

        gamePlayScreen.setUpDraw();
        gamePlayScreen.drawBackground();
        drawCells();
    }

    /**
     * Sets camera zoom according to state and timer
     */
    private void setCameraZoom() {
        float zoom;
        if (state == START) {
            zoom = 1;
        } else if (state == ZOOM_IN) {
            var zoomProgress = timerSeconds / ZOOM_IN_DURATION;
            zoom = MathUtils.lerp(MINIMUM_ZOOM, MAXIMUM_ZOOM, zoomProgress);
            zoom = MathUtils.clamp(zoom, MINIMUM_ZOOM, MAXIMUM_ZOOM); // lerp doesn't clamp
        } else if (state == SPLIT || state == ROTATE || state == SWIM) {
            zoom = MAXIMUM_ZOOM;
        } else {
            // ZOOM_OUT
            var zoomProgress = timerSeconds / ZOOM_IN_DURATION;
            zoom = MathUtils.lerp(MAXIMUM_ZOOM, MINIMUM_ZOOM, zoomProgress);
            zoom = MathUtils.clamp(zoom, MINIMUM_ZOOM, MAXIMUM_ZOOM); // lerp doesn't clamp
        }

        camera.zoom = 1 / zoom;
    }

    private void drawCells() {
        cell.draw(spriteBatch, shapeRenderer);
        if (state != ZOOM_OUT) {
            childCell.draw(spriteBatch, shapeRenderer);
        }
    }


    @Override
    public void resize(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    protected enum State {
        START,
        ZOOM_IN,
        SPLIT,
        ROTATE,
        SWIM,
        ZOOM_OUT,
    }
}
