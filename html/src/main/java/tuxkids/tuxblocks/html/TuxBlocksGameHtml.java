package tuxkids.tuxblocks.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import tuxkids.tuxblocks.core.TuxBlocksGame;

public class TuxBlocksGameHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform platform = HtmlPlatform.register(config);
    platform.assets().setPathPrefix("tuxblocks/");
    PlayN.run(new TuxBlocksGame());
  }
}
