package tuxkids.tuxblocks.core.tutorial;

import playn.core.GroupLayer;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.layers.LayerWrapper;

public class TutorialLayer extends LayerWrapper {

	protected final GroupLayer layer;
	protected final float width, height;
	
	protected TextBoxLayer textBox; 
	
	public TutorialLayer() {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		width = graphics().width();
		height = graphics().height();
		setDepth(1000);
		graphics().rootLayer().add(layer);
	}

	public void showMessage(String text) {
		if (textBox != null) {
			textBox.destroy();
		}
		textBox = new TextBoxLayer(text, width * 0.85f);
		textBox.setTranslation(width * 0.1f, height * 0.90f - textBox.height);
		layer.add(textBox.layerAddable());
	}
	
	public void paint(Clock clock) {
		if (textBox != null) {
			textBox.paint(clock);
		}
	}

}
