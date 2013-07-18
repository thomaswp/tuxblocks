package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.Pointer.Listener;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class BlockLayer extends LayerWrapper implements ImageLayerLike {

	protected static TextFormat textFormat;
	
	protected GroupLayer layer;
	protected ImageLayerLike centerLayer;
	protected ImageLayer textLayer;
	protected ImageLayer[] borderLayers;
	protected float width, height;
	protected String text;
	protected float borderWidth = 1;

	@Override
	public float width() {
		return width;
	}

	@Override
	public float height() {
		return height;
	}

	@Override
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		updateSize();
	}

	@Override
	public void setWidth(float width) {
		this.width = width;
		updateSize();
	}

	@Override
	public void setHeight(float height) {
		this.height = height;
		updateSize();
	}
	
	@Override
	public void setTint(int tint) {
		centerLayer.setTint(tint);
	}

	@Override
	public void setTint(int baseColor, int tintColor, float perc) {
		centerLayer.setTint(baseColor, tintColor, perc);
	}
	
	public BlockLayer(String text, float width, float height) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) super.layerAddable();
		this.text = text;
		createLayers();
		setSize(width, height);
	}
	
	private void createLayers() {
		if (textFormat == null) {
			Font font = graphics().createFont(Constant.FONT_NAME, Font.Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		
		refreshTextLayer();
		createBorderLayers();
		
		centerLayer = new ImageLayerTintable(CanvasUtils.createRect(1, 1, Colors.WHITE));
		centerLayer.layerAddable().setDepth(-1);
		layer.add(centerLayer.layerAddable());
	}
	
	protected void createBorderLayers() {
		Image borderImage = CanvasUtils.createRect(1, 1, Colors.BLACK);
		borderLayers = new ImageLayer[4];
		for (int i = 0; i < 4; i++) {
			borderLayers[i] = graphics().createImageLayer(borderImage);
			layer.add(borderLayers[i]);
		}
	}
	
	protected void refreshTextLayer() {
		textLayer = graphics().createImageLayer(CanvasUtils.createString(textFormat, text, Colors.BLACK));
		centerImageLayer(textLayer);
		textLayer.setDepth(1);
		layer.add(textLayer);
	}
	
	protected void updateSize() {
		textLayer.setTranslation(width / 2, height / 2);
		centerLayer.setTranslation(0, 0);
		centerLayer.setSize(width, height);
		
		borderLayers[0].setTranslation(0, 0);
		borderLayers[0].setSize(width - borderWidth, borderWidth);
		
		borderLayers[1].setTranslation(width - borderWidth, 0);
		borderLayers[1].setSize(borderWidth, height - borderWidth);
		
		borderLayers[2].setTranslation(borderWidth, height - borderWidth);
		borderLayers[2].setSize(width - borderWidth, borderWidth);
		
		borderLayers[3].setTranslation(0, borderWidth);
		borderLayers[3].setSize(borderWidth, height - borderWidth);
	}
	
	@Override
	public void addListener(Listener pointerListener) {
		centerLayer.addListener(pointerListener);
	}
	
	@Override
	public void setInteractive(boolean interactive) {
		centerLayer.setInteractive(interactive);
	}
}
