package cellcorp.gameofcells.hud;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.providers.GraphicsProvider;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
/**
 * HudStats Class
 * <p>
 * Manages the stats of the hud Icons in the gameplay screen
 * Uses a scene 2d table to manages each of the stats
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class HudStats {
    private static final float FONT_SCALE = 0.4f;
    private static final String HEALTH_TEXT = "HEALTH: ";
    private static final String ATP_TEXT = "ATP: ";
    private static final String GAME_TIME_TEXT = "TIME: ";
    private static final String RESPAWNS_TEXT = "RESPAWNS: ";

    private final Cell cell;
    private final Stats stats;

    private final Table table;
    private final Label healthLabel;
    private final Label atpLabel;
    private final Label gameTimeLabel;
    private final Label respawnsLabel;

    public HudStats(GraphicsProvider graphicsProvider, AssetManager assetManager, Cell cell, Stats stats) {
        this.cell = cell;
        this.stats = stats;

        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var labelStyle = new Label.LabelStyle(font, Color.WHITE);

        this.healthLabel = graphicsProvider.createLabel(healthText(), labelStyle);
        healthLabel.setFontScale(FONT_SCALE);
        this.atpLabel = graphicsProvider.createLabel(atpText(), labelStyle);
        atpLabel.setFontScale(FONT_SCALE);
        this.gameTimeLabel = graphicsProvider.createLabel(gameTimeText(), labelStyle);
        gameTimeLabel.setFontScale(FONT_SCALE);
        this.respawnsLabel = graphicsProvider.createLabel(respawnsText(), labelStyle);
        respawnsLabel.setFontScale(FONT_SCALE);

        this.table = table();
    }

    private Table table() {
        var table = new Table();
        table.add(healthLabel).align(Align.left);
        table.row();
        table.add(atpLabel).align(Align.left);
        table.row();
        table.add(gameTimeLabel).align(Align.left);
        table.row();
        table.add(respawnsLabel).align(Align.left);

        return table;
    }

    public Table getTable() {
        return this.table;
    }

    public void update() {
        healthLabel.setText(healthText());
        atpLabel.setText(atpText());
        gameTimeLabel.setText(gameTimeText());
        respawnsLabel.setText(respawnsText());
    }

    private String healthText() {
        return HEALTH_TEXT + cell.getCellHealth();
    }

    private String atpText() {
        return ATP_TEXT + cell.getCellATP();
    }

    private String gameTimeText() {
        return GAME_TIME_TEXT + (int) stats.gameTimer;
    }

    private String respawnsText() {
        var respawns = cell.getRespawns();
        return RESPAWNS_TEXT + respawns;
    }
}
