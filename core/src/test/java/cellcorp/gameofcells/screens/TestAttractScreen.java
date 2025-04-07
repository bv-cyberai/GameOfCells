// package cellcorp.gameofcells.screens;

// import static org.junit.jupiter.api.Assertions.*;

// import java.util.Set;

// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;

// import com.badlogic.gdx.ApplicationListener;
// import com.badlogic.gdx.Game;
// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.Graphics;
// import com.badlogic.gdx.Input;
// import com.badlogic.gdx.backends.headless.HeadlessApplication;
// import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
// import com.badlogic.gdx.graphics.GL20;

// import cellcorp.gameofcells.Main;
// import cellcorp.gameofcells.runner.GameRunner;

// import org.mockito.Mockito;

// public class TestAttractScreen {

//     @BeforeAll
//     public static void setUpLibGDX() {
//         System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
//         // Initialize headless LibGDX
//         HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
//         new HeadlessApplication(new ApplicationListener() {
//             @Override public void create() {}
//             @Override public void resize(int width, int height) {}
//             @Override public void render() {}
//             @Override public void pause() {}
//             @Override public void resume() {}
//             @Override public void dispose() {}
//         }, config);

//         // Mock the graphics provider
//         Gdx.graphics = Mockito.mock(Graphics.class);
//         Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
//         Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);

//         GL20 gl20 = Mockito.mock(GL20.class);
//         Gdx.gl = gl20;
//         Gdx.gl20 = gl20;
//     }

//     @AfterAll
//     public static void cleanUp() {
//         if (Gdx.app != null) {
//             Gdx.app.exit();
//         }
//     }

//     @Test
//     public void TestAttractScreenIntialState() {
//         GameRunner gameRunner = GameRunner.create();
//         gameRunner.runForSeconds(10f);
        
//         var attractScreen = gameRunner.game.getScreen();
//         assertTrue(attractScreen instanceof AttractScreen, "AttractScreen should be the current screen");
//     }

// }
