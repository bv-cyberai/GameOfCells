// package cellcorp.gameofcells.screens;

// import com.badlogic.gdx.Input;
// import com.badlogic.gdx.assets.AssetManager;
// import com.badlogic.gdx.graphics.Camera;
// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.utils.ScreenUtils;
// import com.badlogic.gdx.utils.viewport.Viewport;
// import com.badlogic.gdx.scenes.scene2d.Stage;

// import cellcorp.gameofcells.Main;
// import cellcorp.gameofcells.objects.Particles;
// import cellcorp.gameofcells.providers.GraphicsProvider;
// import cellcorp.gameofcells.providers.ConfigProvider;
// import cellcorp.gameofcells.providers.InputProvider;

// /**
//  * The screen for adjusting game settings and viewing game info/controls.
//  */
// public class SettingsScreen implements GameOfCellsScreen {

//     // Change as is most convenient.
//     /**
//      * Width of the HUD view rectangle.
//      * (the rectangular region of the world which the camera will display)
//      */
//     public static final int VIEW_RECT_WIDTH = 1200;
//     /**
//      * Height of the HUD view rectangle.
//      * (the rectangular region of the world which the camera will display)
//      */
//     public static final int VIEW_RECT_HEIGHT = 800;

//     // Settings options
//     private static final String[] SETTINGS_OPTIONS = { "Game Info & Controls", "Back" };
//     private static final String INSTRUCTIONS = "Use the arrow keys to navigate | Enter to select";

//     private int selectedOption = 0; // Index of the currently selected option

//     private final InputProvider inputProvider;
//     private final GraphicsProvider graphicsProvider;
//     private final ConfigProvider configProvider;
//     private final Main game;
//     private final AssetManager assetManager;
//     private final Viewport viewport;
//     private final Particles particles;
//     private final MenuSystem menuSystem;


//     public SettingsScreen(
//             InputProvider inputProvider,
//             GraphicsProvider graphicsProvider,
//             Main game,
//             AssetManager assetManager,
//             Camera camera,
//             Viewport viewport,
//             ConfigProvider configProvider) {

//         this.inputProvider = inputProvider;
//         this.graphicsProvider = graphicsProvider;
//         this.configProvider = configProvider;
//         this.game = game;
//         this.assetManager = assetManager;
//         this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);

//         // Load white pixel texture and initialize particles
//         this.particles = new Particles(graphicsProvider.createWhitePixelTexture());
//         this.menuSystem = new MenuSystem(
//             new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT)),
//             assetManager,
//             graphicsProvider
//         );
//     }

//     @Override
//     public void show() {
//         menuSystem.initialize("Settings", SETTINGS_OPTIONS, INSTRUCTIONS);
//     }

//     @Override
//     public void render(float delta) {
//         handleInput(delta);
//         update(delta);
//         draw();
//     }

//     @Override
//     public void resize(int width, int height) {
//         viewport.update(width, height, true);
//     }

//     @Override
//     public void pause() {
//     }

//     @Override
//     public void resume() {
//     }

//     @Override
//     public void hide() {
//     }

//     @Override
//     public void dispose() {
//         particles.dispose();
//         menuSystem.clear();
//     }

//     @Override
//     public void handleInput(float deltaTimeSeconds) {
//         // Navigate settings options with arrow keys
//         if (inputProvider.isKeyJustPressed(Input.Keys.UP)) {
//             menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() - 1);
//         }
//         if (inputProvider.isKeyJustPressed(Input.Keys.DOWN)) {
//             menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() + 1);
//         }

//         if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
//             game.setScreen(new MainMenuScreen(
//                     inputProvider,
//                     graphicsProvider,
//                     game,
//                     assetManager,
//                     viewport.getCamera(),
//                     viewport, configProvider));
//             return;
//         }

//         // Confirm selection with Enter key
//         if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
//             || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
//             switch (menuSystem.getSelectedOptionIndex()) {
//                 case 0: // Game Info & Controls
//                     game.setScreen(new GameInfoControlsScreen(
//                             inputProvider,
//                             graphicsProvider,
//                             game,
//                             assetManager,
//                             null,
//                             viewport,
//                             configProvider));
//                     break;
//                 case 1: // Back
//                     game.setScreen(new MainMenuScreen(
//                             inputProvider,
//                             graphicsProvider,
//                             game,
//                             assetManager,
//                             null,
//                             viewport, 
//                             configProvider));
//                     break;
//             }
//         }
//     }

//     @Override
//     public void update(float deltaTimeSeconds) {
//         particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
//     }

//     @Override
//     public void draw() {
//         // Clear the screen with a gradient background
//         ScreenUtils.clear(.157f, .115f, .181f, 1f); // Dark blue background
//         viewport.apply(true);

//         // Draw the particles
//         particles.draw(graphicsProvider.createSpriteBatch());

//         // Draw the menu system
//         menuSystem.getStage().act();
//         menuSystem.getStage().draw();
//     }

//     /**
//      * Get the index of the currently selected option.
//      * @return
//      */
//     public int getSelectedOption() {
//         return menuSystem.getSelectedOptionIndex();
//     }
// }
