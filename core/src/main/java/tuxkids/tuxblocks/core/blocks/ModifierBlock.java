package tuxkids.tuxblocks.core.blocks;

import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.PlayN;
import tuxkids.tuxblocks.core.eqn.Expression;
import tuxkids.tuxblocks.core.eqn.ModificationOperation;

public class ModifierBlock extends Block {
	
	private Block baseBlock;
	private ModificationOperation op;
	private float myWidth, myHeight, width, height;
	private ImageLayer mySprite;
	
	public ModifierBlock(Block baseBlock, ModificationOperation op) {
		this.baseBlock = baseBlock;
		this.op = op;
		if (op.getPrecedence() == Expression.PREC_ADD) {
			myWidth = NUM_SIZE / 3; //baseBlock.getHeight() * RECT_RATIO;
			myHeight = baseBlock.getHeight();
			width = baseBlock.getWidth() + myWidth;
			height = baseBlock.getHeight();
		} else {
			myWidth = baseBlock.getWidth();
			myHeight = NUM_SIZE / 3; //baseBlock.getWidth() * RECT_RATIO;
			width = baseBlock.getWidth();
			height = baseBlock.getHeight() + myHeight;
		}
		mySprite = generateSprite((int)myWidth, (int)myHeight);
		GroupLayer group = PlayN.graphics().createGroupLayer();
		sprite = group;
		group.add(mySprite);
		group.add(baseBlock.getSprite());
		if (op.getPrecedence() == Expression.PREC_ADD) {
			mySprite.setTranslation(baseBlock.getWidth(), 0);
		} else {
			baseBlock.getSprite().setTranslation(0, mySprite.height());
		}
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public int getColor() {
		return op.getColor();
	}

	@Override
	public String getText() {
		return String.format("%s%d", op.getSymbol(), op.getValue());
	}
}
