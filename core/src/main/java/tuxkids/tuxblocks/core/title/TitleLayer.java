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

/**
 * Layer which displays TuxBlocks made out of smaller blocks
 * on the {@link TitleScreen}.
 */
public class TitleLayer extends LayerWrapper implements Listener {
	
	
	// the time (ms) in between the wave animation
	private final static int WAVE_MAX = 10000;
	// the length (me) of the wave animation
	private final static int WAVE_START = 1500;
	
	protected final GroupLayer layer;
	protected final Image image;
	protected final ImageLayer touchCatcher;
	
	protected Block[][] blocks;
	protected float boundsWidth, boundsHeight;
	// dimensions of the image we're building out of blocks (the TuxBlocks text)
	protected int pxWidth, pxHeight;
	// dimensions of the image on-screen
	protected int width, height;
	// the size of one "pixel" (a block) of the loaded image on-screen
	protected int pxSize;
	private int waveCountdown = WAVE_MAX;
	
	// have we snapped the blocks into place
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
		
		// catch all touches on the layer to allow the
		// player to mess with the blocks
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
		
		// render with an ImmediateLayer to avoid the allocation of a bunch of
		// individual ImageLayers. Saves some load time on some devices.
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
		
		// parse the loaded image into pixel blocks
		blocks = new Block[pxWidth][pxHeight];
		for (int i = 0; i < pxWidth; i++) {
			for (int j = 0; j < pxHeight; j++) {
				int color = rgbArray[j * pxWidth + i];
				if (color != 0) {
					// chache them becasue we're likely to reuse them for other pixels
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
			// cause the blocks to animate (to show they're interactive)
			float perc = 1 - (float)waveCountdown / WAVE_START;
			int start = Math.max(Math.round(perc * pxWidth) - 2, 0);
			int end = start + 6;
			for (int i = start; i < end && i < pxWidth; i++) {
				for (int j = 0; j < pxHeight; j++) {
					if (blocks[i][j] != null) {
						// push every block out and to the right
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
						// slow blocks down
						block.velocity.x = lerpTime(block.velocity.x, 0, 0.995f, clock.dt(), 0.001f);
						block.velocity.y = lerpTime(block.velocity.y, 0, 0.995f, clock.dt(), 0.001f);
						// snap blocks back to their original positions
						block.position.x = lerpTime(block.x(), i * pxSize, 0.995f, clock.dt(), 0.1f);
						block.position.y = lerpTime(block.y(), j * pxSize, 0.995f, clock.dt(), 0.1f);
					} else {
						// before the snap, have blocks move arbitrarily
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
	
	// represents one pixel of the image
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

	// moves the block away from (x, y) with the given force
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
	public void onPointerEnd(Event event) { }

	@Override
	public void onPointerDrag(Event event) {
		disturb(event.x(), event.y(), 5);
	}

	@Override
	public void onPointerCancel(Event event) { }
}
