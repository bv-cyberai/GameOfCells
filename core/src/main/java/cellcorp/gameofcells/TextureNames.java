package cellcorp.gameofcells;

/**
 * Configuration class that holds file names for use with {@link com.badlogic.gdx.assets.AssetManager}.
 * Fields ending in `_NAME` are filenames that should be loadable as-is by the asset manager.
 */
public class TextureNames {
    public static String START_BACKGROUND = "startBackground.png";

    public static String GAME_BACKGROUND = "gameBackground.png";

    public static String SHOP_BACKGROUND = "shopBackground.png";

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
