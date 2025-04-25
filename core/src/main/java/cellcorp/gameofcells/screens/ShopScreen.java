package cellcorp.gameofcells.screens;

import java.util.ArrayList;
import java.util.List;

import cellcorp.gameofcells.objects.organelle.*;
import cellcorp.gameofcells.objects.size.*;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.math.Interpolation;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * Shop Screen
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 04/09/2025
 * @course CIS 405
 * @assignment Game of Cells
 * @description This is the shop screen for the game. This class handles displaying
 *              the shop UI, including the size and organelle upgrades available for
 *              purchase. It also handles the logic for purchasing upgrades and
 *              updating the player's cell accordingly.
 */
public class ShopScreen implements GameOfCellsScreen {
    // Constants for UI layout
    private static final float UPGRADE_CARD_WIDTH = 350;
    private static final float UPGRADE_CARD_HEIGHT = 250;
    private final static float SELECTED_CARD_SCALE = 1.1F;
    private static final float UPGRADE_NAME_TEXT_SIZE = 0.25f;
    private static final float UPGRADE_INFO_TEXT_SIZE = 0.2f;

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

    // Instance variables
    protected final Stage stage;
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

    // UI elements
    private List<SizeUpgrade> sizeUpgrades;
    private List<OrganelleUpgrade> organelleUpgrades;
    private Table sizeTable;
    private Table organelleTable;
    private Table currentSizeCard;
    private Table currentOrganelleCard;

