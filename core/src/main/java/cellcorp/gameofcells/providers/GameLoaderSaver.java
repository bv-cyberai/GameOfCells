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
        saveGame.flush();

        //stats state
//        saveGame.putInt("atpGen", stats.atpGenerated);
//        saveGame.putInt("glukeCollected", stats.glucoseCollected);
//        saveGame.putFloat("distanceMoved", stats.distanceMoved);
//        saveGame.putFloat("time", stats.gameTimer);

    }

    public void loadState() {
        //cell state

        cell.setCellHealth(saveGame.getInteger("cellHealth", cell.getCellHealth()));
        cell.setCellATP(saveGame.getInteger("currATP", cell.getCellATP()));

        //stats state
//        saveGame.getInt("atpGen", stats.atpGenerated);
//        saveGame.getInt("glukeCollected", stats.glucoseCollected);
//        saveGame.getFloat("distanceMoved", stats.distanceMoved);
//        saveGame.getFloat("time", stats.gameTimer);
    }
}
