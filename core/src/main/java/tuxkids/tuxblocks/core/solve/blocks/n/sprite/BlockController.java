package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Font.Style;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Sprite.BlockListener;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.MultiList;


public class BlockController extends PlayNObject {
	
	public enum Side {
		Left, Right;
	}

	public final static float EQ_BUFFER = 50;
	public final static float EQ_THRESH = 5; // how far past the equals line you drag to cause a flip
	private static final float DRAGGING_DEPTH = 1;
	
	private GroupLayer layer;
	private List<BaseBlockSprite> leftSide = new ArrayList<BaseBlockSprite>(), rightSide = new ArrayList<BaseBlockSprite>();
	@SuppressWarnings("unchecked")
	private MultiList<BaseBlockSprite> baseBlocks = new MultiList<BaseBlockSprite>(leftSide, rightSide);
	private BaseBlockSprite draggingFrom;
	private List<BaseBlockSprite> draggingFromSide;
	private BlockSprite dragging;
	private Listener listener = new Listener();
	private float blockAnchorPX, blockAnchorPY;
	private float lastTouchX, lastTouchY;
	private boolean inverted;
	private float equalsX;
	private ImageLayer equals;
	
	private Image equationImage;
	private BaseBlockSprite hoverSprite;
	private boolean refreshEquation;
	
	public Layer layer() {
		return layer;
	}
	
	public Image equationImage() {
		return equationImage;
	}
	
