package cellcorp.gameofcells;

/**
 * Configuration class that holds file names for use with {@link com.badlogic.gdx.assets.AssetManager}.
 * Fields ending in `_NAME` are filenames that should be loadable as-is by the asset manager.
 */
public class AssetFileNames {
    /**
     * The file name for the start background.
     */
    public static String START_SCREEN_BACKGROUND = "startBackground.png";

    /**
     * The file name for the game background.
     */
    public static String GAME_BACKGROUND = "gameBackground.jpg";

    /**
     * The file name for the shop background.
     */
    public static String SHOP_BACKGROUND = "shopBackground.jpg";

    /**
     * The file name for our cell texture.
     */
    public static String CELL = "Cell.png";

    /**
     * The file name for our glucose texture.
     */
    public static String GLUCOSE = "glucose2.png";

    /**
     * The file name of the default LibGDX font.
     * Loading this font is equivalent to constructing `new BitmapFont()`,
     * but won't crash test code.
     */
    public static String DEFAULT_FONT = "com/badlogic/gdx/utils/lsans-15.fnt";
}
