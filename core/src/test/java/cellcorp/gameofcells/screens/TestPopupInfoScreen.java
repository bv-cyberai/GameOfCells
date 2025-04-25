package cellcorp.gameofcells.screens;

import static org.junit.jupiter.api.Assertions.*;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.runner.GameRunner;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TestPopupInfoScreen {

    private PopupInfoScreen popup;
    private AssetManager assetManager;
    private GameRunner gameRunner;
    private BitmapFont font;

    @BeforeAll
    public static void setUpLibGDX() {
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
    public static void cleanUp() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    @BeforeEach
    public void setup() {
        gameRunner = GameRunner.create();
        assetManager = Mockito.mock(AssetManager.class);

        font = Mockito.mock(BitmapFont.class);
        BitmapFont.BitmapFontData fontData = Mockito.mock(BitmapFont.BitmapFontData.class);
        Mockito.when(font.getData()).thenReturn(fontData);
        Mockito.when(font.getColor()).thenReturn(Color.WHITE);

        Mockito.when(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class)).thenReturn(font);

        popup = new PopupInfoScreen(
                gameRunner.game.getGraphicsProvider(),
                assetManager,
                gameRunner.configProvider,
                gameRunner.inputProvider,
                gameRunner.game.getGraphicsProvider().createFitViewport(1280, 800),
                Mockito.mock(HUD.class),
                () -> {}
        );
    }

    @Test
    public void testPopupVisibilityToggles() {
        popup.setVisible(true);
        assertTrue(popup.isVisible());

        popup.setVisible(false);
        assertFalse(popup.isVisible());
    }

    @Test
    public void testShowPopupGlucose() {
        popup.show(PopupInfoScreen.Type.glucose);
        assertTrue(popup.isVisible());
    }

    @Test
    public void testShowPopupDanger() {
        popup.show(PopupInfoScreen.Type.danger);
        assertTrue(popup.isVisible());
    }

    @Test
    public void testShowPopupBasic() {
        popup.show(PopupInfoScreen.Type.basic);
        assertTrue(popup.isVisible());
    }

    @Test
    public void testPopupClosesOnKeyPress() throws Exception {
        popup.show(PopupInfoScreen.Type.glucose);
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        overrideStageWithMock(popup);
        popup.render(0.1f);        
        assertFalse(popup.isVisible());
    }

    @Test
    public void testPopupFlagsToggle() {
        popup.setHasShownGlucosePopup(true);
        assertTrue(popup.hasShownGlucosePopup());

        popup.setHasShownAcidZonePopup(true);
        assertTrue(popup.hasShownAcidZonePopup());

        popup.setHasShownBasicZonePopup(true);
        assertTrue(popup.hasShownBasicZonePopup());

        popup.setHasShownHealAvailablePopup(true);
        assertTrue(popup.hasShownHealAvailablePopup());
    }

    @Test
    public void testPopupDisposeDoesNotThrow() {
        assertDoesNotThrow(() -> popup.dispose());
    }

    @Test
    public void testResizeDoesNotThrow() {
        assertDoesNotThrow(() -> popup.resize(1024, 768));
    }

    @Test
    public void testPopupShowMethodDoesNotThrow() {
        assertDoesNotThrow(() -> popup.show());
    }

    // This is needed for mocking the stage in the PopupInfoScreen class
    // to avoid the actual rendering of the stage in the test environment.
    private void overrideStageWithMock(PopupInfoScreen popup) throws Exception {
        Stage mockStage = Mockito.mock(Stage.class);
        var field = PopupInfoScreen.class.getDeclaredField("stage");
        field.setAccessible(true);
        field.set(popup, mockStage);
    }
}
