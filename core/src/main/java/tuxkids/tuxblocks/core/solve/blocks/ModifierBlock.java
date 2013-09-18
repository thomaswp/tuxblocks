package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.util.Clock;
import tuxkids.tuxblocks.core.utils.HashCode;

/**
 * A {@link Block} which modifies a {@link BaseBlock} with an
 * operation, such as addition or multiplication.
 */
public abstract class ModifierBlock extends Block {
	
	protected ModifierGroup group;
	protected int value;
	protected ModifierBlock inverse;
	
	protected abstract String operator();
	protected abstract ModifierBlock inverseChild();
	
	/** Returns the ModifierGroup of which this Block is a part, or null if none. */
	public ModifierGroup group() {
		return group;
	}
	
	@Override
	public final ModifierBlock inverse() {
		return inverse;
	}
	
	protected ModifierBlock(int value) {
		this.value = value;
		inverse = inverseChild();
	}
	
	protected ModifierBlock(ModifierBlock inverse) {
		this.value = inverse.value;
		this.inverse = inverse;
		if (inverse.blockListener != null) {
			addBlockListener(inverse.blockListener);
		}
	}
	
	@Override
	protected void initSpriteImpl() {
		super.initSpriteImpl();
		
		layer = generateImage(text());
		if (inverse.layer == null) inverse.initSprite();
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		if (group == null) {
			interpolateDefaultRect(clock);
		}
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		super.addBlockListener(listener);
		if (listener != null && inverse.blockListener == null) {
			inverse.addBlockListener(listener);
		}
	}
	
	@Override
	public void showInverse() {
		if (!hasSprite()) return;
		layer.setVisible(false);
		Block inverse = inverse();
		inverse.layer.setVisible(true);
		inverse.interpolateRect(x(), y(), width(), height(), 0, 1);
		inverse.layer().setTranslation(layer().tx(), layer().ty());
	}
	
	@Override
	public void remove() {
		if (group != null) group.removeChild(this);
		group = null;
	}

	@Override
	public String text() {
		return operator() + value;
	}
	
	public boolean canSimplify() {
		if (group == null) return false;
		return group.children.contains(inverse);
	}

	public boolean canAddInverse() {
		if (group == null) return false;
		return !canSimplify() && group.modifiers == null;
	}
	
	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(value);
	}
	
	protected void destroy(boolean destroyInverse) {
		super.destroy();
		if (destroyInverse && inverse != null && !inverse.destroyed()) {
			inverse.destroy();
		}
	}
	
	@Override
	public void destroy() {
		destroy(true);
	}
	
	public void setValue(int value) {
		if (this.value == value) return;
		this.value = value;
		if (hasSprite()) {
			layer.setText(text());
		}
		inverse.setValue(value);
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		value = data.persist(value);
		if (!data.writeMode()) {
			setValue(value);
		}
	}
}
