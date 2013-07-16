package tuxkids.tuxblocks.core.solve.blocks.n;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockGroup<T extends ModifierBlock> {

	protected ArrayList<T> blocks = new ArrayList<T>();
	protected BlockGroup<?> modifiers;
	protected BlockGroup<?> parent;
	protected BaseBlock base;
	
	protected abstract BlockGroup<?> createModifiers();
	protected abstract boolean canAdd(ModifierBlock block);
	
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
	
	@SuppressWarnings("unchecked")
	public void addModifier(ModifierBlock block) {
		if (canAdd(block)) {
			if (modifiers == null) {
				blocks.add((T) block);
				block.group = this;
			} else {
				modifiers.addModifier(block);
			}
		} else {
			if (modifiers == null) {
				modifiers = createModifiers();
				modifiers.parent = this;
			}
			modifiers.addModifier(block);
		}
	}	

	protected String toMathString(String base) {
		for (ModifierBlock block : blocks) {
			base = block.toMathString(base);
		}
		if (modifiers != null) base = modifiers.toMathString(base);
		return base;
	}
}
