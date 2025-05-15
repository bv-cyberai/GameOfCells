package cellcorp.gameofcells.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;

/**
 * Menu System class
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 04/09/2025
 * @course CIS 405
 * @assignment Game of Cells
 * @description This is the menu system for the game. This class handles displaying
 * menus, updating menu selections, and clearing the menu when needed.
 * It uses Scene2D for rendering and managing UI elements.
 * The menu system is designed to be flexible and reusable for different
 * types of menus, including main menus, pause menus, and shop menus.
 */


public class MenuSystem {
    // Instance variables
    // The stage to which the menu will be added
    // The asset manager for loading assets
    // The graphics provider for creating UI elements
    private final Stage stage;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;
    private final BitmapFont font;

    // The main table that holds the menu elements
    // The menu options to be displayed
    // The index of the currently selected option
    private Table mainTable;
    private String[] menuOptions;
    private int selectedOptionIndex = 0;
    private boolean[] optionEnabled; // Array to track enabled/disabled options

    // Constants for text sizes
    // These constants define the font sizes for different elements in the menu
    // They are used to ensure consistent styling across the menu
    // The title text size, menu option text size, and instruction text size
    private static final float TITLE_TEXT_SIZE = 0.4f;
    private static final float MENU_OPTION_TEXT_SIZE = 0.25f;
    private static final float INSTRUCTION_TEXT_SIZE = 0.2f;

    /**
     * Constructor for the MenuSystem class.
     *
     * @param stage            The stage to which the menu will be added
     * @param assetManager     The asset manager for loading assets
     * @param graphicsProvider The graphics provider for creating UI elements
     * @description This constructor initializes the MenuSystem with the provided
     * stage, asset manager, and graphics provider.
     * It sets up the necessary components for rendering the menu.
     */
    public MenuSystem(Stage stage,
                      AssetManager assetManager,
                      GraphicsProvider graphicsProvider) {
        this.stage = stage;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;
        this.font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
    }

    // This method will be the default method for initializing a menu

    /**
     * Initializes a menu with the given options.
     *
     * @param title        The title of the menu
     * @param menuOptions  The array of menu options
     * @param instructions The instruction text to display (optional)
     */
    public void initialize(String title, String[] menuOptions, String instructions) {
        // We set the menu options and selected option index
        this.menuOptions = menuOptions;
        this.selectedOptionIndex = 0;

        // Create a new table for the menu
        // The table will fill the parent stage and be aligned to the top
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        // Here we create a label for the title
        // The title label is created with a specific font size
        // and is added to the main table with padding
        Label titleLabel = createLabel(title, TITLE_TEXT_SIZE);
        mainTable.add(titleLabel).padTop(10).row();

        // Here we just add a spacer to the table
        // This is used to create space between the title and the menu options
        mainTable.add().height(20).row();

        // We loop through the menu options and create a label for each one
        for (int i = 0; i < menuOptions.length; i++) {
            Label optionLabel = createLabel(menuOptions[i], MENU_OPTION_TEXT_SIZE);
            optionLabel.setColor(i == selectedOptionIndex ? Color.YELLOW : Color.LIGHT_GRAY);
            mainTable.add(optionLabel).padTop(5).row();
        }

        // We check if there are any instructions to display
        // If there are, we create a label for the instructions
        // and add it to the main table with padding
        if (instructions != null && !instructions.isEmpty()) {
            Label instructionsLabel = createLabel(instructions, INSTRUCTION_TEXT_SIZE);
            instructionsLabel.setAlignment(Align.center);
            mainTable.add(instructionsLabel).padTop(15).padBottom(10).row();
        }

        // Finally, we add the main table to the stage
        // This makes the table visible on the screen
        stage.addActor(mainTable);
    }

    /**
     * Initializes a main menu-specific layout with a title, menu options, and instructions.
     *
     * @param title          The title of the main menu
     * @param menuOptions    The array of menu options
     * @param instructions   Bottom instructions text
     * @param wasdArrowsIcon The texture for the WASD/Arrows icon
     * @param spaceEnterIcon The texture for the Space/Enter icon
     */
    public void initializeMainMenu(String title, String[] menuOptions, String instructions, Texture wasdArrowsIcon, Texture spaceEnterIcon, boolean[] optionEnabled) {
        clear();

        this.menuOptions = menuOptions;
        this.selectedOptionIndex = 0;
        this.optionEnabled = optionEnabled;

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(30);

        // Title - lowered with less bottom padding
        Label titleLabel = createLabel(title, 0.6f);
        titleLabel.setColor(new Color(0.4f, 0.8f, 1f, 1f));
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).padBottom(40).row(); // Reduced from 60

