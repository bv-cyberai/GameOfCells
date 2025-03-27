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

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.objects.Size.LargeSizeUpgrade;
import cellcorp.gameofcells.objects.Size.MassiveSizeUpgrade;
import cellcorp.gameofcells.objects.Size.MediumSizeUpgrade;
import cellcorp.gameofcells.objects.Size.SizeUpgrade;
import cellcorp.gameofcells.objects.Size.SmallSizeUpgrade;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * Screen for purchasing size upgrades
 */
public class SizeUpgradeScreen implements GameOfCellsScreen {
    private final Stage stage;
    private final List<SizeUpgrade> upgrades;
    private final cellcorp.gameofcells.objects.Cell playerCell;
    private final Main game;
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final GameOfCellsScreen previousScreen;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Particles particles;

    // UI Constants
    private static final float SHOP_TEXT_SIZE = 0.3f;
    private static final float UPGRADE_NAME_TEXT_SIZE = 0.2f;
    private static final float UPGRADE_INFO_TEXT_SIZE = 0.15f;
    private static final float INSTRUCTION_TEXT_SIZE = 0.18f;
    private static final float UPGRADE_CARD_WIDTH = 250;
    private static final float UPGRADE_CARD_HEIGHT = 350;
    private static final float SELECTED_CARD_SCALE = 1.4f;

    private int selectedUpgradeIndex = 0;
    private Table upgradeTable;
    private List<Table> upgradeCards;

