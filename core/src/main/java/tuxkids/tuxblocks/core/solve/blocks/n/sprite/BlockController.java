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
import tuxkids.tuxblocks.core.Button.OnDragListener;
import tuxkids.tuxblocks.core.solve.blocks.n.NumberSelectScreen;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Sprite.BlockListener;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.MultiList;


public class BlockController extends PlayNObject {
	
	public enum Side {
		Left, Right;
	}

	public final static float EQ_BUFFER = 50;
	public final static float EQ_THRESH = 5; // how far past the equals line you drag to cause a flip
	private static final float DRAGGING_DEPTH = 1;

	private Parent parent;
	private float width, height;
	private GroupLayer layer;
	private List<BaseBlockSprite> leftSide = new ArrayList<BaseBlockSprite>(), rightSide = new ArrayList<BaseBlockSprite>();
	@SuppressWarnings("unchecked")
	private MultiList<BaseBlockSprite> baseBlocks = new MultiList<BaseBlockSprite>(leftSide, rightSide);
	private BaseBlockSprite draggingFrom, tempDraggingFrom;
	private List<BaseBlockSprite> draggingFromSide;
	private BlockSprite dragging, tempDragging;
	private Listener listener = new Listener();
	private float blockAnchorPX, blockAnchorPY;
	private float lastTouchX, lastTouchY;
	private boolean inverted;
	private float equalsX;
	private ImageLayer equals;
	private boolean solved;
	
	private Image equationImage;
	private BaseBlockSprite hoverSprite;
	private boolean refreshEquation;
	
	public Layer layer() {
		return layer;
	}
	
	public Image equationImage() {
		return equationImage;
	}
	
	private float offX() {
		return getGlobalTx(layer);
	}
	
	private float offY() {
		return getGlobalTy(layer);
	}
	
	public Equation equation() {
		ArrayList<BaseBlockSprite> lhs = new ArrayList<BaseBlockSprite>(),
				rhs = new ArrayList<BaseBlockSprite>();
		for (BaseBlockSprite sprite : leftSide) {
			lhs.add((BaseBlockSprite) sprite.copy());
		}
		for (BaseBlockSprite sprite : rightSide) {
			rhs.add((BaseBlockSprite) sprite.copy());
		}
		return new Equation(lhs, rhs);
	}
	
	public boolean solved() {
		return solved;
		
	}
	
