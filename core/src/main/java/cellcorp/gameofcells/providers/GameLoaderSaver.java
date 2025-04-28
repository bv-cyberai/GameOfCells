package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.hud.HudStats;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.GlucoseManager;
import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.screens.GamePlayScreen;
import cellcorp.gameofcells.screens.PopupInfoScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameLoaderSaver {

    Cell cell;
    GlucoseManager glucoseManager;
    Stats stats;
    HudStats hudStats;

    PopupInfoScreen glucoseCollisionPopup;
    PopupInfoScreen acidZonePopup;
    PopupInfoScreen basicZonePopup;
    PopupInfoScreen healAvailablePopup;
    PopupInfoScreen cellMembranePopup;
    boolean hasNotAutoSaved;

    Preferences saveGame;


    public GameLoaderSaver(GamePlayScreen gamePlayScreen) {
        this.cell = gamePlayScreen.getCell();
        this.glucoseManager = gamePlayScreen.getGlucoseManager();
        this.stats = gamePlayScreen.getStats();
        this.hudStats = gamePlayScreen.getHUD().getHudStats();
        this.glucoseCollisionPopup = gamePlayScreen.getGlucoseCollisionPopup();
        this.acidZonePopup = gamePlayScreen.getAcidZonePopup();
        this.basicZonePopup = gamePlayScreen.getBasicZonePopup();
        this.healAvailablePopup = gamePlayScreen.getHealAvailablePopup();
        this.cellMembranePopup = gamePlayScreen.getCellMembranePopup();
        hasNotAutoSaved = false;
        saveGame = Gdx.app.getPreferences("saveGame");
    }

    public void saveState() {
        //cell state
        saveGame.putInteger("cellHealth", cell.getCellHealth());
        saveGame.putInteger("cellATP", cell.getCellATP());
        saveGame.putFloat("cellSize", cell.getCellSize());

        //size
        saveGame.putBoolean("smallSize", cell.hasSmallSizeUpgrade());
        saveGame.putBoolean("mediumSize", cell.hasMediumSizeUpgrade());
        saveGame.putBoolean("largeSize", cell.hasLargeSizeUpgrade());
        saveGame.putBoolean("massiveSize", cell.hasMassiveSizeUpgrade());

        //organelles
        saveGame.putBoolean("mito", cell.hasMitochondria());
        saveGame.putBoolean("ribo", cell.hasRibosomes());
        saveGame.putBoolean("flag", cell.hasFlagella());
        saveGame.putBoolean("nuke", cell.hasNucleus());

        //stats state
        saveGame.putInteger("atpGen", stats.atpGenerated);
        saveGame.putInteger("glukeCollected", stats.glucoseCollected);
        saveGame.putFloat("distanceMoved", stats.distanceMoved);
        saveGame.putFloat("time", stats.gameTimer);

        //Pop-up States
        saveGame.putBoolean("glukePopup", glucoseCollisionPopup.wasShown());
        saveGame.putBoolean("acidPopup", acidZonePopup.wasShown());
        saveGame.putBoolean("basicPopup", basicZonePopup.wasShown());
        saveGame.putBoolean("healPopup", healAvailablePopup.wasShown());
        saveGame.putBoolean("membranePopup", cellMembranePopup.wasShown());

        saveGame.flush();
    }

    public void loadState() {
        //cell state
        cell.setCellHealth(saveGame.getInteger("cellHealth"));
        cell.setCellATP(saveGame.getInteger("cellATP"));
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

        //Pop-up States
        glucoseCollisionPopup.setWasShone(saveGame.getBoolean("glukePopup"));
        acidZonePopup.setWasShone(saveGame.getBoolean("acidPopup"));
        basicZonePopup.setWasShone(saveGame.getBoolean("basicPoup"));
        healAvailablePopup.setWasShone(saveGame.getBoolean("healPopup"));
        cellMembranePopup.setWasShone(saveGame.getBoolean("membranePopup"));

    }

    public void update() {
        if(cell.hasNucleus() && !hasNotAutoSaved) {
            saveState();
            hasNotAutoSaved = true;
        }
    }


    public void setUpMockPrefrences(Preferences mockedSaveGame) {
        saveGame = mockedSaveGame;
    }
}
