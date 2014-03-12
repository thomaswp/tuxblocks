package tuxkids.tuxblocks.core.solve.blocks;

import java.util.List;

import playn.core.PlayN;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.PlayNObject;

public abstract class EquationManipulator extends PlayNObject {

	public enum Side {
		Left, Right;
	}

	protected boolean inBuildMode; // true if this is being hosted by a BuildScreen

	protected MutableEquation equation; // Holds all blocks in the equation being manipulated
	protected Block dragging, tempDragging; // which block is currently dragging
	protected BaseBlock draggingFrom, tempDraggingFrom; // which BaseBlock the currently dragging Block is coming from
	protected List<BaseBlock> draggingFromSide; // which side the currently dragging Block is coming from
	protected boolean inverted; // is the currently dragging Block inverted (past the equals)

	protected abstract boolean hasSprites();

	protected void triggerTutorial(Trigger trigger) {

	}

	protected List<BaseBlock> leftSide() {
		return equation.leftSideList();
	}

	protected List<BaseBlock> rightSide() {
		return equation.rightSideList();
	}

	protected List<BaseBlock> getBlocks(Side side) {
		return side == Side.Left ? leftSide() : rightSide();
	}

	protected List<BaseBlock> getOpposite(List<BaseBlock> side) {
		return side == rightSide() ? leftSide() : rightSide();
	}

	protected List<BaseBlock> getContaining(BaseBlock block) {
		return leftSide().contains(block) ? leftSide() : rightSide();
	}

	protected void swapExpression(List<BaseBlock> side, BaseBlock original, BaseBlock newExp) {
		int index = side.indexOf(original);
		side.remove(index);
		side.add(index, newExp);
	}

	public boolean dragBlock(Block sprite) {

		// find the BaseBlock of the Sprite that was grabbed 
		for (BaseBlock base : equation) {
			if (base.contains(sprite)) {
				draggingFrom = base;
				break;
			}
		}

		if (draggingFrom == null) {
			if (inBuildMode && hasSprites()) {
				// The happens when we drag from the ToolBox, so we copy the dragging Sprite
				Block nSprite = (Block) sprite.copy(true);
				nSprite.layer().setTranslation(sprite.layer().tx(), sprite.layer().ty());
				nSprite.interpolateDefaultRect(null);
				sprite = nSprite;
			} else {
				// This happens when the dragging block isn't a part of any BaseBlock's group
				// which shouldn't be able to happen but somehow has... so we cancel everything
				debug("BIG PROBLEM!");
				sprite.cancelDrag();
				PlayN.pointer().cancelLayerDrags();
				return false;
			}
		}

		draggingFromSide = getContaining(draggingFrom);


		if (sprite == draggingFrom) {
			// we're picking up a BaseBlock, so replace it with a BlockHolder
			BlockHolder holder = new BlockHolder();
			swapExpression(draggingFromSide, draggingFrom, holder);
			draggingFrom = holder;
		}

		// get the dragging Sprite and add it to our layer
		dragging = sprite.getDraggingSprite();

		// remove the sprite from its group
		sprite.remove();

		return true;
	}

	protected Block dropBlock(BaseBlock target) {

		Block added = null;
		if (target == null) {
			if (!inBuildMode) debug("BIG PROBLEM!");
			// delete the sprite if it's dropped onto the toolbox
			dragging.destroy();
		} else {
			if (target instanceof BlockHolder) {
				// if dropped on a BlockHolder, replace it with the dropped Block

				if (dragging instanceof VerticalModifierBlock) {
					// 0 * or / n = 0
					dragging.destroy();
				} else {
					if (dragging instanceof HorizontalModifierBlock) {
						// turn a HorizontalModifier into a NumberBlock
						NumberBlockProxy proxy = ((HorizontalModifierBlock) dragging).getProxy(false);
						if (hasSprites()) dragging.layer().setVisible(false);
						dragging = proxy;
					} else if (dragging instanceof BaseBlock) {
						ModifierGroup mods = ((BaseBlock) dragging).modifiers;
						if (mods.isModifiedHorizontally() || mods.isModifiedVertically() || mods.children.size() > 0) {
							triggerTutorial(Trigger.Solve_BlockWithModifiersReleasedOnBlank);
						}
					}

					swapExpression(getContaining(target), target, (BaseBlock) dragging);
					if (hasSprites()) target.layer().destroy();
					added = dragging;
				}

				triggerTutorial(Trigger.Solve_BlockReleasedOnBlank);
			} else {
				if (dragging instanceof VariableBlock && target instanceof VariableBlock) {
					triggerTutorial(Trigger.Solve_VariablesStartedCombine);
				}

				added = target.addBlock(dragging, false);
				if (added == null) {
					// this happens when the drop requires a trip to the SolveScreen to resolve
					// when we get back, we use these values to reset the Block if the solve failed
					tempDragging = dragging;
					tempDraggingFrom = draggingFrom;
				}
			}
		}

		dragging = null;
		draggingFrom = null;
		return added;
	}

	public void invertBlock(Block sprite) {
		if (sprite instanceof VerticalModifierBlock) {
			if (!((ModifierBlock) sprite).canAddInverse()) return;
			triggerTutorial(Trigger.Solve_VerticalModifierDoubleClicked);

			// add a Times or OverBlock to each BaseBlock to cancel out the
			// one that was double-clicked

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
			for (BaseBlock base : equation) {
				if (!(base instanceof BlockHolder)) {
					ModifierBlock inverse = (ModifierBlock) ((VerticalModifierBlock) sprite).inverse().copy(true);
					if (hasSprites()) inverse.interpolateRect(base.offsetX(), y, base.totalWidth(), inverse.height(), 0, 1);
					base.addModifier(inverse, false);
				}
			}
		}
	}
}
