package cellcorp.gameofcells.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.objects.organelle.*;
import cellcorp.gameofcells.objects.size.*;
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
    private static final float UPGRADE_CARD_WIDTH = 350;
    private static final float UPGRADE_CARD_HEIGHT = 250;
    private final static float SELECTED_CARD_SCALE = 1.1F;
    private final static float TITLE_TEXT_SIZE = 0.4f;
    private static final float UPGRADE_NAME_TEXT_SIZE = 0.25f;
    private static final float UPGRADE_INFO_TEXT_SIZE = 0.2f;
    private static final float INSTRUCTION_TEXT_SIZE = 0.18f;
    
    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
    // Change as is most convenient.
    /**
     * Width of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1280;
    /**
     * Height of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 800;

    private final Stage stage;
    private final Cell playerCell;
    private final Main game;
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Particles particles;
    private final MenuSystem menuSystem;
    private final GamePlayScreen previousScreen;
    
    private List<SizeUpgrade> sizeUpgrades;
    private List<OrganelleUpgrade> organelleUpgrades;
    private int selectedSizeIndex = 0;
    private int selectedOrganelleIndex = 0;
    private Table sizeTable;
    private Table organelleTable;
    private Table currentSizeCard;
    private Table currentOrganelleCard;

    public ShopScreen(
            Main game,
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            AssetManager assetManager,
            GamePlayScreen previousScreen,
            Cell cell
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
        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());

        this.stage = new Stage(viewport, batch);

        this.menuSystem = new MenuSystem(stage, assetManager, graphicsProvider);

        initializeUpgrades();

         // Create UI
        createUI();
    }

    private void initializeUpgrades() {
        // Initialize size upgrades
        this.sizeUpgrades = new ArrayList<>();
        if (!playerCell.hasSmallSizeUpgrade()) {
            sizeUpgrades.add(new SmallSizeUpgrade());
        }
        if (!playerCell.hasMediumSizeUpgrade()) {
            sizeUpgrades.add(new MediumSizeUpgrade());
        }
        if (!playerCell.hasLargeSizeUpgrade()) {
            sizeUpgrades.add(new LargeSizeUpgrade());
        }
        if (!playerCell.hasMassiveSizeUpgrade()) {
            sizeUpgrades.add(new MassiveSizeUpgrade());
        }

        // Initialize organelle upgrades
        this.organelleUpgrades = new ArrayList<>();
        if (!playerCell.hasMitochondria()) {
            organelleUpgrades.add(new MitochondriaUpgrade());
        }
        if (!playerCell.hasRibosomes()) {
            organelleUpgrades.add(new RibosomeUpgrade());
        }
        if (!playerCell.hasFlagella()) {
            organelleUpgrades.add(new FlagellaUpgrade());
        }
        if (!playerCell.hasNucleus()) {
            organelleUpgrades.add(new NucleusUpgrade());
        }
    }

    private void createUI() {
        String[] leftHeader = {"SIZE UPGRADES", "----------------------"};
        String[] rightHeader = {"ORGANELLE UPGRADES", "----------------------"};

        menuSystem.initializeSplitLayout("CELL SHOP", 
                                        leftHeader, 
                                        rightHeader, 
                                        "Press ARROW keys to switch sides | ENTER to purchase | ESC to exit");

        Table rootTable = menuSystem.getStage(). getRoot().findActor("mainTable");
        if (rootTable == null) return;

        // Get the left and right tables from the split layout
        Table leftTable = (Table) rootTable.getCells().get(2).getActor();
        Table rightTable = (Table) rootTable.getCells().get(3).getActor();

        // Clear existing content but keep headers
        while (leftTable.getChildren().size > 2) {
            leftTable.getChildren().removeIndex(2);
        }
        while (rightTable.getChildren().size > 2) {
            rightTable.getChildren().removeIndex(2);
        }

        // ATP and Size tracker at the top
        Label atpLabel = createLabel("ATP: " + playerCell.getCellATP(), UPGRADE_NAME_TEXT_SIZE);
        Label sizeLabel = createLabel("Current Size: " + ((playerCell.getcellSize() - 100) / 100), UPGRADE_NAME_TEXT_SIZE);

        Table centerTable = new Table();
        centerTable.add(atpLabel).row();
        centerTable.add(sizeLabel).padTop(10).row();

        rootTable.getCells().get(2).setActor(centerTable);
        rootTable.getCells().get(3).setActor(centerTable);

        // Create size upgrade cards
        sizeTable = new Table();
        if (!sizeUpgrades.isEmpty()) {
            currentSizeCard = createUpgradeCard(sizeUpgrades.get(0), true);
            sizeTable.add(currentSizeCard).pad(10);
        } else {
            sizeTable.add(createLabel("All size upgrades purchased!", UPGRADE_INFO_TEXT_SIZE));
        }
        leftTable.add(sizeTable).padTop(20).row();

        // Create organelle upgrade cards
        organelleTable = new Table();
        if (!organelleUpgrades.isEmpty()) {
            currentOrganelleCard = createUpgradeCard(organelleUpgrades.get(0), false);
            organelleTable.add(currentOrganelleCard).pad(10);
        } else {
            organelleTable.add(createLabel("All organelle upgrades purchased!", UPGRADE_INFO_TEXT_SIZE));
        }
        rightTable.add(organelleTable).padTop(20).row();
    }

    private Table createUpgradeCard(Object upgrade, boolean isSizeUpgrade) {
        Table card = new Table();
        card.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);

        // Glowing border
        Image glowingBorder = new Image(createGlowingBorderTexture());
        glowingBorder.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);
        glowingBorder.setVisible(false); // Hide the border by default
        glowingBorder.setName("glowingBorder");
        card.addActor(glowingBorder); // Add the border as an actor (not part of the table layout)

        // Background
        card.setBackground(new TextureRegionDrawable(createOptionBackgroundTexture()));

        if (isSizeUpgrade) {
            SizeUpgrade sizeUpgrade = (SizeUpgrade) upgrade;

            // Name 
            Label nameLabel = createLabel(sizeUpgrade.getName(), UPGRADE_NAME_TEXT_SIZE);
            nameLabel.setColor(Color.YELLOW);
            card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

            // Description
            Label descriptionLabel = createLabel(sizeUpgrade.getDescription(), UPGRADE_INFO_TEXT_SIZE);
            descriptionLabel.setWrap(true);
            card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(5).row();

            // Requirements
            Label atpLabel = createLabel("ATP:", sizeUpgrade.getRequiredATP(), UPGRADE_INFO_TEXT_SIZE);
            Label sizeLabel = createLabel("Req Size:", sizeUpgrade.getRequiredSize(), UPGRADE_INFO_TEXT_SIZE);

            Table reqTable = new Table();
            reqTable.add(atpLabel).row();
            reqTable.add(sizeLabel);
            card.add(reqTable).padTop(10).row();
        } else {
            OrganelleUpgrade organelleUpgrade = (OrganelleUpgrade) upgrade;

            // Name
            Label nameLabel = createLabel(organelleUpgrade.getName(), UPGRADE_NAME_TEXT_SIZE);
            nameLabel.setColor(getDescriptionColor(organelleUpgrade));
            card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

            // Description
            Label descriptionLabel = createLabel(organelleUpgrade.getDescription(), UPGRADE_INFO_TEXT_SIZE);
            descriptionLabel.setWrap(true);
            card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(5).row();

            // Requirements
            Label atpLabel = createLabel("ATP:", organelleUpgrade.getRequiredATP(), UPGRADE_INFO_TEXT_SIZE);
            Label sizeLabel = createLabel("Req Size:", organelleUpgrade.getRequiredSize(), UPGRADE_INFO_TEXT_SIZE);

            Table reqTable = new Table();
            reqTable.add(atpLabel).row();
            reqTable.add(sizeLabel);
            card.add(reqTable).padTop(10).row();
        }

        return card;
    }

    /**
     * Create a label with the specified text and scale.
     * @param text
     * @param scale
     * @return
     */
    protected Label createLabel(String text,float scale) {
        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelStyle);
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
        Texture texture = graphicsProvider.createTexture(width, height, Pixmap.Format.RGBA8888);
        Pixmap pixmap = graphicsProvider.createPixmap(width, height, Pixmap.Format.RGBA8888);

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
    public Texture createGlowingBorderTexture() {
        int width = (int) OPTION_CARD_WIDTH;
        int height = (int) OPTION_CARD_HEIGHT;
        Texture texture = graphicsProvider.createTexture(width, height, Pixmap.Format.RGBA8888);
        Pixmap pixmap = graphicsProvider.createPixmap(width, height, Pixmap.Format.RGBA8888);

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

    /**
     * Update the ATP and Size labels in the shop screen.
     */
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
        update(delta);

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
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
            || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            handleShopSelection();
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitShop();
        }
    }

    private void handleShopSelection() {
        if (selectedOptionIndex == 0) {
            game.setScreen(new SizeUpgradeScreen(
                game, inputProvider, graphicsProvider, assetManager, this, playerCell));
            } else if (selectedOptionIndex == 1) {
            game.setScreen(new OrganelleUpgradeScreen(
                game, inputProvider, graphicsProvider, assetManager, this, playerCell
            ));
        }
    }

    private void exitShop() {
        menuSystem.getStage().getRoot().addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run(() -> {
                if (previousScreen instanceof GamePlayScreen) {
                    ((GamePlayScreen) previousScreen).resumeGame();
                }
                game.setScreen(previousScreen);
            })
        ));
    }

    /**
     * Update the screen.
     *
     * @param deltaTimeSeconds The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTimeSeconds) {
        // Update the ATP and Size labels
        updateTrackers();
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
        // No action needed
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
     * Dispose of the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();  // Dispose of the batch
        stage.dispose(); // Dispose of the stage
        shapeRenderer.dispose(); // Dispose of the shape renderer
        particles.dispose(); // Dispose of the particles
    }

    public GamePlayScreen getPreviousScreen() {
        return this.previousScreen;
    }

    /**
     * Get the list of option cards.
     * This is used to update the option cards in the shop screen.
     * The list contains the size and organelle upgrade cards.
     * @return 
     */
    public List<Table> getOptionCards() {
        return optionCards;
    }

    /**
     * Get the option card at the specified index.
     * This is used to get the selected option in the shop screen.
     * The index corresponds to the size and organelle upgrade options.
     * @param selectedOptionIndex
     * @return
     */
    public Table getOptionCards(int selectedOptionIndex) {
        return optionCards.get(selectedOptionIndex);
    }

    /**
     * Set the list of option cards.
     * This is used to update the option cards in the shop screen.
     * @param optionCards
     */
    public void setOptionCards(List<Table> optionCards) {
        this.optionCards = optionCards;
    }

    /**
     * Get the selected option index.
     * This is used to get the selected option in the shop screen.
     * The index corresponds to the size and organelle upgrade options.
     * 0 = Size upgrade
     * 1 = Organelle upgrade
     * @return 
     */
    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    /**
     * Set the selected option index.
     * This is used to update the selected option in the shop screen.
     * @param selectedOptionIndex
     */
    public void setSelectedOptionIndex(int selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }

    /**
     * Get the player cell size tracker.
     * This is used to get the size of the player cell.
     * @return
     */
    public int getSizeTracker() {
        return playerCell.getcellSize();
    }

    /**
     * Get the ATP tracker.
     * This is used to get the ATP of the player cell.
     * @return 
     */
    public int getATPTracker() {
        return playerCell.getCellATP();
    }

    /**
     * Get the player cell.
     * This is used to get the player cell object.
     * @return playerCell
     */
    public Cell getPlayerCell() {
        return playerCell;
    }

    /**
     * Check if the option card is highlighted.
     * This is used to check if the option card is highlighted.
     * @return true if the option card is highlighted, false otherwise.
     */
    public boolean isHighlighted(Table card) {
        Image glowingBorder = (Image) card.findActor("glowingBorder");
        return glowingBorder != null && glowingBorder.isVisible();
    }

    /**
     * Clear the options in the shop screen.
     * This is used to clear the options in the shop screen.
     * This is useful when transitioning to a new screen or resetting the shop.
     */
    public void clearOptions() {
        this.optionCards.clear();
    }

    public boolean isTransitioning() {
        // Check if the stage is currently transitioning (fading out)
        return stage.getRoot().getActions().size > 0 && stage.getRoot().getActions().peek() instanceof AlphaAction;
    }

    public boolean isPaused() {
        // Check if the game is paused
        return previousScreen instanceof GamePlayScreen && ((GamePlayScreen) previousScreen).isPaused();
    }

    /**
     * Get the particles object.
     * This is used to get the particles object for rendering.
     * @return
     */
    public Particles getParticles() {
        return particles;
    }

    /**
     * Get the option background texture.
     * This is used to get the background texture for the option cards.
     * This is useful for creating a custom background for the option cards.
     * @return
     */
    public Texture getOptionBackgroundTexture() {
        return createOptionBackgroundTexture();
    }

    /**
     * Get the glowing border texture.
     * This is used to get the glowing border texture for the option cards.
     * This is useful for creating a glowing effect around the selected option card.
     * @return
     */
    public Texture getGlowingBorderTexture() {
        return createGlowingBorderTexture();
    }
}