        // Menu options - lowered with less padding
        for (int i = 0; i < menuOptions.length; i++) {
            Label optionLabel = createLabel(menuOptions[i], 0.3f);
            if (!optionEnabled[i]) {
                optionLabel.setColor(Color.DARK_GRAY); // disabled look
            } else {
                optionLabel.setColor(i == selectedOptionIndex ? Color.YELLOW : Color.LIGHT_GRAY);
            }
            mainTable.add(optionLabel).padBottom(15).row();
        }

        // Control icons at bottom
        Table controlsTable = new Table();
        controlsTable.defaults().pad(8);

        Image wasdImage = graphicsProvider.createImage(wasdArrowsIcon);
        wasdImage.setColor(0.8f, 0.9f, 1f, 0.9f);
        controlsTable.add(wasdImage)
            .size(150, 150 * (428f / 636f)) // Smaller size
            .padRight(20);

        Image spaceImage = graphicsProvider.createImage(spaceEnterIcon);
        spaceImage.setColor(0.8f, 0.9f, 1f, 0.9f);
        controlsTable.add(spaceImage)
            .size(180, 190 * (210f / 800f))
            .padTop(60);
//                .size(180, 180 * (202f/800f)); // Smaller size

        mainTable.add().height(40).row(); // New space to push icons down

        // Add the controls table to the main table
        mainTable.add(controlsTable)
            .padBottom(20)
            .row(); // Lowered position

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    // This method creates a split layout with two columns: left for game info and right for controls
    // It doesn't necessarily need to be for info and controls, but that's the current use case
    // I thought it would be better to have a separate method for this
    // to avoid confusion with the regular menu initialization

    /**
     * Initializes the split layout with the given title, left text, right text, and instructions.
     *
     * @param title
     * @param leftText
     * @param rightText
     * @param instructions
     */
    public void initializeSplitLayout(String title, String[] leftText, String[] rightText, String instructions) {
        clear();

        // Create a new table for the split layout
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(40);

        // Title
        Label titleLabel = createLabel(title, TITLE_TEXT_SIZE);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(2).padBottom(30).row();

        // Content tables
        Table leftTable = new Table();
        Table rightTable = new Table();

        // Left column (game info)
        for (String line : leftText) {
            Label label = createLabel(line, MENU_OPTION_TEXT_SIZE);
            label.setAlignment(Align.left);
            leftTable.add(label).padBottom(5).left().row();
        }

        // Right column (controls)
        for (String line : rightText) {
            Label label = createLabel(line, MENU_OPTION_TEXT_SIZE);
            label.setAlignment(Align.left);
            if (line.equals("CONTROLS")) {
                label.setColor(Color.CYAN);
            } else if (line.contains(":")) {
                label.setColor(Color.YELLOW);
            }
            rightTable.add(label).padBottom(5).left().row();
        }

        // Add the left and right tables to the main table
        mainTable.add(leftTable).width(500).padRight(50);
        mainTable.add(rightTable).width(500);
        mainTable.row();

        // Instructions
        if (instructions != null && !instructions.isEmpty()) {
            Label instructionsLabel = createLabel(instructions, INSTRUCTION_TEXT_SIZE);
            instructionsLabel.setAlignment(Align.center);
            mainTable.add(instructionsLabel).colspan(2).padTop(50).row();
        }

        stage.addActor(mainTable);
    }

    /**
     * Initializes a pause-specific menu layout with a title, menu options, and instructions.
     *
     * @param title        The title of the pause menu
     * @param menuOptions  The array of menu options
     * @param instructions The instruction text to display (optional)
     */
    public void initializePauseMenu(String title, String[] menuOptions, String instructions) {
        clear();

        this.menuOptions = menuOptions;
        this.selectedOptionIndex = 0;
        this.optionEnabled = new boolean[menuOptions.length];
        for (int i = 0; i < optionEnabled.length; i++) {
            optionEnabled[i] = true; // Enable all options by default
        }

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(40);

        // Title
        Label titleLabel = createLabel(title, 0.6f);
        titleLabel.setColor(Color.CYAN);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).padBottom(50).row();

        // Menu options
        for (int i = 0; i < menuOptions.length; i++) {
            Label optionLabel = createLabel(menuOptions[i], 0.4f);
            optionLabel.setColor(i == selectedOptionIndex ? Color.YELLOW : Color.LIGHT_GRAY);
            mainTable.add(optionLabel).padBottom(25).row();
        }

        // Instructions
        if (instructions != null && !instructions.isEmpty()) {
            Label instructionsLabel = createLabel(instructions, 0.25f);
            instructionsLabel.setColor(0.8f, 0.9f, 1f, 0.9f);
            instructionsLabel.setAlignment(Align.center);
            mainTable.add(instructionsLabel).padTop(40).row();
        }

