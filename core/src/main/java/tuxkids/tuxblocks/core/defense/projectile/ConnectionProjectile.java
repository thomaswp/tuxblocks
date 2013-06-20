package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.ImageLayer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class ConnectionProjectile extends Projectile {

	protected abstract int duration();
	
	private Vector direction = new Vector();
	private int timer;
	
	@Override
	public void place(Grid grid, Walker target, Tower source) {
		super.place(grid, target, source);
		layer.setOrigin(0, layer.height() / 2);
		layer.setTranslation(source.position().x, source.position().y);
	}
	
	@Override
	public boolean update(int delta) {
		timer += delta;
		if (timer > duration()) {
			target.damage(damage);
			layer.destroy();
			return true;
		}
		return false;
	}

	@Override
	public void paint(Clock clock) {
		direction.set(target.position());
		direction.subtract(source.position(), direction);
		layer.transform().setRotation(direction.angle());
		layer.transform().setScale(direction.length() / layer.image().width(), 0.1f);
	}

}
