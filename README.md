 # Game Of Cells
 
 ## ID Block
 
 A game used to teach students about cells and cell functions.
 
 - **Brendon Vinyard** / vineyabn207  
 - **Andrew Sennoga-Kimuli** / sennogat106  
 - **Mark Murphy** / murphyml207  
 - **Tim Davey** / daveytj206  
 - **Date:** - 02/27/2025
 - **Course:** - CIS 405
 - **Assignment:** - GameOfCells
 
 # Pull Request Reviewer Roster
 When your PR is ready for review, ping the next person in the list to review:
 - Brendon
 - Tim
 - Andrew
 - Mark
 
 ## Problem
 
 Use agile methods to develop 'Game Of Cells' as part of a software engineering team. *Game Of Cells* is a game used to teach students about cells and cell functions.
 
 ## Directory
 
 **Note:** This directory has been modified slightly to ignore files that are build-specific or temporary. For a full directory, run the `tree` command in your favorite shell.
 
 ```
 .
├── assets
│   ├── assets-Brendon.txt
│   ├── assets.txt
│   ├── Cell.png
│   ├── config.txt
│   ├── gameBackground2.png
│   ├── gameBackground.jpg
│   ├── gameBackground.png
│   ├── glucose2.png
│   ├── glucose_orange.png
│   ├── glucose_yellow.png
│   ├── mitochondria.png
│   ├── rubik1.png
│   ├── rubik2.png
│   ├── Rubik-Bold.ttf
│   ├── rubik.fnt
│   ├── rubik_settings.hiero
│   ├── rubik_yellow1.png
│   ├── rubik_yellow2.png
│   ├── rubik_yellow.fnt
│   ├── shopBackground.jpg
│   ├── startBackground.jpg
│   ├── startBackground.png
│   └── white_pixel.png
├── build.gradle
├── core
│   ├── build.gradle
│   └── src
│       ├── main
│       │   └── java
│       │       └── cellcorp
│       │           └── gameofcells
│       │               ├── AssetFileNames.java
│       │               ├── Main.gwt.xml
│       │               ├── Main.java
│       │               ├── objects
│       │               │   ├── Cell.java
│       │               │   ├── EnergyBars.java
│       │               │   ├── Glucose.java
│       │               │   ├── GlucoseManager.java
│       │               │   ├── HUD.java
│       │               │   └── Particles.java
│       │               ├── providers
│       │               │   ├── ConfigProvider.java
│       │               │   ├── DefaultGraphicsProvider.java
│       │               │   ├── DefaultInputProvider.java
│       │               │   ├── GraphicsProvider.java
│       │               │   └── InputProvider.java
│       │               └── screens
│       │                   ├── AttractScreen.java
│       │                   ├── GameInfoControlsScreen.java
│       │                   ├── GameOfCellsScreen.java
│       │                   ├── GameOverScreen.java
│       │                   ├── GamePlayScreen.java
│       │                   ├── MainMenuScreen.java
│       │                   ├── PopupInfoScreen.java
│       │                   ├── SettingsScreen.java
│       │                   └── ShopScreen.java
│       └── test
│           └── java
│               └── cellcorp
│                   └── gameofcells
│                       ├── FakeInputProvider.java
│                       ├── objects
│                       │   ├── TestCell.java
│                       │   ├── TestGlucoseManager.java
│                       │   ├── TestHUD.java
│                       │   └── TestParticles.java
│                       ├── providers
│                       │   ├── FakeGraphicsProvider.java
│                       │   ├── FakeInputProvider.java
│                       │   ├── TestAssetManager.java
│                       │   └── TestConfigProvider.java
│                       ├── runner
│                       │   └── GameRunner.java
│                       ├── screens
│                       │   └── TestGamePlayScreen.java
│                       ├── TestMain.java
│                       └── TestTestClass.java
├── docs
│   ├── assets
│   │   ├── problem_1_private.png
│   │   ├── problem_2_bad.png
│   │   ├── problem_2_error.png
│   │   ├── problem_2_good.png
│   │   ├── problem_3_bad.png
│   │   ├── problem_3_error.png
│   │   ├── problem_3_good.png
│   │   ├── problem_3_interface.png
│   │   ├── problem_4_bad_construtor.png
│   │   ├── problem_4_bad_test.png
│   │   ├── problem_4_crash.png
│   │   ├── problem_4_good_cell.png
│   │   ├── problem_4_good_main.png
│   │   └── problem_4_good_test.png
│   ├── git_branch_workflow.md
│   └── testing_problems.md
├── gradle.properties
├── gradlew
├── gradlew.bat
├── html
│   ├── build.gradle
│   ├── src
│   │   └── main
│   │       └── java
│   │           └── cellcorp
│   │               └── gameofcells
│   │                   ├── GdxDefinition.gwt.xml
│   │                   ├── GdxDefinitionSuperdev.gwt.xml
│   │                   └── gwt
│   │                       └── GwtLauncher.java
├── local_web_server.py
├── lwjgl3
│   ├── build.gradle
│   └── src
│       └── main
│           ├── java
│           │   └── cellcorp
│           │       └── gameofcells
│           │           └── lwjgl3
│           │               ├── Lwjgl3Launcher.java
│           │               └── StartupHelper.java
│           └── resources
│               ├── libgdx128.png
│               ├── libgdx16.png
│               ├── libgdx32.png
│               └── libgdx64.png
├── pull_request_template.md
├── README.md
└── settings.gradle
 ```
 
 **340 directories, 39 files**
 
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

## Uploading to CSDev
1. Ensure CONFING_URL is set properly in ConfigProvider.
2. `gradle build`
3. `gradle dist`
4. This makes/updates `html/build/dist` in your repo.
5. On a lab machine, copy the contents of that folder to `/home/student/Classes/405/CellCorp`
6. Place config.txt from the project assets folder inside of `/home/student/Classes/405/CellCorp/assets`. (This is required as `gradle dist`) ruins the file.
7. Run `chmod g+w /home/student/Classes/405/CellCorp/assets/*` to allow others to delete folders you create.

## Python Web Server Setup
A local web server instance has been provided to aid in testing of adding user
configurable variables to the web version of the game. Below are instructions 
to initialize this setup.

In a terminal window containing our GameOfCells project enter:
   ```python local_web_server.py```
This will create a basic web server configured to handle requests from GameOfCells.

Run ```gradle superdev``` and navigate to  [http://localhost:8080/index.html](http://localhost:8080/index.html)


Edit and save config.txt as desired. Refresh web instance of game to see updated configs.


 ## Testing
 
 ### Test
 Testing is performed via JUnit tests on each build.
 
 ## Asset list
 
 ### List of assets/licenses:
 
 ### Creative Commons:
 Glucose: - Still Undecided.
 - https://en.wikipedia.org/wiki/Glucose#/media/File:Beta-D-glucose-from-xtal-3D-balls.png
 - https://openclipart.org/detail/75313/glossy-balls
 
 ### SIL Open Font License
 - Rubik
