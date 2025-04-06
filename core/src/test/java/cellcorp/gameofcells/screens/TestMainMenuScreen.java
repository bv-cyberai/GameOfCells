// package cellcorp.gameofcells.screens;

// import static org.junit.jupiter.api.Assertions.*;

// import java.util.Set;

// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

// import com.badlogic.gdx.ApplicationListener;
// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.Graphics;
// import com.badlogic.gdx.Application;
// import com.badlogic.gdx.Input;
// import com.badlogic.gdx.backends.headless.HeadlessApplication;
// import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
// import com.badlogic.gdx.graphics.GL20;

// import cellcorp.gameofcells.Main;
// import cellcorp.gameofcells.runner.GameRunner;

// public class TestMainMenuScreen {

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

//         // Mock Gdx.app for exit
//         Gdx.app = Mockito.mock(Application.class);
//         Mockito.doNothing().when(Gdx.app).exit();

//         GL20 gl20 = Mockito.mock(GL20.class);
//         Gdx.gl = gl20;
//         Gdx.gl20 = gl20;
//     }

//     @Test
//     public void testMainMenuScreenInitialState() {
//         // Create a game runner and get the main menu screen
//         GameRunner gameRunner = GameRunner.create();
//         var screen = gameRunner.game.getScreen();
//         assertInstanceOf(MainMenuScreen.class, screen);
        
//         MainMenuScreen mainMenuScreen = (MainMenuScreen) screen;
        
//         // Verify initial state
//         assertEquals(0, mainMenuScreen.getSelectedOption());
//         assertEquals(0f, mainMenuScreen.getInactivityTimer());
//         assertEquals(3, mainMenuScreen.getMenuOptionCount());
//         assertEquals(10f, mainMenuScreen.getInactivityTimeout());
//         assertNotNull(mainMenuScreen.getParticles());
//         assertNotNull(mainMenuScreen.getSpriteBatch());
//         assertNotNull(mainMenuScreen.getViewport());
//         assertNotNull(mainMenuScreen.getInputProvider());
//         assertNotNull(mainMenuScreen.getGame());
//         assertNotNull(mainMenuScreen.getAssetManager());
//     }

//     @Test
//     public void testMenuNavigation() {
//         GameRunner gameRunner = GameRunner.create();
//         MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();
        
//         // Initial selection should be 0 (Start Game)
//         assertEquals(0, mainMenuScreen.getSelectedOption());
        
//         // Press DOWN key - should move to option 1 (Settings)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(1, mainMenuScreen.getSelectedOption());
        
//         // Press DOWN key again - should move to option 2 (Exit)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(2, mainMenuScreen.getSelectedOption());
        
//         // Press DOWN key again - should wrap around to option 0 (Start Game)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(0, mainMenuScreen.getSelectedOption());
        
//         // Press UP key - should wrap around to option 2 (Exit)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(2, mainMenuScreen.getSelectedOption());
        
//         // Press UP key again - should move to option 1 (Settings)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(1, mainMenuScreen.getSelectedOption());
        
//         // Test alternate keys (W/S for UP/DOWN)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.W));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(0, mainMenuScreen.getSelectedOption());
        
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.S));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of()); // Release key
//         gameRunner.step();
//         assertEquals(1, mainMenuScreen.getSelectedOption());
//     }

//     @Test
//     public void testMainMenuSelection() {
//         GameRunner gameRunner = GameRunner.create();
//         MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();
        
//         // Test selecting "Start Game" (option 0)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
//         gameRunner.step();
//         assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
        
//         // Reset to main menu
//         gameRunner.game.setScreen(mainMenuScreen);
        
//         // Move to "Settings" (option 1)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of());
//         gameRunner.step();
        
//         // Test selecting "Settings"
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
//         gameRunner.step();
//         assertInstanceOf(SettingsScreen.class, gameRunner.game.getScreen());
        
//         // Reset to main menu - IMPORTANT: Create fresh instance
//         mainMenuScreen = new MainMenuScreen(
//             gameRunner.inputProvider,
//             gameRunner.game.getGraphicsProvider(),
//             gameRunner.game,
//             gameRunner.game.getAssetManager(),
//             gameRunner.game.getCamera(),
//             gameRunner.game.getViewport(),
//             gameRunner.configProvider
//         );
//         gameRunner.game.setScreen(mainMenuScreen);
        
//         // Move to "Exit" (option 2)
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of());
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
//         gameRunner.step();
//         gameRunner.setHeldDownKeys(Set.of());
//         gameRunner.step();
        
//         // Test exit - should remain on main menu screen
//         gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
//         gameRunner.step();
//         assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
//     }
// }