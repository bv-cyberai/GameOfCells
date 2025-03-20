package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import static cellcorp.gameofcells.screens.ShopScreen.CanPurchaseResultType.*;

/**
 * Shop Screen
 * <p>
 * This screen allows players to evolve their cell into a new form using the shop
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */


/**
 * First screen of the application. Displayed after the application is created.
 */
public class ShopScreen implements GameOfCellsScreen {
    private static final float SHOP_TEXT_SIZE = 0.3f;
    private static final float UPGRADE_NAME_TEXT_SIZE = 0.2f;
    private static final float UPGRADE_INFO_TEXT_SIZE = 0.15f;

    private final static float UPGRADE_CARD_WIDTH = 300;
    private final static float UPGRADE_CARD_HEIGHT = 200;

    private static final String SIZE_UPGRADE_NAME = "Size upgrade";
    private static final int SIZE_UPGRADE_COST = 40;
    private static final int SIZE_UPGRADE_DIAMETER_INCREASE = 100;

    private static final int MITOCHONDRIA_UPGRADE_COST = 50;

    protected enum CanPurchaseResultType {
        CAN_PURCHASE,
        ALREADY_PURCHASED,
        PREVIOUS_UPGRADE_REQUIRED,
        NOT_ENOUGH_ATP;
    }

    /**
     * @param requiredUpgrade Set only if type == PREVIOUS_UPGRADE_REQUIRED. Null otherwise.
     */
    private record CanPurchaseResult(CanPurchaseResultType type, String requiredUpgrade) {
    }

    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
    // Change as is most convenient.
    /**
     * Width of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1200;
    /**
     * Height of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 800;

    private Main game;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;

    private final AssetManager assetManager;

    // Camera/Viewport
    private final Viewport viewport;

    // Keeps track of the initial screen prior to transition
    private final GamePlayScreen gamePlayScreen;

    // For rendering text
    private final SpriteBatch batch;  // Define the batch for drawing text

    private final Cell cell;


    // Shop state
    private int evolutionCost = 30; // The cost to evolve into a mitochondria

    // Animation variables
    private float animationTimer = 0f; // Timer for animations

    /**
     * Constructs the GamePlayScreen.
     *
     * @param game           The main game instance.
     * @param inputProvider  Handles user input.
     * @param camera         The camera for rendering.
     * @param viewport       The viewport for screen rendering scaling.
     * @param previousScreen The current screen gameplayscreen
     */
    public ShopScreen(
            Main game,
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            AssetManager assetManager,
            GamePlayScreen previousScreen,
            Cell cell // Required to get game state out of cell
    ) {
        this.game = game;
        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.gamePlayScreen = previousScreen;
        this.cell = cell;

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
    }

