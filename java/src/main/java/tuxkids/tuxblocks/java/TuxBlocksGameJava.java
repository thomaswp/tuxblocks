package tuxkids.tuxblocks.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import tuxkids.tuxblocks.core.TuxBlocksGame;

public class TuxBlocksGameJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    config.width = 1000;
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new TuxBlocksGame());
  }
}
