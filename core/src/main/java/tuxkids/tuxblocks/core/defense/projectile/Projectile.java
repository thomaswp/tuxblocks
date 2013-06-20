package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class Projectile extends GridObject {

	protected Grid grid;
	protected ImageLayer layer;
	protected Walker target;
	protected int damage;
	protected Tower source;
	
	public abstract Image createImage();
	
	public Layer layer() {
		return layer;
	}
	
	public void place(Grid grid, Walker target, Tower source) {
		this.grid = grid;
		this.target = target;
		this.source = source;
		this.damage = source.damage();
		layer = graphics().createImageLayer(createImage());
	}

	protected void onFinish() {
		layer.destroy();
	}
}
