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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

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
    
    private final static float UPGRADE_CARD_WIDTH = 400;
    private final static float UPGRADE_CARD_HEIGHT = 450;
    private final static float SELECTED_CARD_SCALE = 1.4f;

    private int selectedUpgradeIndex = 0;
    private Table upgradeTable;
    private List<Table> upgradeCards;

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
        ScreenUtils.clear(0, 0, 0, 0.5f); // Semi-transparent black background

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
            // Handle purchase of the selected upgrade
            Upgrade selectedUpgrade = upgrades.get(selectedUpgradeIndex);
            if (selectedUpgrade.canPurchase(cell, this)) {
                selectedUpgrade.applyUpgrade(cell);
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Return to the previous screen (ShopScreen)
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
        Label sizeLabel = new Label("Size: " + cell.getcellSize() / 100,
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT,
            BitmapFont.class), Color.WHITE));
        sizeLabel.setFontScale(SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(sizeLabel).padTop(10).row();

        // Upgrade Table
        upgradeTable = new Table();
        upgradeCards = new ArrayList<>();

        for (Upgrade upgrade : upgrades) {
            Table card = createUpgradeCard(upgrade);
            upgradeCards.add(card);
            upgradeTable.add(card).pad(10);
        }

        mainTable.add(upgradeTable).expand().fill().pad(20).row();

        // Exit instructions
        Label exitLabel = new Label("Press ESC to go back | Arrow keys to navigate | Enter to purchase",
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        exitLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        mainTable.add(exitLabel).padBottom(20).row();

        stage.addActor(mainTable);

        // Center the selected upgrade initially
        centerSelectedUpgrade();
    }

    /**
     * Create a table for an upgrade card.
     */
    private Table createUpgradeCard(Upgrade upgrade) {
        Table card = new Table();
        card.defaults().center().pad(5);
        card.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);

        // Glowing border
        Image glowingBorder = new Image(createGlowingBorderTexture());
        glowingBorder.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);
        glowingBorder.setVisible(false);
        glowingBorder.setName("glowingBorder");
        card.addActor(glowingBorder);

        // Background based on upgrade type
        if (upgrade instanceof MitochondriaUpgrade) {
            card.setBackground(new TextureRegionDrawable(createMitochondriaBackgroundTexture()));
        } else if (upgrade instanceof RibosomeUpgrade) {
            card.setBackground(new TextureRegionDrawable(createRibosomeBackgroundTexture()));
        } else if (upgrade instanceof FlagellaUpgrade) {
            card.setBackground(new TextureRegionDrawable(createFlagellaBackgroundTexture()));
        } else if (upgrade instanceof NucleusUpgrade) {
            card.setBackground(new TextureRegionDrawable(createNucleusBackgroundTexture()));
        }

        // Upgrade name
        Label nameLabel = new Label(upgrade.getName(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        nameLabel.setFontScale(UPGRADE_NAME_TEXT_SIZE);
        nameLabel.setAlignment(Align.center);
        card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

        // Upgrade cost
        Label costLabel = new Label("Cost: " + upgrade.getCost() + " ATP",
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        costLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        costLabel.setAlignment(Align.center);
        card.add(costLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

        // Upgrade description
        Label descriptionLabel = new Label(upgrade.getDescription(),
                new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        descriptionLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center);
        card.add(descriptionLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

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

    /**
     * Center the selected upgrade card in the upgrade table.
     */
    private void centerSelectedUpgrade() {
        float screenCenterX = ShopScreen.VIEW_RECT_WIDTH / 2;
        float selectedCardCenterX = selectedUpgradeIndex * UPGRADE_CARD_WIDTH + (UPGRADE_CARD_WIDTH / 2);
        float tableOffsetX = screenCenterX - selectedCardCenterX;

        upgradeTable.addAction(Actions.moveTo(tableOffsetX, upgradeTable.getY(), 0.5f, Interpolation.smooth));

        for (int i = 0; i < upgradeCards.size(); i++) {
            Table card = upgradeCards.get(i);
            Image glowingBorder = (Image) card.findActor("glowingBorder");

            if (glowingBorder != null) {
                if (i == selectedUpgradeIndex) {
                    card.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.5f, Interpolation.smooth));
                    glowingBorder.setVisible(true);
                } else {
                    card.addAction(Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.smooth));
                    glowingBorder.setVisible(false);
                }
            }
        }
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
     * Draw the background for the Mitochondria upgrade.
     */
    private Texture createMitochondriaBackgroundTexture() {
        // Create a texture for the Mitochondria upgrade background
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
        Texture texture = new Texture(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Draw the Mitochondria background using blue for energy
        pixmap.setColor(0.1f, 0.4f, 0.8f, 0.5f); // Blue energy
        for (int i = 0; i < 50; i++) {
            float x = MathUtils.random(0, width);
            float y = MathUtils.random(0, height);
            float size = MathUtils.random(10, 30);
            pixmap.fillCircle((int) x, (int) y, (int) size);
        }

        // Draw the pixmap to the texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose(); // Clean up the pixmap

        return texture;
    }

    /**
     * Check if the cell has mitochondria.
     * @return True if the cell has mitochondria, false otherwise.
     */
    public boolean hasMitochondria() {
        return cell.hasMitochondria();
    }

    /**
     * Draw the background for the Ribosome upgrade.
     */
    private Texture createRibosomeBackgroundTexture() {
        // Create a texture for the Ribosome upgrade background
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
        Texture texture = new Texture(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Draw the Ribosome background using yellow for protein synthesis
        pixmap.setColor(0.8f, 0.8f, 0.1f, 0.5f); // Yellow protein (color-blind friendly)
        for (int i = 0; i < 50; i++) {
            float x = MathUtils.random(0, width);
            float y = MathUtils.random(0, height);
            float size = MathUtils.random(10, 30);
            pixmap.fillCircle((int) x, (int) y, (int) size);
        }

        // Draw the pixmap to the texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose(); // Clean up the pixmap

        return texture;
    }

    public boolean hasRibosome() {
        return cell.getProteinSynthesisMultiplier() > 1.0f;
    }

    /**
     * Draw the background for the Flagella upgrade.
     */
    private Texture createFlagellaBackgroundTexture() {
        // Create a texture for the Flagella upgrade background
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
        Texture texture = new Texture(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Draw the Flagella background using purple for movement
        pixmap.setColor(0.6f, 0.1f, 0.8f, 0.5f); // Purple movement (color-blind friendly)
        for (int i = 0; i < 50; i++) {
            float x = MathUtils.random(0, width);
            float y = MathUtils.random(0, height);
            float size = MathUtils.random(10, 30);
            pixmap.fillCircle((int) x, (int) y, (int) size);
        }

        // Draw the pixmap to the texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose(); // Clean up the pixmap

        return texture;
    }

    public boolean hasFlagella() {
        return cell.getMovementSpeedMultiplier() > 1.0f;
    }
    
    /**
     * Draw the background for the Nucleus upgrade.
     */
    private Texture createNucleusBackgroundTexture() {
        // Create a texture for the Nucleus upgrade background
        int width = (int) UPGRADE_CARD_WIDTH;
        int height = (int) UPGRADE_CARD_HEIGHT;
        Texture texture = new Texture(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // Draw the Nucleus background using orange for DNA
        pixmap.setColor(0.8f, 0.4f, 0.1f, 0.5f); // Orange DNA (color-blind friendly)
        for (int i = 0; i < 50; i++) {
            float x = MathUtils.random(0, width);
            float y = MathUtils.random(0, height);
            float size = MathUtils.random(10, 30);
            pixmap.fillCircle((int) x, (int) y, (int) size);
        }

        // Draw the pixmap to the texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose(); // Clean up the pixmap

        return texture;
    }
}