package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class Projectile extends GridObject {
	
	protected Grid grid;
	protected Vector position;
	protected Vector velocity;
	protected ImageLayer layer;
	protected Walker target;
	
	public Layer layer() {
		return layer;
	}
	
	public abstract float speed();
	public abstract ImageLayer createLayer();
	
	public void place(Grid grid, Walker target, Vector position) {
		this.grid = grid;
		this.target = target;
		this.position = position;
		velocity = new Vector();
		target.position().subtract(position, velocity);
		if (velocity.length() > 0) velocity = velocity.scale(speed() / velocity.length());
		layer = createLayer();
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
	}
	
	@Override
	public boolean update(int delta) {
		float dx = target.position().x - position.x;
		float dy = target.position().y - position.y;
		if (Math.abs(dx) < target.width() / 2 && Math.abs(dy) < target.height() / 2) {
			layer.destroy();
			debug("!");
			return true;
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
