package tuxkids.tuxblocks.android;

import playn.android.GameActivity;
import playn.core.PlayN;
import tuxkids.tuxblocks.core.TuxBlocksGame;
import android.view.Menu;

public class TuxBlocksGameActivity extends GameActivity {

	@Override
	public void main(){
		PlayN.run(new TuxBlocksGame());
	}

	@Override 
	public void onBackPressed () {
		if (TuxBlocksGame.screenDepth() == 0) {
			finish();
		}
	} 
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return false; //stops the menu from opening on Android 4+
	}
}