    /**
     * Constructor for the SizeUpgradeScreen
     * @param game
     * @param inputProvider
     * @param graphicsProvider
     * @param assetManager
     * @param previousScreen
     * @param cell
     */
    public SizeUpgradeScreen(
            Main game,
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            AssetManager assetManager,
            GameOfCellsScreen previousScreen,
            Cell cell) {
        
        this.game = game;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;
        this.previousScreen = previousScreen;
        this.playerCell = cell;

        this.viewport = graphicsProvider.createFitViewport(ShopScreen.VIEW_RECT_WIDTH, ShopScreen.VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.particles = new Particles(assetManager.get(AssetFileNames.WHITE_PIXEL, Texture.class));
        this.stage = new Stage(viewport, batch);

        // Initialize size upgrades
        upgrades = new ArrayList<>();
        upgrades.add(new SmallSizeUpgrade());   // +1 size for 50 ATP
        upgrades.add(new MediumSizeUpgrade());  // +1 size for 65 ATP
        upgrades.add(new LargeSizeUpgrade());  // +1 size for 85 ATP
        upgrades.add(new MassiveSizeUpgrade());  // +1 size for 100 ATP

        createUI();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Dark blue background
        particles.update(delta, viewport.getWorldWidth(), viewport.getWorldHeight());
        particles.draw(batch);

        handleInput(delta);
        update(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedUpgradeIndex > 0) {
                selectedUpgradeIndex--;
                updateUpgradeSelection();
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectedUpgradeIndex < upgrades.size() - 1) {
                selectedUpgradeIndex++;
                updateUpgradeSelection();
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            SizeUpgrade selectedUpgrade = upgrades.get(selectedUpgradeIndex);
            if (selectedUpgrade.canPurchase(playerCell, null)) {
                selectedUpgrade.applyUpgrade(playerCell);
                upgrades.remove(selectedUpgrade);
                selectedUpgradeIndex = Math.min(selectedUpgradeIndex, upgrades.size() - 1);
                
                updateUpgradeDisplay();
                showMessage("Size increased to " + (playerCell.getcellSize()/100) + "!");
            } else {
                showMessage("Cannot purchase - check ATP and size requirements");
            }
        } else if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stage.getRoot().addAction(Actions.sequence(Actions.fadeOut(1f), Actions.run(() -> game.setScreen(previousScreen))));
        }
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label titleLabel = new Label("Size Upgrades", 
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        titleLabel.setFontScale(SHOP_TEXT_SIZE);
        mainTable.add(titleLabel).padTop(20).row();

        // ATP Tracker
        Label atpLabel = new Label("ATP: " + playerCell.getCellATP(),
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        atpLabel.setFontScale(SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(atpLabel).padTop(10).row();

        // Current Size
        Label sizeLabel = new Label("Current Size: " + (playerCell.getcellSize() - 100)/100,
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        sizeLabel.setFontScale(SHOP_TEXT_SIZE - 0.1f);
        mainTable.add(sizeLabel).padTop(10).row();

        // Upgrade Cards
        upgradeTable = new Table();
        upgradeCards = new ArrayList<>();
        
        for (SizeUpgrade upgrade : upgrades) {
            Table card = createUpgradeCard(upgrade);
            upgradeCards.add(card);
            upgradeTable.add(card).pad(10);
        }

        mainTable.add(upgradeTable).expand().fill().padTop(20).row();

        // Instructions
        Label instructions = new Label("Press ESC to go back | Arrows to navigate | Enter to buy",
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        instructions.setFontScale(INSTRUCTION_TEXT_SIZE);
        mainTable.add(instructions).padBottom(20).row();

        stage.addActor(mainTable);
    }

    private Table createUpgradeCard(SizeUpgrade upgrade) {
        Table card = new Table();
        card.defaults().pad(5).center();
        card.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);

        // Glowing border (for selected state)
        Image border = new Image(createGlowingBorderTexture());
        border.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);
        border.setVisible(false);
        border.setName("glowingBorder");
        card.addActor(border);

        // Card background
        card.setBackground(new TextureRegionDrawable(createOptionBackgroundTexture()));

        // Upgrade name
        Label nameLabel = new Label(upgrade.getName(),
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.YELLOW));
        nameLabel.setFontScale(UPGRADE_NAME_TEXT_SIZE);
        card.add(nameLabel).width(UPGRADE_CARD_WIDTH - 20).row();

        // Description
        Label descLabel = new Label(upgrade.getDescription(),
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        descLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        descLabel.setWrap(true);
        card.add(descLabel).width(UPGRADE_CARD_WIDTH - 20).row();

        // Perks
        Label perksLabel = new Label(upgrade.getPerks(),
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.LIGHT_GRAY));
        perksLabel.setFontScale(UPGRADE_INFO_TEXT_SIZE - 0.05f);
        perksLabel.setWrap(true);
        card.add(perksLabel).width(UPGRADE_CARD_WIDTH - 20).padTop(10).row();

        // Requirements
        Table reqTable = new Table();
        reqTable.defaults().left().padLeft(10);
        
        Label atpReq = new Label("ATP: " + upgrade.getRequiredATP(),
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        atpReq.setFontScale(UPGRADE_INFO_TEXT_SIZE);
        
        Label sizeReq = new Label("Req Size: " + upgrade.getRequiredSize(),
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        sizeReq.setFontScale(UPGRADE_INFO_TEXT_SIZE);

        reqTable.add(atpReq).row();
        reqTable.add(sizeReq);
        card.add(reqTable).expand().bottom().left().padBottom(10);

        // Lock overlay if not purchasable
        if (!upgrade.canPurchase(playerCell, null)) {
            card.getColor().a = 0.6f;
            Image lock = new Image(assetManager.get(AssetFileNames.LOCK_ICON, Texture.class));
            lock.setSize(UPGRADE_CARD_WIDTH, UPGRADE_CARD_HEIGHT);
            lock.getColor().a = 0.7f;
            card.addActor(lock);
        }

        return card;
    }

    private void updateUpgradeSelection() {
        for (int i = 0; i < upgradeCards.size(); i++) {
            Table card = upgradeCards.get(i);
            Image border = (Image) card.findActor("glowingBorder");
            
            if (i == selectedUpgradeIndex) {
                card.addAction(Actions.scaleTo(SELECTED_CARD_SCALE, SELECTED_CARD_SCALE, 0.2f));
                if (border != null) border.setVisible(true);
            } else {
                card.addAction(Actions.scaleTo(1f, 1f, 0.2f));
                if (border != null) border.setVisible(false);
            }
        }
    }

    private void updateUpgradeDisplay() {
        upgradeTable.clear();
        upgradeCards.clear();
        
        for (SizeUpgrade upgrade : upgrades) {
            Table card = createUpgradeCard(upgrade);
            upgradeCards.add(card);
            upgradeTable.add(card).pad(10);
        }
        
        updateUpgradeSelection();
    }

    private void showMessage(String message) {
        Label messageLabel = new Label(message,
            new Label.LabelStyle(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class), Color.WHITE));
        messageLabel.setFontScale(0.3f);
        messageLabel.setAlignment(Align.center);

        // Position the message label at the bottom of the screen
        messageLabel.setPosition(ShopScreen.VIEW_RECT_HEIGHT / 2 - messageLabel.getWidth() / 2, 50); // Center horizontally, 50 pixels from the bottom

        // Add message label to the stage
        stage.addActor(messageLabel);

        // Fade out the message label after 2 seconds
        messageLabel.addAction(Actions.sequence(Actions.fadeOut(2.5f), Actions.removeActor()));
    }

    /**
     * Updates the stage and all actors within it
     * @param delta Time since last frame (in seconds)
     */
    @Override 
    public void update(float delta) { 
        stage.act(delta); 
    }

    /**
     * Shows the screen by fading it in
     */
    @Override 
    public void show() {
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(2f)); 
    }

    /**
     * Resizes the screen to the new width and height
     * @param width New width of the screen
     * @param height New height of the screen
     */
    @Override 
    public void resize(int width, int height) { 
        viewport.update(width, height); 
    }

    /**
     * Pauses the screen
     */
    @Override 
    public void pause() {

    }

    /**
     * Resumes the screen
     */
    @Override 
    public void resume() {

    }

    /**
     * Hides the screen by fading it out
     */
    @Override 
    public void hide() {

    }

    /**
     * Draws the screen
     */
    @Override 
    public void draw() {

    }

    /**
     * Disposes of all resources used by this screen
     */
    @Override 
    public void dispose() {
        batch.dispose();
        stage.dispose();
        shapeRenderer.dispose();
        particles.dispose();
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
}