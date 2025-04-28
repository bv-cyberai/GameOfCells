package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.screens.GamePlayScreen;
import cellcorp.gameofcells.screens.PopupInfoScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Class that allows loading and
 * saving of GameOfCells. Also provides
 * auto save functionality on nucleus purchase.
 */
public class GameLoaderSaver {
    private static Preferences saveGame;

    private final Cell cell;
    private final Stats stats;

    private final PopupInfoScreen glucoseCollisionPopup;
    private final PopupInfoScreen acidZonePopup;
    private final PopupInfoScreen basicZonePopup;
    private final PopupInfoScreen healAvailablePopup;
    private final PopupInfoScreen cellMembranePopup;
    private boolean hasNotAutoSaved;

    /**
     * Constructor
     *
     * From the game state, pulls various objects that track game state
     * to save.
     * @param gamePlayScreen The game state
     */
    public GameLoaderSaver(GamePlayScreen gamePlayScreen) {
        this.cell = gamePlayScreen.getCell();
        this.stats = gamePlayScreen.getStats();
        this.glucoseCollisionPopup = gamePlayScreen.getGlucoseCollisionPopup();
        this.acidZonePopup = gamePlayScreen.getAcidZonePopup();
        this.basicZonePopup = gamePlayScreen.getBasicZonePopup();
        this.healAvailablePopup = gamePlayScreen.getHealAvailablePopup();
        this.cellMembranePopup = gamePlayScreen.getCellMembranePopup();

        //auto save flag.
        hasNotAutoSaved = false;

        //The actual saved game file. If this file does not exit, it is made.
        //If it exists, it is overwritten.
        //The data is stored as a map/xml file. See
        //https://libgdx.com/wiki/preferences for full details.
        saveGame = Gdx.app.getPreferences("saveGame");
    }

    /**
     * saveState
     *
     * Saves the state of the game.
     */
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

        //Writes to the file.
        saveGame.flush();
    }

    /**
     * loadState
     *
     * loads the state of the game.
     */
    public void loadState() {
        System.out.println(GameLoaderSaver.saveGame.get().isEmpty());
        if (isInvalidSaveFile()) {
            return;
        }
        System.out.println("HERE");
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
        glucoseCollisionPopup.setWasShown(saveGame.getBoolean("glukePopup"));
        acidZonePopup.setWasShown(saveGame.getBoolean("acidPopup"));
        basicZonePopup.setWasShown(saveGame.getBoolean("basicPoup"));
        healAvailablePopup.setWasShown(saveGame.getBoolean("healPopup"));
        cellMembranePopup.setWasShown(saveGame.getBoolean("membranePopup"));

    }

    public boolean isInvalidSaveFile() {
        return saveGame.get().isEmpty();
    }

    public static void clearSaveFile() {
        saveGame.get().clear();
        saveGame.clear();
        saveGame.flush();
    }

    /**
     * Update
     *
     * Used for autosaving the game.
     */
    public void update() {
        if (cell.hasNucleus() && !hasNotAutoSaved) {
            saveState();
            hasNotAutoSaved = true;
        }
    }

    /**
     * Preference Injector
     *
     * Allows changing the preference object so that tests can be
     * run with a fake preferences object to check logic.
     * @param fakePreferences The fake object.
     */
    public void injectFakePreferences(Preferences fakePreferences) {
        saveGame = fakePreferences;
    }
}
