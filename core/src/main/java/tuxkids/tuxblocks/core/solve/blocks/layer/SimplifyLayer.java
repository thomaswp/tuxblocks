package tuxkids.tuxblocks.core.solve.blocks.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.Sprite;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class SimplifyLayer extends LayerWrapper {

	private GroupLayer layer;
	private Simplifiable parent;
	
	public SimplifyLayer(Simplifiable parent) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		this.parent = parent;
	}

	private static Image simplifyImage;
	private List<ImageLayer> simplifyButtons = new ArrayList<ImageLayer>();
	private HashMap<ImageLayer, ModifierBlock> simplifyMap = new HashMap<ImageLayer, ModifierBlock>();
	private HashMap<ImageLayer, ModifierBlock> pairMap = new HashMap<ImageLayer, ModifierBlock>();
	private Listener simplifyListener = new Listener() {
		@Override
		public void onPointerStart(Event event) { onSimplify(event.hit()); }
		@Override
		public void onPointerEnd(Event event) { }
		@Override
		public void onPointerDrag(Event event) { }
		@Override
		public void onPointerCancel(Event event) { }
	};
	
	public ImageLayer getSimplifyButton(ModifierBlock sprite) {
		return getSimplifyButton(sprite, null, 0);
	}
	
	public ImageLayer getSimplifyButton(ModifierBlock sprite, ModifierBlock pair) {
		return getSimplifyButton(sprite, pair, 0);
	}
	
	public ImageLayer getSimplifyButton(ModifierBlock sprite, ModifierBlock pair, int depth) {
		while (simplifyButtons.size() <= simplifyMap.size()) { 
			addSimplifyButton();
		}
		ImageLayer layer = simplifyButtons.get(simplifyMap.size());
		simplifyMap.put(layer, sprite);
		pairMap.put(layer, pair);
		layer.setVisible(true);
		layer.setDepth(depth);
		return layer;
	}
	
	private void addSimplifyButton() {
		if (simplifyImage == null) {
			float radius = Sprite.modSize() * 0.35f;
			if (PlayN.touch().hasTouch()) {
				simplifyImage = graphics().createImage(radius * 3f, radius * 3f);
				Canvas canvas = ((CanvasImage) simplifyImage).canvas();
				Image image = CanvasUtils.createCircle(radius, Colors.GRAY, 1, Colors.BLACK);
				canvas.drawImage(image, (simplifyImage.width() - image.width()) / 2, (simplifyImage.height() - image.height()) / 2);
			} else {
				simplifyImage = CanvasUtils.createCircle(radius, Colors.GRAY, 1, Colors.BLACK);
			}
		}
		ImageLayer simplifyButton = graphics().createImageLayer(simplifyImage);
		simplifyButton.setAlpha(0.5f);
		simplifyButton.setVisible(false);
		simplifyButton.addListener(simplifyListener );
		centerImageLayer(simplifyButton);
		simplifyButtons.add(simplifyButton);
		layer.add(simplifyButton);
	}
	
	private void onSimplify(Layer hit) {
		ModifierBlock sprite = simplifyMap.get(hit);
		if (sprite != null) {
			ModifierBlock pair = pairMap.get(hit);
			parent.simplify(sprite, pair);
		}
	}
	
	public void update() {
		for (ImageLayer button : simplifyButtons) {
			button.setVisible(false);
		}
		simplifyMap.clear();
		if (parent.showSimplify()) {
			parent.updateSimplify();
		}
	}
	
	public interface Simplifiable {
		void updateSimplify();
		void simplify(ModifierBlock sprite, ModifierBlock pair);
		boolean showSimplify();
	}
}
