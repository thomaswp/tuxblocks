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

public abstract class BodyProjectile extends Projectile {
	
	protected Vector position;
	protected Vector velocity;
	
	private Vector temp = new Vector();
	
	public abstract float maxSpeed();
	public abstract float acceleration();
	
	public void place(Grid grid, Walker target, Tower source) {
		super.place(grid, target, source);
		centerImageLayer(layer);
		
		this.position = source.position().clone();
		velocity = new Vector();
		update(0);
	}
	
	@Override
	public boolean update(int delta) {
		Walker hit = grid.getHitWalker(position);
		if (hit != null) {
			target.damage(damage);
			onFinish();
			return true;
		}
		if (grid.isOutOfBounds(position)) {
			onFinish();
			return true;
		}
		if (target.isAlive()) {
			target.position().subtract(position, temp);
			temp.normalizeLocal();
			layer.setRotation(temp.angle());
			velocity.addScaled(temp, acceleration() * delta * 30, velocity);
			if (velocity.length() > maxSpeed()) velocity.scale(maxSpeed() / velocity.length(), velocity);
		} else if (velocity.length() == 0) {
			onFinish();
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
