package tuxkids.tuxblocks.core.tutorial;

import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class TextBoxDisplayLayer extends LayerWrapper implements Listener {

	protected final GroupLayer layer;
	protected final float width, height;
	protected final ImageLayer touchCatcher;
	protected final TextBoxLayer textBox; 
	
	protected boolean cancelling;
	
	public TextBoxDisplayLayer() {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		width = graphics().width();
		height = graphics().height();
		
		textBox = new TextBoxLayer(width * 0.85f);
		textBox.setTranslation(width * 0.1f, height * 0.90f);
		textBox.setDepth(1);
		layer.add(textBox.layerAddable());
		
		// catch touches to dismiss the textbox
		touchCatcher = graphics().createImageLayer(CanvasUtils.createRect(1, 1, CanvasUtils.TRANSPARENT));
		touchCatcher.setDepth(10);
		touchCatcher.setSize(width, height);
		touchCatcher.addListener(this);
		touchCatcher.setVisible(false);
		layer.add(touchCatcher);
	}
	
	/** Shows the given message in the {@link TextBoxLayer} */
	public void showMessage(String text) {
		textBox.show(text);
		touchCatcher.setVisible(true);
	}
	
	/** Repeats the last shown message */
	public void repeatMessage() {
		textBox.show(null);
		touchCatcher.setVisible(true);
	}

	public void hide() {
		textBox.hide();
	}
	
	/** Called from {@link TutorialInstance#paint(Clock)} */
	public void paint(Clock clock) {
		textBox.paint(clock);
	}

	@Override
	public void onPointerStart(Event event) {
		textBox.hide();
		touchCatcher.setVisible(false);
		Audio.se().play(Constant.SE_OK);
	}

	@Override
	public void onPointerEnd(Event event) { }

	@Override
	public void onPointerDrag(Event event) { }

	@Override
	public void onPointerCancel(Event event) { }

}
