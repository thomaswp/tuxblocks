package tuxkids.tuxblocks.core.layers;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.util.Callback;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;

public class NinepatchLayer extends PlayNObject {

	private GroupLayer layer;
	private ImageLayer[][] imageLayers;
	private int[] widthDims, heightDims;
	private int imageWidth, imageHeight;
	private float width, height;
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public Layer layerAddable() {
		return layer;
	}
	
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		onSizeChanged();
	}

	public NinepatchLayer(Image image) {
		layer = graphics().createGroupLayer();
		image.addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				load(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			};
		});
	}
	
	private void load(Image image) {
		imageWidth = (int)image.width() - 2;
		imageHeight = (int)image.height() - 2;
		
		int[] topPixels = new int[imageWidth];
		image.getRgb(1, 0, imageWidth, 1, topPixels, 0, imageWidth);
		
		int[] sidePixels = new int[imageHeight];
		image.getRgb(0, 1, 1, imageHeight, sidePixels, 0, 1);
		
		widthDims = getDims(topPixels);
		heightDims = getDims(sidePixels);
		
		imageLayers = new ImageLayer[3][];
		int y = 0;
		for (int i = 0; i < 3; i++) {
			imageLayers[i] = new ImageLayer[3];
			int x = 0;
			for (int j = 0; j < 3; j++) {
				if (widthDims[j] > 0 && heightDims[j] > 0) {
					Image subImage = image.subImage(x + 1, y + 1, widthDims[j], heightDims[i]);
					imageLayers[i][j] = graphics().createImageLayer(subImage);
					layer.add(imageLayers[i][j]);
				}
				x += widthDims[j];
			}
			y += heightDims[i];
		}
		
		if (width == 0 || height == 0) {
			setSize(imageWidth, imageHeight);
		} else {
			onSizeChanged();
		}
	}
	
	private void onSizeChanged() {
		float w = 0, h = 0;
		if (imageLayers == null) return;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (imageLayers[i][j] == null) continue;
				float width = getLength(j, this.width, widthDims);
				float height = getLength(i, this.height, heightDims);
				float x = getPos(j, this.width, widthDims);
				float y = getPos(i, this.height, heightDims);
				imageLayers[i][j].setTranslation(x, y);
				if (width > 0 && height > 0) {
					imageLayers[i][j].setSize(width, height);
					imageLayers[i][j].setVisible(true);
					w = imageLayers[i][j].tx() + imageLayers[i][j].width();
					h = imageLayers[i][j].ty() + imageLayers[i][j].height();
				} else {
					imageLayers[i][j].setVisible(false);
				}
			}
		}
		float scaleX = Math.min(width / w, 1);
		float scaleY = Math.min(height / h, 1);
		layer.setScale(scaleX, scaleY);
		
	}
	
	private float getLength(int index, float total, int[] dims) {
		if (index == 1) {
			return total - dims[0] - dims[2];
		}
		return dims[index];
	}
	
	private float getPos(int index, float total, int[] dims) {
		if (index == 0) return 0;
		else if (index == 2) return Math.max(dims[0], total - dims[2]);
		return dims[0];
	}
	
	private int[] getDims(int[] pixels) {
		int black = Colors.BLACK;
		int pixelsBefore = 0, pixelsStretch = 0, pixelsAfter = 0;
		boolean stretched = false;
		for (int i = 0; i < pixels.length; i++) {
			boolean isBlack = pixels[i] == black;
			if (isBlack) {
				stretched = true;
				pixelsStretch++;
			} else {
				if (stretched) {
					pixelsAfter++;
				} else {
					pixelsBefore++;
				}
			}
		}
		return new int[] { pixelsBefore, pixelsStretch, pixelsAfter };
	}
}
