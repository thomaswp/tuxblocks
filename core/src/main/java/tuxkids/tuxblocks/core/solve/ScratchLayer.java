package tuxkids.tuxblocks.core.solve;

import playn.core.Canvas;
import playn.core.Canvas.LineCap;
import playn.core.Canvas.LineJoin;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import pythagoras.f.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class ScratchLayer extends LayerWrapper implements Listener {

	protected final static int SEGMENT = 100;

	protected final GroupLayer layer;
	protected final ImageLayer[][] canvasLayers;
	protected final ImageLayer backgroundLayer;
	protected final CanvasImage[][] images;
	protected final boolean[][] dirty;
	protected final int rows, cols;
	protected final float strokeWidth;

	protected Point lastPoint = new Point();

	public ScratchLayer(float width, float height) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();

		rows = (int)height / SEGMENT + 1;
		cols = (int)width / SEGMENT + 1;
		debug(rows + ", " + cols);

		backgroundLayer = graphics().createImageLayer();
		backgroundLayer.setImage(CanvasUtils.createRect(width, height, Colors.WHITE));
		backgroundLayer.setAlpha(0.95f);
		backgroundLayer.addListener(this);
		backgroundLayer.setDepth(-1);
		layer.add(backgroundLayer);

		canvasLayers = new ImageLayer[rows][cols];
		images = new CanvasImage[rows][cols];
		dirty = new boolean[rows][cols];

		strokeWidth = height / 100;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				ImageLayer canvasLayer = graphics().createImageLayer();
				canvasLayer.setImage(graphics().createImage(SEGMENT, SEGMENT));
				canvasLayer.setTranslation(j * SEGMENT, i * SEGMENT);
				layer.add(canvasLayer);

				CanvasImage image = (CanvasImage) canvasLayer.image();
				image.canvas().setStrokeColor(Colors.BLACK);
				image.canvas().setStrokeWidth(strokeWidth);
				image.canvas().setLineCap(LineCap.ROUND);
				image.canvas().setLineJoin(LineJoin.ROUND);

				images[i][j] = image;
			}
		}
	}

	@Override
	public void onPointerStart(Event event) {
		float x = event.localX(), y = event.localY();
		lastPoint.set(x, y);
	}

	@Override
	public void onPointerEnd(Event event) {
		draw(event);
	}

	@Override
	public void onPointerDrag(Event event) {
		draw(event);
	}

	private void draw(Event event) {
		float x = event.localX(), y = event.localY();

		float minX = Math.min(x, lastPoint.x) - strokeWidth;
		float maxX = Math.max(x, lastPoint.x) + strokeWidth;
		float minY = Math.min(y, lastPoint.y) - strokeWidth;
		float maxY = Math.max(y, lastPoint.y) + strokeWidth;


		int startRow = (int)(minY / SEGMENT);
		int endRow = (int)(maxY / SEGMENT);
		int startCol = (int)(minX / SEGMENT);
		int endCol = (int)(maxX / SEGMENT);

		for (int i = startRow; i <= endRow; i++) {
			for (int j = startCol; j <= endCol; j++) {
				if (i < 0 || i >= rows) continue;
				if (j < 0 || j >= cols) continue;

				dirty[i][j] = true;
				int offsetX = j * SEGMENT, offsetY = i * SEGMENT;
				images[i][j].canvas().drawLine(
						lastPoint.x - offsetX, lastPoint.y - offsetY, 
						x - offsetX, y - offsetY);
			}
		}

		lastPoint.set(x, y);
	}

	@Override
	public void onPointerCancel(Event event) { }

	public void clear() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (dirty[i][j]) {
					images[i][j].canvas().clear();
					dirty[i][j] = false;
				}
			}
		}
	}
}
