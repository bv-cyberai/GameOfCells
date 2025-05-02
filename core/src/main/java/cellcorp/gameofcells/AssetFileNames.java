package cellcorp.gameofcells;

/**
 * Configuration class that holds file names for use with {@link com.badlogic.gdx.assets.AssetManager}.
 * Fields ending in `_NAME` are filenames that should be loadable as-is by the asset manager.
 */
public class AssetFileNames {
    /**
     * The file name for our cell texture.
     */
    public static String CELL = "cell_no_membrane_bust_no_white_edge_cropped.png";

    public static String CELL_MEMBRANE = "cell_membrane_bust_no_white_edge_cropped.png";

    public static String CELL_MEMBRANE_DAMAGED = "cell_membrane_damaged.png";

    /**
     * The file name for our hud texture for the escape button
     */
    public static String ESC_KEY = "esc_key_2px_cropped.png";
    /*
     * The file name of our hud texture for the movement controls
     */
    public static String MOVE_KEY = "key_start_2_stroke_cropped.png";
    /*
     * The file name of our hud texture for the pause button
     */
    public static String PAUSE_BUTTON = "p_key_2px_cropped.png";
    /*
     * The file name of our hud texture for the quit button
     */
    public static String QUIT_BUTTON = "esc_key_2px_cropped.png";
    /*
     * The file name of our hud texture for the shop button
     */
    public static String SHOP_BUTTON = "q_key_2px_cropped.png";
    /**
     * The file name for our glucose texture.
     */
    public static String GLUCOSE = "glucose_orange.png";

    /**
     * The file name for our danger texture.
     */
    public static String DANGER = "danger.png";

    /**
     * The file name of the default LibGDX font.
     * Loading this font is equivalent to constructing `new BitmapFont()`,
     * but won't crash test code.
     */
    public static String DEFAULT_FONT = "com/badlogic/gdx/utils/lsans-15.fnt";

    /**
     * The file name for the arrow to basic zone texture.
     */
    public static String ARROW_TO_BASIC_ZONE = "arrow_to_basic_zone.png";

    /**
     * The file name for the deeper, blurred background texture
     */
    public static String PARALLAX_FAR = "parallax_far.png";

    /**
     * The file name for the sharper mid-depth layer background texture
     */
    public static String PARALLAX_MID = "parallax_mid.png";

    /**
     * The file name for the closest layer background texture
     */
    public static String PARALLAX_NEAR = "parallax_near.png";

    /**
     * The file name for the transparent floating texture.
     */
    public static String FLOATING_OVERLAY = "transparent_floating_texture.png";

    /**
     * The file name for the vignette texture.
     */
    public static String VIGNETTE_LOW_HEALTH = "vignette_low_atp.png";

    /**
     * The file name for the mitochondria texture.
     */
    public static String MITOCHONDRIA_ICON = "mitochondria_real.png";

    /**
     * The file name for the ribosome texture.
     */
    public static String RIBOSOME_ICON = "ribosomes_real.png";

    /**
     * The file name for the nucleus texture.
     */
    public static String NUCLEUS_ICON = "nucleus_real.png";

    /**
     * The file name for the lock texture.
     */
    public static String LOCK_ICON = "lock.png";

    /**
     * The file name of the font used in the HUD
     */
    public static String HUD_FONT = "rubik.fnt";

    /**
     * The file name of the font used in the HUD.
     * This is needed due to the way bitmap fonts work.
     */
    public static String HUD_FONT_YELLOW = "rubik_yellow.fnt";

    /**
     * The file name for our background particle texture.
     */
    public static String WHITE_PIXEL = "white_pixel.png";

    /**
     * The file name for the acid zone gradient.
     */
    public static String ACID_ZONE = "acid_zone.png";
    /**
     * The file name for the basic zone gradient.
     */
    public static String BASIC_ZONE = "basic_zone.png";

    /**
     * The file name for our config file.
     */

    public static String USER_CONFIG = "config.txt";

    /**
     * File that is read to test parsing sense user_config can
     * change.
     */
    public static String TEST_CONFIG = "test_config.txt";

    /**
     * The file name for the WASD and arrow keys icon.
     */
    public static String WASD_ARROWS_ICON = "wasd.png";

    /**
     * The file name for the space and enter keys icon.
     */
    public static String SPACE_ENTER_ICON = "space_enter_stroke_1.png";

    public static String HEAL_ICON = "H_KEY_2px.png";

    public static String SCROLL_BAR = "scroll_bar.png";

    public static String WS_KEYS = "ws.png";
}
