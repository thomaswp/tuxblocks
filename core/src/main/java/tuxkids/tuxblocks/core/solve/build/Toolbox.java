package tuxkids.tuxblocks.core.solve.build;

import java.util.ArrayList;
import java.util.List;

import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.solve.NumberSelectScreen;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.BlockController.BuildToolbox;
import tuxkids.tuxblocks.core.solve.blocks.HorizontalModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.MinusBlock;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.PlusBlock;
import tuxkids.tuxblocks.core.solve.blocks.Sprite;
import tuxkids.tuxblocks.core.solve.blocks.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.tutorial.Highlightable;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

/**
 * A layer to display the {@link Block}s which can be dragged onto
 * the {@link BuildScreen}.
 */
public class Toolbox extends LayerWrapper implements BuildToolbox, Highlightable {

	protected GroupLayer layer;
	protected ImageLayer numberLayer;
	protected ImageLayerTintable backgroundLayer;
	protected float width, height;
	protected BlockController controller;
	protected List<Block> blocks = new ArrayList<Block>();
	protected Button buttonNumber, buttonLess, buttonMore;
	// the number for the NumberBlock and the ModifierBlocks to displays
	protected int number = 1;
	protected TextFormat textFormat;
	protected NumberSelectListener listener;
	
	public int number() {
		return number;
	}
	
	public Toolbox(BlockController controller, NumberSelectListener numberSelectListener, 
			float width, float height, int themeColor) {
		super(graphics().createGroupLayer());
		this.layer = (GroupLayer) layerAddable();
		this.width = width;
		this.height = height;
		this.controller = controller;
		this.listener = numberSelectListener;
		controller.setBuildToolbox(this);
		
		// create the solid background layer
		backgroundLayer = new ImageLayerTintable();
		backgroundLayer.setImage(CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY));
		backgroundLayer.setAlpha(0.75f);
		layer.add(backgroundLayer.layerAddable());

		// the starting y-coordinate of the Blocks
		float blockStart = 2 * width / 3;
		createButtons(blockStart, themeColor);
		
		// add each Block the player can select
		VariableBlock variableBlock = new VariableBlock("x");
		blocks.add(variableBlock);
		
		NumberBlock numberBlock = new NumberBlock(number);
		blocks.add(numberBlock);
		
		PlusBlock plusBlock = new PlusBlock(number);
		blocks.add(plusBlock);
		
		MinusBlock minusBlock = new MinusBlock(number);
		blocks.add(minusBlock);
		
		TimesBlock timesBlock = new TimesBlock(number);
		blocks.add(timesBlock);
		
		OverBlock overBlock = new OverBlock(number);
		blocks.add(overBlock);
		
		// initialize the Blocks
		for (Block block : blocks) {
			block.initSprite();
			// the BlockController still handles their movement
			block.addBlockListener(controller.blockListener());
			// snap the blocks into place
			block.interpolateDefaultRect(null);
			layer.add(block.layer());
		}

