package test.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import test.core.TestGameII;

public class TestGameIIActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new TestGameII());
  }
}
