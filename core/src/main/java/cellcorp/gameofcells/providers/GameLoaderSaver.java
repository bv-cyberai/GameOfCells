package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.hud.HudStats;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.GlucoseManager;
import cellcorp.gameofcells.objects.Stats;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameLoaderSaver {

    Cell cell;
    GlucoseManager glucoseManager;
    Stats stats;
    HudStats hudStats;
    Preferences saveGame;

    public GameLoaderSaver(Cell cell,
                           GlucoseManager glucoseManager,
                           Stats stats,
                           HudStats hudStats) {
        this.cell = cell;
        this.glucoseManager =glucoseManager;
        this.stats = stats;
        this.hudStats = hudStats;
        saveGame = Gdx.app.getPreferences("saveGame");
    }

    public GameLoaderSaver() {
        this(null,null,null,null);
    }



    public void saveState() {
        //cell state
        saveGame.putInteger("cellHealth", cell.getCellHealth());
        saveGame.putInteger("currATP", cell.getCellATP());
        saveGame.putFloat("cellSize", cell.getCellSize());

        //size
        saveGame.putBoolean("smallSize",cell.hasSmallSizeUpgrade());
        saveGame.putBoolean("mediumSize",cell.hasMediumSizeUpgrade());
        saveGame.putBoolean("largeSize",cell.hasLargeSizeUpgrade());
        saveGame.putBoolean("massiveSize",cell.hasMassiveSizeUpgrade());

        //organelles
        saveGame.putBoolean("mito",cell.hasMitochondria());
        saveGame.putBoolean("ribo",cell.hasRibosomes());
        saveGame.putBoolean("flag",cell.hasFlagella());
        saveGame.putBoolean("nuke",cell.hasNucleus());


        //stats state
        saveGame.putInteger("atpGen", stats.atpGenerated);
        saveGame.putInteger("glukeCollected", stats.glucoseCollected);
        saveGame.putFloat("distanceMoved", stats.distanceMoved);
        saveGame.putFloat("time", stats.gameTimer);

        saveGame.flush();
    }

    public void loadState() {
        //cell state

        cell.setCellHealth(saveGame.getInteger("cellHealth"));
        cell.setCellATP(saveGame.getInteger("currATP"));
        cell.setCellSize(saveGame.getFloat("cellSize"));

        //size
        cell.setHasSmallSizeUpgrade(saveGame.getBoolean("smallSize"));
        cell.setHasMediumSizeUpgrade(saveGame.getBoolean("mediumSize"));
        cell.setHasLargeSizeUpgrade(saveGame.getBoolean("largeSize"));
        cell.setHasMassiveSizeUpgrade(saveGame.getBoolean("massiveSize"));

        //organelles
        cell.setHasMitochondria(saveGame.getBoolean("mito"));
        cell.setHasRibosomes(saveGame.getBoolean("ribo"));
        cell.setHasFlagella(saveGame.getBoolean("flag"));
        cell.setHasNucleus(saveGame.getBoolean("nuke"));


        //stats state
        stats.atpGenerated = saveGame.getInteger("atpGen");
        stats.glucoseCollected = saveGame.getInteger("glukeCollected");
        stats.distanceMoved = saveGame.getFloat("distanceMoved");
        stats.gameTimer = saveGame.getFloat("time");



    }
}
