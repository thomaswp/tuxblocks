package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Font.Style;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tripleplay.particle.Emitter;
import tripleplay.particle.Generator;
import tripleplay.particle.Particles;
import tripleplay.particle.TuxParticles;
import tripleplay.particle.effect.Alpha;
import tripleplay.particle.effect.Move;
import tripleplay.particle.init.Color;
import tripleplay.particle.init.Lifespan;
import tripleplay.particle.init.TuxTransform;
import tripleplay.particle.init.TuxVelocity;
import tripleplay.util.Colors;
import tripleplay.util.Interpolator;
import tripleplay.util.Randoms;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.effect.MissileExplosion;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.BlockListener;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.MultiList;


public class BlockController extends PlayNObject {
	
	public enum Side {
		Left, Right;
	}

	public final static float EQ_BUFFER = 50;
	public final static float EQ_THRESH = 5; // how far past the equals line you drag to cause a flip
	private static final float DRAGGING_DEPTH = 1;
	private static final int MAX_BLOCKS = 4;

	private Parent parent;
	private float width, height;
	private GroupLayer layer;
	private List<BaseBlock> leftSide = new ArrayList<BaseBlock>(), rightSide = new ArrayList<BaseBlock>(),
			removingLeft = new ArrayList<BaseBlock>(), removingRight = new ArrayList<BaseBlock>();
	@SuppressWarnings("unchecked")
	private MultiList<BaseBlock> baseBlocks = new MultiList<BaseBlock>(leftSide, rightSide);
	private BaseBlock draggingFrom, tempDraggingFrom;
	private List<BaseBlock> draggingFromSide;
	private Block dragging, tempDragging;
	private Listener listener = new Listener();
	private float blockAnchorPX, blockAnchorPY;
	private float lastTouchX, lastTouchY;
	private boolean inverted;
	private float equalsX;
	private ImageLayer equals;
	private boolean solved;
	private float equationImageHeight = 120;
	
	private boolean inBuildMode;
	private BuildToolbox buildToolbox;
	
	private Image equationImage;
	private BaseBlock hoverSprite;
	private boolean refreshEquation;
	
	public float equationImageHieght() {
		return equationImageHeight;
	}
	
	public void setEquationImageHeight(float equationImageHeight) { 
		this.equationImageHeight = equationImageHeight;
		refreshEquation = true;
	}
	
	public Layer layer() {
		return layer;
	}
	
	public boolean inBuildMode() {
		return inBuildMode;
	}
	
	public Image equationImage() {
		return equationImage;
	}
	
	public void setBuildToolbox(BuildToolbox buildToolbox) {
		this.buildToolbox = buildToolbox;
		inBuildMode = buildToolbox != null;
	}
	
	private float offX() {
		return getGlobalTx(layer);
	}
	
	private float offY() {
		return getGlobalTy(layer);
	}
	
	public Equation equation() {
		ArrayList<BaseBlock> lhs = new ArrayList<BaseBlock>(),
				rhs = new ArrayList<BaseBlock>();
		for (BaseBlock sprite : leftSide) {
			lhs.add((BaseBlock) sprite.copy());
		}
		for (BaseBlock sprite : rightSide) {
			rhs.add((BaseBlock) sprite.copy());
		}
		return new Equation(lhs, rhs);
	}
	
	public boolean solved() {
		return solved;
	}

	public BlockListener blockListener() {
		return listener;
	}
	
