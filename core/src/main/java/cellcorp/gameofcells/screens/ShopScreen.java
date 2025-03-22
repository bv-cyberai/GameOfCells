package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.objects.*;
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

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

// Scene_2d UI implemenation. I believe this is the minimum of what we need (adjust if needed)
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.List;

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
    private final Stage stage;  
    private final List<Upgrade> upgrades;
    private final Cell cell;
    private final Main game; 
    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final AssetManager assetManager;

    // Camera/Viewport
    private final Viewport viewport;

    // Keeps track of the initial screen prior to transition
    private final GameOfCellsScreen previousScreen;

    // For rendering text
    private final SpriteBatch batch;  // Define the batch for drawing text

    private static final float SHOP_TEXT_SIZE = 0.3f;
    private static final float UPGRADE_NAME_TEXT_SIZE = 0.2f;
    private static final float UPGRADE_INFO_TEXT_SIZE = 0.15f;

    private final static float UPGRADE_CARD_WIDTH = 300;
    private final static float UPGRADE_CARD_HEIGHT = 200;

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

    /**
     * Constructs the GamePlayScreen.
     *
     * @param game           The main game instance.
     * @param inputProvider  Handles user input.
     * @param assetManager   Manages game assets.
     * @param previousScreen The current screen gameplayscreen
     * @param cell           The cell object
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
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;
        this.previousScreen = previousScreen;
        this.cell = cell;

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
        this.stage = new Stage(viewport, batch);

         // Initialize upgrades
         upgrades = new ArrayList<>();
         upgrades.add(new MitochondriaUpgrade());
         upgrades.add(new RibosomeUpgrade());
         upgrades.add(new FlagellaUpgrade());
         upgrades.add(new NucleusUpgrade());

         // Create UI
         createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);

        for (Upgrade upgrade : upgrades) {
            table.add(createUpgradeCard(upgrade)).pad(10);
        }

        stage.addActor(table);
    }

    private Table createUpgradeCard(Upgrade upgrade) {
        Table card = new Table();
        card.setBackground(new TextureRegionDrawable(
            assetManager.get(
                AssetFileNames.SHOP_BACKGROUND, 
                Texture.class)));
        
        // Upgrade name
        Label nameLabel = new Label(upgrade.getName(), 
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, 
            BitmapFont.class), Color.WHITE));
        nameLabel.setFontScale(UPGRADE_NAME_TEXT_SIZE);
        card.add(nameLabel).padTop(10).row();

        // Upgrade cost
        Label costLabel = new Label("Cost: " + upgrade.getCost() + "ATP", 
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT,
            BitmapFont.class), Color.WHITE));
        costLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        card.add(costLabel).padTop(5).row();

        // Upgrade description
        Label descriptionLabel = new Label(upgrade.getDescription(), 
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT,
            BitmapFont.class), Color.WHITE));
        descriptionLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        descriptionLabel.setWrap(true);
        card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH).padTop(5).row();

        // Purchase button
        TextButton purchaseButton = new TextButton("Purchase", 
            new TextButton.TextButtonStyle(null, null, null, 
            assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class)));
        purchaseButton.setDisabled(!upgrade.canPurchase(cell, this));
        purchaseButton.addListener(event -> {
            if (upgrade.canPurchase(cell, this)) {
                upgrade.applyUpgrade(cell);
            }
            return false;
        });
        card.add(purchaseButton).padTop(10).row();

        return card;
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

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyJustPressed(Input.Keys.E)) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        stage.act(deltaTimeSeconds);
    }

    @Override
    public void draw() {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.draw();
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
        stage.dispose();
    }

    /**
     * Get the batch for rendering.
     *
     * @return The batch for rendering.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    public boolean hasMitochondria() {
        return cell.hasMitochondria();
    }

    public boolean hasRibosome() {
        return cell.getProteinSynthesisMultiplier() > 1.0f;
    }

    public boolean hasFlagella() {
        return cell.getMovementSpeedMultiplier() > 1.0f;
    }
}
