package tuxkids.tuxblocks.core.layers;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.TextLayout;
import pythagoras.f.Rectangle;

public class SegmentedCanvasLayer extends LayerWrapper {


	protected final GroupLayer layer;
	protected final ImageLayer backgroundLayer;
	protected final Canvas[][] canvases;
	protected final boolean[][] dirty;
	protected final int rows, cols;
	protected final float height, width;
	protected final int segmentSize;
	protected final Canvas testCanvas;

	public SegmentedCanvasLayer(float width, float height, int segmentSize) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();

		this.width = width;
		this.height = height;
		this.segmentSize = segmentSize;

		rows = (int)height / segmentSize + 1;
		cols = (int)width / segmentSize + 1;
		debug(rows + ", " + cols);

		backgroundLayer = graphics().createImageLayer();
		backgroundLayer.setDepth(-1);
		layer.add(backgroundLayer);

		canvases = new Canvas[rows][cols];
		dirty = new boolean[rows][cols];


		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				ImageLayer canvasLayer = graphics().createImageLayer();
				canvasLayer.setImage(graphics().createImage(segmentSize, segmentSize));
				canvasLayer.setTranslation(j * segmentSize, i * segmentSize);
				canvases[i][j] = ((CanvasImage) canvasLayer.image()).canvas();
				canvases[i][j].translate(j * segmentSize, i * segmentSize);

				layer.add(canvasLayer);
			}
		}
		
		testCanvas = graphics().createImage(1, 1).canvas();
	}


	private Rectangle rect = new Rectangle();
	public void operate(CanvasOperation op) {
		op.clip().clip(rect);
		op(rect.x, rect.y, rect.maxX(), rect.maxY(), op);
	}
	
//	private void op(CanvasOperation op) {
//		op(0, 0, width, height, op);
//	}

	private void op(float x0, float x1, float y0, float y1,
			CanvasOperation op) {
		float strokeWidth = 0;
		float minX = Math.min(x0, x1) - strokeWidth;
		float maxX = Math.max(x0, x1) + strokeWidth;
		float minY = Math.min(y0, y1) - strokeWidth;
		float maxY = Math.max(y0, y1) + strokeWidth;


		int startRow = (int)(minY / segmentSize);
		int endRow = (int)(maxY / segmentSize);
		int startCol = (int)(minX / segmentSize);
		int endCol = (int)(maxX / segmentSize);

		for (int i = startRow; i <= endRow; i++) {
			for (int j = startCol; j <= endCol; j++) {
				if (i < 0 || i >= rows) continue;
				if (j < 0 || j >= cols) continue;

				dirty[i][j] = true;
				op.operate(canvases[i][j]);
			}
		}
		op.operate(testCanvas);
	}

	public interface CanvasOperation {
		void operate(Canvas canvas);
		Clip clip();
	}
	
	private interface Clip {
		void clip(Rectangle rect);
	}
	
	public static class RectClip implements Clip {
		private float x, y, width, height;
		
		public RectClip(float x, float y, float width, float height) {
			this.x = x; this.y = y;
			this.width = width; this.height = height;
		}
		
		@Override
		public void clip(Rectangle rect) {
			rect.setBounds(x, y, width, height);
		}
	}
	
	public static class CircleClip implements Clip {
		private float x, y, rad;
		
		public CircleClip(float x, float y, float rad) {
			this.x = x; this.y = y;
			this.rad = rad;
		}
		
		@Override
		public void clip(Rectangle rect) {
			rect.setBounds(x - rad, y - rad, rad * 2, rad * 2);
		}
	}
	
	public class AllClip implements Clip {
		@Override
		public void clip(Rectangle rect) {
			rect.setBounds(0, 0, width, height);
		}
	}
	
	public class TextClip implements Clip {
		private float x, y;
		private TextLayout layout;
		
		public TextClip(float x, float y, TextLayout layout) {
			this.x = x; this.y = y;
			this.layout = layout;
		}
		
		@Override
		public void clip(Rectangle rect) {
			rect.setBounds(x, y, x + layout.width(), y + layout.height());
		}
	}

//	private class MultiCanvas implements Canvas {
//
//		@Override
//		public Canvas clear() {
//			for (int i = 0; i < rows; i++) {
//				for (int j = 0; j < cols; j++) {
//					if (dirty[i][j]) {
//						canvases[i][j].clear();
//						dirty[i][j] = false;
//					}
//				}
//			}
//			return this;
//		}
//
//		@Override
//		public Canvas clearRect(final float x, final float y, final float width, final float height) {
//			op(x, y, x + width, x + height, new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.clearRect(x, y, width, height);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas clip(final Path clipPath) {
//			op(new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.clip(clipPath);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas clipRect(final float x, final float y, final float width, final float height) {
//			op(x, y, x + width, x + height, new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.clipRect(x, y, width, height);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Path createPath() {
//			if (rows > 0 && cols > 0) return canvases[0][0].createPath();
//			return null;
//		}
//
//		@Override
//		public Canvas drawImage(final Image image, final float dx, final float dy) {
//			op(dx, dy, dx + image.width(), dy + image.height(), new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.drawImage(image, dx, dy);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas drawImageCentered(Image image, float dx, float dy) {
//			return drawImageCentered(image, dx - image.width() / 2, dy - image.height() / 2);
//		}
//
//		@Override
//		public Canvas drawImage(final Image image, final float dx, final float dy, final float dw,
//				final float dh) {
//			op(dx, dy, dx + dw, dy + dh, new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.drawImage(image, dx, dy, dw, dh);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas drawImage(final Image image, final float dx, final float dy, final float dw,
//				final float dh, final float sx, final float sy, final float sw, final float sh) {
//			op(dx, dy, dx + dw, dy + dh, new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.drawImage(image, dx, dy, dw, dh, sx, sy, sw, sh);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas drawLine(final float x0, final float y0, final float x1, final float y1) {
//			op(x0, y0, x1, y1, new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					drawLine(x0, y0, x1, y1);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas drawPoint(final float x, final float y) {
//			op(x, y, x, y, new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.drawPoint(x, y);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas drawText(final String text, final float x, final float y) {
//			op(new CanvasOperation() {
//				@Override
//				public void operate(Canvas canvas) {
//					canvas.drawText(text, x, y);
//				}
//			});
//			return this;
//		}
//
//		@Override
//		public Canvas fillCircle(float x, float y, float radius) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas fillPath(Path path) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas fillRect(float x, float y, float width, float height) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas fillRoundRect(float x, float y, float width,
//				float height, float radius) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas fillText(TextLayout text, float x, float y) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public float height() {
//			return height;
//		}
//
//		@Override
//		public Canvas restore() {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas rotate(float radians) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas save() {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas scale(float x, float y) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setAlpha(float alpha) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setCompositeOperation(Composite composite) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setFillColor(int color) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setFillGradient(Gradient gradient) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setFillPattern(Pattern pattern) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setLineCap(LineCap cap) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setLineJoin(LineJoin join) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setMiterLimit(float miter) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setStrokeColor(int color) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas setStrokeWidth(float strokeWidth) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas strokeCircle(float x, float y, float radius) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas strokePath(Path path) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas strokeRect(float x, float y, float width, float height) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas strokeRoundRect(float x, float y, float width,
//				float height, float radius) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas strokeText(TextLayout text, float x, float y) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas transform(float m11, float m12, float m21, float m22,
//				float dx, float dy) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public Canvas translate(float x, float y) {
//			// TODO Auto-generated method stub
//			return this;
//		}
//
//		@Override
//		public float width() {
//			return width;
//		}
//
//	}
}
