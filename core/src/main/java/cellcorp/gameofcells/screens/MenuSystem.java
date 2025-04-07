package cellcorp.gameofcells.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;

public class MenuSystem {
    private final Stage stage;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;

    private Table mainTable;
    private String[] menuOptions;
    private int selectedOptionIndex = 0;

    // Constants for text sizes
    private static final float TITLE_TEXT_SIZE = 0.4f;
    private static final float MENU_OPTION_TEXT_SIZE = 0.25f;
    private static final float INSTRUCTION_TEXT_SIZE = 0.2f;

    /**
     * Creates a new MenuSystem.
     * 
     * @param stage The stage to ad menu actors to
     * @param assetManager The asset manager for loading fonts
     * @param graphicsProvider The graphics provider for creating UI elements
     */
    public MenuSystem(Stage stage, AssetManager assetManager, GraphicsProvider graphicsProvider) {
        this.stage = stage;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;
    }

    // This method will be the default method for initializing a menu
    /**
     * Initializes the menu with the given options.
     * 
     * @param title The title of the menu
     * @param menuOptions The array of menu options
     * @param instructions The instruction text to display (optional)
     */
    public void initialize(String title, String [] menuOptions, String instructions) {
        this.menuOptions = menuOptions;
        this.selectedOptionIndex = 0;

        mainTable = new Table();
        mainTable.setFillParent(true);

        // Create title label
        Label titleLabel = createLabel(title, TITLE_TEXT_SIZE);
        mainTable.add(titleLabel).padTop(20).row();

        // Create menu options
        for (int i = 0; i < menuOptions.length; i++) {
            Label optionLabel = createLabel(menuOptions[i], MENU_OPTION_TEXT_SIZE);
            optionLabel.setColor(i == selectedOptionIndex ? Color.YELLOW : Color.WHITE);
            mainTable.add(optionLabel).padTop(10).row();
        }

        // instructions
        if (instructions != null && !instructions.isEmpty()) {
            Label instructionsLabel = createLabel(instructions, INSTRUCTION_TEXT_SIZE);
            instructionsLabel.setAlignment(Align.center);
            mainTable.add(instructionsLabel).padTop(30).padBottom(20).row();
        }

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    public void initializeMainMenu(String title, String[] menuOptions, String instructions, Texture wasdArrowsIcon, Texture spaceEnterIcon) {
        clear();
        this.menuOptions = menuOptions;
        this.selectedOptionIndex = 0;

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(50);

        // Title
        Label titleLabel = createLabel(title, TITLE_TEXT_SIZE);
        titleLabel.setColor(new Color(0.4f, 0.8f, 1f, 1f));
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).padBottom(60).row();

        // Menu options
        for (int i = 0; i < menuOptions.length; i++) {
            Label optionLabel = createLabel(menuOptions[i], MENU_OPTION_TEXT_SIZE);
            optionLabel.setColor(i == getSelectedOptionIndex() ? Color.YELLOW : Color.LIGHT_GRAY);
            mainTable.add(optionLabel).padBottom(25).row();
        }

        // Control icons at bottom
        Table controlsTable = new Table();
        controlsTable.defaults().pad(15);

        // WASD/Arrow keys icon with label
        Table movementTable = new Table();
        movementTable.add(new Image(wasdArrowsIcon)).size(120);
        movementTable.row();
        movementTable.add(createLabel("Move", 0.25f)).padTop(5);

        // Space/Enter icon with label
        Table actionTable = new Table();
        actionTable.add(new Image(spaceEnterIcon)).size(120);
        actionTable.row();
        actionTable.add(createLabel("Select", 0.25f)).padTop(5);

        // Add the movement and action tables to the controls table
        controlsTable.add(movementTable);
        controlsTable.add(actionTable);

        // Add the controls table to the main table
        mainTable.add(controlsTable).padTop(50).row();

        // Instructions
        Label instructionLabel = createLabel(instructions, INSTRUCTION_TEXT_SIZE);
        instructionLabel.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
        instructionLabel.setAlignment(Align.center);
        mainTable.add(instructionLabel).padTop(40).row();

        // Add the main table to the stage
        stage.addActor(mainTable);
    }

    // This method creates a split layout with two columns: left for game info and right for controls
    // It doesn't necessarily need to be for info and controls, but that's the current use case
    // I thought it would be better to have a separate method for this
    // to avoid confusion with the regular menu initialization
    /**
     * Initializes the split layout with the given title, left text, right text, and instructions.
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
     * Updates the menu selection
     * 
     * @param newIndex The new selected option index
     */
    public void updateSelection(int newIndex) {
        if (newIndex < 0 || newIndex >= menuOptions.length) {
            return; // Invalid index, do nothing
        }

        // Update the color of the previously selected option
        Label previousOptionLabel = (Label) mainTable.getCells().get(selectedOptionIndex + 1).getActor();
        previousOptionLabel.setColor(Color.WHITE);

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
     * @param text
     * @param delta
     * @return A Label object with the specified text and font size
     */
    private Label createLabel(String text, float delta) {
        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setFontScale(delta);
        return label;
    } 
}
