package tuxkids.tuxblocks.core.widget;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.Pointer.Event;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;


public class ContinueMenuLayer extends MenuLayer {
	
	private static ContinueMenuLayer instance; 
	
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
		
		TextFormat format = createFormat(height / 6);
		textLayer = graphics().createImageLayer();
		textLayer.setImage(CanvasUtils.createText("Continue from save?", format, Colors.BLACK));
		textLayer.setTy(height * -0.2f);
		centerImageLayer(textLayer);
		layer.add(textLayer);
		
		float buttonTextSize = height / 8f;
		buttonContinue = new Button(null, false);
		buttonContinue.setPosition(width * -0.25f, height * 0.2f);
		createButton(buttonContinue, width * 0.4f, "Continue", buttonTextSize, new OnReleasedListener() {
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
		createButton(buttonStartOver, width * 0.4f, "Start Over", buttonTextSize, new OnReleasedListener() {
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
