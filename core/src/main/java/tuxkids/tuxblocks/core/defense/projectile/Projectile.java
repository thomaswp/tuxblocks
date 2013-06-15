package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class Projectile extends GridObject {
	
	protected Grid grid;
	protected Vector position;
	protected Vector velocity;
	protected ImageLayer layer;
	protected Walker target;
	protected int damage;
	
	public Layer layer() {
		return layer;
	}
	
	public abstract float speed();
	public abstract ImageLayer createLayer();
	
	public void place(Grid grid, Walker target, Tower source) {
		this.grid = grid;
		this.target = target;
		this.position = source.position().clone();
		this.damage = source.damage();
		velocity = new Vector();
		target.position().subtract(position, velocity);
		if (velocity.length() > 0) velocity = velocity.scale(speed() / velocity.length());
		layer = createLayer();
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
	}
	
	@Override
	public boolean update(int delta) {
		Walker hit = grid.getHitWalker(position);
		if (hit != null) {
			layer.destroy();
			target.damage(damage);
			return true;
		}
		if (grid.isOutOfBounds(position)) {
			layer.destroy();
			return true;
		}
		if (target.isAlive()) {
			target.position().subtract(position, velocity);
			if (velocity.length() > 0) velocity = velocity.scale(speed() / velocity.length());
		}
		return false;
	}
	
	@Override
	public void paint(Clock clock) {
		position.x += velocity.x * clock.dt();
		position.y += velocity.y * clock.dt();
		layer.setTranslation(position.x, position.y);
	}
}
