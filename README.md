TuxBlocks
=========

TuxBlocks is a cross-platform aglebra game, originally created for [Tux4Kids](http://tux4kids.alioth.debian.org/) during Google's Summer of code 2013. The game is built on top of the [PlayN](code.google.com/p/playn/) game engine. The game is currently in an open Beta, with full release pending.


Concept
-------

TuxBlocks aims to be both a fun math game and a virtual manipulative which can help students visualize algebra problems in a new and useful way. The game plays like a tradition tower defense style game, with players building mazes out of a variety of towers, each with their own special ability, to prevent enemies from crossing from one side of a grid to the other. However, in TuxBlocks, the way the player acquires new towers is by solving algebra problems. Instead of simply asking the player for the solution the equation, forcing them to do a little math as the price for the game content, TuxBlocks integrates the solving process into the gameplay. The equation is represented by movable blocks, which the player can pick up, move around, simplify and modify to help solve equation. The game rewards successful solving, not only with towers for the gameplay, but also with levels up, which allow players to skip easier arithmetic. TuxBlocks has a variety of difficulty levels, both for the gameplay and the mathematics, allowing students of many achievement levels to enjoy the game. TuxBlocks also features a "Build" mode which allows players to construct their own problems, perhaps even homework problems, and use the solve mechanics in the game to help solve it.


Platforms
---------

TuxBlocks has working versions on the following platforms:
 * Android 2.3+
 * [HTML5](http://tux-blocks.appspot.com/) (using both Canvas and WebGL)
 
An iOS version is currently being co-developed with the [PlayN-IOS](https://github.com/thomaswp/playn-ios) library, which will allow an iOS release without the use of the proprietary Xamarin Studio.

License
-------

TuxBlocks, as well as all of its visual art assets, are licensed under the Creative Commons Attribution-ShareAlike License ([CC BY-SA](http://creativecommons.org/licenses/by-sa/3.0/us/)). If you would like to release a version of TuxBlocks under a different open source license, please contact the developers.

The game also uses Creative Commons licensed music and sounds by the following artists: [Rezoner](soundcloud.com/rezoner/), FoxSynergy, [David McKee (ViRiX)](soundcloud.com/virix), Lokif, [Kenny Vleugels](kenny.nl), Broumbroum, Neotone, Mojomills, m_O_m, klangfabrik, JoelAudio, SunnySideSound and Kastenfrosch.

Development Environment
-----------------------

TuxBlocks is built on top of PlayN and can be set up using the instructions in their [getting started guide](http://code.google.com/p/playn/wiki/GettingStarted). The only difference is that you will be using the contents of this repository instead of the playn-samples. The project is designed for development on the [eclipse](http://eclipse.org/) IDE, so make sure you have eclipse set up and follow the appropriate instructions on the guide.

As explained in the getting started guide, PlayN requires [Apache Maven](http://maven.apache.org/) 3.03 or newer, and the [m2eclipse plugin](http://m2eclipse.codehaus.org/) is recommended. Once you have the m2eclipse plugin installed, you can import the  **android**, **assets**, **core**, **html** and **java** projects into eclispe, using the instructions in the getting started guide (you may import the **flash** and **ios** projects as well, but they are not in current use). If you get errors during the import process, choose to "Resolve Later" and continue. They should not be a problem.

Running the Game
----------------

See the appropriate instructions for the platform you would like to use:

### Java ###

Once you have Maven installed, you should be able to run the Java version of the game by navigating to the root directory of the repository (where this README is located) and running the following command on a command prompt: 

    mvn install

This may require a number of downloads, but this is only required the first time. Once it is finished, the game should start. After you have built the java project once, you should be able to run it again by right-clicking the project in eclipse and selecting Run As->Java Application. When asked for a main class, select TuxBlocksGameJava.

It is likely you will receive and error such as:

    Exception in thread "main" java.lang.UnsatisfiedLinkError: no lwjgl in java.library.path
	
If this is the case, go to Run->Run Configurations, select the TuxBlockGameJava item, go to the Arguments tab and add the following as a **VM** argument:
	
	-Djava.library.path=target/natives
	
The game should run successfully after this, and continue to work. If you run a mvn -clean on the project, however, you may have to run mvn install again.

### Android ###

To set up an Android build, for make sure you have the [Android SDK](http://developer.android.com/sdk/index.html) installed, along with your target API (TuxBlocks currently targets v11, Andriod 3.0).

Next, follow the instructions on [this guide](http://code.google.com/p/playn/wiki/MavenAndroidBuild) to set up your SDK path with Maven.

You should be able to run TuxBlock on Android by executing the following command in the root directory of this repository:

    mvn -Pandroid install
	
This will install the game on any conencted Android device; however, it will not run the game. If you are on a Windows machine, you can intall *and run* by executing the run_android.bat file instead.