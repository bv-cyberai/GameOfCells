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
    private final PopupInfoScreen splitCellPopup;
    private boolean hasAutoSaved;

    /**
     * Constructor
     * <p>
     * From the game state, pulls various objects that track game state
     * to save.
     *
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
        this.splitCellPopup = gamePlayScreen.getSplitCellPopup();

        //auto save flag.
        hasAutoSaved = false;

        //The actual saved game file. If this file does not exit, it is made.
        //If it exists, it is overwritten.
        //The data is stored as a map/xml file. See
        //https://libgdx.com/wiki/preferences for full details.
        saveGame = Gdx.app.getPreferences("saveGame");
    }

    /**
     * saveState
     * <p>
     * Saves the state of the game.
     */
    public void saveState() {
        //cell state
        saveGame.putInteger("cellHealth", cell.getCellHealth());
        saveGame.putInteger("cellATP", cell.getCellATP());
        saveGame.putFloat("cellSize", cell.getCellSize());
        saveGame.putBoolean("cellSplit", cell.hasSplit());
        saveGame.putInteger("organelleLevel", cell.getOrganelleUpgradeLevel());

        //size
        saveGame.putBoolean("smallSize", cell.hasSmallSizeUpgrade());
        saveGame.putBoolean("mediumSize", cell.hasMediumSizeUpgrade());
        saveGame.putBoolean("largeSize", cell.hasLargeSizeUpgrade());
        saveGame.putBoolean("massiveSize", cell.hasMassiveSizeUpgrade());

        //organelles
        saveGame.putBoolean("mito", cell.hasMitochondria());
        saveGame.putBoolean("ribo", cell.hasRibosomes());
        saveGame.putBoolean("flag", cell.hasFlagellum());
        saveGame.putFloat("flagThick", cell.getFlagellumThickness());
        saveGame.putInteger("flagLength", cell.getFlagellumLength());
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
        saveGame.putBoolean("splitPopup", splitCellPopup.wasShown());

        //Writes to the file.
        saveGame.flush();
    }

    /**
     * loadState
     * <p>
     * loads the state of the game. If a field is included in saveState, but
     * not in loadState the current/default value is kept.
     * <p>
     * If we load something, that does not exist, the default values of provider
     * will be given (0, null, etc...);
     */
    public void loadState() {
        if (isSaveFileEmpty()) {
            return;
        }
        //cell state
        cell.setCellHealth(saveGame.getInteger("cellHealth", 100));
        cell.setCellATP(saveGame.getInteger("cellATP", 30));
        cell.setCellSize(saveGame.getFloat("cellSize", 100));
        cell.setHasSplit(saveGame.getBoolean("cellSplit", false));


        //size
        cell.setHasSmallSizeUpgrade(saveGame.getBoolean("smallSize", false));
        cell.setHasMediumSizeUpgrade(saveGame.getBoolean("mediumSize", false));
        cell.setHasLargeSizeUpgrade(saveGame.getBoolean("largeSize", false));
        cell.setHasMassiveSizeUpgrade(saveGame.getBoolean("massiveSize", false));

        //organelles
        cell.setHasMitochondria(saveGame.getBoolean("mito", false));
        cell.setHasRibosomes(saveGame.getBoolean("ribo", false));
        cell.setHasFlagellum(saveGame.getBoolean("flag", false));
        cell.setFlagellumThickness(saveGame.getFloat("flagThick", 9.375f));
        cell.setFlagellumLength(saveGame.getInteger("flagLength", 225));
        cell.setHasNucleus(saveGame.getBoolean("nuke", false));
        cell.setOrganelleUpgradeLevel(saveGame.getInteger("organelleLevel", 0));

        //stats state
        stats.atpGenerated = saveGame.getInteger("atpGen", 0);
        stats.glucoseCollected = saveGame.getInteger("glukeCollected", 0);
        stats.distanceMoved = saveGame.getFloat("distanceMoved", 0);
        stats.gameTimer = saveGame.getFloat("time", 0);

        //Pop-up States
        glucoseCollisionPopup.setWasShown(saveGame.getBoolean("glukePopup", false));
        acidZonePopup.setWasShown(saveGame.getBoolean("acidPopup", false));
        basicZonePopup.setWasShown(saveGame.getBoolean("basicPopup", false));
        healAvailablePopup.setWasShown(saveGame.getBoolean("healPopup", false));
        cellMembranePopup.setWasShown(saveGame.getBoolean("membranePopup", false));
        splitCellPopup.setWasShown(saveGame.getBoolean("splitPopup", false));

    }

    /**
     * Checks if the save file is empty.
     *
     * @return True if empty.
     */
    public static boolean isSaveFileEmpty() {
        if (Gdx.app == null) {
            return true; // assume no save in test mode
        }

        try {
            if (saveGame == null) {
                saveGame = Gdx.app.getPreferences("saveGame");
            }
            return saveGame.get().isEmpty();
        } catch (Exception e) {
            // Log or silently ignore if preferences fail during testing
            System.err.println("[GameLoaderSaver] Skipping save check (testing mode or failure): " + e);
            return true; // assume no save in test mode
        }
    }

    /**
     * Erases the current save file.
     */
    public static void clearSaveFile() {
        // Do nothing if Gdx.app is null or not fully initialized
        // This is important for unit tests or if Gdx.app is not available
        if (Gdx.app == null) {
            return;
        }

        try {
            if (saveGame == null) {
                saveGame = Gdx.app.getPreferences("saveGame");
            }
            saveGame.get().clear();
            saveGame.clear();
            saveGame.flush();
        } catch (Exception e) {
            // Log or silently ignore if preferences fail during testing
            System.err.println("[GameLoaderSaver] Skipping save clear (testing mode or failure): " + e);
        }
    }

    /**
     * Update
     * <p>
     * Used for autosaving the game.
     */
    public void update() {
        if (cell.hasSplit() && !hasAutoSaved) {
            saveState();
            hasAutoSaved = true;
        }
    }

    /**
     * Preference Injector
     * <p>
     * Allows changing the preference object so that tests can be
     * run with a fake preferences object to check logic.
     *
     * @param fakePreferences The fake object.
     */
    public void injectFakePreferences(Preferences fakePreferences) {
        saveGame = fakePreferences;
    }
}
