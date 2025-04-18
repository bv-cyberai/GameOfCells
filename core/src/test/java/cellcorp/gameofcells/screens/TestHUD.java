package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.notification.NotificationManager;
import cellcorp.gameofcells.notification.NotificationSource;
import cellcorp.gameofcells.objects.Stats;
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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class TestHUD {

    private HUD hud;
    private AssetManager mockAssetManager;
    private GraphicsProvider mockGraphicsProvider;
    private GamePlayScreen mockGamePlayScreen;
    private Stats mockStats;
    private SpriteBatch fakeBatch;
    private Texture fakeTexture;
    private BitmapFont font;

    @BeforeAll
    public static void initLibGDX() {
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

        Gdx.graphics = mock(Graphics.class);
        when(Gdx.graphics.getWidth()).thenReturn(1920);
        when(Gdx.graphics.getHeight()).thenReturn(1080);

        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = (GL20) Gdx.gl;
        Gdx.gl30 = mock(GL30.class);
    }

    @BeforeEach
    public void setup() {
        mockAssetManager = mock(AssetManager.class);
        mockGraphicsProvider = mock(GraphicsProvider.class);
        mockGamePlayScreen = mock(GamePlayScreen.class);
        mockStats = mock(Stats.class);

        font = new BitmapFont(); // real font to avoid Label NPE
        when(mockAssetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class)).thenReturn(font);

        when(mockGraphicsProvider.createLabel(any(CharSequence.class), any(Label.LabelStyle.class)))
            .thenAnswer(invocation -> {
                CharSequence text = invocation.getArgument(0);
                Label.LabelStyle style = invocation.getArgument(1);
                Label label = new Label(text, style);
                label.setFontScale(0.2f); // mimic the game’s scaling
                return label;
            });

        SpriteBatch fakeBatch = mock(SpriteBatch.class);
        Texture fakeTexture = mock(Texture.class);
        when(mockGraphicsProvider.createSpriteBatch()).thenReturn(fakeBatch);
        when(mockGraphicsProvider.createRoundedRectangleTexture(anyInt(), anyInt(), any(), anyFloat()))
            .thenReturn(fakeTexture);

        OrthographicCamera camera = new OrthographicCamera();
        camera.update();
        FitViewport viewport = new FitViewport(1920, 1080, camera);
        when(mockGraphicsProvider.createFitViewport(anyFloat(), anyFloat())).thenReturn(viewport);

        when(mockGamePlayScreen.getCell()).thenReturn(mock(cellcorp.gameofcells.objects.Cell.class));
        when(mockGamePlayScreen.getZoneManager()).thenReturn(mock(cellcorp.gameofcells.objects.ZoneManager.class));

        hud = new HUD(mockGraphicsProvider, mockAssetManager, mockGamePlayScreen, mockStats);
    }


    @AfterEach
    public void tearDown() {
        if (hud != null) hud.dispose();
    }

    @Test
    public void testHUDConstructionDoesNotCrash() {
        assertNotNull(hud);
    }

    @Test
    public void testHUDQueuePopupStoresCorrectData() {
        SpriteBatch batch = mock(SpriteBatch.class);
        hud.queuePopup("Test Popup", 100, 200, 300, 150, Color.ORANGE);
        assertDoesNotThrow(() -> hud.drawPopup(batch));
    }

    @Test
    public void testHUDGetNotificationManager() {
        assertNotNull(hud.getNotificationManager());
    }

    @Test
    public void testDisposeDoesNotThrow() {
        assertDoesNotThrow(() -> hud.dispose());
    }
}
