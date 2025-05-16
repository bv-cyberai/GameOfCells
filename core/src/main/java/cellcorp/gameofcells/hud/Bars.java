package cellcorp.gameofcells.hud;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.providers.GraphicsProvider;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Bars Class
 * <p>
 * Manages the constructions of the bars displayed in the hud
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class Bars {
    private static final Color DARK_RED = new Color(0.7f, 0, 0, 1);

    private final Cell cell;

    private final Table table;
    private final Bar healthBar;
    private final Bar atpBar;

    /**
     * Construct the bars
     */
    public Bars(GraphicsProvider graphicsProvider, AssetManager assetManager, Cell cell) {
        super();
        this.cell = cell;

        this.healthBar = new Bar(graphicsProvider, assetManager, "HEALTH", DARK_RED);
        this.atpBar = new Bar(graphicsProvider, assetManager, "ATP", Color.YELLOW);
        this.table = table(healthBar, atpBar);
    }

    private Table table(Bar healthBar, Bar atpBar) {
        var table = new Table();
        table.add(new Image(healthBar));
        table.row().padTop(10f);
        table.add(new Image(atpBar));
        return table;
    }

    public Table getTable() {
        return this.table;
    }

    /**
     * Update the bars for this game tick.
     */
    public void update() {
        var percentHealth = (float) cell.getCellHealth() / (float) cell.getMaxHealth();
        healthBar.setFillPercent(percentHealth);

        var percentAtp = (float) cell.getCellATP() / (float) cell.getMaxATP();
        atpBar.setFillPercent(percentAtp);
    }
}