	public BlockController(Parent parent, float width, float height) {
		this.parent = parent;
		this.width = width;
		this.height = height;
		layer = graphics().createGroupLayer();
		equals = graphics().createImageLayer(CanvasUtils.createTextCached("=", 
				new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, 20)), Colors.WHITE));
		centerImageLayer(equals);
		layer.add(equals);
	}
	
	public void clear() {
		for (BaseBlock sprite : baseBlocks) {
			sprite.destroy();
		}
		leftSide.clear();
		rightSide.clear();
		solved = false;
		dragging = draggingFrom = null;
	}
	
	public void addEquation(Equation equation) {
		for (BaseBlock sprite : equation.leftSide()) {
			addExpression(Side.Left, sprite);
		}
		for (BaseBlock sprite : equation.rightSide()) {
			addExpression(Side.Right, sprite);
		}
		updateExpressionPositions(0, 1);
	}
	
	public void addExpression(Side side, BaseBlock expression) {
		List<BaseBlock> blocks = getBlocks(side);
		addExpression(blocks, expression, 0, 0, blocks.size());
		refreshEquationImage();
	}
	
	private void addExpression(List<BaseBlock> side, BaseBlock expression, float x, float y, int index) {
		expression.initSprite();
		layer.addAt(expression.layerAddable(), x, y);
		expression.layer().setDepth(0);
		side.add(index, expression);
		expression.addBlockListener(listener);
		refreshEquation = true;
		refreshEquals();
	}
	
	private void removeExpression(List<BaseBlock> side, BaseBlock expression) {
		side.remove(expression);
		refreshEquation = true;
		refreshEquals();
//		expression.destroy();
	}
	
	private void refreshEquals() {
		equalsX = (leftSide.size() + 0.5f) / (baseBlocks.size() + 1) * width;
		equals.setTranslation(equalsX, height / 2);
	}
	
	private void swapExpression(List<BaseBlock> side, BaseBlock original, BaseBlock newExp) {
		int index = side.indexOf(original);
		side.remove(index);
		addExpression(side, newExp, original.layer().tx(), original.layer().ty(), index);
	}
	
	private boolean refreshSolved() {
		if (dragging != null) return false;
		int numbers = 0, variables = 0;
		for (BaseBlock sprite : baseBlocks) {
			if (!sprite.simplified()) return false;
			if (sprite instanceof NumberBlock) {
				numbers++;
			}
			if (sprite instanceof VariableBlock) {
				variables++;
			}
		}
		return numbers <= 1 && variables == 1;
	}
	
	private void refreshEquationImage() {
		Renderer lhs = getRenderer(leftSide);
		Renderer rhs = getRenderer(rightSide);
		Renderer equation = new JoinRenderer(lhs, rhs, "=");
		

		float textSize = equationImageHeight * 0.6f / Math.max(3, equation.lines());
		TextFormat format = new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, textSize));
		ExpressionWriter writer = equation.getExpressionWriter(format);
		
		CanvasImage image = graphics().createImage(writer.width(), writer.height());
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().setStrokeColor(Colors.WHITE);
		writer.drawExpression(image.canvas());
		
		equationImage = image;
		refreshEquation = false;
	}
	
	private Renderer getRenderer(List<BaseBlock> side) {
		if (hoverSprite == null) hoverSprite = draggingFrom;
		Renderer renderer = null;
		for (BaseBlock base : side) {
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
	
	private List<BaseBlock> getBlocks(Side side) {
		return side == Side.Left ? leftSide : rightSide;
	}
	
	private List<BaseBlock> getOpposite(List<BaseBlock> side) {
		return side == rightSide ? leftSide : rightSide;
	}
	
	private List<BaseBlock> getContaining(BaseBlock block) {
		return leftSide.contains(block) ? leftSide : rightSide;
	}
	
	public void update(int delta) {
		int bb = 0;
		for (BaseBlock s : baseBlocks) {
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
	
	private void updateSide(int delta, List<BaseBlock> side, int totalBlocks) {
		boolean multiExpression = totalBlocks > 2; // can't drag factors if there are >2 expressions
		if (!multiExpression) {
			int bb = 0;
			for (BaseBlock s : side) {
				if (!(s instanceof BlockHolder)) bb++;
			}
			if (bb > 1) multiExpression = true; // or if there is >1 expression on any given side
		}
		for (BaseBlock sprite : side) {
			boolean moveBase = false;
			for (BaseBlock other : side) {
				if (other != sprite && other.canAccept(sprite)) {
					moveBase = true;
				}
			}
			if (!moveBase) {
				for (BaseBlock other : getOpposite(side)) {
					if (other.canAccept(sprite)) {
						moveBase = true;
					}
				}
			}
			sprite.update(delta, multiExpression, moveBase);
		}
	}
	
	public void paint(Clock clock) {
		for (BaseBlock sprite : baseBlocks) {
			sprite.paint(clock);
		}
		
		updateExpressionPositions(0.99f, clock.dt());
		updateRemoving(0.99f, clock.dt());
		
		if (dragging != null) dragging.paint(clock);
		updatePosition();
		particles.paint(clock);
	}
	
	private void updateRemoving(float base, float dt) {
		updateRemovingSide(removingLeft, -width / 2, base, dt);
		updateRemovingSide(removingRight, width * 3 / 2, base, dt);
	}
	
	private void updateRemovingSide(List<BaseBlock> list, float off, float base, float dt) {
		for (int i = 0; i < list.size(); i++) {
			BaseBlock removing = list.get(i);
			removing.layer().setTx(lerpTime(removing.layer().tx(), off, base, dt, 1f));
			if (removing.layer().tx() == off) {
				removing.destroy();
				list.remove(i--);
			}
		}
	}

	private void updateExpressionPositions(float base, float dt) {
		int i = 1;
		for (BaseBlock sprite : baseBlocks) {
			float x = i++ * (width - EQ_BUFFER) / (baseBlocks.size() + 1) - sprite.totalWidth() / 2 - sprite.offsetX();
			if (i > leftSide.size() + 1) x += EQ_BUFFER;
			sprite.layer().setTx(lerpTime(sprite.layer().tx(), x, base, dt, 1f));
			sprite.layer().setTy(lerpTime(sprite.layer().ty(), (height - sprite.height()) / 2, base, dt, 1f));
//			debug("%d %d", sprite.layer().tx(), sprite.layer().ty());
		}
	}
	
	private void updatePosition() {
		if (dragging != null) {
			float x = lastTouchX - dragging.width() * blockAnchorPX;
			float y = lastTouchY - dragging.height() * blockAnchorPY;
			dragging.layer().setTranslation(x, y);
		}
	}
	
	private boolean canDropOn(BaseBlock base, float x, float y) {
		if (base.intersects(dragging)) {
			if (base instanceof BlockHolder && dragging instanceof VerticalModifierBlock) {
				int blocks = 0;
				for (BaseBlock block : baseBlocks) {
					if (!(block instanceof BlockHolder)) blocks++;
				}
				return blocks == 1;
			}
			return base.canAccept(dragging);
		}
		return false;
	}
	
	private void invertDragging(boolean refresh) {
		Block block = dragging.inverse();
		dragging.showInverse();
		if (refresh) {
			layer.remove(dragging.layer());
			layer.add(block.layer());
			block.layer().setDepth(DRAGGING_DEPTH);
		}
		dragging = block;
	}
	
	private TuxParticles particles = new TuxParticles();
	private void showInvertAnimation(float x, float y) {
		int p = 30;
		Emitter emitter = particles.createEmitter(p, CanvasUtils.createCircleCached(10, Colors.WHITE), layer);
		emitter.generator = Generator.impulse(p);
        emitter.initters.add(Lifespan.constant(1));
        emitter.initters.add(Color.constant(Colors.WHITE));
        emitter.initters.add(TuxTransform.layer(emitter.layer));
        Randoms rando = Randoms.with(new Random());
        emitter.initters.add(TuxVelocity.randomCircle(rando, 50));
        emitter.effectors.add(new Move());
        emitter.effectors.add(Alpha.byAge(Interpolator.EASE_OUT, 1, 0));
        emitter.destroyOnEmpty();
        emitter.layer.setTranslation(x - offX(), y - offY());
        emitter.layer.setDepth(100);
	}
	
	private void updateBlockHolders() {
		if (!inBuildMode) return;
		updateBlockHolder(leftSide, Side.Left);
		updateBlockHolder(rightSide, Side.Right);
		updateBlockHolder(leftSide, Side.Left);
	}
	
	private void updateBlockHolder(List<BaseBlock> blocks, Side side) {
		int holders = 0;
		BaseBlock lastHolder = null;
		for (BaseBlock block : blocks) {
			if (block instanceof BlockHolder) {
				holders++;
				if (block != draggingFrom) lastHolder = block;
			}
		}
		if (holders == 0 && baseBlocks.size() < MAX_BLOCKS) {
			BlockHolder holder = new BlockHolder();
			float x = side == Side.Left ? - width / 2 : width * 3 / 2;
			float y = height / 2 - Sprite.baseSize() / 2;
			addExpression(blocks, holder, x, y, side == Side.Left ? 0 : blocks.size());
		} else if (holders > 1) {
			removeExpression(blocks, lastHolder);
			if (side == Side.Left) {
				removingLeft.add(lastHolder);
			} else {
				removingRight.add(lastHolder);
			}
			blocks.remove(lastHolder);
		}
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
	
	// TODO: should probably just pass the controller to the blocks,
	// rather than trying to accomplish everything through a listener
	private class Listener implements BlockListener {

		@Override
		public void wasGrabbed(Block sprite, Event event) {
			if (buildToolbox != null) buildToolbox.wasGrabbed(event);
			
			float x = getTouchX(event), y = getTouchY(event);
			for (BaseBlock base : baseBlocks) {
				if (base.contains(sprite)) {
					draggingFrom = base;
					break;
				}
			}
			
			if (draggingFrom == null) {
				if (inBuildMode) {
					Block nSprite = (Block) sprite.copy(true);
					nSprite.layer().setTranslation(sprite.layer().tx(), sprite.layer().ty());
					nSprite.interpolateDefaultRect(null);
					sprite = nSprite;
				} else {
					debug("BIG PROBLEM!");
				}
			}
			
			draggingFromSide = getContaining(draggingFrom);
			
			blockAnchorPX = (x - spriteX(sprite)) / sprite.width();
			blockAnchorPY = (y - spriteY(sprite)) / sprite.height();
			
			if (sprite == draggingFrom) {
				BlockHolder holder = new BlockHolder();
				swapExpression(draggingFromSide, draggingFrom, holder);
				draggingFrom = holder;
//				updateBlockHolders();
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
			
			Audio.se().play(Constant.SE_TICK);
		}

		@Override
		public void wasReleased(Event event) {
			
			if (buildToolbox != null && buildToolbox.wasDropped(event)) {
				draggingFrom = null;
			}
			
			float x = getTouchX(event), y = getTouchY(event);
			lastTouchX = x - layer.tx();
			lastTouchY = y - layer.ty();
			
			BaseBlock target = null;
			for (BaseBlock base : baseBlocks) {
				base.clearPreview();
				if (target == null && canDropOn(base, x, y)) {
					target = base;
				}
			}
			
			if (target == null) {
				target = draggingFrom;
				if (inverted) {
					invertDragging(true);
				}
			} 
			
			if (target != draggingFrom){
				if (dragging instanceof BaseBlock) {
					Tutorial.trigger(Trigger.Solve_BaseBlockReleasedOnOther);
				}
				Tutorial.trigger(Trigger.Solve_BlockReleasedOnOther);
			}
			Tutorial.trigger(Trigger.Solve_BlockReleased);
			
			dropOn(target);
		}
		
		private void dropOn(BaseBlock target) {
//			debug(target.hierarchy());
			
			
			if (target == null) {
				if (!inBuildMode) debug("BIG PROBLEM!");
				dragging.destroy();
			} else {
				if (target instanceof BlockHolder) {
					
					if (dragging instanceof VerticalModifierBlock) {
						dragging.destroy();
					} else {
						if (dragging instanceof HorizontalModifierBlock) {
							NumberBlockProxy proxy = ((HorizontalModifierBlock) dragging).getProxy(false);
							dragging.layer().setVisible(false);
							dragging = proxy;
						} else if (dragging instanceof BaseBlock) {
							ModifierGroup mods = ((BaseBlock) dragging).modifiers;
							if (mods.isModifiedHorizontally() || mods.isModifiedVertically() || mods.children.size() > 0) {
								Tutorial.trigger(Trigger.Solve_BlockWithModifiersReleasedOnBlank);
							}
						}
						
						swapExpression(getContaining(target), target, (BaseBlock) dragging);
						target.layer().destroy();
					}
					
					Tutorial.trigger(Trigger.Solve_BlockReleasedOnBlank);
				} else {
					if (dragging instanceof VariableBlock && target instanceof VariableBlock) {
						Tutorial.trigger(Trigger.Solve_VariablesStartedCombine);
					}
					
					ModifierBlock added = target.addBlock(dragging, false);
					if (added == null) {
						tempDragging = dragging;
						tempDraggingFrom = draggingFrom;
					} else {
						added.layer().setTranslation(added.layer().tx() - spriteX(target), 
								added.layer().ty() - spriteY(target));
					}
				}
			}
			updateBlockHolders();
			
			dragging = null;
			draggingFrom = null;
			hoverSprite = null;
			
			refreshEquation = true;
//			debug(target.hierarchy());
			
			Audio.se().play(Constant.SE_DROP);
		}

		@Override
		public void wasMoved(Event event) {
			if (buildToolbox != null) buildToolbox.wasMoved(event);
			
			float x = getTouchX(event), y = getTouchY(event);
			lastTouchX = x;
			lastTouchY = y;
			
			BaseBlock lastHover = hoverSprite;
			hoverSprite = null;
			for (BaseBlock base : baseBlocks) {
				if (hoverSprite == null && canDropOn(base, x, y)) {
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
			
			if (!inBuildMode) {
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
					showInvertAnimation(equalsX, 
							event.y() - (blockAnchorPY - 0.5f) * dragging.height());
				}
			}
		}

		@Override
		public void wasDoubleClicked(Block sprite, Event event) {
			if (sprite instanceof VerticalModifierBlock) {
				if (!((ModifierBlock) sprite).canAddInverse()) return;
				Tutorial.trigger(Trigger.Solve_VerticalModifierDoubleClicked);
				
				float y;
				if (sprite instanceof TimesBlock) {
					if (((VerticalModifierBlock) sprite).value == -1) {
						y = -graphics().height() / 2;
					} else {
						y = graphics().height() / 2;
					}
				} else {
					y = -graphics().height() / 2;
				}
				for (BaseBlock base : baseBlocks) {
					if (!(base instanceof BlockHolder)) {
						ModifierBlock inverse = (ModifierBlock) ((VerticalModifierBlock) sprite).inverse().copy(true);
						inverse.interpolateRect(base.offsetX(), y, base.totalWidth(), inverse.height(), 0, 1);
						base.addModifier(inverse, false);
					}
				}
				refreshEquation = true;
			}
			

			Audio.se().play(Constant.SE_TICK);
		}

		@Override
		public void wasSimplified() {
			refreshEquation = true;
			Tutorial.trigger(Trigger.Solve_Simplified);
			Audio.se().play(Constant.SE_TICK);
		}

		@Override
		public void wasReduced(Renderer problem, int answer, int startNumber, 
				Stat stat, int level, SimplifyListener callback) {
			parent.showNumberSelectScreen(problem, answer, startNumber, stat, level, callback);
			Audio.se().play(Constant.SE_TICK);
		}
		
		@Override
		public void wasCanceled() {
			dragging = tempDragging;
			draggingFrom = tempDraggingFrom;
			tempDragging = tempDraggingFrom = null;
			dropOn(draggingFrom);
		}

		@Override
		public boolean inBuildMode() {
			return inBuildMode;
		}
		
		
	}
	
	public interface Parent {
		void showNumberSelectScreen(Renderer problem, int answer, int startNumber, 
				Stat stat, int level, SimplifyListener callback);
	}
	
	public interface BuildToolbox {
		void wasGrabbed(Event event);
		void wasMoved(Event event);
		boolean wasDropped(Event event);
	}
}