    /**
     * Constructor for the ShopScreen class.
     *
     * @param game The main game instance
     * @param inputProvider The input provider for handling user input
     * @param graphicsProvider The graphics provider for rendering
     * @param assetManager The asset manager for loading assets
     * @param previousScreen The previous screen (GamePlayScreen) to return to
     * @param cell The player's cell used to determine available upgrades
     */
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
        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());

        this.stage = new Stage(viewport, batch);

        this.menuSystem = new MenuSystem(stage, assetManager, graphicsProvider);

        initializeUpgrades();

         // Create UI
        createUI();
    }

    // Here we initialize the size and organelle upgrades available for the player cell
    // based on the current state of the cell.
    // This method checks if the player cell has already purchased certain upgrades
    // and only adds the available ones to the respective lists.
    private void initializeUpgrades() {
        // Initialize size upgrades
        sizeUpgrades = new ArrayList<>();
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
        organelleUpgrades = new ArrayList<>();
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

    // Here we create the UI for the shop screen.
    // This method sets up the layout of the shop, including the size and organelle
    // upgrade columns, the ATP and size information, and the upgrade cards.
    // It also handles the display of upgrade information and the selection of
    // upgrades for purchase.
    // The UI is created using Scene2D's Table layout system for better organization
    // and alignment of UI elements.
    private void createUI() {
        Table[] shopTables = menuSystem.initializeShopLayout(
            "CELL SHOP",
            "Press ARROW keys to switch sides | ENTER to purchase | ESC to exit"
        );

        Table leftTable = shopTables[0];    // Size upgrades column
        Table centerTable = shopTables[1];  // ATP/Size info column
        Table rightTable = shopTables[2];   // Organelle upgrades column

        // Add ATP/Size info to center column
        Label atpLabel = createLabel("ATP: " + playerCell.getCellATP(), UPGRADE_NAME_TEXT_SIZE);
        Label sizeLabel = createLabel("Size: " + ((playerCell.getCellSize() - 100)/100), UPGRADE_NAME_TEXT_SIZE);

        centerTable.add(atpLabel).row();
        centerTable.add(sizeLabel).padTop(10).row();

        // Create and add size upgrades
        sizeTable = new Table();
        if (!sizeUpgrades.isEmpty()) {
            currentSizeCard = createUpgradeCard(sizeUpgrades.get(0), true);
            sizeTable.add(currentSizeCard).width(UPGRADE_CARD_WIDTH).height(UPGRADE_CARD_HEIGHT);
        } else {
            sizeTable.add(createLabel("All size upgrades purchased!", UPGRADE_INFO_TEXT_SIZE));
        }
        leftTable.add(sizeTable).expand().fill().padTop(20);

        // Create and add organelle upgrades
        organelleTable = new Table();
        if (!organelleUpgrades.isEmpty()) {
            currentOrganelleCard = createUpgradeCard(organelleUpgrades.get(0), false);
            organelleTable.add(currentOrganelleCard).width(UPGRADE_CARD_WIDTH).height(UPGRADE_CARD_HEIGHT);
        } else {
            organelleTable.add(createLabel("All organelle upgrades purchased!", UPGRADE_INFO_TEXT_SIZE));
        }
        rightTable.add(organelleTable).expand().fill().padTop(20);

        // Set initial selection
        updateSelection(true);
    }

    // This method is called to create the upgrade card UI elements.
    // It takes an upgrade object and a boolean indicating if it's a size upgrade.
    // The method creates a table for the upgrade card, sets its background,
    // and adds the upgrade information (name, description, requirements) to it.
    // The upgrade card is styled with a glowing border and a background texture.
    private Table createUpgradeCard(Object upgrade, boolean isSizeUpgrade) {
        Table card = new Table();
        card.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);

        Pixmap debugPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        debugPixmap.setColor(Color.RED);
        debugPixmap.fill();
        card.setBackground(new TextureRegionDrawable(new Texture(debugPixmap)));
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
            nameLabel.setAlignment(Align.center);
            card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

            // Description
            Label descriptionLabel = createLabel(sizeUpgrade.getDescription(), UPGRADE_INFO_TEXT_SIZE);
            descriptionLabel.setWrap(true);
            descriptionLabel.setAlignment(Align.center);
            card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(5).row();

            // Requirements
            Label atpLabel = createLabel("ATP:" + sizeUpgrade.getRequiredATP(), UPGRADE_INFO_TEXT_SIZE);
            Label sizeLabel = createLabel("Req Size:" + sizeUpgrade.getRequiredSize(), UPGRADE_INFO_TEXT_SIZE);

            Table reqTable = new Table();
            reqTable.add(atpLabel).row();
            reqTable.add(sizeLabel);
            card.add(reqTable).padTop(10).row();
        } else {
            OrganelleUpgrade organelleUpgrade = (OrganelleUpgrade) upgrade;

            // Name
            Label nameLabel = createLabel(organelleUpgrade.getName(), UPGRADE_NAME_TEXT_SIZE);
            nameLabel.setColor(getDescriptionColor(organelleUpgrade));
            nameLabel.setAlignment(Align.center);
            card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

            // Description
            Label descriptionLabel = createLabel(organelleUpgrade.getDescription(), UPGRADE_INFO_TEXT_SIZE);
            descriptionLabel.setWrap(true);
            descriptionLabel.setAlignment(Align.center);
            card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(5).row();

            // Requirements
            Label atpLabel = createLabel("ATP:" + organelleUpgrade.getRequiredATP(), UPGRADE_INFO_TEXT_SIZE);
            Label sizeLabel = createLabel("Req Size:" + organelleUpgrade.getRequiredSize(), UPGRADE_INFO_TEXT_SIZE);

            Table reqTable = new Table();
            reqTable.add(atpLabel).row();
            reqTable.add(sizeLabel);
            card.add(reqTable).padTop(10).row();
        }

        return card;
    }

    // This method returns a color based on the type of organelle upgrade.
    // It uses different colors for different organelle types to visually distinguish them.
    // The colors can be changed if needed.
    private Color getDescriptionColor(OrganelleUpgrade upgrade) {
        if (upgrade instanceof MitochondriaUpgrade) {
            return new Color(0.0f, 0.45f, 0.7f, 1.0f); // Blue
        }
        if (upgrade instanceof RibosomeUpgrade) {
            return new Color(1.0f, 0.8f, 0.0f, 1.0f); // Yellow
        }
        if (upgrade instanceof FlagellaUpgrade) {
            return new Color(0.6f, 0.2f, 0.8f, 1.0f); // Purple
        }
        if (upgrade instanceof NucleusUpgrade) {
            return new Color(1.0f, 0.5f, 0.0f, 1.0f); // Orange
        }
        return Color.WHITE; // Default color
    }

    // This method handles user input for the shop screen.
    // It checks for key presses to navigate between size and organelle upgrades,
    // and to purchase the selected upgrade.
    private void updateSelection(boolean isSizeSelected) {
        if (isSizeSelected && currentSizeCard != null) {
            Image border = (Image) currentSizeCard.findActor("glowingBorder");
            if (border != null) {
                currentSizeCard.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.2f, Interpolation.swingOut));
                border.setVisible(true);
            }

            if (currentOrganelleCard != null) {
                Image borderOrganelle = (Image) currentOrganelleCard.findActor("glowingBorder");
                if (borderOrganelle != null) {
                    currentOrganelleCard.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.swingOut));
                    borderOrganelle.setVisible(false);
                }
            }
        } else if (currentOrganelleCard != null) {
            Image border = (Image) currentOrganelleCard.findActor("glowingBorder");
            if (border != null) {
                currentOrganelleCard.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.2f, Interpolation.swingOut));
                border.setVisible(true);
            }

            if (currentSizeCard != null) {
                Image borderSize = (Image) currentSizeCard.findActor("glowingBorder");
                if (borderSize != null) {
                    currentSizeCard.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.swingOut));
                    borderSize.setVisible(false);
                }
            }
        }
    }

    // Here this method checks if the user tries to purchase an upgrade.
    // It checks if the ENTER key is pressed and calls the attemptPurchase method
    // to handle the purchase logic.
    private void attemptPurchase(boolean isSizeUpgrade) {
        if (isSizeUpgrade && !sizeUpgrades.isEmpty()) {
            SizeUpgrade upgrade = sizeUpgrades.get(0);
            if (upgrade.canPurchase(playerCell)) {
                upgrade.applyUpgrade(playerCell);
                sizeUpgrades.remove(0);
                updateSizeDisplay();
                showMessage("Size upgrade purchased!");
                resumeGame();
            } else {
                showPurchaseError(upgrade);
            }
        } else if (!organelleUpgrades.isEmpty()) {
            OrganelleUpgrade upgrade = organelleUpgrades.get(0);
            if (upgrade.canPurchase(playerCell)) {
                upgrade.applyUpgrade(playerCell);
                organelleUpgrades.remove(0);
                updateOrganelleDisplay();
                showMessage("Organelle upgrade purchased!");
                resumeGame();
            } else {
                showPurchaseError(upgrade);
            }
        }
    }

    // This method is used to resume the game after an upgrade is pruchased.
    // An implementation that I think would be fluid and reduce excess key interaction
    // to return back to the game after every purchase.
    private void resumeGame() {
        // Clear any existing actions first
        stage.getRoot().clearActions();

        // Create fade out action
        AlphaAction fadeOutAction = Actions.fadeOut(0.5f);

        RunnableAction returnAction = Actions.run(() -> {
            if (previousScreen != null) {
                previousScreen.resumeGame();
                game.setScreen(previousScreen);
            }
        });

        // Safely sequence the actions
        stage.getRoot().addAction(Actions.sequence(
                fadeOutAction,
                returnAction
        ));
    }

    // This method handles the purchase error messages.
    // It checks if the player has enough ATP and size to purchase the selected upgrade.
    // If not, it shows a message indicating the specific issue (not enough ATP, size, or both).
    private void showPurchaseError(Object upgrade) {
        String message = "";
        if (upgrade instanceof SizeUpgrade) {
            SizeUpgrade sizeUpgrade = (SizeUpgrade) upgrade;
            boolean notEnoughATP = playerCell.getCellATP() < sizeUpgrade.getRequiredATP();
            boolean notEnoughSize = (playerCell.getCellSize() - 100) / 100 < sizeUpgrade.getRequiredSize();
            if (notEnoughATP && notEnoughSize) {
                message = "Not enough ATP and size!";
            } else if (notEnoughATP) {
                message = "Not enough ATP!";
            } else if (notEnoughSize) {
                message = "Not enough size!";
            }
        } else if (upgrade instanceof OrganelleUpgrade) {
            OrganelleUpgrade organelleUpgrade = (OrganelleUpgrade) upgrade;
            boolean notEnoughATP = playerCell.getCellATP() < organelleUpgrade.getRequiredATP();
            boolean notEnoughSize = (playerCell.getCellSize() - 100) / 100 < organelleUpgrade.getRequiredSize();
            if (notEnoughATP && notEnoughSize) {
                message = "Not enough ATP and size!";
            } else if (notEnoughATP) {
                message = "Not enough ATP!";
            } else if (notEnoughSize) {
                message = "Not enough size!";
            }
        }
        showMessage(message);
    }

    // This method is used to update the display of the size upgrades.
    // It clears the current size table and adds the first available size upgrade card.
    // If all size upgrades are purchased, it shows a message indicating that.
    private void updateSizeDisplay() {
        sizeTable.clear();
        if (!sizeUpgrades.isEmpty()) {
            currentSizeCard = createUpgradeCard(sizeUpgrades.get(0), true);
            sizeTable.add(currentSizeCard).pad(10);
            updateSelection(true);
        } else {
            sizeTable.add(createLabel("All size upgrades purchased!", UPGRADE_INFO_TEXT_SIZE));
        }
    }

    // This method is used to update the display of the organelle upgrades.
    // It clears the current organelle table and adds the first available organelle upgrade card.
    // If all organelle upgrades are purchased, it shows a message indicating that.
    private void updateOrganelleDisplay() {
        organelleTable.clear();
        if (!organelleUpgrades.isEmpty()) {
            currentOrganelleCard = createUpgradeCard(organelleUpgrades.get(0), false);
            organelleTable.add(currentOrganelleCard).pad(10);
            updateSelection(false);
        } else {
            organelleTable.add(createLabel("All organelle upgrades purchased!", UPGRADE_INFO_TEXT_SIZE));
        }
    }

    // This method is used to show a message on the screen.
    // It creates a label with the specified message and fades it out after a delay.
    private void showMessage(String message) {
        Label messageLabel = createLabel(message, 0.3f);

        float x = (viewport.getWorldWidth() - messageLabel.getWidth()) / 2 - 40f;
        float y = 50f;

        messageLabel.setPosition(x, y);

        stage.addActor(messageLabel);
        messageLabel.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.fadeOut(0.5f),
                Actions.removeActor()
        ));
    }

    /**
     * Create a label with the specified text and scale.
     * @param text
     * @param scale
     * @return
     */
    protected Label createLabel(String text, float scale) {
        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = graphicsProvider.createLabel(text, labelStyle);
        label.setFontScale(scale);
        return label;
    }

    /**
     * Create a custom background texture for the option cards.
     * @return The background texture.
     */
    private Texture createOptionBackgroundTexture() {
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
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
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
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
     * Render the screen.
     *
     * @param delta The time since the last frame in seconds.
     */
    @Override
    public void render(float delta) {
        // Clear the screen with a semi-transparent black color
        ScreenUtils.clear(Main.PURPLE); // Dark purple background

        // Update and draw the particles
        particles.update(delta, VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        batch.begin();
        particles.draw(batch);
        batch.end();

        // Handle the input first
        handleInput(delta);
        update(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyJustPressed(Input.Keys.LEFT)) {
            updateSelection(true);
        } else if (inputProvider.isKeyJustPressed(Input.Keys.RIGHT)) {
            updateSelection(false);
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
            || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            boolean isSizeSelected = currentSizeCard != null &&
                ((Image) currentSizeCard.findActor("glowingBorder")).isVisible();
            attemptPurchase(isSizeSelected);
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)
            || inputProvider.isKeyJustPressed(Input.Keys.Q)) {
            exitShop();
        }
    }

    // This method is used to exit the shop screen.
    // It fades out the shop screen and returns to the previous screen (GamePlayScreen).
    // It also resumes the game state and updates the player's cell.
    private void exitShop() {
        stage.getRoot().addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run(() -> {
                if (previousScreen != null) {
                    previousScreen.resumeGame();
                    game.setScreen(previousScreen);
                }
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
        if (previousScreen != null) {
            previousScreen.pauseGame();
        }
        updateSelection(true); // Default to selecte size upgrades
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
        return previousScreen;
    }

    /**
     * Get the player cell size tracker.
     * This is used to get the size of the player cell.
     * @return
     */
    public int getSizeTracker() {
        return playerCell.getCellSize();
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

    public boolean isTransitioning() {
        // Check if the stage is currently transitioning (fading out)
        return stage.getRoot().getActions().size > 0 && stage.getRoot().getActions().peek() instanceof AlphaAction;
    }

    /**
     * Set the transitioning state of the stage.
     * This is used to set the transitioning state of the stage.
     * 
     * @param transitioning
     */
    public void setTransitioning(boolean transitioning) {
        // Set the transitioning state of the stage
        if (transitioning) {
            stage.getRoot().addAction(Actions.fadeOut(1f));
        } else {
            stage.getRoot().clearActions();
            stage.getRoot().setColor(1, 1, 1, 1); // Reset to fully opaque
        }
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

    /**
     * Get the current size card.
     * This is used to get the current size card for the size upgrades.
     * 
     * @return the current size card
     */
    public Table getCurrentSizeCard() {
        return currentSizeCard;
    }

    /**
     * Get the current organelle card.
     * This is used to get the current organelle card for the organelle upgrades.
     * 
     * @return the current organelle card
     */
    public Table getCurrentOrganelleCard() {
        return currentOrganelleCard;
    }
    
}
