package cellcorp.gameofcells.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Actor;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Particles;
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
    private final cellcorp.gameofcells.objects.Cell playerCell;
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
    private final ShapeRenderer shapeRenderer;  // For drawing custom backgrounds
    private final Particles particles; // For drawing particles

    private static final float SHOP_TEXT_SIZE = 0.3f;
    private static final float OPTION_NAME_TEXT_SIZE = 0.2f;
    private static final float OPTION_INFO_TEXT_SIZE = 0.15f;
    private static final float INSTRUCTION_TEXT_SIZE = 0.18f;

    private final static float OPTION_CARD_WIDTH = 400;
    private final static float OPTION_CARD_HEIGHT = 450;
    private final static float SELECTED_CARD_SCALE = 1.4f; // Scale of the selected card

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

    private int selectedOptionIndex = 0; // Tracks the currently selected option
    private Table sizeUpgradeTable; // Table containing the size upgrade cards
    private List<Table> optionCards; // List of individual option card tables

    /**
     * Constructs the GamePlayScreen.
     *
     * @param game           The main game instance.
     * @param inputProvider  Handles user input.
     * @param assetManager   Manages game assets.
     * @param previousScreen The current screen gameplayscreen
     * @param playerCell           The cell object
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
        this.playerCell = cell;

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
        this.shapeRenderer = graphicsProvider.createShapeRenderer(); // Initialize the shape renderer for custom backgrounds

        Texture whitePixelTexture = assetManager.get(AssetFileNames.WHITE_PIXEL, Texture.class);
        this.particles = new Particles(whitePixelTexture);
        this.stage = new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT), graphicsProvider.createSpriteBatch());

        // Pre load textures
        Texture optionBgTexture = createOptionBackgroundTexture();
        Texture glowingBorderTexture = createGlowingBorderTexture();

         // Create UI
        createUI(optionBgTexture, glowingBorderTexture);
    }

    /**
     * Render the screen.
     *
     * @param delta The time since the last frame in seconds.
     */
    @Override
    public void render(float delta) {
        // Clear the screen with a semi-transparent black color
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Clear the screen with a dark color

        // Update and draw the particles
        particles.update(delta, VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        particles.draw(batch);

        // Handle the input first
        handleInput(delta);

        // Update the ATP and size labels 
        updateTrackers();

        // Update the game state
        update(delta);

        // Draw the state (UI elements)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedOptionIndex > 0) {
                selectedOptionIndex--;
                updateOptionSelection();
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectedOptionIndex < optionCards.size() - 1) {
                selectedOptionIndex++;
                updateOptionSelection();
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            // Handle selection of the current option
            if (selectedOptionIndex == 0) {
                // Size option selected
                // Navigate to the size upgrade screen
                game.setScreen(new SizeUpgradeScreen(
                    game, 
                    inputProvider, 
                    graphicsProvider, 
                    assetManager, 
                    previousScreen, 
                    playerCell));
            } else if (selectedOptionIndex == 1) {
                // Organelle option selected
                // Navigate to the organelle upgrade screen
                game.setScreen(new OrganelleUpgradeScreen(
                    game, 
                    inputProvider, 
                    graphicsProvider, 
                    assetManager, 
                    this, 
                    playerCell));
            }
        }
        
        if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Fade put before exiting the shop screen
            stage.getRoot().addAction(Actions.sequence(
                Actions.fadeOut(1f), // Fade out over 1 seconds
                Actions.run(() -> {
                    // Unpause the game state (resume the cell movement)
                    if (previousScreen instanceof GamePlayScreen) {
                        ((GamePlayScreen) previousScreen).resumeGame();
                    }

                    // Transition back to the previous screen
                    game.setScreen(previousScreen);
                })
            ));
        }
    }

    /**
     * Update the screen.
     *
     * @param deltaTimeSeconds The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTimeSeconds) {
        stage.act(deltaTimeSeconds);
    }

    /**
     * Draw the screen.
     */
    @Override
    public void draw() {
        // This method is no longer needed as rendering is handled in `render`.
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        // This method is called when the screen becomes the current screen for the game.

        // Set the stage's root actor to be transparent initially
        stage.getRoot().getColor().a = 0; // Full transparent

        // Fade in the shop screen
        stage.getRoot().addAction(Actions.fadeIn(2f)); // Fade in over 1 seconds

        // Pause the game state (stop the cell from moving)
        if (previousScreen instanceof GamePlayScreen) {
            ((GamePlayScreen) previousScreen).pauseGame();
        }
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
        // No action neede
    }

    /**
     * Resume the screen.
     */
    @Override
    public void resume() {
        // No action needed
    }

    /**
     * Hide the screen.
     */
    @Override
    public void hide() {
        // No action needed
    }

    /**
     * Get the batch for rendering.
     *
     * @return The batch for rendering.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Dispose of the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();  // Dispose of the batch
        stage.dispose();
        shapeRenderer.dispose();
        particles.dispose();
    }

    /**
     * Create the UI for the shop screen.
     */
    private void createUI(Texture optionBgTexture, Texture glowingBorderTexture) {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label titleLabel = createLabel("Organelle Shop", SHOP_TEXT_SIZE);
        mainTable.add(titleLabel).padTop(20).row();

        // ATP Tracker
        Label atpLabel = createLabel("ATP: " + playerCell.getCellATP(), SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(atpLabel).padTop(10).row();

        // Size Tracker
        Label sizeLabel = createLabel("Size: " + (playerCell.getcellSize() - 100) / 100, SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(sizeLabel).padTop(10).row();

        // Table for Size and Organelle
        Table optionsTable = new Table();
        optionCards = new ArrayList<>();

        Table sizeCard = createOptionCard("Size", "Increase the size of the cell", optionBgTexture, glowingBorderTexture);
        Table organelleCard = createOptionCard("Organelle", "Purchase organelle upgrades", optionBgTexture, glowingBorderTexture);

        // Add the cards to the list and table
        optionCards.add(sizeCard);
        optionCards.add(organelleCard);
        optionsTable.add(sizeCard).pad(10);
        optionsTable.add(organelleCard).pad(10);

        // Add the main table to the stage
        mainTable.add(optionsTable).padTop(50).row();

        // Exit instructions
        Label exitLabel = createLabel("Press ESC to exit | Arrow keys to navigate | Enter to select", INSTRUCTION_TEXT_SIZE);
        exitLabel.setAlignment(Align.center); // Center the text
        mainTable.add(exitLabel).padBottom(20).row();

        stage.addActor(mainTable);

        // Highlight the initially selected option (size)
        updateOptionSelection();
    }

    /**
     * Create a label with the specified text and scale.
     * @param text
     * @param scale
     * @return
     */
    private Label createLabel(String text,float scale) {
        Label label = new Label(text, new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        label.setFontScale(scale);
        return label;
    }

    /**
     * Create a table for an option card.
     * @param title
     * @param description
     * @return
     */
    private Table createOptionCard(String title, String description, Texture bgTexture, Texture borderTexture) {
        Table card = new Table();
        card.center(); // Center all elements and add padding

        // Set a fixed size for the card to match the glowing border
        card.setSize(OPTION_CARD_WIDTH, OPTION_CARD_HEIGHT); // Set the size of the card

        // Create a glowing border for the card
        Image glowingBorder = new Image(borderTexture);
        glowingBorder.setSize(OPTION_CARD_WIDTH, OPTION_CARD_HEIGHT); // Match the card size
        glowingBorder.setVisible(false); // Hide the border by default
        glowingBorder.setName("glowingBorder");

        // Add the glowing border to the card
        card.addActor(glowingBorder); // Add the border as an actor (not part of the table layout)

        // Set a custom background for the card
        card.setBackground(new TextureRegionDrawable(bgTexture));

        // Add padding to the top of the card
        card.padTop(10);

        // Title
        Label titleLabel = createLabel(title, OPTION_NAME_TEXT_SIZE);
        titleLabel.setAlignment(Align.center); // Center the text
        card.add(titleLabel).width(OPTION_CARD_WIDTH - 20).padTop(10).row(); // Add the label to the table

        // Description
        Label descriptionLabel = createLabel(description, OPTION_INFO_TEXT_SIZE);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center); // Center the text
        card.add(descriptionLabel).width(OPTION_CARD_WIDTH - 20).padTop(10).row(); // Adjusted width for padding

        return card;
    }

    /**
     * Update the selected option card.
     */
    private void updateOptionSelection() {
        for (int i = 0; i < optionCards.size(); i++) {
            Table card = optionCards.get(i);
            Image glowingBorder = (Image) card.findActor("glowingBorder"); // Find the glowing border by name

            if (glowingBorder != null) { // Check if the glowing border exists
                if (i == selectedOptionIndex) {
                    card.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.5f, Interpolation.smooth));
                    glowingBorder.setVisible(true); // Show the glowing border
                } else {
                    card.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.smooth));
                    glowingBorder.setVisible(false); // Hide the glowing border
                }
            }
        }
    }

    /**
     * Create a custom background texture for the option cards.
     * @return
     */
    private Texture createOptionBackgroundTexture() {
        int width = (int) OPTION_CARD_WIDTH;
        int height = (int) OPTION_CARD_HEIGHT;
        Texture texture = new Texture(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Draw the background using a gradient effect
        pixmap.setColor(0.2f, 0.2f, 0.2f, 0.8f); // Dark gray with transparency
        pixmap.fillRectangle(0, 0, width, height); // Fill the pixmap with the color

        // Draw the pixmap to the texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose(); // Clean up the pixmap

        return texture;
    }

    /**
     * Draw a glowing border around the selected upgrade card.
     */
    private Texture createGlowingBorderTexture() {
        int width = (int) OPTION_CARD_WIDTH;
        int height = (int) OPTION_CARD_HEIGHT;
        Texture texture = new Texture(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Draw the glowing border
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < 10 || x >= width - 10 || y < 10 || y >= height - 10) {
                    // Create a gradient effect for the border
                    float alpha = Math.max(
                        Math.max(10 - x, x - (width - 10)), 
                        Math.max(10 - y, y - (height - 10))
                    ) / 10f; // Normalize to 0..1
                    pixmap.setColor(1, 1, 0, alpha); // Yellow with transparency
                    pixmap.drawPixel(x, y);
                }
            }
        }

        // Draw the pixmap to the texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose(); // Clean up the pixmap

        return texture;
    }

    private void updateTrackers() {
        // Get all actors from the stage
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Table) {
                // Search through all children of the table
                for (Actor child : ((Table)actor).getChildren()) {
                    if (child instanceof Label) {
                        Label label = (Label)child;
                        String text = label.getText().toString();
                        
                        // Update ATP label for your custom Cell
                        if (text.startsWith("ATP:")) {
                            label.setText("ATP: " + playerCell.getCellATP());
                        } 
                        // Update Size label for your custom Cell
                        else if (text.startsWith("Size:")) {
                            label.setText("Size: " + ((playerCell.getcellSize() - 100) / 100));
                        }
                    }
                }
            }
        }
    }
}
