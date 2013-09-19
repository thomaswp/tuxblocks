package tuxkids.tuxblocks.core.solve.blocks.layer;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Cache.Key;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/**
 * {@link BlockLayer} used with {@link BlockHolder}s.
 */
public class EmptyBlockLayer extends BlockLayerDefault {

	private static Key hKey = Key.fromClass(EmptyBlockLayer.class, "h");
	private static Key vKey = Key.fromClass(EmptyBlockLayer.class, "v");
	
	public EmptyBlockLayer(float width, float height) {
		super(" ", width, height);
		borderWidth = 3;
		updateSize();
	}
	
	@Override
	protected void createBorderLayers() {
		float length = 14;
		
		// thicker, dashed borders
		Image hImage = Cache.getImage(hKey);
		if (hImage == null) {
			CanvasImage hBorderImage = CanvasUtils.createRect(length, 1, Colors.BLACK);
			hBorderImage.canvas().clearRect(0, 0, hBorderImage.width() / 2, 1);
			hBorderImage.setRepeat(true, true);
			hImage = Cache.putImage(hKey, hBorderImage);
		}
		Image vImage = Cache.getImage(vKey);
		if (vImage == null) {
			CanvasImage vBorderImage = CanvasUtils.createRect(1, length, Colors.BLACK);
			vBorderImage.canvas().clearRect(0, 0, 1, vBorderImage.height() / 2);
			vBorderImage.setRepeat(true, true);
			vImage = Cache.putImage(vKey, vBorderImage);
		}
		borderLayers = new ImageLayer[4];
		for (int i = 0; i < 4; i++) {
			borderLayers[i] = graphics().createImageLayer(i % 2 == 0 ? hImage : vImage);
			layer.add(borderLayers[i]);
		}
	}
}
