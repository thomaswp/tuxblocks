package tuxkids.tuxblocks.core.layers;

import java.util.ArrayList;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Listener;
import playn.core.util.Callback;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.layers.ImageLayerLike.Factory;

public class NinepatchLayer extends PlayNObject implements ImageLayerLike {

	private Factory factory;
	private GroupLayer layer;
	private ImageLayerLike[][] imageLayers;
	private int[] widthDims, heightDims;
	private int imageWidth, imageHeight;
	private float width, height;
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	@Override
	public Layer layerAddable() {
		return layer;
	}
	
	@Override
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		onSizeChanged();
	}
	
	public void setWidth(float width) {
		setSize(width, height);
	}
	
	public void setHeight(float height) {
		setSize(width, height);
	}

	@Override
	public void setTranslation(float x, float y) {
		layer.setTranslation(x, y);
	}

	@Override
	public void setVisible(boolean visible) {
		layer.setVisible(visible);
	}

	public NinepatchLayer(Factory factory, Image image) {
		this(factory, image, null, null);
	}
	
	public NinepatchLayer(Factory factory, Image image, final int[] widthDims, final int[] heightDims) {
		this.factory = factory;
		layer = graphics().createGroupLayer();
		image.addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				load(result, widthDims, heightDims);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			};
		});
	}
	
	public void destroy() {
		layer.destroy();
	}
	
	private void load(Image image, int[] widthDims, int[] heightDims) {
		imageWidth = (int)image.width();
		imageHeight = (int)image.height();
		
		int xOffset = 0, yOffset = 0;
		if (widthDims == null || heightDims == null) {
			imageWidth -= 2;
			imageHeight -= 2;
			
			int[] topPixels = new int[imageWidth];
			image.getRgb(1, 0, imageWidth, 1, topPixels, 0, imageWidth);
			
			int[] sidePixels = new int[imageHeight];
			image.getRgb(0, 1, 1, imageHeight, sidePixels, 0, 1);
			
			widthDims = getDims(topPixels);
			heightDims = getDims(sidePixels);
			
			xOffset = yOffset = 1;
		}
		
		this.widthDims = widthDims;
		this.heightDims = heightDims;
		
		imageLayers = new ImageLayerLike[heightDims.length][widthDims.length];
		int y = 0;
		for (int i = 0; i < heightDims.length; i++) {
			int x = 0;
			for (int j = 0; j < widthDims.length; j++) {
				if (widthDims[j] > 0 && heightDims[i] > 0) {
					Image subImage = image.subImage(x + xOffset, y + yOffset, widthDims[j], heightDims[i]);
					imageLayers[i][j] = factory.create(subImage);
					layer.add(imageLayers[i][j].layerAddable());
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
		float x = 0, y = 0;
		if (imageLayers == null) return;
		for (int i = 0; i < heightDims.length; i++) {
			float yPlus = 0;
			for (int j = 0; j < widthDims.length; j++) {
				if (imageLayers[i][j] == null) continue;
				float width = getLength(j, this.width, widthDims);
				float height = getLength(i, this.height, heightDims);
				imageLayers[i][j].setTranslation(x, y);
				if (width > 0 && height > 0) {
					float displayWidth = width, displayHeight = height;
					if (graphics().ctx() == null) {
						//HTML5 Canvas shows some seams, so stretch a little more
						displayHeight += 1f;
						displayWidth += 1f;
					}
					imageLayers[i][j].setSize(displayWidth, displayHeight);
					imageLayers[i][j].setVisible(true);
					w = x + width;
					h = y + height;
					x += width;
					yPlus = height;
				} else {
					imageLayers[i][j].setVisible(false);
				}
			}
			y += yPlus;
			x = 0;
		}
		float scaleX = width / w;
		float scaleY = height / h;
		layer.setScale(scaleX, scaleY);
		
	}
	
	private float getLength(int index, float total, int[] dims) {
		if (index % 2 == 1) {
			float d = total;
			for (int i = 0; i < dims.length; i += 2) d -= dims[i];
			return d / (dims.length / 2);
		}
		return dims[index];
	}
	
	private int[] getDims(int[] pixels) {
		int black = Colors.BLACK;
		ArrayList<Integer> dims = new ArrayList<Integer>();
		int current = 0;
		boolean stretching = false;
		for (int i = 0; i < pixels.length; i++) {
			boolean isBlack = pixels[i] == black;
			if (stretching != isBlack) {
				dims.add(current);
				current = 0;
			}
			stretching = isBlack;
			current++;
		}
		dims.add(current);
		int[] dimsArray = new int[dims.size()];
		for (int i = 0; i < dimsArray.length; i++) dimsArray[i] = dims.get(i);
		return dimsArray;
	}

	@Override
	public void setTint(int tint) {
		for (int i = 0; i < heightDims.length; i++) {
			for (int j = 0; j < widthDims.length; j++) {
				if (imageLayers[i][j] != null) {
					imageLayers[i][j].setTint(tint);
				}
			}
		}
	}

	@Override
	public void setTint(int baseColor, int tintColor, float perc) {
		for (int i = 0; i < heightDims.length; i++) {
			for (int j = 0; j < widthDims.length; j++) {
				if (imageLayers[i][j] != null) {
					imageLayers[i][j].setTint(baseColor, tintColor, perc);
				}
			}
		}
	}

	@Override
	public void addListener(Listener pointerListener) {
		for (int i = 0; i < heightDims.length; i++) {
			for (int j = 0; j < widthDims.length; j++) {
				if (imageLayers[i][j] != null) {
					imageLayers[i][j].addListener(pointerListener);
				}
			}
		}
	}
}
