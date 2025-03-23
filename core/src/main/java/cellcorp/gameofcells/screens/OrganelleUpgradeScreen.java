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

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.objects.FlagellaUpgrade;
import cellcorp.gameofcells.objects.MitochondriaUpgrade;
import cellcorp.gameofcells.objects.NucleusUpgrade;
import cellcorp.gameofcells.objects.RibosomeUpgrade;
import cellcorp.gameofcells.objects.Upgrade;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * Organelle Upgrade Screen
 * <p>
 * This screen displays the detailed upgrades for the organelles.
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */


public class OrganelleUpgradeScreen implements GameOfCellsScreen {
    private final Stage stage;
    private final List<Upgrade> upgrades;
    private final Cell cell;
    private final Main game;
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final GameOfCellsScreen previousScreen;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Particles particles;

    // Constants for UI layout
    private static final float SHOP_TEXT_SIZE = 0.3f; 
    private static final float UPGRADE_NAME_TEXT_SIZE = 0.2f;
    private static final float UPGRADE_INFO_TEXT_SIZE = 0.15f;
    private final static float INSTRUCTION_TEXT_SIZE = 0.18f;
    
    private final static float UPGRADE_CARD_WIDTH = 250;
    private final static float UPGRADE_CARD_HEIGHT = 350;
    private final static float SELECTED_CARD_SCALE = 1.4f;

    private int selectedUpgradeIndex = 0; // Index of the selected upgrade card
    private Table upgradeTable; // Table containing the upgrade cards
    private List<Table> upgradeCards; // List of upgrade cards

    /**
     * Constructs the OrganelleUpgradeScreen.
     *
     * @param game           The main game instance.
     * @param inputProvider  Handles user input.
     * @param graphicsProvider Provides graphics utilities.
     * @param assetManager   Manages game assets.
     * @param previousScreen The previous screen (ShopScreen).
     * @param cell           The cell object.
     */
    public OrganelleUpgradeScreen(
            Main game,
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            AssetManager assetManager,
            GameOfCellsScreen previousScreen,
            Cell cell
    ) {
        this.game = game;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;
        this.previousScreen = previousScreen;
        this.cell = cell;

        this.viewport = graphicsProvider.createFitViewport(ShopScreen.VIEW_RECT_WIDTH, ShopScreen.VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
        this.shapeRenderer = graphicsProvider.createShapeRenderer();

        this.particles = new Particles(assetManager.get(AssetFileNames.WHITE_PIXEL, Texture.class));
        this.stage = new Stage(graphicsProvider.createFitViewport(ShopScreen.VIEW_RECT_WIDTH, ShopScreen.VIEW_RECT_HEIGHT), graphicsProvider.createSpriteBatch());

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
     * Render the screen.
     * @param delta The time passed since the last call to `render`, in seconds.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Clear the screen with a dark color

        particles.update(delta, ShopScreen.VIEW_RECT_WIDTH, ShopScreen.VIEW_RECT_HEIGHT);
        particles.draw(batch);

        handleInput(delta);
        update(delta);

        stage.act(delta);
        stage.draw();
    }

    /**
     * Handle user input.
     * @param deltaTimeSeconds The time passed since the last call to `handleInput`, in seconds.
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        if(inputProvider.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedUpgradeIndex > 0) {
                selectedUpgradeIndex--;
                updateUpgradeSelection();
            }
        } else if(inputProvider.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectedUpgradeIndex < upgrades.size() - 1) {
                selectedUpgradeIndex++;
                updateUpgradeSelection();
            }
        } else if(inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            Upgrade selectedUpgrade = upgrades.get(selectedUpgradeIndex);
            if (selectedUpgrade.canPurchase(cell, this)) {
                selectedUpgrade.applyUpgrade(cell);
                upgrades.remove(selectedUpgrade); // Remove the upgrade from the list
                selectedUpgradeIndex = Math.min(selectedUpgradeIndex, upgrades.size() - 1); // Clamp the index
                
                // Update the display
                updateUpgradeDisplay();
            } else {
                // Display a message indicating that the upgrade cannot be purchased
                String message = "";
                boolean notEnoughATP = cell.getCellATP() < selectedUpgrade.getRequiredATP();
                boolean notEnoughSize = (cell.getcellSize() - 100) / 100 < selectedUpgrade.getRequiredSize();

                if (notEnoughATP && notEnoughSize) {
                    message = "Not enough ATP and size to purchase this upgrade.";
                } else if (notEnoughATP) {
                    message = "Not enough ATP to purchase this upgrade.";
                } else if (notEnoughSize) {
                    message = "Not enough size to purchase this upgrade.";
                }

                showMessage(message);
            }
        } else if(inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previousScreen);

        }
    }

    /**
     * Update the screen.
     * @param deltaTimeSeconds The time passed since the last call to `update`, in seconds.
     */
    @Override
    public void update(float deltaTimeSeconds) {
        stage.act(deltaTimeSeconds);
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(2f));
    }

