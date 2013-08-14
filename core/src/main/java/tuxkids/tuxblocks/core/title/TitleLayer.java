package tuxkids.tuxblocks.core.title;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.Surface;
import playn.core.util.Callback;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class TitleLayer extends LayerWrapper implements Listener {
	
	private final static int WAVE_MAX = 10000, WAVE_START = 1500;
	
	protected final GroupLayer layer;
	protected final Image image;
	protected final ImageLayer touchCatcher;
	
	protected Block[][] blocks;
	protected float boundsWidth, boundsHeight;
	protected int pxWidth, pxHeight;
	protected int width, height;
	protected int pxSize;
	private int waveCountdown = WAVE_MAX;
	
	protected boolean snapped;
	
	public void snap() {
		snapped = true;
	}
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public TitleLayer(final float targetWidth) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		
		image = assets().getImage(Constant.IMAGE_LOGO);
		image.addCallback(new Callback<Image>() {

			@Override
			public void onSuccess(Image result) {
				setup(targetWidth);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
		
		touchCatcher = graphics().createImageLayer(
				CanvasUtils.createRect(1, 1, CanvasUtils.TRANSPARENT));
		touchCatcher.setSize(gWidth(), gHeight());
		touchCatcher.setDepth(100);
		touchCatcher.addListener(this);
		layer.add(touchCatcher);
	}
	
	private void setup(float targetWidth) {
		pxWidth = (int) image.width();
		pxHeight = (int) image.height();
		pxSize = (int) (targetWidth / image.width());
		width = pxSize * pxWidth;
		height = pxSize * pxHeight;
		boundsWidth = graphics().width();
		boundsHeight = graphics().height();
		
		int[] rgbArray = new int[4 * pxWidth * pxHeight];
		image.getRgb(0, 0, pxWidth, pxHeight, rgbArray, 0, pxWidth);
		
		ImmediateLayer render = graphics().createImmediateLayer(new Renderer() {
			@Override
			public void render(Surface surface) {
				for (int i = 0; i < pxWidth; i++) {
					for (int j = 0; j < pxHeight; j++) {
						Block block = blocks[i][j];
						if (block != null) {
							surface.drawImage(block.image, block.x(), block.y());
						}
					}
				}
			}
		});
		layer.add(render);
		
		blocks = new Block[pxWidth][pxHeight];
		for (int i = 0; i < pxWidth; i++) {
			for (int j = 0; j < pxHeight; j++) {
				int color = rgbArray[j * pxWidth + i];
				if (color != 0) {
					Image blockImage = CanvasUtils.createRectCached(pxSize, pxSize, color, 1, Colors.BLACK);
					Block block = new Block(blockImage);
					block.setPosition((float) Math.random() * boundsWidth, (float) Math.random() * boundsHeight);
					blocks[i][j] = block;
				}
			}
		}
	}

	public void update(int delta) {
		
	}
	
	public void paint(Clock clock) {
		if (!image.isReady()) return;
		
		waveCountdown -= clock.dt();
		if (waveCountdown < 0) {
			waveCountdown += WAVE_MAX;
		} else if (waveCountdown < WAVE_START) {
			float perc = 1 - (float)waveCountdown / WAVE_START;
			int start = Math.max(Math.round(perc * pxWidth) - 2, 0);
			int end = start + 6;
			for (int i = start; i < end && i < pxWidth; i++) {
				for (int j = 0; j < pxHeight; j++) {
					if (blocks[i][j] != null) {
						Block block = blocks[i][j];
						block.velocity.x += 0.002f * clock.dt();
						block.velocity.y += 0.005f * clock.dt() * ((float)j / pxHeight - 0.5f);
					}
				}
			}
		}
		
		for (int i = 0; i < pxWidth; i++) {
			for (int j = 0; j < pxHeight; j++) {
				if (blocks[i][j] != null) {
					Block block = blocks[i][j];
					if (snapped) {
						block.velocity.x = lerpTime(block.velocity.x, 0, 0.995f, clock.dt(), 0.001f);
						block.velocity.y = lerpTime(block.velocity.y, 0, 0.995f, clock.dt(), 0.001f);
						block.position.x = lerpTime(block.x(), i * pxSize, 0.995f, clock.dt(), 0.1f);
						block.position.y = lerpTime(block.y(), j * pxSize, 0.995f, clock.dt(), 0.1f);
					} else {
						float x = block.x();
						float y = block.y();
						if (x < 0) x += boundsWidth;
						if (x > boundsWidth) x -= boundsWidth;
						if (y < 0) y += boundsHeight;
						if ( y > boundsHeight) y -= boundsHeight;
						block.setPosition(x, y);
					}
					
					block.paint(clock);
				}
			}
		}
	}
	
	private static class Block {
		public Vector position = new Vector();
		public Vector velocity = new Vector();
		public final Image image;
		
		public Block(Image image) {
			this.image = image;
			velocity.set(((float) Math.random() - 0.5f) / 10,
					((float) Math.random() - 0.5f) / 10);
		}
		
		public float x() { return position.x; }
		public float y() { return position.y; }
		
		public void setPosition(float x, float y) {
			position.set(x, y);
		}
		
		public void paint(Clock clock) {
			position.x = x() + velocity.x * clock.dt();
			position.y = y() + velocity.y * clock.dt();
		}
	}

	private void disturb(float x, float y, float force) {
		for (int i = 0; i < pxWidth; i++) {
			for (int j = 0; j < pxHeight; j++) {
				if (blocks[i][j] != null) {
					Block block = blocks[i][j];
					float distance = distance(x, y, block.x(), block.y());
					float f = force * pxSize -  distance;
					if (f > 0) {
						float dx = block.x() - x;
						float dy = block.y() - y;
						block.velocity.x += dx / 200;
						block.velocity.y += dy / 200;
					}
				}
			}
		}
	}
	
	@Override
	public void onPointerStart(Event event) {
		disturb(event.x(), event.y(), 10);
	}

	@Override
	public void onPointerEnd(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPointerDrag(Event event) {
		disturb(event.x(), event.y(), 5);
	}

	@Override
	public void onPointerCancel(Event event) {
		// TODO Auto-generated method stub
		
	}
}