	public BlockController() {
		layer = graphics().createGroupLayer();
		equals = graphics().createImageLayer(CanvasUtils.createText("=", new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, 20)), Colors.WHITE));
		centerImageLayer(equals);
		layer.add(equals);
	}
	
	public void addExpression(Side side, BaseBlockSprite expression) {
		List<BaseBlockSprite> blocks = getBlocks(side);
		addExpression(blocks, expression, 0, 0, blocks.size());
		equalsX = (leftSide.size() + 0.5f) / (baseBlocks.size() + 1) * graphics().width();
		equals.setTranslation(equalsX, graphics().height() / 2);
		refreshEquationImage();
	}
	
	private void addExpression(List<BaseBlockSprite> side, BaseBlockSprite expression, float x, float y, int index) {
		expression.initSprite();
		layer.addAt(expression.layerAddable(), x, y);
		expression.layer().setDepth(0);
		side.add(index, expression);
		expression.addBlockListener(listener);
	}
	
	private void swapExpression(List<BaseBlockSprite> side, BaseBlockSprite original, BaseBlockSprite newExp) {
		int index = side.indexOf(original);
		side.remove(index);
		addExpression(side, newExp, original.layer().tx(), original.layer().ty(), index);
	}
	
	private void refreshEquationImage() {
		Renderer lhs = getRenderer(leftSide);
		Renderer rhs = getRenderer(rightSide);
		Renderer equation = new JoinRenderer(lhs, rhs, "=");
		
		TextFormat format = new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, 20));
		ExpressionWriter writer = equation.getExpressionWriter(format);
		
		CanvasImage image = graphics().createImage(writer.width(), writer.height());
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().setStrokeColor(Colors.WHITE);
		writer.drawExpression(image.canvas());
		
		equationImage = image;
		refreshEquation = false;
	}
	
	private Renderer getRenderer(List<BaseBlockSprite> side) {
		Renderer renderer = null;
		for (BaseBlockSprite base : side) {
			Renderer toAdd;
			if (base == hoverSprite) {
				toAdd = base.createRendererWith(dragging);
			} else {
				if (base instanceof BlockHolder) continue;
				toAdd = base.createRenderer();
			}
			if (renderer == null) renderer = toAdd;
			else {
				renderer = new JoinRenderer(renderer, toAdd, "+");
			}
		}
		if (renderer == null) renderer = new BaseRenderer("0");
		return renderer;
	}
	
	private List<BaseBlockSprite> getBlocks(Side side) {
		return side == Side.Left ? leftSide : rightSide;
	}
	
	@SuppressWarnings("unused")
	private List<BaseBlockSprite> getOpposite(List<BaseBlockSprite> side) {
		return side == rightSide ? leftSide : rightSide;
	}
	
	private List<BaseBlockSprite> getContaining(BaseBlockSprite block) {
		return leftSide.contains(block) ? leftSide : rightSide;
	}
	
	public void update(int delta) {
		int bb = 0;
		for (BaseBlockSprite s : baseBlocks) {
			if (!(s instanceof BlockHolder)) bb++;
		}
		updateSide(delta, leftSide, bb);
		updateSide(delta, rightSide, bb);
		if (dragging != null) dragging.update(delta);
		if (refreshEquation) refreshEquationImage();
	}
	
	private void updateSide(int delta, List<BaseBlockSprite> side, int totalBlocks) {
		boolean multiExpression = totalBlocks > 2; // can't drag factors if there are >2 expressions
		if (!multiExpression) {
			int bb = 0;
			for (BaseBlockSprite s : side) {
				if (!(s instanceof BlockHolder)) bb++;
			}
			if (bb > 1) multiExpression = true; // or if there is >1 expression on any given side
		}
		for (BaseBlockSprite sprite : side) {
			sprite.update(delta, multiExpression);
		}
	}
	
	public void paint(Clock clock) {
		for (BaseBlockSprite sprite : baseBlocks) {
			sprite.paint(clock);
		}
		
		int i = 1;
		for (BaseBlockSprite sprite : baseBlocks) {
			float x = i++ * (graphics().width() - EQ_BUFFER) / (baseBlocks.size() + 1) - sprite.totalWidth() / 2 - sprite.offsetX();
			if (i > leftSide.size() + 1) x += EQ_BUFFER;
			sprite.layer().setTx(lerpTime(sprite.layer().tx(), x, 0.98f, clock.dt(), 1f));
			sprite.layer().setTy(lerpTime(sprite.layer().ty(), (graphics().height() - sprite.height()) / 2, 0.98f, clock.dt(), 1f));
		}
		
		if (dragging != null) dragging.paint(clock);
		updatePosition();
	}
	
	private void updatePosition() {
		if (dragging != null) {
			float x = lastTouchX - dragging.width() * blockAnchorPX;
			float y = lastTouchY - dragging.height() * blockAnchorPY;
			dragging.layer().setTranslation(x, y);
		}
	}
	
	private boolean canDropOn(BaseBlockSprite base, float x, float y) {
		return base.contains(x, y) && base.canAccept(dragging);
	}
	
	private void invertDragging(boolean refresh) {
		BlockSprite block = dragging.inverse();
		dragging.showInverse();
		if (refresh) {
			layer.remove(dragging.layer());
			layer.add(block.layer());
			block.layer().setDepth(DRAGGING_DEPTH);
		}
		dragging = block;
	}
	
	private class Listener implements BlockListener {

		@Override
		public void wasGrabbed(BlockSprite sprite, Event event) {
			for (BaseBlockSprite base : baseBlocks) {
				if (base.contains(event.x(), event.y())) {
					draggingFrom = base;
					break;
				}
			}
			
			if (draggingFrom == null) {
				debug("BIG PROBLEM!");
			}
			
			draggingFromSide = getContaining(draggingFrom);
			
			blockAnchorPX = (event.x() - getGlobalTx(sprite.layer())) / sprite.width();
			blockAnchorPY = (event.y() - getGlobalTy(sprite.layer())) / sprite.height();
			
			if (sprite == draggingFrom) {
				BlockHolder holder = new BlockHolder();
				swapExpression(draggingFromSide, draggingFrom, holder);
				draggingFrom = holder;
			}
			
			
			dragging = sprite.getDraggingSprite();
			layer.add(dragging.layer());
			dragging.layer().setDepth(DRAGGING_DEPTH);

			sprite.remove();
			if (sprite != dragging) {
				sprite.layer().setVisible(false);
			}
			
			lastTouchX = event.x();
			lastTouchY = event.y();
			inverted = false;
			updatePosition();
			
			
		}

		@Override
		public void wasReleased(Event event) {
			lastTouchX = event.x();
			lastTouchY = event.y();
			
			BaseBlockSprite target = null;
			for (BaseBlockSprite base : baseBlocks) {
				base.clearPreview();
				if (canDropOn(base, event.x(), event.y())) {
					target = base;
				}
			}
			
			if (target == null) {
				target = draggingFrom;
				if (inverted) {
					invertDragging(true);
				}
			}
			debug(target.hierarchy());
			
			if (target instanceof BlockHolder) {
				if (dragging instanceof HorizontalModifierSprite) {
					NumberBlockSpriteProxy proxy = ((HorizontalModifierSprite) dragging).getProxy(false);
					dragging.layer().setVisible(false);
					dragging = proxy;
				}
				
				swapExpression(getContaining(target), target, (BaseBlockSprite) dragging);
				target.layer().destroy();
			} else {
				dragging.layer().setTranslation(dragging.layer().tx() - getGlobalTx(target.layer()), 
						dragging.layer().ty() - getGlobalTy(target.layer()));
				target.addBlock(dragging, false);
			}
			
			dragging = null;
			draggingFrom = null;
			hoverSprite = null;
			
			refreshEquation = true;
			
			debug(target.hierarchy());
		}

		@Override
		public void wasMoved(Event event) {
			lastTouchX = event.x();
			lastTouchY = event.y();
			
			BaseBlockSprite lastHover = hoverSprite;
			hoverSprite = null;
			for (BaseBlockSprite base : baseBlocks) {
				if (canDropOn(base, event.x(), event.y())) {
					base.setPreview(true);
					hoverSprite = base;
				} else {
					base.setPreview(false);
				}
			}
			if (hoverSprite == null) {
				hoverSprite = draggingFrom;
			}
			if (hoverSprite != lastHover) {
				if (inverted && hoverSprite == draggingFrom) {
					invertDragging(false);
					refreshEquationImage();
					invertDragging(false);
				} else {
					refreshEquationImage();
				}
			}
			
			boolean invert;
			boolean checkLeftDistance = draggingFromSide == leftSide;
			if (inverted) checkLeftDistance = !checkLeftDistance;
			if (checkLeftDistance) {
				invert = event.x() > equalsX + 5;
			} else {
				invert = event.x() < equalsX - 5;
			}
			
			if (invert) {
				inverted = !inverted;
				invertDragging(true);
			}
		}

		@Override
		public void wasDoubleClicked(BlockSprite sprite, Event event) {
			if (sprite instanceof VerticalModifierSprite) {
				if (!((ModifierBlockSprite) sprite).canAddInverse()) return;
				
				float y;
				if (sprite instanceof TimesBlockSprite) {
					if (((VerticalModifierSprite) sprite).value == -1) {
						y = -graphics().height() / 2;
					} else {
						y = graphics().height() / 2;
					}
				} else {
					y = -graphics().height() / 2;
				}
				for (BaseBlockSprite base : baseBlocks) {
					if (!(base instanceof BlockHolder)) {
						ModifierBlockSprite inverse = (ModifierBlockSprite) ((VerticalModifierSprite) sprite).inverse().copy(true);
						inverse.interpolateRect(base.offsetX(), y, base.totalWidth(), inverse.height(), 0, 1);
						base.addModifier(inverse, false, true);
					}
				}
			}
		}

		@Override
		public void wasSimplified() {
			refreshEquation = true;
		}
	}
}