    /// Move the game state forward a tick, handling input, performing updates, and rendering.
    /// LibGDX combines these into a single method call, but we separate them out into public methods,
    /// to let us write tests where we call only [ShopScreen#handleInput] and [ShopScreen#update]
    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    /**
     * Resize the screen.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Pause the screen.
     */
    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    /**
     * Resume the screen.
     */
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    /**
     * Hide the screen.
     */
    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    /**
     * Dispose of the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();  // Dispose of the batch
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyPressed(Input.Keys.E)) {
            game.setScreen(gamePlayScreen);
        }

        if (canPurchaseSizeUpgrade().type == CAN_PURCHASE && inputProvider.isKeyPressed(Input.Keys.U)) {
            gamePlayScreen.sizeUpgradePurchased = true;
            cell.removeCellATP(SIZE_UPGRADE_COST);
            cell.increaseCellDiameter(SIZE_UPGRADE_DIAMETER_INCREASE);
        }

        // Evolve into a mitochondria when 'M' is pressed
        if (canPurchaseMitochondriaUpgrade().type == CAN_PURCHASE && inputProvider.isKeyPressed(Input.Keys.M)) {
            // Deduct cost and evolve
            gamePlayScreen.hasMitochondria = true;
            cell.removeCellATP(MITOCHONDRIA_UPGRADE_COST);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        // Update shop logic (check if player has enough energy to evolve)
        animationTimer += deltaTimeSeconds;
    }


    @Override
    public void draw() {
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var shopBackground = assetManager.get(AssetFileNames.SHOP_BACKGROUND, Texture.class);
        var mitochondriaIcon = assetManager.get(AssetFileNames.MITOCHONDRIA_ICON, Texture.class);

        // Set up font
        var callerFontScaleX = font.getScaleX();
        var callerFontScaleY = font.getScaleY();
        font.getData().setScale(SHOP_TEXT_SIZE);  // Set the font size
        font.setColor(Color.WHITE); // Default font color

        ScreenUtils.clear(0, 0, 0, 1);  // Clear the screen with a black background

        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();  // Start the batch for drawing 2d elements

        // Draw the shop screen
        batch.draw(shopBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());  // Draw the game background

        // Draw shop title
        font.draw(batch, "Cell Evolution Shop", viewport.getWorldWidth() / 2 - 120, viewport.getWorldHeight() - 50);
        batch.end();

        drawSizeUpgrade();

        batch.begin();
        // Draw mitochondria evolution card
        float cardX = viewport.getWorldWidth() / 2 - 150;
        float cardY = viewport.getWorldHeight() / 2 - 150;

        // Card background
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f); // Semi-transparent dark background
        batch.draw(shopBackground, cardX, cardY, UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT); // Reuse shop background for card
        batch.setColor(Color.WHITE); // Reset color

        // Card border (glow efect)
        var canPurchase = canPurchaseMitochondriaUpgrade();
        if (canPurchase.type == CAN_PURCHASE) {
            float glowIntensity = MathUtils.sin(animationTimer * 3f) * 0.5f + 0.8f; // Pulsating glow

            batch.setColor(0.5f, 1f, 0.5f, glowIntensity); // Green glow
            batch.draw(shopBackground, cardX - 5, cardY - 5, UPGRADE_CARD_WIDTH + 10, UPGRADE_CARD_HEIGHT + 10); // Glow effect

            batch.setColor(Color.WHITE); // Reset color
        } else {
            batch.setColor(1f, 1f, 1f, 0.4f);
        }

        // Mitochondria icon (floating animation)
        float iconY = cardY + 100 + MathUtils.sin(animationTimer * 2f) * 10f; // Floating effect
        batch.draw(mitochondriaIcon, cardX + 100, iconY, 100, 100); // Draw mitochondria icon

        font.getData().setScale(UPGRADE_NAME_TEXT_SIZE);
        font.draw(batch, "Mitochondria Evolution", cardX + 20, cardY + 150);

        font.draw(batch, "Cost: " + evolutionCost + " Energy", cardX + 20, cardY + 120);

        font.getData().setScale(UPGRADE_INFO_TEXT_SIZE);
        if (canPurchase.type == CAN_PURCHASE) {
            font.draw(batch, "Press M to evolve", cardX + 20, cardY + 90);
        } else if (canPurchase.type == NOT_ENOUGH_ATP) {
            font.draw(batch, "Not enough ATP", cardX + 20, cardY + 90);
        } else if (canPurchase.type == PREVIOUS_UPGRADE_REQUIRED) {
            font.draw(batch, "Purchase " + canPurchase.requiredUpgrade + " first", cardX + 20, cardY + 90);
        }
        batch.setColor(Color.WHITE);

        // Draw exit instructions
        font.getData().setScale(SHOP_TEXT_SIZE);
        font.draw(batch, "Press E to exit", viewport.getWorldWidth() / 2 - 80, 50);

        // End drawing
        font.getData().setScale(callerFontScaleX, callerFontScaleY);
        batch.end();
    }

    private void drawSizeUpgrade() {
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var shopBackground = assetManager.get(AssetFileNames.SHOP_BACKGROUND, Texture.class);
        float cardX = viewport.getWorldWidth() / 2 - 150;
        float cardY = viewport.getWorldHeight() / 2 + 100;

        batch.begin();

        // ==== Background ====

        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f); // Semi-transparent dark background
        batch.draw(shopBackground, cardX, cardY, UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT); // Reuse shop background for card
        batch.setColor(Color.WHITE); // Reset color

        var canPurchase = canPurchaseSizeUpgrade();
        if (canPurchase.type == CAN_PURCHASE) {
            float glowIntensity = MathUtils.sin(animationTimer * 3f) * 0.5f + 0.8f; // Pulsating glow

            batch.setColor(0.5f, 1f, 0.5f, glowIntensity); // Green glow
            batch.draw(shopBackground, cardX - 5, cardY - 5, UPGRADE_CARD_WIDTH + 10, UPGRADE_CARD_HEIGHT + 10); // Glow effect
            batch.setColor(Color.WHITE);
        } else {
            // TODO -- This isn't working. It's getting called and changing the color,
            //  but the transparency doesn't affect the text. Even though it does in other places.
            batch.setColor(1f, 1f, 1f, 0.4f);
        }

        font.getData().setScale(UPGRADE_NAME_TEXT_SIZE);
        font.draw(batch, "Size Upgrade", cardX + 20, cardY + 150);

        font.getData().setScale(UPGRADE_INFO_TEXT_SIZE);
        font.draw(batch, "Cost: " + SIZE_UPGRADE_COST + " Energy", cardX + 20, cardY + 120);

        if (canPurchase.type == CAN_PURCHASE) {
            font.draw(batch, "Press U to evolve", cardX + 20, cardY + 90);
        } else if (canPurchase.type == NOT_ENOUGH_ATP) {
            font.draw(batch, "Not enough ATP", cardX + 20, cardY + 90);
        }
        batch.end();

        batch.setColor(Color.WHITE);
    }

    /**
     * Get the batch for rendering.
     *
     * @return The batch for rendering.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    private CanPurchaseResult canPurchaseSizeUpgrade() {
        if (gamePlayScreen.sizeUpgradePurchased) return new CanPurchaseResult(ALREADY_PURCHASED, null); else if (cell.getCellATP() <= SIZE_UPGRADE_COST) {
            return new CanPurchaseResult(NOT_ENOUGH_ATP, null);
        } else {
            return new CanPurchaseResult(CAN_PURCHASE, null);
        }
    }

    private CanPurchaseResult canPurchaseMitochondriaUpgrade() {
        if (gamePlayScreen.hasMitochondria) {
            return new CanPurchaseResult(ALREADY_PURCHASED, null);
        } else if (!gamePlayScreen.sizeUpgradePurchased) {
            return new CanPurchaseResult(PREVIOUS_UPGRADE_REQUIRED, SIZE_UPGRADE_NAME);
        } else if (cell.getCellATP() <= MITOCHONDRIA_UPGRADE_COST) {
            return new CanPurchaseResult(NOT_ENOUGH_ATP, null);
        } else {
            return new CanPurchaseResult(CAN_PURCHASE, null);
        }
    }
}
