package cellcorp.gameofcells.hud;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.List;

import static cellcorp.gameofcells.hud.ControlInstructions.State.SHOW_CONTROLS_ICONS;
import static cellcorp.gameofcells.hud.ControlInstructions.State.SHOW_INFO_ICON;

/**
 * Manages display of the "I" info icon and the control instructions in the top-right of the HUD.
 * Strategy:
 * - We have an outer `Table` which is placed in the HUD's table
 * - We have two inner tables, which we swap between:
 * - `infoTable`, which shows the single info key
 * - `controlsTable`, which shows the full controls
 */
public class ControlInstructions {
    private static final float FONT_SCALE = 0.4f;
    private static final float TEXTURE_SCALE = 0.25f;
    private static final float ROW_PADDING = 20f;
    private static final float COL_PADDING = 20f;

    private static final float INFO_PADDING_TOP = 40f;
    private static final float INFO_PADDING_RIGHT = 40f;

    private static final IconData INFO_ICON = new IconData("Controls", AssetFileNames.CONTROLS_INFO_BUTTON);

    private static final List<IconData> CONTROLS_ICON = List.of(
            new IconData("Pause", AssetFileNames.PAUSE_BUTTON),
            new IconData("Move", AssetFileNames.MOVE_KEY),
            new IconData("Quit", AssetFileNames.QUIT_BUTTON),
            new IconData("Shop", AssetFileNames.SHOP_BUTTON),
            new IconData("Heal", AssetFileNames.HEAL_ICON)
    );

    private final GraphicsProvider graphicsProvider;
    private final InputProvider inputProvider;
    private final AssetManager assetManager;

    /**
     * Outer table, which is placed in the HUD
     */
    private final Table outerTable;

    /**
     * Inner info table, with a single "info" key displayed
     */
    private final Table infoTable;

    /**
     * Inner controls table, which displays the full game controls.
     */
    private final Table controlsTable;

    private State state = SHOW_INFO_ICON;

    public ControlInstructions(GraphicsProvider graphicsProvider, InputProvider inputProvider, AssetManager assetManager) {
        this.graphicsProvider = graphicsProvider;
        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.infoTable = infoTable();
        this.controlsTable = controlsTable();
        this.outerTable = outerTable(infoTable);
    }

    private Table infoTable() {
        var label = iconLabel(INFO_ICON);
        var icon = iconImage(INFO_ICON);

        var table = new Table();
        table.row().padTop(INFO_PADDING_TOP).padRight(INFO_PADDING_RIGHT);
        table.add(label).bottom().left();
        table.add(icon).padLeft(COL_PADDING);
        return table;
    }

    private Table controlsTable() {
        var table = new Table();

        for (var iconData : CONTROLS_ICON) {
            var label = iconLabel(iconData);
            var icon = iconImage(iconData);
            table.row().padBottom(ROW_PADDING);
            table.add(label).bottom().left();
            table.add(icon).padLeft(COL_PADDING);
        }
        return table;
    }

    /**
     * Create the initial outer table.
     * It has a single cell, which is filled at the start by `infoTable`
     */
    private Table outerTable(Table infoTable) {
        var table = new Table();

        table.row();
        table.add(infoTable).fill();
        return table;
    }

    public void handleInput() {
        if (inputProvider.isKeyPressed(Input.Keys.I)) {
            moveToControlsIcons();
        } else {
            moveToInfoIcon();
        }
    }

    public Table getTable() {
        return outerTable;
    }

    /**
     * Replaces the contents of `outerTable` with the given table.
     */
    private void setTable(Table table) {
        outerTable.clear();

        outerTable.row();
        outerTable.add(table).fill();
    }

    private Label iconLabel(IconData iconData) {
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = graphicsProvider.createLabel(iconData.text, labelStyle);
        label.setFontScale(FONT_SCALE);
        return label;
    }

    private Image iconImage(IconData iconData) {
        var texture = assetManager.get(iconData.textureFileName, Texture.class);
        var iconDrawable = graphicsProvider.createTextureRegionDrawable(texture);
        if (texture != null) {
            // Only null in tests...
            iconDrawable.setMinWidth(texture.getWidth() * TEXTURE_SCALE);
            iconDrawable.setMinHeight(texture.getHeight() * TEXTURE_SCALE);
        }
        return new Image(iconDrawable);
    }

    /**
     * Move the state to `SHOW_INFO_ICON`, updating `outerTable` appropriately
     */
    private void moveToInfoIcon() {
        if (state == SHOW_CONTROLS_ICONS) {
            state = SHOW_INFO_ICON;
            setTable(infoTable);
        }
    }

    /**
     * Move the state to `SHOW_CONTROLS_ICON`, updating `outerTable` appropriately
     */
    private void moveToControlsIcons() {
        if (state == SHOW_INFO_ICON) {
            state = SHOW_CONTROLS_ICONS;
            setTable(controlsTable);
        }
    }

    protected enum State {
        SHOW_INFO_ICON,
        SHOW_CONTROLS_ICONS,
    }

    private static class IconData {
        public String text;
        public String textureFileName;

        public IconData(String text, String textureFileName) {
            this.text = text;
            this.textureFileName = textureFileName;
        }
    }
}
