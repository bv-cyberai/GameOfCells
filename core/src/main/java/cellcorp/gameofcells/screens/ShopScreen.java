package cellcorp.gameofcells.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Interpolation;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.FlagellaUpgrade;
import cellcorp.gameofcells.objects.MitochondriaUpgrade;
import cellcorp.gameofcells.objects.NucleusUpgrade;
import cellcorp.gameofcells.objects.RibosomeUpgrade;
import cellcorp.gameofcells.objects.Upgrade;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

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
    private final static float SELECTED_CARD_SCALE = 1.2f; // Scale of the selected card

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

    private int selectedUpgradeIndex = 0; // Tracks the currently selected upgrade
    private Table upgradeTable; // Table containing the upgrade cards
    private List<Table> upgradeCards; // List of individual upgrade card tables

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

    /**
     * Create the UI for the shop screen.
     */
    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label titleLabel = new Label("Organelle Shop",
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT,
            BitmapFont.class), Color.WHITE));
        titleLabel.setFontScale(SHOP_TEXT_SIZE);
        mainTable.add(titleLabel).padTop(20).row();

        // Upgrade cards
        upgradeTable = new Table();
        upgradeCards = new ArrayList<>();

        for (Upgrade upgrade : upgrades) {
            Table card = createUpgradeCard(upgrade);
            upgradeCards.add(card);
            upgradeTable.add(card).padRight(10);
        }

        mainTable.add(upgradeTable).expand().fill().pad(20).row();

        // Exit instructions
        Label exitLabel = new Label("Press E to exit | Arrow keys to navigate | Enter to purchase",
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));

        exitLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        mainTable.add(exitLabel).padBottom(20).row();

        stage.addActor(mainTable);

        // Center the selected upgrade initially
        centerSelectedUpgrade();
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
                return true;
            }
            return false;
        });
        card.add(purchaseButton).padTop(10).row();

        return card;
    }

    private void centerSelectedUpgrade() {
        // Calculate the target X position for the upgrade table
        float targetX = (VIEW_RECT_HEIGHT / 2) - (UPGRADE_CARD_WIDTH / 2) - 
        (selectedUpgradeIndex * (UPGRADE_CARD_WIDTH + 20));

        // Animate the upgrade table to the target X position
        upgradeTable.addAction(Actions.moveTo(targetX, upgradeTable.getY(), 0.5f, Interpolation.smooth));

        // Scale the selected upgrade card up and others down
        for (int i = 0; i < upgradeCards.size(); i++) {
            Table card = upgradeCards.get(i);
            if (i == selectedUpgradeIndex) {
                card.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.5f, Interpolation.smooth));
            } else {
                card.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.smooth));
            }
        }
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
        if (inputProvider.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedUpgradeIndex > 0) {
                selectedUpgradeIndex--;
                centerSelectedUpgrade();
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectedUpgradeIndex < upgrades.size() - 1) {
                selectedUpgradeIndex++;
                centerSelectedUpgrade();
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            Upgrade selectedUpgrade = upgrades.get(selectedUpgradeIndex);
            if (selectedUpgrade.canPurchase(cell, this)) {
                selectedUpgrade.applyUpgrade(cell);
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.E)) {
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

    @Override
    public void show() {
        // This method is called when the screen becomes the current screen for the game.
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
