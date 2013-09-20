package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

/**
 * Abstract class representing the projectiles fired from
 * {@link Tower}s at {@link Walker}. These are kept track
 * of by the {@link Grid}.
 */
public abstract class Projectile extends GridObject {

	protected ImageLayer layer;
	protected Walker target; // walker to damage
	protected float damage; // damage to deal
	protected Tower source; // tower that fired it
	
	public abstract Image createImage();
	
	public Layer layer() {
		return layer;
	}
	
	public Walker target() {
		return target;
	}
	
	public Tower source() {
		return source;
	}
	
	public void place(Grid grid, Walker target, Tower source) {
		place(grid);
		this.target = target;
		this.source = source;
		this.damage = source.damage();
		layer = graphics().createImageLayer(createImage());
		// make sure it's in front of all Walkers
		layer.setDepth(grid.rows() * grid.cols() * MAX_BASE_DEPTH);
	}
	
	protected abstract boolean doUpdate(int delta);
	
	@Override
	public final boolean update(int delta) {
		if (doUpdate(delta)) {
			onFinish();
			return true;
		}
		return false;
	}

	protected void onFinish() {
		layer.destroy();
	}
	
	protected void dealDamage() {
		grid.dealDamage(source, target, damage, getHitPosition());
	}
	
	/** Returns the taget position */
	protected Vector getHitPosition() {
		return target.position();
	}
}
