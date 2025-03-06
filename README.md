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
 │   └── assets.txt
 ├── build.gradle
 ├── core
 │   ├── build
 │   │   ├── classes
 │   │   │   └── java
 │   │   │       ├── main
 │   │   │       └── test
 │   │   ├── resources
 │   │     ├── main
 │   │     └── test
 │   ├── src
 │   │   ├── main
 │   │   │   └── java
 │   │   │       └── cellcorp
 │   │   │           └── gameofcells
 │   │   │               ├── Main.gwt.xml
 │   │   │               ├── Main.java
 │   │   │               └── objects
 │   │   │                   └── Cell.java
 │   │   │               └── screens
 │   │   │                   └── GamePlayScreen.java
 │   │   └── test
 │   │       └── java
 │   │           └── cellcorp
 │   │               └── gameofcells
 │   │                   └── TestTestClass.java
 ├── gradle
 │   └── wrapper
 │       ├── gradle-wrapper.jar
 │       └── gradle-wrapper.properties
 ├── gradle.properties
 ├── gradlew
 ├── gradlew.bat
 ├── html
 │   ├── build
 │   │   ├── classes
 │   │   │   └── java
 │   │   │       ├── main
 │   │   │       └── test
 │   │   ├── gwt
 │   │   │   ├── cache
 │   │   │   └── work
 │   │   ├── resources
 │   │     ├── main
 │   │     └── test
 │   ├── build.gradle
 │   ├── src
 │   │   └── main
 │   │       └── java
 │   │           └── cellcorp
 │   │               └── gameofcells
 │   │                   ├── GdxDefinition.gwt.xml
 │   │                   ├── GdxDefinitionSuperdev.gwt.xml
 │   │                   └── gwt
 │   │                       └── GwtLauncher.java
 │   ├── war
 │   └── webapp
 │       ├── index.html
 │       ├── refresh.png
 │       ├── styles.css
 │       └── WEB-INF
 │           └── web.xml
 ├── lwjgl3
 │   ├── build
 │   │   ├── classes
 │   │   │   └── java
 │   │   │       ├── main
 │   │   │       └── test
 │   │   ├── resources
 │   │     ├── main
 │   │     └── test
 │   ├── build.gradle
 │   ├── icons
 │   │   ├── logo.icns
 │   │   ├── logo.ico
 │   │   └── logo.png
 │   ├── nativeimage.gradle
 │   └── src
 │       └── main
 │           ├── java
 │           │   └── cellcorp
 │           │       └── gameofcells
 │           │           └── lwjgl3
 │           │               ├── Lwjgl3Launcher.java
 │           │               └── StartupHelper.java
 │           └── resources
 │               ├── libgdx128.png
 │               ├── libgdx16.png
 │               ├── libgdx32.png
 │               └── libgdx64.png
 ├── README.txt
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
 
 ## Testing
 
 ### Test
 Testing is performed via JUnit tests on each build.
 
 
 ## Asset list
 
 ### List of assets/licenses:
 
 
 ### Creative Commons:
 Glucose: - Still Undecided.
 - https://en.wikipedia.org/wiki/Glucose#/media/File:Beta-D-glucose-from-xtal-3D-balls.png
 - https://openclipart.org/detail/75313/glossy-balls
+
+### SIL Open Font License
+- Rubik
