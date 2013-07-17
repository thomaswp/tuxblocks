package tuxkids.tuxblocks.core.solve.blocks.n;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.utils.Debug;

public abstract class BlockGroup<T extends ModifierBlock> {

	protected ArrayList<T> blocks = new ArrayList<T>();
	protected BlockGroup<?> modifiers;
	protected BlockGroup<?> parent;
	protected BaseBlock base;
	
	protected abstract BlockGroup<?> createModifiers();
	public abstract boolean canAdd(ModifierBlock block);
	
	public List<T> blocks() {
		return blocks;
	}
	
	public BlockGroup<?> modifiers() {
		return modifiers;
	}
	
	public boolean modifiesNumber() {
		if (base != null) return base.modifiesNumber();
		return parent.blocks.size() == 0 && parent.modifiesNumber();
	}
	
	public boolean isModifiedHorizontally() {
		if (modifiers == null) return false;
		if (modifiers.blocks.size() > 0 && modifiers.blocks.get(0) instanceof HorizontalBlock) return true;
		return modifiers.isModifiedHorizontally();
	}
	
	public boolean isModifiedVertically() {
		if (modifiers == null) return false;
		if (modifiers.blocks.size() > 0 && modifiers.blocks.get(0) instanceof VerticalBlock) return true;
		return modifiers.isModifiedVertically();
	}
	
	public void addVerticalModifiers(List<VerticalBlock> mods) {
		if (parent == null) return;
		for (ModifierBlock b : parent.blocks) {
			if (b instanceof VerticalBlock) {
				mods.add((VerticalBlock) b);
			} else {
				break;
			}
		}
		parent.addVerticalModifiers(mods);
		
	}
	
	public void forceCreateModifiers() {
		modifiers = createModifiers();
	}
	
	@SuppressWarnings("unchecked")
	public BlockGroup<?> addModifierToExpression(ModifierBlock block) {
		if (canAdd(block)) {
			if (modifiers == null) {
				blocks.add((T) block);
				block.group = this;
				return this;
			} else {
				return modifiers.addModifierToExpression(block);
			}
		} else {
			if (modifiers == null) {
				modifiers = createModifiers();
				modifiers.parent = this;
			}
			return modifiers.addModifierToExpression(block);
		}
	}	

	protected String toMathString(String base) {
		for (ModifierBlock block : blocks) {
			base = block.toMathString(base);
		}
		if (modifiers != null) base = modifiers.toMathString(base);
		return base;
	}
	
	@SuppressWarnings("unchecked")
	public void addBlock(ModifierBlock block) {
		Debug.write("added %s", block.text());
		blocks.add((T) block);
		block.group = this;
	}
	
	public void removeBlock(ModifierBlock block) {
		blocks.remove(block);
		block.group = null;
	}
}
