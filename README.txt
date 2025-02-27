/**
* Game Of Cells
*
* A game used to teach students about cells, and cell functions.
*
* @author Brendon Vinyard / vineyabn207
* @author Andrew Sennoga-Kimuli / sennogat106
* @author Mark Murphy / murphyml207
* @author Tim Davey / daveytj206
*
* @date 02/18/2025
* @course CIS 405
* @assignment GameOfCells
*/

Problem: Use agile methods to develop 'Game Of Cells' as part of a software 
engineering team. Game Of Cells is a game used to teach students about cells,
and cell functions.

Directory:

Note: This directory has been modified slightly to ignore files that are build
specific or temporary. For a full directory run the tree command in your 
favorite shell.

.
├── assets
│   └── assets.txt
├── core
│   ├── build
│   │   ├── classes
│   │   │   └── java
│   │   │       ├── main
│   │   │       └── test
│   │   ├── resources
│   │       ├── main
│   │       └── test
│   │   
│   └── src
│       ├── main
│       │   └── java
│       │       └── cellcorp
│       │           └── gameofcells
│       │               ├── Main.gwt.xml
│       │               ├── Main.java
│       │               └── screens
│       │                   └── GamePlayScreen.java
│       └── test
│           └── java
│               └── cellcorp
│                   └── gameofcells
│                       └── TestTestClass.java
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── html
│   ├── build
│   │   ├── classes
│   │   │   └── java
│   │   │       ├── main
│   │   │       └── test
│   │   ├── gwt
│   │   │   ├── cache
│   │   │   └── work
│   │   ├── resources
│   │       ├── main
│   │       └── test
│   │   
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
│   ├── war
│   └── webapp
│       ├── index.html
│       ├── refresh.png
│       ├── styles.css
│       └── WEB-INF
│           └── web.xml
├── lwjgl3
│   ├── build
│   │   ├── classes
│   │   │   └── java
│   │   │       ├── main
│   │   │       └── test
│   │   ├── resources
│   │       ├── main
│   │       └── test
│   │   
│   ├── build.gradle
│   ├── icons
│   │   ├── logo.icns
│   │   ├── logo.ico
│   │   └── logo.png
│   ├── nativeimage.gradle
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
├── README.txt
└── settings.gradle

340 directories, 39 files


Note: Java version has been set to 17 to avoid some warnings on build.

Desktop:
    Compilation:
        gradle build
    Run:
        gradle Run
Web:
    Compilation:
        gradle build
    Run:
        gradle superdev
        Access the following url via a web browser.
        http://localhost:8080/index.html
        

Testing:
    Test:
        Feature:
        Expected:
        Result:
    