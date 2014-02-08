package tuxkids.tuxblocks.android;

import java.util.Locale;

import playn.android.AndroidGraphics;
import playn.android.GameActivity;
import playn.core.Font.Style;
import playn.core.PlayN;
import tuxkids.tuxblocks.core.TuxBlocksGame;
import android.view.Menu;

public class TuxBlocksGameActivity extends GameActivity {

	@Override
	public void main(){
		AndroidGraphics graphics = (AndroidGraphics) PlayN.graphics();
		graphics.registerFont("fonts/RAAVI.TTF", "Raavi", Style.PLAIN);
		graphics.registerFont("fonts/RAAVIB.TTF", "Raavi", Style.BOLD);
		graphics.registerFont("fonts/MANGAL.TTF", "Mangal", Style.PLAIN);
		graphics.registerFont("fonts/MANGALB.TTF", "Mangal", Style.BOLD);
		PlayN.run(new TuxBlocksGame(Locale.getDefault().getLanguage()));
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
