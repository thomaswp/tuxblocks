package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Image;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.solve.blocks.n.ModifierBlock;

public class ModifierBlockSprite extends BlockSprite {
	
	protected ModifierBlock block;
	
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

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paint(Clock clock) {
		// TODO Auto-generated method stub
		
	}
}
