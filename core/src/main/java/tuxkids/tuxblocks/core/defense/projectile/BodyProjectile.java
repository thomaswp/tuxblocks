package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.util.Clock;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

/**
 * Abstract class for {@link Projectile} that have a discrete
 * position and velocity and follow a target. 
 */
public abstract class BodyProjectile extends Projectile {
	
	protected Vector position;
	protected Vector velocity;
	
	private Vector temp = new Vector();
	
	public abstract float maxSpeed();
	public abstract float acceleration();
	
	@Override
	public void place(Grid grid, Walker target, Tower source) {
		super.place(grid, target, source);
		centerImageLayer(layer);
		
		this.position = source.projectileStart().clone();
		velocity = new Vector();
		update(0);
	}
	
	@Override
	public boolean doUpdate(int delta) {
		Walker hit = grid.getHitWalker(position);
		if (hit != null) {
			// hit!
			dealDamage();
			return true;
		}
		if (grid.isOutOfBounds(position)) {
			// give up
			return true;
		}
		if (target.isAlive()) {
			// chase the target
			target.position().subtract(position, temp);
			temp.normalizeLocal();
			layer.setRotation(temp.angle());
			velocity.addScaled(temp, acceleration() * delta * 30, velocity);
			if (velocity.length() > maxSpeed()) velocity.scale(maxSpeed() / velocity.length(), velocity);
		} else if (velocity.length() == 0) {
			// if the target it dead and we're not moving, give up
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
	
	@Override
	protected Vector getHitPosition() {
		return position;
	}
}