		// how much vertical space each (group of) blocks takes up
		float blockSeg = (height - blockStart) / 4;
		// padding in between two modifier blocks in the same group
		float barSpace = (Sprite.baseSize() - Sprite.modSize()) / 2;
		// position all the blocks
		variableBlock.layer().setTranslation((width - variableBlock.width()) / 2, blockStart + blockSeg * 0.5f - variableBlock.height() / 2);
		numberBlock.layer().setTranslation((width - numberBlock.width()) / 2, blockStart + blockSeg * 1.5f - numberBlock.height() / 2);
		plusBlock.layer().setTranslation(width / 2 - barSpace - plusBlock.width() / 2, blockStart + blockSeg * 2.5f - plusBlock.height() / 2);
		minusBlock.layer().setTranslation(width / 2 + barSpace - minusBlock.width() / 2, blockStart + blockSeg * 2.5f - minusBlock.height() / 2);
		timesBlock.layer().setTranslation((width - timesBlock.width()) / 2 , blockStart + blockSeg * 3.5f - barSpace - timesBlock.height() / 2);
		overBlock.layer().setTranslation((width - overBlock.width()) / 2 , blockStart + blockSeg * 3.5f + barSpace - overBlock.height() / 2);
	}
	
	private void createButtons(float blockStart, int themeColor) {
		// size of the number-select button
		float circleSize = width * 0.6f;
		
		// create the number-select button
		buttonNumber = new Button(Constant.BUTTON_CIRCLE, circleSize, circleSize, true);
		buttonNumber.setPosition(width / 2, circleSize * 0.6f);
		buttonNumber.setTint(themeColor);
		buttonNumber.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) listener.selectNumber(number);
			}
		});
		layer.add(buttonNumber.layerAddable());
		
		// create the decrease-number button
		float arrowX = width / 10;
		buttonLess = new Button(Constant.BUTTON_LESS, width / 6, width / 3, false);
		buttonLess.setPosition(arrowX, buttonNumber.y());
		buttonLess.setTint(themeColor);
		buttonLess.setSoundPath(Constant.SE_DROP);
		buttonLess.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					setNumber(number - 1);
				}
			}
		});
		layer.add(buttonLess.layerAddable());
		
		// create the increase-number button
		buttonMore = new Button(Constant.BUTTON_MORE, width / 6, width / 3, false);
		buttonMore.setPosition(width - arrowX, buttonNumber.y());
		buttonMore.setTint(themeColor);
		buttonMore.setSoundPath(Constant.SE_DROP);
		buttonMore.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					setNumber(number + 1);
				}
			}
		});
		layer.add(buttonMore.layerAddable());
		
		textFormat = new TextFormat().withFont(graphics().createFont(
				Constant.NUMBER_FONT, Style.PLAIN, buttonNumber.height() / 3));
		numberLayer = graphics().createImageLayer();
		numberLayer.setTranslation(buttonNumber.x(), buttonNumber.y());
		layer.add(numberLayer);
		refreshNumberSprite();
	}
	
	/** 
	 * Called from {@link BuildScreen} when the {@link NumberSelectScreen}
	 * returns a newly selected number. Resets the Blocks to show that number.
	 */
	protected void setNumber(int number) {
		if (number == this.number) return;
		this.number = number;
		for (Block block : blocks) {
			if (block instanceof NumberBlock) {
				((NumberBlock) block).setValue(number);
			} else if (block instanceof ModifierBlock) {
				if (block instanceof HorizontalModifierBlock) {
					// Plus- and MinusBlocks take a magnitude (you can't have --3 or +-3)
					((ModifierBlock) block).setValue(Math.abs(number));	
				} else {
					((ModifierBlock) block).setValue(number);
				}
				block.layer().setVisible(number != 0);
			}
		}
		refreshNumberSprite();
	}

	// redraw the number shown on the numberLayer
	private void refreshNumberSprite() {
		numberLayer.setImage(CanvasUtils.createText("" + number, textFormat, Colors.BLACK));
		centerImageLayer(numberLayer);
	}

	// called from BuildScreen.update()
	public void update(int delta) {
		for (Block block : blocks) {
			block.update(delta);
		}
	}
	
	// called from BuildScreen.paint()
	public void paint(Clock clock) {
		for (Block block : blocks) {
			block.paint(clock);
		}
	}

	// called from BlockController
	@Override
	public void wasGrabbed(Event event) {
		wasMoved(event);
	}

	// called from BlockController
	@Override
	public void wasMoved(Event event) {
		if (inRect(event)) {
			layer.setAlpha(1);
		} else {
			layer.setAlpha(BaseBlock.PREVIEW_ALPHA);
		}
	}

	// called from BlockController
	@Override
	public boolean wasDropped(Event event) {
		layer.setAlpha(1);
		return inRect(event);
	}

	// returns true if the Event's coordinates are in the Toolbox
	private boolean inRect(Event event) {
		float x = event.x(), y = event.y();
		return x >= layer.tx() && x < layer.tx() + width && 
				y >= layer.ty() && y < layer.ty() + height;
	}
	
	/**
	 * Provide a callback for when a new number should be selected for the displayed Blocks 
	 * This should display a new {@link NumberSelectScreen}.
	 */
	interface NumberSelectListener {
		void selectNumber(int startNumber);
	}

	// for highlighting in the tutorial
	private final Highlighter highlighter = new Highlighter() {
		@Override
		protected void setTint(int baseColor, int tintColor, float perc) {
			backgroundLayer.setTint(baseColor, tintColor, perc);
		}
		
		@Override
		protected ColorState colorState() {
			return new ColorState() {
				@Override
				public void reset() {
					backgroundLayer.setTint(Colors.WHITE);
				}
			};
		}
	};
	
	@Override
	public Highlighter highlighter() {
		return highlighter;
	}
}
