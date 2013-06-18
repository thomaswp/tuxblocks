package tuxkids.tuxblocks.flash;

import playn.core.PlayN;
import playn.flash.FlashGame;
import playn.flash.FlashPlatform;
import tuxkids.tuxblocks.core.TuxBlocksGame;

public class TuxBlocksGameFlash extends FlashGame {

  @Override
  public void start() {
    FlashPlatform platform = FlashPlatform.register();
    platform.assets().setPathPrefix("cuteflash/");
    PlayN.run(new TuxBlocksGame());
  }
}
