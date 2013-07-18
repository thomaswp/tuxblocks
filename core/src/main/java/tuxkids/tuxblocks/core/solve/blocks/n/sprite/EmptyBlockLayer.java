package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.CanvasImage;
import playn.core.ImageLayer;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class EmptyBlockLayer extends BlockLayer {

	public EmptyBlockLayer(float width, float height) {
		super("", width, height);
		borderWidth = 3;
		updateSize();
	}
	
	@Override
	protected void createBorderLayers() {
		float length = 14;
		CanvasImage hBorderImage = CanvasUtils.createRect(length, 1, Colors.BLACK);
		hBorderImage.canvas().clearRect(0, 0, hBorderImage.width() / 2, 1);
		hBorderImage.setRepeat(true, true);
		CanvasImage vBorderImage = CanvasUtils.createRect(1, length, Colors.BLACK);
		vBorderImage.canvas().clearRect(0, 0, 1, vBorderImage.height() / 2);
		vBorderImage.setRepeat(true, true);
		borderLayers = new ImageLayer[4];
		for (int i = 0; i < 4; i++) {
			borderLayers[i] = graphics().createImageLayer(i % 2 == 0 ? hBorderImage : vBorderImage);
			layer.add(borderLayers[i]);
		}
	}
}
