# Game Of Cells

## ID Block

A game used to teach students about cells and cell functions.

- **Brendon Vineyard** / vineyabn207
- **Andrew Sennoga-Kimuli** / sennogat106
- **Mark Murphy** / murphyml207
- **Tim Davey** / daveytj206
- **Date:** - 02/27/2025
- **Course:** - CIS 405
- **Assignment:** - GameOfCells

## Problem

Use agile methods to develop 'Game Of Cells' as part of a software engineering team. *Game Of Cells* is a game used to
teach students about cells and cell functions.

## Directory

**Note:** This directory has been modified slightly to ignore files that are build-specific or temporary. For a full
directory, run the `tree` command in your favorite shell.

 ```
.
│   .gitignore
│   build.gradle
│   local_web_server.py
│   pull_request_template.md
│   README.md
│   settings.gradle
│   
├───assets
│       .gitkeep
│       acid_zone.png
│       acid_zone.svg
│       arrow_to_basic_zone.png
│       assets-Brendon-2.txt
│       assets-Brendon.txt
│       assets.txt
│       basic_zone.png
│       basic_zone.svg
│       Cell.png
│       cell_membrane.png
│       cell_membrane_bust_no_white_edge_cropped.png
│       cell_membrane_damaged.png
│       cell_no_membrane.png
│       cell_no_membrane_bust_no_white_edge_cropped.png
│       cell_real.png
│       cell_with_membrane.png
│       cell_w_organelles_cropped.png
│       config.txt
│       c_key.png
│       danger.png
│       esc_key_2px_cropped.png
│       gameBackground.jpg
│       gameBackground.png
│       gameBackground2.png
│       glucose2.png
│       glucose_orange.png
│       glucose_yellow.png
│       H_KEY_2px.png
│       i_key.png
│       keys_stroke_2.png
│       key_start_2_stroke_cropped.png
│       key_template.svg
│       lock.png
│       membrane.png
│       mitochondria_real.png
│       mod_cell.png
│       notification_font.fnt
│       notification_font1.png
│       notification_font2.png
│       nucleus_purple.png
│       nucleus_real.png
│       parallax_far.png
│       parallax_mid.png
│       parallax_near.png
│       p_key_2px_cropped.png
│       q_key_2px_cropped.png
│       ribosomes_real.png
│       Rubik-Bold.ttf
│       rubik.fnt
│       rubik1.png
│       rubik2.png
│       rubik_settings.hiero
│       rubik_yellow.fnt
│       rubik_yellow1.png
│       rubik_yellow2.png
│       scroll_bar.png
│       scroll_bar.svg
│       shopBackground.jpg
│       space_enter_stroke.png
│       space_enter_stroke_1.png
│       space_enter_stroke_2.png
│       startBackground.jpg
│       startBackground.png
│       temp_cell.png
│       test_config.txt
│       transparent_floating_texture.png
│       vignette_low_atp.png
│       wasd.png
│       white_pixel.png
│       ws.png
│
├───core
│   │   build.gradle
│   │
│   └───src
│       ├───main
│       │   └───java
│       │       └───cellcorp
│       │           └───gameofcells
│       │               │   AssetFileNames.java
│       │               │   Main.gwt.xml
│       │               │   Main.java
│       │               │   Util.java
│       │               │
│       │               ├───hud
│       │               │       Bar.java
│       │               │       Bars.java
│       │               │       ControlInstructions.java
│       │               │       HudStats.java
│       │               │
│       │               ├───notification
│       │               │       AcidZoneSource.java
│       │               │       CanBuyFirstUpgradeSource.java
│       │               │       CanDivideCellSource.java
│       │               │       LowATPSource.java
│       │               │       LowHealthSource.java
│       │               │       NoATPSource.java
│       │               │       Notification.java
│       │               │       NotificationManager.java
│       │               │       NotificationSource.java
│       │               │
│       │               ├───objects
│       │               │   │   Cell.java
│       │               │   │   Chunk.java
│       │               │   │   Glucose.java
│       │               │   │   GlucoseManager.java
│       │               │   │   HUD.java
│       │               │   │   MinimapRenderer.java
│       │               │   │   Particles.java
│       │               │   │   RandomFromHash.java
│       │               │   │   SpawnManager.java
│       │               │   │   Stats.java
│       │               │   │   Upgrade.java
│       │               │   │   Zone.java
│       │               │   │   ZoneManager.java
│       │               │   │
│       │               │   ├───organelle
│       │               │   │       FlagellumUpgrade.java
│       │               │   │       MitochondriaUpgrade.java
│       │               │   │       NucleusUpgrade.java
│       │               │   │       OrganelleUpgrade.java
│       │               │   │       RibosomeUpgrade.java
│       │               │   │
│       │               │   └───size
│       │               │           LargeSizeUpgrade.java
│       │               │           MassiveSizeUpgrade.java
│       │               │           MediumSizeUpgrade.java
│       │               │           SizeUpgrade.java
│       │               │           SmallSizeUpgrade.java
│       │               │
│       │               ├───providers
│       │               │       ConfigProvider.java
│       │               │       DefaultGraphicsProvider.java
│       │               │       DefaultInputProvider.java
│       │               │       GameLoaderSaver.java
│       │               │       GraphicsProvider.java
│       │               │       InputProvider.java
│       │               │
│       │               └───screens
│       │                       AttractScreen.java
│       │                       GameInfoControlsScreen.java
│       │                       GameOfCellsScreen.java
│       │                       GameOverScreen.java
│       │                       GamePlayScreen.java
│       │                       HUD.java
│       │                       MainMenuScreen.java
│       │                       MenuSystem.java
│       │                       PauseScreen.java
│       │                       PopupInfoScreen.java
│       │                       ShopScreen.java
│       │                       SplitCellScreen.java
│       │
│       └───test
│           └───java
│               └───cellcorp
│                   └───gameofcells
│                       │   TestMain.java
│                       │   TestTestClass.java
│                       │
│                       ├───notification
│                       │       TestNotification.java
│                       │       TestNotificationManager.java
│                       │
│                       ├───objects
│                       │   │   TestCell.java
│                       │   │   TestChunk.java
│                       │   │   TestGlucose.java
│                       │   │   TestGlucoseManager.java
│                       │   │   TestHUD.java
│                       │   │   TestParticles.java
│                       │   │   TestRandomFromHash.java
│                       │   │   TestSpawnManager.java
│                       │   │   TestStats.java
│                       │   │   TestUpgrade.java
│                       │   │   TestZone.java
│                       │   │   TestZoneManager.java
│                       │   │
│                       │   ├───organelle
│                       │   │       TestOrganelleUpgrades.java
│                       │   │
│                       │   └───size
│                       │           TestSizeUpgrades.java
│                       │
│                       ├───providers
│                       │       FakeGraphicsProvider.java
│                       │       FakeInputProvider.java
│                       │       FakePreferences.java
│                       │       TestAssetManager.java
│                       │       TestConfigProvider.java
│                       │       TestGameLoaderSaver.java
│                       │
│                       ├───runner
│                       │       GameRunner.java
│                       │
│                       └───screens
│                               TestAttractScreen.java
│                               TestGameInfoControlsScreen.java
│                               TestGameOverScreen.java
│                               TestGamePlayScreen.java
│                               TestGamePlayScreenNotifications.java
│                               TestHUD.java
│                               TestMainMenuScreen.java
│                               TestMenuSystem.java
│                               TestPauseScreen.java
│                               TestPopupInfoScreen.java
│                               TestShopScreen.java
│                               TestSplitCellScreen.java
│
├───docs
│   │   git_branch_workflow.md
│   │   testing_problems.md
│   │
│   └───assets
│           problem_1_private.png
│           problem_2_bad.png
│           problem_2_error.png
│           problem_2_good.png
│           problem_3_bad.png
│           problem_3_error.png
│           problem_3_good.png
│           problem_3_interface.png
│           problem_4_bad_construtor.png
│           problem_4_bad_test.png
│           problem_4_crash.png
│           problem_4_good_cell.png
│           problem_4_good_main.png
│           problem_4_good_test.png
│
├───html
│   │   build.gradle
│   │
│   └───src
│       └───main
│           └───java
│               └───cellcorp
│                   └───gameofcells
│                       │   GdxDefinition.gwt.xml
│                       │   GdxDefinitionSuperdev.gwt.xml
│                       │
│                       └───gwt
│                               GwtLauncher.java
│   
│
└───lwjgl3
    │   build.gradle
    │   nativeimage.gradle
    │
    └───src
        └───main
            ├───java
            │   └───cellcorp
            │       └───gameofcells
            │           └───lwjgl3
            │                   Lwjgl3Launcher.java
            │                   StartupHelper.java
            │
            └───resources
                    libgdx128.png
                    libgdx16.png
                    libgdx32.png
                    libgdx64.png

 ```

