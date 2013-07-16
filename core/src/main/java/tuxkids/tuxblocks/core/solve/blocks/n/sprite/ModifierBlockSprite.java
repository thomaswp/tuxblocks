package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.Image;
import playn.core.Path;
import playn.core.PlayN;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.solve.blocks.n.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class ModifierBlockSprite extends BlockSprite {
	
	protected ModifierBlock block;
	protected int timeElapsed;
	protected boolean flash;
	protected boolean removed;
	
	public boolean removed() {
		return removed;
	}
	
	public ModifierBlockSprite(ModifierBlock block) {
		this.block = block;
		layer = generateNinepatch(block.text(), Colors.WHITE);
	}
	
	public void interpolateRect(float x, float y, float width, float height, float base, float dt) {
		float snap = 1f;
		layer().setTx(lerpTime(layer().tx(), x, base, dt, snap));
		layer().setTy(lerpTime(layer().ty(), y, base, dt, snap));
		layer.setWidth(lerpTime(width(), width, base, dt, snap));
		layer.setHeight(lerpTime(height(), height, base, dt, snap));
	}

	protected NinepatchLayer generateNinepatch(String text, int color) {
		if (block instanceof TimesBlock) {// || block instanceof OverBlock) {
			boolean times = block instanceof TimesBlock;
			
			TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
			int legs = wrapSize();
			int sides = 2;
			int width = legs * 2 + sides * 2 + (int)layout.width();
			int height = modSize() + sides * 2;
			
			int[] widthDims = new int[] { legs + 1, sides - 1, width - legs * 2 - sides * 2, sides - 1, legs + 1 };
			int[] heightDims;
			if (times) {
				heightDims = new int[] { modSize() + 1, sides - 1, sides };
			} else {
				heightDims = new int[] { sides, sides - 1, modSize() + 1 };
			}
			
			float hlw = 0;//0.5f;
			float pWidth = width - 1;
			float pHeight = height - 1;
			float lx, ly;
			
			CanvasImage image = graphics().createImage(width, height);
			if (!times) {
				image.canvas().save();
				image.canvas().translate(0, image.height() / 2);
				image.canvas().scale(1, -1);
				image.canvas().translate(0, -image.height() / 2 + 1);
			}
			
			Path path = image.canvas().createPath();
			path.moveTo(lx = hlw, ly = hlw);
			path.lineTo(lx = pWidth - hlw, ly);
			path.lineTo(lx, ly = pHeight - hlw);
			path.lineTo(lx = pWidth - legs + hlw + 1, ly);
			path.lineTo(lx, ly = pHeight - sides * 2 + hlw);
			path.lineTo(lx = legs - hlw - 1, ly);
			path.lineTo(lx, ly = pHeight - hlw);
			path.lineTo(lx = hlw, ly);
			path.lineTo(lx, hlw);
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().setStrokeColor(Colors.DARK_GRAY);
			image.canvas().fillPath(path);
			image.canvas().strokePath(path);
			
			float textX = (image.width() - layout.width()) / 2;
			float textY;
			if (times) {
				textY = (modSize() - layout.height()) / 2;
			} else {
				image.canvas().restore();
				textY = image.height() - (modSize() + layout.height()) / 2;
			}
			image.canvas().setFillColor(Colors.BLACK);
			image.canvas().fillText(layout, textX, textY);
			
			NinepatchLayer ninePatch = new NinepatchLayer(factory, image, widthDims, heightDims);
			return ninePatch;
		}
		return super.generateNinepatch(text, color);
	}
	
	@Override
	public void update(int delta) {
		if (flash != block.canRelease(true)) {
			flash = !flash;
			if (!flash) {
				layer.setTint(Colors.WHITE);
			}
		}
	}
	
	@Override
	public void paint(Clock clock) {
		timeElapsed += clock.dt();
		if (flash) {
			layer.setTint(Color.rgb(200, 200, 255), Colors.WHITE, FloatMath.pow(FloatMath.sin(timeElapsed / 1250f * 2 * FloatMath.PI) / 2 + 0.5f, 0.7f));
		}
	}

	public void addBlockListener(BlockListener listener) {
		layer.addListener(new Listener() {
			@Override
			public void onPointerStart(Event event) {
				if (flash) {
					removed = true;
				}
			}
			
			@Override
			public void onPointerEnd(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPointerDrag(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPointerCancel(Event event) {
				
			}
		});
	}
}
