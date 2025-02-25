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

.
├── assets
│   └── assets.txt
├── build
│   ├── reports
│   │   └── problems
│   │       └── problems-report.html
│   └── tmp
│       ├── buildEnvironment
│       ├── cleanEclipse
│       ├── cleanEclipseProject
│       ├── cleanIdea
│       ├── cleanIdeaModule
│       ├── cleanIdeaProject
│       ├── cleanIdeaWorkspace
│       ├── components
│       ├── dependencies
│       ├── dependencyInsight
│       ├── dependentComponents
│       ├── eclipse
│       ├── eclipseProject
│       ├── help
│       ├── idea
│       ├── ideaModule
│       ├── ideaProject
│       ├── ideaWorkspace
│       ├── init
│       ├── javaToolchains
│       ├── model
│       ├── openIdea
│       ├── outgoingVariants
│       ├── prepareKotlinBuildScriptModel
│       ├── projects
│       ├── properties
│       ├── resolvableConfigurations
│       ├── tasks
│       ├── updateDaemonJvm
│       └── wrapper
├── build.gradle
├── core
│   ├── build
│   │   ├── classes
│   │   │   └── java
│   │   │       ├── main
│   │   │       └── test
│   │   ├── resources
│   │   │   ├── main
│   │   │   └── test
│   │   └── tmp
│   │       ├── assemble
│   │       ├── build
│   │       ├── buildDependents
│   │       ├── buildEnvironment
│   │       ├── buildNeeded
│   │       ├── check
│   │       ├── classes
│   │       ├── clean
│   │       ├── cleanEclipse
│   │       ├── cleanEclipseClasspath
│   │       ├── cleanEclipseJdt
│   │       ├── cleanEclipseProject
│   │       ├── cleanIdea
│   │       ├── cleanIdeaModule
│   │       ├── compileJava
│   │       ├── compileTestJava
│   │       ├── components
│   │       ├── dependencies
│   │       ├── dependencyInsight
│   │       ├── dependentComponents
│   │       ├── eclipse
│   │       ├── eclipseClasspath
│   │       ├── eclipseJdt
│   │       ├── eclipseProject
│   │       ├── generateAssetList
│   │       ├── help
│   │       ├── idea
│   │       ├── ideaModule
│   │       ├── jar
│   │       │   └── MANIFEST.MF
│   │       ├── javadoc
│   │       ├── javaToolchains
│   │       ├── model
│   │       ├── outgoingVariants
│   │       ├── processResources
│   │       ├── processTestResources
│   │       ├── projects
│   │       ├── properties
│   │       ├── resolvableConfigurations
│   │       ├── runSingle
│   │       ├── tasks
│   │       ├── test
│   │       └── testClasses
│   ├── build.gradle
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
│   │   │   ├── main
│   │   │   └── test
│   │   └── tmp
│   │       ├── addSource
│   │       ├── afterRun
│   │       ├── appAfterIntegrationTest
│   │       ├── appBeforeIntegrationTest
│   │       ├── appRestart
│   │       ├── appRun
│   │       ├── appRunDebug
│   │       ├── appRunWar
│   │       ├── appRunWarDebug
│   │       ├── appStart
│   │       ├── appStartDebug
│   │       ├── appStartWar
│   │       ├── appStartWarDebug
│   │       ├── appStop
│   │       ├── archiveAllProducts
│   │       ├── archiveProduct
│   │       ├── assemble
│   │       ├── beforeRun
│   │       ├── build
│   │       ├── buildAllProducts
│   │       ├── buildDependents
│   │       ├── buildEnvironment
│   │       ├── buildNeeded
│   │       ├── buildProduct
│   │       ├── check
│   │       ├── checkGwt
│   │       ├── classes
│   │       ├── clean
│   │       ├── cleanEclipse
│   │       ├── cleanEclipseClasspath
│   │       ├── cleanEclipseJdt
│   │       ├── cleanEclipseProject
│   │       ├── cleanEclipseWtp
│   │       ├── cleanEclipseWtpComponent
│   │       ├── cleanEclipseWtpFacet
│   │       ├── cleanIdea
│   │       ├── cleanIdeaModule
│   │       ├── compileGwt
│   │       ├── compileJava
│   │       ├── compileTestJava
│   │       ├── components
│   │       ├── createInplaceWebAppFolder
│   │       ├── dependencies
│   │       ├── dependencyInsight
│   │       ├── dependentComponents
│   │       ├── dist
│   │       ├── distZip
│   │       ├── draftCompileGwt
│   │       ├── draftWar
│   │       │   └── MANIFEST.MF
│   │       ├── eclipse
│   │       ├── eclipseClasspath
│   │       ├── eclipseJdt
│   │       ├── eclipseProject
│   │       ├── eclipseWtp
│   │       ├── eclipseWtpComponent
│   │       ├── eclipseWtpFacet
│   │       ├── explodedWar
│   │       ├── farmAfterIntegrationTest
│   │       ├── farmBeforeIntegrationTest
│   │       ├── farmIntegrationTest
│   │       ├── farmRestart
│   │       ├── farmRun
│   │       ├── farmRunDebug
│   │       ├── farmRunWar
│   │       ├── farmRunWarDebug
│   │       ├── farmStart
│   │       ├── farmStartDebug
│   │       ├── farmStartWar
│   │       ├── farmStartWarDebug
│   │       ├── farmStop
│   │       ├── generateAssetList
│   │       ├── gwtDev
│   │       ├── gwtSuperDev
│   │       ├── help
│   │       ├── idea
│   │       ├── ideaModule
│   │       ├── jar
│   │       │   └── MANIFEST.MF
│   │       ├── javadoc
│   │       ├── javaToolchains
│   │       ├── jettyRestart
│   │       ├── jettyRun
│   │       ├── jettyRunDebug
│   │       ├── jettyRunWar
│   │       ├── jettyRunWarDebug
│   │       ├── jettyStart
│   │       ├── jettyStartDebug
│   │       ├── jettyStartWar
│   │       ├── jettyStartWarDebug
│   │       ├── jettyStop
│   │       ├── model
│   │       ├── outgoingVariants
│   │       ├── prepareArchiveWebApp
│   │       ├── prepareInplaceWebApp
│   │       ├── prepareInplaceWebAppClasses
│   │       ├── prepareInplaceWebAppFolder
│   │       ├── processResources
│   │       ├── processTestResources
│   │       ├── projects
│   │       ├── properties
│   │       ├── resolvableConfigurations
│   │       ├── runSingle
│   │       ├── showClassPath
│   │       ├── startHttpServer
│   │       ├── superDev
│   │       ├── tasks
│   │       ├── test
│   │       ├── testClasses
│   │       ├── tomcatRestart
│   │       ├── tomcatRun
│   │       ├── tomcatRunDebug
│   │       ├── tomcatRunWar
│   │       ├── tomcatRunWarDebug
│   │       ├── tomcatStart
│   │       ├── tomcatStartDebug
│   │       ├── tomcatStartWar
│   │       ├── tomcatStartWarDebug
│   │       ├── tomcatStop
│   │       ├── war
│   │       │   └── MANIFEST.MF
│   │       └── warTemplate
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
│   │   │   ├── main
│   │   │   └── test
│   │   └── tmp
│   │       ├── assemble
│   │       ├── assembleDist
│   │       ├── build
│   │       ├── buildDependents
│   │       ├── buildEnvironment
│   │       ├── buildMacAppBundleMacM1
│   │       ├── buildMacAppBundleMacX64
│   │       ├── buildNeeded
│   │       ├── check
│   │       ├── classes
│   │       ├── clean
│   │       ├── cleanEclipse
│   │       ├── cleanEclipseClasspath
│   │       ├── cleanEclipseJdt
│   │       ├── cleanEclipseProject
│   │       ├── cleanIdea
│   │       ├── cleanIdeaModule
│   │       ├── compileJava
│   │       ├── compileTestJava
│   │       ├── components
│   │       ├── createRuntimeImageLinuxX64
│   │       ├── createRuntimeImageMacM1
│   │       ├── createRuntimeImageMacX64
│   │       ├── createRuntimeImageWinX64
│   │       ├── dependencies
│   │       ├── dependencyInsight
│   │       ├── dependentComponents
│   │       ├── dist
│   │       ├── distTar
│   │       ├── distZip
│   │       ├── downloadJdkLinuxX64
│   │       ├── downloadJdkMacM1
│   │       ├── downloadJdkMacX64
│   │       ├── downloadJdkWinX64
│   │       ├── downloadRoastLinuxX64
│   │       ├── downloadRoastMacM1
│   │       ├── downloadRoastMacX64
│   │       ├── downloadRoastWinX64
│   │       ├── eclipse
│   │       ├── eclipseClasspath
│   │       ├── eclipseJdt
│   │       ├── eclipseProject
│   │       ├── generateAssetList
│   │       ├── generatePListMacM1
│   │       ├── generatePListMacX64
│   │       ├── help
│   │       ├── idea
│   │       ├── ideaModule
│   │       ├── installDist
│   │       ├── jar
│   │       │   └── MANIFEST.MF
│   │       ├── javadoc
│   │       ├── javaToolchains
│   │       ├── model
│   │       ├── outgoingVariants
│   │       ├── packageLinuxX64
│   │       ├── packageMacM1
│   │       ├── packageMacX64
│   │       ├── packageWinX64
│   │       ├── processResources
│   │       ├── processTestResources
│   │       ├── projects
│   │       ├── properties
│   │       ├── resolvableConfigurations
│   │       ├── roastLinuxX64
│   │       ├── roastMacM1
│   │       ├── roastMacX64
│   │       ├── roastWinX64
│   │       ├── run
│   │       ├── runSingle
│   │       ├── startScripts
│   │       ├── tasks
│   │       ├── test
│   │       ├── testClasses
│   │       ├── unzipJdkLinuxX64
│   │       ├── unzipJdkMacM1
│   │       ├── unzipJdkMacX64
│   │       ├── unzipJdkWinX64
│   │       ├── unzipRoastLinuxX64
│   │       ├── unzipRoastMacM1
│   │       ├── unzipRoastMacX64
│   │       └── unzipRoastWinX64
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
    