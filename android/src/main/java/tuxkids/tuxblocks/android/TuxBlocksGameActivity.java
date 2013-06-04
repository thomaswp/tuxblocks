package tuxkids.tuxblocks.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import tuxkids.tuxblocks.core.TuxBlocksGame;

public class TuxBlocksGameActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new TuxBlocksGame());
  }
}