	public BlockController(Parent parent, float width, float height) {
		this.parent = parent;
		this.width = width;
		this.height = height;
		layer = graphics().createGroupLayer();
		equals = graphics().createImageLayer(CanvasUtils.createText("=", new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, 20)), Colors.WHITE));
		centerImageLayer(equals);
		layer.add(equals);
	}
	
	public void clear() {
		baseBlocks.clear();
		solved = false;
		dragging = draggingFrom = null;
	}
	
	public void addEquation(Equation equation) {
		for (BaseBlockSprite sprite : equation.leftSide()) {
			addExpression(Side.Left, sprite);
		}
		for (BaseBlockSprite sprite : equation.rightSide()) {
			addExpression(Side.Right, sprite);
		}
	}
	
	public void addExpression(Side side, BaseBlockSprite expression) {
		List<BaseBlockSprite> blocks = getBlocks(side);
		addExpression(blocks, expression, 0, 0, blocks.size());
		equalsX = (leftSide.size() + 0.5f) / (baseBlocks.size() + 1) * width;
		equals.setTranslation(equalsX, height / 2);
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
	
	private boolean refreshSolved() {
		int numbers = 0, variables = 0;
		for (BaseBlockSprite sprite : baseBlocks) {
			if (!sprite.simplified()) return false;
			if (sprite instanceof NumberBlockSprite) {
				numbers++;
			}
			if (sprite instanceof VariableBlockSprite) {
				variables++;
			}
		}
		return numbers == 1 && variables == 1;
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
		if (hoverSprite == null) hoverSprite = draggingFrom;
		Renderer renderer = null;
		for (BaseBlockSprite base : side) {
			Renderer toAdd;
			if (dragging != null && base == hoverSprite) {
				toAdd = base.createRendererWith(dragging, hoverSprite == draggingFrom && inverted);
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
		if (refreshEquation) {
			refreshEquationImage();
			solved = refreshSolved();
		}
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
			float x = i++ * (width - EQ_BUFFER) / (baseBlocks.size() + 1) - sprite.totalWidth() / 2 - sprite.offsetX();
			if (i > leftSide.size() + 1) x += EQ_BUFFER;
			sprite.layer().setTx(lerpTime(sprite.layer().tx(), x, 0.98f, clock.dt(), 1f));
			sprite.layer().setTy(lerpTime(sprite.layer().ty(), (height - sprite.height()) / 2, 0.98f, clock.dt(), 1f));
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
	
	private float getTouchX(Event event) {
		return event.x() - offX();
	}
	
	private float getTouchY(Event event) {
		return event.y() - offY();
	}
	
	private float spriteX(Sprite sprite) {
		return getGlobalTx(sprite.layer()) - offX();
	}
	
	private float spriteY(Sprite sprite) {
		return getGlobalTy(sprite.layer()) - offY();
	}
	
	private class Listener implements BlockListener {

		@Override
		public void wasGrabbed(BlockSprite sprite, Event event) {
			float x = getTouchX(event), y = getTouchY(event);
			for (BaseBlockSprite base : baseBlocks) {
				if (base.contains(x, y)) {
					draggingFrom = base;
					break;
				}
			}
			
			if (draggingFrom == null) {
				debug("BIG PROBLEM!");
			}
			
			draggingFromSide = getContaining(draggingFrom);
			
			blockAnchorPX = (x - spriteX(sprite)) / sprite.width();
			blockAnchorPY = (y - spriteY(sprite)) / sprite.height();
			
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
			
			lastTouchX = x;
			lastTouchY = y;
			inverted = false;
			refreshEquation = true;
			updatePosition();
			
			
		}

		@Override
		public void wasReleased(Event event) {
			float x = getTouchX(event), y = getTouchY(event);
			lastTouchX = x - layer.tx();
			lastTouchY = y - layer.ty();
			
			BaseBlockSprite target = null;
			for (BaseBlockSprite base : baseBlocks) {
				base.clearPreview();
				if (canDropOn(base, x, y)) {
					target = base;
				}
			}
			
			if (target == null) {
				target = draggingFrom;
				if (inverted) {
					invertDragging(true);
				}
			}
			dropOn(target);
		}
		
		private void dropOn(BaseBlockSprite target) {
			
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
				ModifierBlockSprite added = target.addBlock(dragging, false);
				if (added == null) {
					tempDragging = dragging;
					tempDraggingFrom = draggingFrom;
				} else {
					added.layer().setTranslation(added.layer().tx() - spriteX(target), 
							added.layer().ty() - spriteY(target));
				}
			}
			
			dragging = null;
			draggingFrom = null;
			hoverSprite = null;
			
			refreshEquation = true;
			debug(target.hierarchy());
		}

		@Override
		public void wasMoved(Event event) {
			float x = getTouchX(event), y = getTouchY(event);
			lastTouchX = x;
			lastTouchY = y;
			
			BaseBlockSprite lastHover = hoverSprite;
			hoverSprite = null;
			for (BaseBlockSprite base : baseBlocks) {
				if (canDropOn(base, x, y)) {
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
				refreshEquation = true;
			}
			
			boolean invert;
			boolean checkLeftDistance = draggingFromSide == leftSide;
			if (inverted) checkLeftDistance = !checkLeftDistance;
			if (checkLeftDistance) {
				invert = x > equalsX + 5;
			} else {
				invert = x < equalsX - 5;
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
						base.addModifier(inverse, false);
					}
				}
				refreshEquation = true;
			}
		}

		@Override
		public void wasSimplified() {
			refreshEquation = true;
		}

		@Override
		public void wasReduced(Renderer problem, int answer, int startNumber,
				SimplifyListener callback) {
			parent.showNumberSelectScreen(problem, answer, startNumber, callback);
		}
		
		@Override
		public void wasCanceled() {
			dragging = tempDragging;
			draggingFrom = tempDraggingFrom;
			tempDragging = tempDraggingFrom = null;
			dropOn(draggingFrom);
		}
	}
	
	public interface Parent {
		void showNumberSelectScreen(Renderer problem, int answer, int startNumber,
				SimplifyListener callback);
	}
}