**604 directories, 5688 files**

## Notes

- Java version has been set to 17 to avoid some warnings on build.

## Desktop

### Compilation

 ```sh
 gradle build
 ```

### Run

 ```sh
 gradle Run
 ```

## Web

### Compilation

 ```sh
 gradle build
 ```

### Run

 ```sh
 gradle superdev
 ```

Access the following URL via a web browser:

[http://localhost:8080/index.html](http://localhost:8080/index.html)

## Testing

### Tests

Testing is performed via JUnit tests on each build.

## Debug mode

For manual testing, the game has two debug facilities.

Setting the constant `GamePlayScreen.DEBUG_DRAW_ENABLED`
enables debug draw of Scene2dUi elements, and adds debug wireframes
to many game objects.

There are also a set of debug keys:

* `G`: Go to game-over screen
* `E`: Add ATP to cell
* `F`: Remove ATP from cell
* `T`: Damage the cell
* `Y`: Report heal is available
* `N`: Give cell a nucleus
* `M`: Go to main menu
* `0`: Set health to zero
* `O`: Overwrite Save
* `L`: Load Save

## Asset list

### List of assets/licenses:

### Creative Commons:
Glucose
- https://openclipart.org/detail/75313/glossy-balls

### SIL Open Font License:

- Rubik

### BioRender Attribution:
**Scientific illustrations and textures created with [BioRender.com](https://biorender.com).**
BioRender assets are used in this project with permission for internal educational purposes only, based on guidance provided by BioRender Support. No redistribution or commercial use is intended.
- Cell Organelles



# ====== Process Information ======
The following is information related to team specific processes. 

# Pull Request Reviewer Order

When your PR is ready for review, ping the next person in the list to review:

- Brendon
- Tim
- Andrew
- Mark


## Uploading to CSDev

1. Ensure `CONFIG_URL` is set properly in ConfigProvider.
2. `gradle build`
3. `gradle dist`
4. This makes/updates `html/build/dist` in your repo.
5. On a lab machine, copy the contents of that folder to `/home/student/Classes/405/CellCorp`
6. Place config.txt from the project assets folder inside of `/home/student/Classes/405/CellCorp/assets`. (This is
   required as `gradle dist`) ruins the file.
7. Run `chmod g+w /home/student/Classes/405/CellCorp/*` to allow others to delete folders you create.

## Python Web Server Setup

A local web server instance has been provided to aid in testing of adding user
configurable variables to the web version of the game. Below are instructions
to initialize this setup.

In a terminal window containing our GameOfCells project enter:
```python local_web_server.py```
This will create a basic web server configured to handle requests from GameOfCells.

Run ```gradle superdev``` and navigate to  [http://localhost:8080/index.html](http://localhost:8080/index.html)

Edit and save config.txt as desired. Refresh web instance of game to see updated configs.
