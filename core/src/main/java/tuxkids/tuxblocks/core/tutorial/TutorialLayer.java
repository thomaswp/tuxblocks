package tuxkids.tuxblocks.core.tutorial;

import playn.core.Pointer.Event;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.HeaderLayer;


/**
 * Overlay layer shown during the tutorial
 */
public class TutorialLayer extends TextBoxDisplayLayer {
	
	protected final Button buttonRepeat, buttonCancel;

	public TutorialLayer() {
		setDepth(50);
		graphics().rootLayer().add(layer);
		
		float size = HeaderLayer.defaultButtonSize() * 0.7f;
		buttonRepeat = new Button(Constant.BUTTON_RESET, size, size, true);
		buttonRepeat.setPosition(width / 2 - size * 0.6f, height - buttonRepeat.height() * 0.75f);
		buttonRepeat.layerAddable().setDepth(touchCatcher.depth() + 1);
		buttonRepeat.setTint(Colors.LIGHT_GRAY, 0.4f);
		layer.add(buttonRepeat.layerAddable());
		
		buttonCancel = new Button(Constant.BUTTON_CANCEL, size, size, true);
		buttonCancel.setPosition(width / 2 + size * 0.6f, buttonRepeat.y());
		buttonCancel.layerAddable().setDepth(buttonRepeat.layerAddable().depth());
		buttonCancel.setTint(Colors.LIGHT_GRAY, 0.4f);
		buttonCancel.setCancelSound();
		layer.add(buttonCancel.layerAddable());
		
		buttonRepeat.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					repeatMessage();
					Tutorial.messageRepeated();
				}
			}
		});
		
		buttonCancel.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				// make the player confirm a cancel
				if (cancelling) {
					Tutorial.clear();
				} else {
					cancelling = true;
					buttonCancel.setTint(Colors.darker(Colors.RED), Colors.RED);
					touchCatcher.setVisible(true);
				}
			}
		});
	}
	
	@Override
	public void onPointerStart(Event event) {
		super.onPointerStart(event);
		if (cancelling) {
			// cancel the cancel
			cancelling = false;
			buttonCancel.setTint(Colors.LIGHT_GRAY, 0.4f);
		}
	}
}
