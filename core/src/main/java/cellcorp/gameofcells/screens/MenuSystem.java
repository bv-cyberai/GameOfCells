package cellcorp.gameofcells.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

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
    private static final float TITLE_TEXT_SIZE = 0.3f;
    private static final float MENU_OPTION_TEXT_SIZE = 0.2f;
    private static final float INSTRUCTION_TEXT_SIZE = 0.18f;

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

    private Label createLabel(String text, float delta) {
        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setFontScale(delta);
        return label;
    }
}