    /**
     * Resize the screen.
     * @param width The new width of the screen.
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
    public void pause() {}

    /**
     * Resume the screen.
     */
    @Override
    public void resume() {}

    /**
     * Hide the screen.
     */
    @Override
    public void hide() {}

    /**
     * Dispose of the screen.
     */
    @Override
    public void draw() {}

    /**
     * Dispose of the screen.
     */
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        shapeRenderer.dispose();
        particles.dispose();
    }

    /**
     * Create the UI for the organelle upgrade screen.
     */
    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label titleLabel = new Label("Organelle Upgrades",
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, 
                BitmapFont.class), Color.WHITE));
        titleLabel.setFontScale(SHOP_TEXT_SIZE);
        mainTable.add(titleLabel).padTop(20).row();

        // ATP Tracker
        Label atpLabel = new Label("ATP: " + cell.getCellATP(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, 
                BitmapFont.class), Color.WHITE));
        atpLabel.setFontScale(SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(atpLabel).padTop(10).row();

        // Size Tracker
        Label sizeLabel = new Label("Size: " + (cell.getcellSize() - 100) / 100,
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT,
            BitmapFont.class), Color.WHITE));
        sizeLabel.setFontScale(SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(sizeLabel).padTop(10).row();

        // Upgrade Table
        upgradeTable = new Table();
        upgradeCards = new ArrayList<>();
        
        // Create and add upgrade cards to the table
        for (Upgrade upgrade : upgrades) {
            Table upgradeCard = createUpgradeCard(upgrade);
            upgradeCards.add(upgradeCard);
            upgradeTable.add(upgradeCard).pad(10);
        }

        // Add the upgrade table to the main table
        mainTable.add(upgradeTable).expand().fill().padTop(10).row();

        // Exit instructions
        Label exitLabel = new Label("Press ESC to go back | Arrow keys to navigate | Enter to purchase",
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        exitLabel.setFontScale(INSTRUCTION_TEXT_SIZE);
        mainTable.add(exitLabel).padBottom(20).row();

        stage.addActor(mainTable);

        // Display the first upgrade card
        updateUpgradeSelection();
    }

    /**
     * Update the display of the upgrade cards.
     */
    private void updateUpgradeDisplay() {
        upgradeTable.clear();
        upgradeCards.clear();

        // Recreate and add upgrade cards to the table
        for (Upgrade upgrade : upgrades) {
            Table upgradeCard = createUpgradeCard(upgrade);
            upgradeCards.add(upgradeCard);
            upgradeTable.add(upgradeCard).pad(10);
        }

        // Highlight the selected upgrade card
        updateUpgradeSelection();
    }

    /**
     * Update the selected upgrade card.
     */
    private void updateUpgradeSelection() {
        for (int i = 0; i < upgradeCards.size(); i++) {
            Table upgradeCard = upgradeCards.get(i);
            Image glowingBorder = (Image) upgradeCard.findActor("glowingBorder");

            if (glowingBorder != null) {
                if (i == selectedUpgradeIndex) {
                    upgradeCard.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.5f, Interpolation.swingOut));
                    glowingBorder.setVisible(true);
                } else {
                    upgradeCard.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.swingOut));
                    glowingBorder.setVisible(false);
                }
            }
        }
    }

    /**
     * Create a table for an upgrade card.
     */
    private Table createUpgradeCard(Upgrade upgrade) {
        Table card = new Table();
        card.defaults().center().pad(5); // Center the actors in the table

        // Set the size of the card
        card.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);

        // Glowing border
        Image glowingBorder = new Image(createGlowingBorderTexture());
        glowingBorder.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);
        glowingBorder.setVisible(false);
        glowingBorder.setName("glowingBorder");
        card.addActor(glowingBorder);

        // Background
        card.setBackground(new TextureRegionDrawable(createOptionBackgroundTexture()));

        // Upgrade name
        Label nameLabel = new Label(upgrade.getName(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        nameLabel.setFontScale(UPGRADE_NAME_TEXT_SIZE);
        nameLabel.setAlignment(Align.center);
        card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

        // Upgrade description
        Label descriptionLabel = new Label(upgrade.getDescription(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), getDescriptionColor(upgrade)));
        descriptionLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center);
        card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

        // Upgrade perks
        Label perksLabel = new Label(upgrade.getPerks(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        perksLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        perksLabel.setWrap(true);
        perksLabel.setAlignment(Align.center);
        card.add(perksLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

        // Upgrade icon
        Image upgradeIcon = getUpgradeIcon(upgrade);
        if (upgradeIcon != null) {
            card.add(upgradeIcon).width(upgradeIcon.getWidth()).height(upgradeIcon.getHeight()).padTop(20).padBottom(20).row();
        }

        // Required ATP and Size (Bottom left of the card)
        Table requirementsTable = new Table();
        requirementsTable.defaults().left().pad(5);

        Label atpLabel = new Label("ATP: " + upgrade.getRequiredATP(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        atpLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);

        Label sizeLabel = new Label("Size: " + upgrade.getRequiredSize(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        sizeLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);

        requirementsTable.add(atpLabel).row();
        requirementsTable.add(sizeLabel).row();

        card.add(requirementsTable).expand().bottom().left().padBottom(10).padLeft(10);
        card.row();

        // Lock overlay (if the upgrade is locked)
        if (!upgrade.canPurchase(cell, this)) {
            card.getColor().a = 0.4f; // Semi-transparent

            // Add a semi-transparent overlay to indicate that the upgrade is locked
            Image lockIcon = new Image(assetManager.get(AssetFileNames.LOCK_ICON, Texture.class));
            lockIcon.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);
            lockIcon.getColor().a = 0.6f; // Set lock icon opacity to 80%
            lockIcon.setPosition(0, 0);
            card.addActor(lockIcon);
        }

        return card;
    }

    /**
     * Draw a glowing border around the selected upgrade card.
     */
    private Texture createGlowingBorderTexture() {
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
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

    /**
     * Create a custom background texture for the option cards.
     * @return
     */
    private Texture createOptionBackgroundTexture() {
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
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
     * Returns a colorblind-friendly color for the upgrade description.
     * @return The colorblind-friendly color.
     */
    private Color getDescriptionColor(Upgrade upgrade) {
        if (upgrade instanceof MitochondriaUpgrade) {
            return new Color(0.0f, 0.45f, 0.7f, 1.0f); // Blue
        } else if (upgrade instanceof RibosomeUpgrade) {
            return new Color(1.0f, 0.8f, 0.0f, 1.0f); // Yellow
        } else if (upgrade instanceof FlagellaUpgrade) {
            return new Color(0.6f, 0.2f, 0.8f, 1.0f); // Purple
        } else if (upgrade instanceof NucleusUpgrade) {
            return new Color(1.0f, 0.5f, 0.0f, 1.0f); // Orange
        } else {
            return Color.WHITE; // Default color
        }
    }

    /**
     * Returns the icon for the upgrade.
     */
    private Image getUpgradeIcon(Upgrade upgrade) {
        Image icon = null;
        float iconWidth = 100;
        float iconHeight = 100;

        if (upgrade instanceof MitochondriaUpgrade) {
            icon = new Image(assetManager.get(AssetFileNames.MITOCHONDRIA_ICON, Texture.class));
        } else if (upgrade instanceof RibosomeUpgrade) {
            icon = new Image(assetManager.get(AssetFileNames.RIBOSOME_ICON, Texture.class));
        } else if (upgrade instanceof FlagellaUpgrade) {
            icon = new Image(assetManager.get(AssetFileNames.FLAGELLA_ICON, Texture.class));
        } else if (upgrade instanceof NucleusUpgrade) {
            icon = new Image(assetManager.get(AssetFileNames.NUCLEUS_ICON, Texture.class));
        } 

        if (icon != null) {
            icon.setSize(iconWidth, iconHeight);

            icon.setAlign(Align.center);

            // Alternatively you can use scaling if the icon's aspect ratio need to be preserved
            // float scale = 0.5f;
            // icon.setScale(scale);
        }

        return icon;
    }

    /**
     * Show a message on the screen.
     * @param message The message to display.
     */
    private void showMessage(String message) {
        // Create a label with the message
        Label messageLabel = new Label(message,
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        messageLabel.setFontScale(0.3f);
        messageLabel.setAlignment(Align.center);

        // Position the message label at the bottom of the screen
        messageLabel.setPosition(ShopScreen.VIEW_RECT_WIDTH / 2 - messageLabel.getWidth() / 2, 50); // Centered horizontally

        // Add the message label to the stage
        stage.addActor(messageLabel);

        // Fade out the message label after 2 seconds
        messageLabel.addAction(Actions.sequence(Actions.fadeOut(2.5f), Actions.removeActor()));
    }

    /**
     * Check if the cell has mitochondria.
     * @return True if the cell has mitochondria, false otherwise.
     */
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