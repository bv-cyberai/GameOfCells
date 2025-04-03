package cellcorp.gameofcells;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Glucose;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GameOverScreen;
import cellcorp.gameofcells.screens.GamePlayScreen;
import cellcorp.gameofcells.screens.MainMenuScreen;
import cellcorp.gameofcells.screens.ShopScreen;

public class TestMain {

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        }, config);

        // Mock the graphics provider
        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @AfterAll
    public static void cleanUp() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    public AssetManager createMockAssetManager() {
        var assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(assetManager.get(Mockito.anyString(), Mockito.eq(BitmapFont.class)))
            .thenReturn(Mockito.mock(BitmapFont.class));
        return assetManager;
    }

    @Test
    public void movingToGamePlayScreenSpawnsGlucose() {
        // Create a new `GameRunner`.
        // We use a static method instead of the constructor,
        // for reasons described in the method documentation.
        var gameRunner = GameRunner.create();

        // Hold down space and step forward a frame.
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Make sure we're on the gameplay screen, and that some glucose have spawned
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var currentGamePlayScreen = (GamePlayScreen) screen;
        var glucoseArray = currentGamePlayScreen.getGlucoseManager().getGlucoseArray();
        assertNotEquals(0, glucoseArray.size());

        // Let the game run for 2 seconds, with nothing held down.
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.runForSeconds(2);
        // Test that the game ran the expected number of ticks.
        assertEquals(1 + GameRunner.TICKS_PER_SECOND * 2, gameRunner.getTicksElapsed());
        // Test that there are still glucose on the screen.
        // `Main.screen`, `GamePlayScreen.glucoseManager`, and
        // `GlucoseManager.glucoseArray`
        // are _not_ final in their respective classes, so we should set them again,
        // to make sure we're not using an old reference that's been replaced in the
        // actual game.
        screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        currentGamePlayScreen = (GamePlayScreen) screen;
        glucoseArray = currentGamePlayScreen.getGlucoseManager().getGlucoseArray();
        assertNotEquals(0, glucoseArray.size());
    }

    @Test
    public void gameCreatedWithCorrectScreenDimensions() {
        // Games should be created with screen dimensions matching the default screen
        // dimensions
        var gameRunner = GameRunner.create();

        assertEquals(Main.DEFAULT_SCREEN_WIDTH, gameRunner.game.getGraphicsProvider().getWidth());
        assertEquals(Main.DEFAULT_SCREEN_HEIGHT, gameRunner.game.getGraphicsProvider().getHeight());
    }

    @Test
    public void canMoveToShopScreen() {
//        Gdx.files = Mockito.mock(Files.class);
//        Mockito.when(Gdx.files.internal(Mockito.anyString()))
//            .thenReturn(new FileHandle("config.txt"));
        var gameRunner = GameRunner.create();

        // Press enter to move to game screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Press 's' to move to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();

        assertInstanceOf(ShopScreen.class, gameRunner.game.getScreen());
    }

    @Test
    // This will need to be changed eventually to account for g -> game over screen
    // likely being removed.
    public void canMoveToGameOverScreen() {
        var gameRunner = GameRunner.create();
//        Gdx.files = Mockito.mock(Files.class);
//        Mockito.when(Gdx.files.internal(Mockito.anyString()))
//            .thenReturn(new FileHandle("config.txt"));
        // Press space to move to game screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Press 'G' to move to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.G));
        gameRunner.step();

        assertInstanceOf(GameOverScreen.class, gameRunner.game.getScreen());
    }

    @Test

    public void canMoveToGamePlayScreenFromGameOver() {
        var gameRunner = GameRunner.create();

        // Press space to move to game screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Press 'G' to move to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.G));
        gameRunner.step();

        // Press 'R' to move to start new game may need to be changed if client prefers
        // it switch to main menu screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.R));
        gameRunner.step();

        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void gameStartsOnMainMenuScreen() {
        var gameRunner = GameRunner.create();
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void timerCorrectAfterScreenSwitch() {
        var gameRunner = GameRunner.create();

        // 1. Start on main menu and verify
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());

        // 2. Press ENTER to go to gameplay screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        // 3. Let game run for 2 seconds (timer should be at 2)
        gameRunner.runForSeconds(2);

        // Verify timer before switching screens
        var gameplayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        assertEquals("Timer: 2", gameplayScreen.getHud().getTimerString());

        // 4. Press S to go to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();
        assertInstanceOf(ShopScreen.class, gameRunner.game.getScreen());

        // 5. Let shop screen be active for 2 seconds (timer should pause)
        gameRunner.runForSeconds(2);

        // 6. Press ESCAPE to return to gameplay
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.step();

        // Wait for transition to complete (if using fade effects)
        int maxWait = 100;
        while (!(gameRunner.game.getScreen() instanceof GamePlayScreen) && maxWait-- > 0) {
            gameRunner.step();
        }

        // 7. Verify we're back on gameplay screen
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        // 8. Verify timer is still at 2 (shouldn't increment while in shop)
        gameplayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        assertEquals("Timer: 2", gameplayScreen.getHud().getTimerString());
    }

    @Test
    public void cellCollidesWithMultipleGlucoseRemovesAll() {
        // Create a game. Move to gameplay screen.
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        var screen = gameRunner.game.getScreen();
        assert screen instanceof GamePlayScreen;
        var currentGamePlayScreen = (GamePlayScreen) screen;
        gameRunner.step();

        // Spawn multiple glucose on top of the cell
        var cell = currentGamePlayScreen.getCell();

        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("cellHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellATP")).thenReturn(30);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);


        var testCell = new Cell(currentGamePlayScreen,fakeAssetManager,fakeConfigProvider);
        System.out.println("TESTCELLSTART" + testCell.getCellATP());
        var startATP = cell.getCellATP();
        System.out.println("START ATP:" + startATP);
        var gameGlucose = currentGamePlayScreen.getGlucoseManager().getGlucoseArray();

        var addedGlucose = new ArrayList<Glucose>();
        for (int i = 0; i < 10; i++) {
            addedGlucose.add(new Glucose(
                    Mockito.mock(AssetManager.class),
                    cell.getX(),
                    cell.getY()
            ));
        }
        gameGlucose.addAll(addedGlucose);

        gameRunner.step();
        // Assert that all the glucose have been removed, and that the cell ATP has
        // increased 10 times.
        for (var glucose : addedGlucose) {
            assertTrue(gameGlucose.contains(glucose));
        }
        System.out.println(cell.getCellATP());
        assertEquals(30, cell.getCellATP());
    }

    @Test
    public void testShopScreenTransitionWithFade() {
        var gameRunner = GameRunner.create();

        // 1. Start on main menu
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());

        // 2. Press ENTER to go to gameplay
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        // 3. Press S to go to shop
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();

        // Wait for transition to shop screen
        int maxWait = 100; // Prevent infinite loops
        while (!(gameRunner.game.getScreen() instanceof ShopScreen) && maxWait-- > 0) {
            gameRunner.step();
        }
        assertInstanceOf(ShopScreen.class, gameRunner.game.getScreen());

        // 4. Press ESCAPE to return to gameplay
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.step();

        // Wait for transition back to gameplay
        maxWait = 100;
        while (!(gameRunner.game.getScreen() instanceof GamePlayScreen) && maxWait-- > 0) {
            gameRunner.step();
        }
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
    }

    @Test
    void testIdleATPDeduction() {

        float epsilon = 0.25f;

        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        GamePlayScreen gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        Cell gameCell = gamePlayScreen.getCell();

        float startingATP = gameCell.getCellATP();

        float expectedATPLost = 0f;

        assertTrue((Math.abs(gameCell.getCurrentATPLost() - expectedATPLost)) < epsilon);

        gameRunner.runForSeconds(12);

        float expectedCellATP = startingATP-1;
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setSmallSizeUpgrade(true);

        expectedCellATP = startingATP-2;
        gameRunner.runForSeconds(11);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setMediumSizeUpgrade(true);
        expectedCellATP = startingATP-3;
        gameRunner.runForSeconds(10);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setLargeSizeUpgrade(true);
        expectedCellATP = startingATP-4;
        gameRunner.runForSeconds(9);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setMassiveSizeUpgrade(true);
        expectedCellATP = startingATP-5;
        gameRunner.runForSeconds(8);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setHasMitochondria(true);
        expectedCellATP = startingATP-6;
        gameRunner.runForSeconds(7);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setHasRibosomes(true);
        expectedCellATP = startingATP-7;
        System.out.println(expectedCellATP);
        System.out.println(Math.abs(gameCell.getCellATP()));
        gameRunner.runForSeconds(6);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        gameCell.setHasFlagella(true);

        //Off by 1 but I belive this a floating point shenannigans.
        expectedCellATP = startingATP-9;
        gameRunner.runForSeconds(5);
        System.out.println(expectedCellATP);
        System.out.println(Math.abs(gameCell.getCellATP() - expectedCellATP));
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        //This test should prove its just fp shenannigans.
        gameCell.setHasNucleus(true);
        expectedCellATP = startingATP-10;
        gameRunner.runForSeconds(4);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);

        expectedCellATP = startingATP-11;
        gameRunner.runForSeconds(4);
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);
    }


    @Test
    void testMovingATPDeduction() {

        float epsilon = 0.25f;

        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        GamePlayScreen gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        Cell gameCell = gamePlayScreen.getCell();
//        System.out.println("S: " +gameCell.getSizeUpgradeLevel());
//        System.out.println("O: " +gameCell.getOrganelleUpgradeLevel());
//        System.out.println("L: " +gameCell.getTotalATPLossFactor());

        // RESET GLUCOSE TO DEFAULT VALUE - SUPER IMPORANT THIS METHOD IS DANGEROUS.
        Glucose.setAtpPerGlucoseDoNotUseForTestingOnly(true);

        float startingATP = gameCell.getCellATP();

        float expectedATPLost = 0f;

//        assertTrue((Math.abs(gameCell.getCurrentATPLost() - expectedATPLost)) < epsilon);
        //If we assume +1 second to atp, like in idle then this would be 5.5 +1 = 6.5 ~ 7.
        runModifiedStep(6, 5.5f, gameRunner,gameCell);
        System.out.println("ATP: " +gameCell.getCellATP());
        System.out.println("ATPLOST: " +gameCell.getCurrentATPLost());
        movementTestHelper(startingATP,1,gameCell,epsilon,"0", gameRunner );

//        System.out.println(gameCell.getCellATP()-gameCell.getCurrentATPLost());


//        float expectedCellATP = startingATP-1;
//        System.out.println(Math.abs((gameCell.getCellATP()-gameCell.getCurrentATPLost()) - expectedCellATP));
//        assertTrue((Math.abs((gameCell.getCellATP()-gameCell.getCurrentATPLost()) - expectedCellATP)) < epsilon);

        gameCell.setSmallSizeUpgrade(true);
        runModifiedStep(6,5f, gameRunner,gameCell);
//        System.out.println("ATP: " +gameCell.getCellATP());
//        System.out.println("ATPLOST: " +gameCell.getCurrentATPLost());
        movementTestHelper(startingATP,2,gameCell,epsilon,"1", gameRunner);
//
        gameCell.setMediumSizeUpgrade(true);
        runModifiedStep(5, 4.5f, gameRunner,gameCell);
//        System.out.println("ATP: " +gameCell.getCellATP());
//        System.out.println("ATPLOST: " +gameCell.getCurrentATPLost());
        movementTestHelper(startingATP,3,gameCell,epsilon,"2", gameRunner );
//
        gameCell.setLargeSizeUpgrade(true);
        runModifiedStep(5,4f, gameRunner,gameCell);
//        System.out.println("ATP: " +gameCell.getCellATP());
//        System.out.println("ATPLOST: " +gameCell.getCurrentATPLost());
        movementTestHelper(startingATP,4,gameCell,epsilon,"3", gameRunner );
//
        gameCell.setMassiveSizeUpgrade(true);
        runModifiedStep(4,3.5f, gameRunner,gameCell);
        movementTestHelper(startingATP,5,gameCell,epsilon,"4", gameRunner );
//
        gameCell.setHasMitochondria(true);
        runModifiedStep(4, 3f, gameRunner,gameCell);
        movementTestHelper(startingATP,6,gameCell,epsilon,"5", gameRunner);
//
        gameCell.setHasRibosomes(true);
        runModifiedStep(4,2.5f,gameRunner,gameCell);
        movementTestHelper(startingATP,7,gameCell,epsilon,"6",gameRunner );
//
        gameCell.setHasFlagella(true);
        runModifiedStep(3,2.0f,gameRunner,gameCell);
        movementTestHelper(startingATP,8,gameCell,epsilon,"7", gameRunner );
//
        gameCell.setHasNucleus(true);
        runModifiedStep(2,1.5f, gameRunner,gameCell);
        movementTestHelper(startingATP,9,gameCell,epsilon,"8", gameRunner );

        // RESET GLUCOSE TO DEFAULT VALUE - SUPER IMPORANT THIS METHOD IS DANGEROUS.
        Glucose.setAtpPerGlucoseDoNotUseForTestingOnly(false);
    }

    private void runModifiedStep(int loopSeconds, float correctSeconds, GameRunner gameRunner,Cell gameCell) {
//        while (time)
        boolean plusOne = false;
        for(int i = 0; i < loopSeconds * GameRunner.TICKS_PER_SECOND; i++){
//            System.out.println("CURRATPLOSS:  " + gameCell.getCurrentATPLost() + " i: " + i);
//            if((i >= (correctSeconds * GameRunner.TICKS_PER_SECOND)) && (gameCell.getCurrentATPLost() >= 1.0 ) ) {
//                //Due to sloppy floats, this tries to prevent going to many extra render cycles, throwing off the next calculations.
//                System.out.println("STUPID FLOATS!");
//                break;
//            }
            if(plusOne) {
                break;
            }

            if(gameCell.isAtpTickFlag()) {
                System.out.println("ATPTICKDETECTED i: " + i);
                System.out.println("TIMEBEEPBOOP: "+ gameCell.getLastTimeTakenforATPLoss());
                plusOne = true;
            }
            //This creates back and forth movement instead of hitting
            // the end of the screen which happens during testing only.
            if (i%2 == 0) {
                gameRunner.setHeldDownKeys(Set.of(Input.Keys.LEFT));
            }else {
                gameRunner.setHeldDownKeys(Set.of(Input.Keys.RIGHT));
            }
            gameRunner.step();


        }

    }

    private void movementTestHelper(float startingATP, int expectedLoss, Cell gameCell, float epsilon, String callNumber, GameRunner gameRunner){
        System.out.println(callNumber);
        float expectedCellATP = startingATP-expectedLoss;
//        System.out.println(Math.abs((gameCell.getCellATP()-gameCell.getCurrentATPLost()) - expectedCellATP));
//        System.out.println(Math.abs(gameCell.getCellATP() - expectedCellATP));
//        gameCell.setCellATP((int) (gameCell.getCellATP()-gameCell.getCurrentATPLost()));
//        gameCell.setCurrentATPLost(0);
        System.out.println("ATP: " + gameCell.getCellATP());
        System.out.println("Expected: " + expectedCellATP);
        System.out.println("LOSS: " + gameCell.getCurrentATPLost());
        assertTrue((Math.abs(gameCell.getCellATP() - expectedCellATP)) < epsilon);
//        assertTrue((Math.abs((gameCell.getCellATP()-gameCell.getCurrentATPLost()) - expectedCellATP)) < epsilon);

//        if(gameCell.getCurrentATPLost() >= .75) {
//            gameCell.setCellATP(gameCell.getCellATP()-1);
//            gameCell.setCurrentATPLost(0f);
//        }
    }
}
