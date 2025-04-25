package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestMenuSystem {

    private MenuSystem menuSystem;
    private Stage mockStage;
    private AssetManager mockAssetManager;
    private GraphicsProvider mockGraphicsProvider;
    private BitmapFont mockFont;
    private Label mockLabel;

    @BeforeAll
    public static void setUpHeadlessApp() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");

        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        }, config);

        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(1280);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(800);

        GL20 gl20 = Mockito.mock(GL20.class);
        GL30 gl30 = Mockito.mock(GL30.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
        Gdx.gl30 = gl30;
    }

    @AfterAll
    public static void tearDown() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    @BeforeEach
    public void setup() {
        mockStage = mock(Stage.class);
        mockAssetManager = mock(AssetManager.class);
        mockGraphicsProvider = mock(GraphicsProvider.class);
        mockFont = new BitmapFont(); // Use a real BitmapFont for testing

        // Return real Labels to avoid StackOverflow
        when(mockGraphicsProvider.createLabel(any(CharSequence.class), any(Label.LabelStyle.class)))
            .thenAnswer(invocation -> {
                CharSequence text = invocation.getArgument(0);
                Label.LabelStyle style = invocation.getArgument(1);
                Label label = new Label(text, style);
                label.setFontScale(0.2f); // Set a default font scale
                return label;
            });
        
        Array<Actor> fakeActors = new Array<>();
        Table fakeTable = new Table();
        fakeActors.add(fakeTable);
        when(mockStage.getActors()).thenReturn(fakeActors);

        Image mockImage = mock(Image.class);
        when(mockGraphicsProvider.createImage(any(Texture.class))).thenReturn(mockImage);

        when(mockAssetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class)).thenReturn(mockFont);

        menuSystem = new MenuSystem(mockStage, mockAssetManager, mockGraphicsProvider);
    }


    @Test
    public void testInitializeBasicMenu() {
        menuSystem.initialize("Title", new String[]{"Option 1", "Option 2"}, "Press Enter");
        assertEquals(0, menuSystem.getSelectedOptionIndex());
        assertEquals(2, menuSystem.getMenuOptionCount());
        assertNotNull(menuSystem.getStage());
    }

    @Test
    public void testUpdateSelectionOutOfBounds() {
        menuSystem.initialize("Title", new String[]{"Only Option"}, null);
        menuSystem.updateSelection(-1); // should be ignored
        assertEquals(0, menuSystem.getSelectedOptionIndex());
        menuSystem.updateSelection(5);  // should be ignored
        assertEquals(0, menuSystem.getSelectedOptionIndex());
    }

    @Test
    public void testClearDoesNotThrow() {
        menuSystem.initialize("Title", new String[]{"Option A"}, null);
        assertDoesNotThrow(() -> menuSystem.clear());
    }

    @Test
    public void testInitializeMainMenuDoesNotThrow() {
        Texture mockTex1 = mock(Texture.class);
        Texture mockTex2 = mock(Texture.class);
        assertDoesNotThrow(() ->
            menuSystem.initializeMainMenu("Main", new String[]{"Start", "Quit"}, "Keys", mockTex1, mockTex2));
    }

    @Test
    public void testInitializeSplitLayoutDoesNotThrow() {
        assertDoesNotThrow(() -> menuSystem.initializeSplitLayout("Info", new String[]{"A", "B"}, new String[]{"C", "D"}, "ESC to return"));
    }

    @Test
    public void testInitializePauseMenuDoesNotThrow() {
        assertDoesNotThrow(() -> menuSystem.initializePauseMenu("Pause", new String[]{"Resume", "Exit"}, "Press SPACE"));
    }

    @Test
    public void testInitializeShopLayoutReturnsColumns() {
        Table[] columns = menuSystem.initializeShopLayout("Shop", "Buy upgrades");
        assertNotNull(columns);
        assertEquals(3, columns.length);
        for (Table t : columns) assertNotNull(t);
    }
}
