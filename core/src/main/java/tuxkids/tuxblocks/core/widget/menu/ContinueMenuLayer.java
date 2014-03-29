package tuxkids.tuxblocks.core.widget.menu;

import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

/**
 * A {@link MenuLayer} for asking if players want to continue the 
 * previous game or start a new one.
 */
public class ContinueMenuLayer extends MenuLayer {
	
	// keep an instance so it's only created once
	private static ContinueMenuLayer instance; 
	
	/** Shows this menu with a listener for the response */
	public static void show(ResponseListener listener) {
		if (instance == null || instance.destroyed()) {
			instance = new ContinueMenuLayer();
		}
		instance.listener = listener;
		show(instance);
	}

	protected final ImageLayer textLayer;
	protected final Button buttonContinue, buttonStartOver;
	
	protected ResponseListener listener;
	
	public ContinueMenuLayer() {
		super(gWidth() * 0.5f, gHeight() * 0.4f);
		
		float buttonWidth = width * 0.4f;
		
		TextFormat format = createFormat(height / 6);
		textLayer = graphics().createImageLayer();
		textLayer.setImage(CanvasUtils.createText(getString(key_continueFromSave), format, Colors.BLACK));
		textLayer.setTy(height * -0.2f);
		centerImageLayer(textLayer);
		layer.add(textLayer);
		
		float buttonTextSize = height / 8f;
		buttonContinue = new Button(null, false);
		buttonContinue.setPosition(width * -0.25f, height * 0.2f);
		setButton(buttonContinue, buttonWidth, getString(key_continue), buttonTextSize, new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton && listener != null) {
					listener.responded(true);
					hideInstance();
				}
			}
		});
		layer.add(buttonContinue.layerAddable());
		
		buttonStartOver = new Button(null, false);
		buttonStartOver.setPosition(width * 0.25f, height * 0.2f);
		setButton(buttonStartOver, buttonWidth, getString(key_startOver), buttonTextSize, new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton && listener != null) {
					listener.responded(false);
					hideInstance();
				}
			}
		});
		layer.add(buttonStartOver.layerAddable());
		
	}
	
	protected void showInstance(ResponseListener listener) {
		super.showInstance();
		this.listener = listener;
	}

	public interface ResponseListener {
		void responded(boolean coninue);
	}
}
