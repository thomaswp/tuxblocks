TuxBlocks
=========

TuxBlocks is a cross-platform aglebra game, originally created for [Tux4Kids](http://tux4kids.alioth.debian.org/) during Google's Summer of code 2013. The game is built on top of the [PlayN](http://code.google.com/p/playn/) game engine. The game is currently in an open Beta, with full release pending.


Concept
-------

TuxBlocks aims to be both a fun math game and a virtual manipulative which can help students visualize algebra problems in a new and useful way. The game plays like a traditional tower defense style game, with players building mazes out of a variety of towers, each with their own special ability, to prevent enemies from crossing from one side of a grid to the other. However, in TuxBlocks, the way the player acquires new towers is by solving algebra problems. Instead of simply asking the player for the solution the equation, forcing them to do a little math as the price for the game content, TuxBlocks integrates the solving process into the gameplay. The equation is represented by movable blocks, which the player can pick up, move around, simplify and modify to help solve the equation. The game rewards successful solving, not only with towers for the tower defense, but also with levels up, which allow players to skip easier arithmetic. TuxBlocks has a variety of difficulty levels, both for the gameplay and the mathematics, allowing students of many achievement levels to enjoy the game. TuxBlocks also features a "Build" mode which allows players to construct their own problems, perhaps even homework problems, and use the solve mechanics in the game to help find a solution.


Platforms
---------

TuxBlocks has working versions on the following platforms:
 * Android 2.3+
 * [HTML5](http://tux-blocks.appspot.com/) (using both Canvas and WebGL)
   + Currently Chrome, Firefox and Internet Explorer are supported (with Chrome preferred)
 
An iOS version is currently being co-developed with the [PlayN-IOS](http://github.com/thomaswp/playn-ios) library, which will allow an iOS release without the use of the proprietary Xamarin Studio.

License
-------

TuxBlocks, as well as all of its visual art assets, are licensed under the Creative Commons Attribution-ShareAlike License 3.0 ([CC BY-SA](http://creativecommons.org/licenses/by-sa/3.0/us/)). If you would like to release a version of TuxBlocks under a different open source license, please contact the developers.

The game also uses Creative Commons licensed music and sounds by the following artists: [Rezoner](http://www.soundcloud.com/rezoner/), FoxSynergy, [David McKee (ViRiX)](http://www.soundcloud.com/virix), Lokif, [Kenny Vleugels](http://www.kenny.nl), Broumbroum, Neotone, Mojomills, m_O_m, klangfabrik, JoelAudio, SunnySideSound and Kastenfrosch.

Development Environment
-----------------------

TuxBlocks is built on top of PlayN and can be set up using the instructions in their [getting started guide](http://code.google.com/p/playn/wiki/GettingStarted). The only difference is that you will be using the contents of this repository instead of the playn-samples. The project is designed for development with the [Eclipse](http://eclipse.org/) IDE, so make sure you have Eclipse set up and follow the appropriate instructions on the guide.

As explained in the getting started guide, PlayN requires [Apache Maven](http://maven.apache.org/) 3.03 or newer, and the [m2eclipse plugin](http://m2eclipse.codehaus.org/) is recommended. Once you have the m2eclipse plugin installed, you can import the  **android**, **assets**, **core**, **html** and **java** projects into eclispe, using the instructions in the getting started guide (you may import the **flash** and **ios** projects as well, but they are not in current use). If you get errors during the import process, choose to "Resolve Later" and continue. They should not be a problem.

It is recommended that you unpack the TuxBlocks repository in a folder with no spaces in its path, for instance:

    C:\Users\Thomas\tux\
	
and not

    C:\Users\Thomas\Tux Blocks\

Running the Game
----------------

See the appropriate instructions for the platform you would like to use:

### Java ###

Once you have Maven installed, you should be able to run the Java version of the game by navigating to the root directory of the repository (where this README is located) and running the following command on a command prompt: 

    mvn install

This may require maven to make a number of downloads, but this is only required the first time. Once it is finished, the game should start. After you have built the java project once, you should be able to run it again by right-clicking the project in eclipse and selecting Run As->Java Application. When asked for a main class, select TuxBlocksGameJava.

It is likely you will receive and error such as:

    Exception in thread "main" java.lang.UnsatisfiedLinkError: no lwjgl in java.library.path
	
If this is the case, go to Run->Run Configurations, select the TuxBlockGameJava configuration, go to the Arguments tab and add the following as a **VM** argument:
	
	-Djava.library.path=target/natives
	
The game should run successfully after this, and continue to work. If you run a "mvn clean" on the project, however, you may have to run "mvn install" again.

### Android ###

To set up an Android build, first make sure you have the [Android SDK](http://developer.android.com/sdk/index.html) installed, along with your target API (TuxBlocks currently targets v11, Andriod 3.0).

You also have to setup your android device with your operating system. You can follow [this guide](http://developer.android.com/tools/device.html)for setting up android device.

Next, follow the instructions on [this guide](http://code.google.com/p/playn/wiki/MavenAndroidBuild) to set up your SDK path with Maven.

You should be able to run TuxBlock on Android by executing the following command in the root directory of this repository:

    mvn -Pandroid install
	
This will install the game on any connected Android device; however, it will not run the game. You must start the game manually. If you are on a Windows machine, you can install *and run* by executing the run_android.bat file instead.

### HTML ###

To run the web version of TuxBlocks, navigate to the root folder and run:

    mvn -Phtml integration-test
	
When the command prompt informs you that the jetty server has started, navigate to [http://localhost:8080/](http://localhost:8080/), and you should find the game running there.

Unlike with other App Engine projects, you must recompile TuxBlocks every time you wish to run the HTML version. Make sure to terminate the jetty server (Ctrl+C) before running another instance. A running jetty server will mask a newly run one. If the the project still does not seem to be updating, you can try the command:

    mvn -Phtml clean
	
The run_html.bat combines these commands for your convenience. 

You may also find it useful to have the [Google App Engine plugin](http://developers.google.com/appengine/docs/java/tools/eclipse) for eclipse. This will allow you to treat the project as a GAE project and deploy it to App Engine.

#### HTML5 Caveats ####

The HTML5 backend for PlayN contains both an HTML5 Canvas graphics implementation, and a WebGL graphics implementation. Browsers such as Chrome and Firefox should default to WebGL, which is faster and more featureful, but some users, such as Internet Explorer users will not have WebGL. For this reason, it is important to test the HTML version with multiple browsers, at least one of which should have WebGL and one should not. One particularly important difference between the Canvas and WebGL implementations of the game is that Canvas does not have ability to set the tint of Layers. For this reason the [ImageLayerTintable](core/src/main/java/tuxkids/tuxblocks/core/layers/ImageLayerTintable.java) was created, which emulates the tint on HTML5 Canvas. Also, GWT (the basis of the HTML backend) does not support the String.format() method, so make sure to use the Formatter.format() method instead.

### iOS ###

The iOS build is still in development, but for more information on how to run a PlayN game on iOS, see [PlayN-IOS](http://github.com/thomaswp/playn-ios).

Structure
---------

To understand how TuxBlocks is organized, it will be useful to read the [PlayN documentation](http://code.google.com/p/playn/w/list) on the subject.

Like all PlayN projects, TuxBlocks is split into separate sub-projects, one for each platform. There is also an **assets** project, which contains all of the game's assets. It also contains an *Originals* folder, which contains some of the original art and sound for the game. Much of the art is in .xcf format, the native format for the [GIMP](http://www.gimp.org/) image editor.

Each platform project, such as **java** or **android** will have platform-specific code. This will likely not need to be changed, unless you need to change the screen resolution or Android version, etc.

The logic of TuxBlocks resides in the **core** project. The code is organized into a number of packages and sub-packages, so if you are using eclipse, it is recommended you select the Package Explorer Options->Package Presentation->Hierarchical option (look for the drop-facing triangle button in the Package Explorer). You can use the package names and code documentation to navigate the project. However, a few important base classes are discussed below.

Base and Utility Classes
------------------------

You may want to make use of these classes as you contribute to TuxBlocks. See the code documentation for more details.

**PlayNObject**: the base class of most Objects in the game, contains many convenience methods for debugging, linear interpolation and accessing PlayN's static classes without the static import.

**HashCode/Hashable**: a class and interface for easy generation of hash codes based on a list of fields. It also allows for equality comparison. PlayNObjects which implement Hashable will also override equals() to use those same hashed properties in comparison.

**PersistUtils/Peristable**: a class and interface for making an object persistable using PlayN's Storage interface. This is akin to a simplified serialization routine using the String key/value pairs that PlayN uses for storage.

**CanvasUtils**: a class for procedurally generating various shape images and text. Most images in TuxBlocks are generated at runtime.

**LayerLike**: emulates the behavior of PlayN's Layer interface. LayerLike classes cannot be directly added to other GroupLayers, but they have a layerAddable() property which can. This class is useful for creating composite Layers made of others, such as the NinepatchLayer or ImageLayerLike. 

**ImageLayerTintable**: This class emulates the behavior of PlayN's ImageLayer interface but allows for tinting that works even on HTML5 Canvas backends. It works by overlaying a tinted version of the image on top of the original, and changing opacity to create variants in tint.

**BaseScreen**: An extension of [TriplePlay](http://github.com/threerings/tripleplay)'s Screen class. This is the base of every screen you see in the game, which transition on- and off-screen by sliding into position.

Localization
------------

Tuxblocks includes languages support. Presently there are four supported languages:
* English (the default lanugage)
* French
* Hindi 
* Punjabi

To add a new supported lanugage to Tuxblocks, perform the following steps:

1. **Add you language folder to the [Text Assets](/assets/src/main/resources/assets/text/) folder.** Make sure to name the folder according its [ISO Language Code](http://www.loc.gov/standards/iso639-2/php/code_list.php). Then copy the contents of the [en](assets/src/main/resources/assets/text/en/) folder to you new folder. These are the text assets you will need to translate.

2. **Translate the game strings.** These are text strings that appear in-game. They are contained in the Strings.json files you just copied over. The strings are organized by where they appear in the game. Each translation consists of a string/string pair, such as: "round":"Round". Simply replace the string on the right side with the correct translation. Make sure not to delete any commas by accident. Any missing translations will simply use the default language instead.

3. **Translate the About and Tutorial files.** These can be found in the About.txt, TutorialBuild.txt, TutorialPlay.txt and TutorialStart.txt files. Simple translate them directly. In the About.txt file, a newline represents a new page, and a "\b" represents a line break. In the tutorial files, a double line break represents a new paragraph. You can change the layout of the About.txt, but make sure to keep the paragraphs the same in the tutorial. If you do not plan to translate any of these files, simply omit them, and the game will default to English.

4. **Add Fonts.** By default, TuxBlocks displays all text in Arial, which can only display latin languages. If your language is non-latin, make sure to register appropriate font files.
 * First, add the truetype (.TTF) font files of your language to the [fonts](assets/src/main/resources/assets/fonts/) folder. Preferably, add a regular and bold variant.
 * In the android project, open the  [TuxBlocksGameActivity.java](android/src/main/java/tuxkids/tuxblocks/android/TuxBlocksGameActivity.java) class and add your font registration to the existing fonts. Again, make sure to register both regular and bold variants. If you have no bold variant, register the regular .TTF for both variants.
 
5. **Add you language to the [Lang.java](core/src/main/java/tuxkids/tuxblocks/core/Lang.java) class.** Find the Langauge enum and add an entry for your language. The arguments are the languages name, the name of the directory where you stored your language files (which should be the country code), the word for "Welcome" in you language, and optionally the name of the font needed for your langauge (without the file extension), if different than Arial. For instance:
    
        EN("English", "en", "Welcome")
    
6. **HTML5 Font Registration.** This is an experimental feature under development, but you can try it on and let us know about results. In the [TuxBlocksGameHtml.java](html/src/main/java/tuxkids/tuxblocks/html/TuxBlocksGameHtml.java) class, you can use the platform object to register the metrics for your font, so it will render more appropriately on the HTML5 platform. For more see [here](https://code.google.com/p/playn/wiki/CustomFonts).

Future Work
-----------

A list of intended improvements and bug fixes can be found in the [TODO](TODO.md) file.
