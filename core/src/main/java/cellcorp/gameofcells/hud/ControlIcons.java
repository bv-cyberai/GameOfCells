package cellcorp.gameofcells.hud;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.List;

public class ControlIcons {
    private static final float FONT_SCALE = 0.4f;
    private static final float TEXTURE_SCALE = 0.25f;
    private static final float ROW_PADDING = 20f;
    private static final float COL_PADDING = 20f;

    private static final List<IconData> ICON_DATA = List.of(
            new IconData("Pause", AssetFileNames.PAUSE_BUTTON),
            new IconData("Move", AssetFileNames.MOVE_KEY),
            new IconData("Quit", AssetFileNames.QUIT_BUTTON),
            new IconData("Shop", AssetFileNames.SHOP_BUTTON),
            new IconData("Heal", AssetFileNames.HEAL_ICON)
    );

    private final Table table;

    public ControlIcons(GraphicsProvider graphicsProvider, AssetManager assetManager) {
        this.table = table(graphicsProvider, assetManager);
    }

    private Table table(GraphicsProvider graphicsProvider, AssetManager assetManager) {
        var table = new Table();

        for (var iconData : ICON_DATA) {
            var label = iconLabel(graphicsProvider, assetManager, iconData);
            var icon = iconImage(graphicsProvider, assetManager, iconData);
            table.row().padBottom(ROW_PADDING);
            table.add(label).bottom().left();
            table.add(icon).padLeft(COL_PADDING);
        }
        return table;
    }

    private Label iconLabel(GraphicsProvider graphicsProvider, AssetManager assetManager, IconData iconData) {
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = graphicsProvider.createLabel(iconData.text, labelStyle);
        label.setFontScale(FONT_SCALE);
        return label;
    }

    private Image iconImage(GraphicsProvider graphicsProvider, AssetManager assetManager, IconData iconData) {
        var texture = assetManager.get(iconData.textureFileName, Texture.class);
        var iconDrawable = graphicsProvider.createTextureRegionDrawable(texture);
        if (texture != null) {
            // Only null in tests...
            iconDrawable.setMinWidth(texture.getWidth() * TEXTURE_SCALE);
            iconDrawable.setMinHeight(texture.getHeight() * TEXTURE_SCALE);
        }
        return new Image(iconDrawable);
    }

    public Table getTable() {
        return table;
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