        stage.addActor(mainTable);
    }

    /**
     * Initializes a shop-specific layout with three columns:
     * Left for size upgrades, center for stats, and right for organelle upgrades.
     *
     * @param title        The shop title
     * @param instructions Bottom instructions text
     * @return An array of tables for size upgrades, stats, and organelle upgrades
     * (left, center, right respectively)
     */
    public Table[] initializeShopLayout(String title, String instructions) {
        clear();

        // Main container table
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(30); // Uniform padding

        // Title setup
        Label titleLabel = createLabel(title, 0.5f); // Slightly larger than standard
        titleLabel.setColor(new Color(0.4f, 0.8f, 1f, 1f)); // Cyan-blue
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).colspan(3).padBottom(40).row();

        // Create columns
        Table leftTable = createShopColumn("SIZE UPGRADES");
        Table centerTable = new Table();
        Table rightTable = createShopColumn("ORGANELLE UPGRADES");

        // Column layout
        mainTable.add(leftTable).width(350).padRight(15);
        mainTable.add(centerTable).width(250); // Wider center for stats
        mainTable.add(rightTable).width(350).padLeft(15);
        mainTable.row();

        // Instructions footer
        if (instructions != null && !instructions.isEmpty()) {
            Label instructionsLabel = createLabel(instructions, 0.2f);
            instructionsLabel.setAlignment(Align.center);
            mainTable.add(instructionsLabel).colspan(3).padTop(30).row();
        }

        stage.addActor(mainTable);
        return new Table[]{leftTable, centerTable, rightTable};
    }

    /**
     * Helper method to create consistent shop columns
     *
     * @param headerText The header text for the column
     * @return A Table object representing the column
     * with a header and separator line
     * (for size upgrades or organelle upgrades)
     * aligned to the top
     */
    private Table createShopColumn(String headerText) {
        Table column = new Table();
        column.top(); // Align content to top

        // Column header
        Label header = createLabel(headerText, 0.3f);
        header.setColor(Color.YELLOW);
        column.add(header).padBottom(5).row();

        // Separator line
        Image separator = new Image(createSeparatorLine(350, 2));
        column.add(separator).padBottom(15).row();

        return column;
    }

    /**
     * Creates a simple line texture for separators
     *
     * @param width  The width of the line
     * @param height The height of the line
     * @return A Texture object representing the line
     */
    private Texture createSeparatorLine(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**
     * Updates the menu selection
     *
     * @param newIndex The new selected option index
     */
    public void updateSelection(int newIndex) {
        if (menuOptions == null || optionEnabled == null) {
            return; // No options to select from
        }

        int direction = (newIndex > selectedOptionIndex) ? 1 : -1;

        // clamp newIndex within bounds and skip disabled options
        while (newIndex >= 0 && newIndex < menuOptions.length && !optionEnabled[newIndex]) {
            newIndex += direction;
        }

        // if out of bounds or still disabled after skipping, don't update
        if (newIndex < 0 || newIndex >= menuOptions.length || !optionEnabled[newIndex]) {
            return; // Invalid index, do nothing
        }

        // Update the color of the previously selected option
        Label previousOptionLabel = (Label) mainTable.getCells().get(selectedOptionIndex + 1).getActor();
        previousOptionLabel.setColor(Color.LIGHT_GRAY);

        // Update the selected option index
        selectedOptionIndex = newIndex;

        // Update the color of the newly selected option
        Label currentOptionLabel = (Label) mainTable.getCells().get(selectedOptionIndex + 1).getActor();
        currentOptionLabel.setColor(Color.YELLOW);
    }


    /**
     * Gets the currently selected option index.
     *
     * @return The index of the currently selected option
     */
    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    /**
     * Gets the stage associated with this menu system.
     *
     * @return The stage used for rendering the menu
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Clears the menu from the stage.
     */
    public void clear() {
        if (mainTable != null) {
            mainTable.clear();
            mainTable.remove();
        }
    }

    /**
     * Creates a label with the given text and font size.
     *
     * @param text
     * @param delta
     * @return A Label object with the specified text and font size
     */
    private Label createLabel(String text, float delta) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = graphicsProvider.createLabel(text, labelStyle);
        label.setFontScale(delta);
        return label;
    }

    /**
     * Gets the menu options.
     *
     * @return The array of menu options
     */
    public int getMenuOptionCount() {
        return menuOptions.length;
    }

    /**
     * Checks if the option at the given index is enabled.
     *
     * @param index
     * @return
     */
    public boolean isOptionEnabled(int index) {
        return optionEnabled[index];
    }
}
