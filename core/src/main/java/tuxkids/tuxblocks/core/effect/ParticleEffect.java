package tuxkids.tuxblocks.core.effect;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import pythagoras.f.Vector;

public abstract class ParticleEffect extends Effect {
	protected int maxParitcles;
	protected Image image;
	protected int lifespan = 1000;
	
	private int timer;
	
	private List<Particle> particles = new ArrayList<Particle>(),
			toRemove = new ArrayList<Particle>();
	
	public ParticleEffect(int maxParticles, Image image) {
		this.image = image;
	}

	@Override
	public boolean update(int delta) {
		if (timer < lifespan) {
			particles.add(new Particle(image, layer));
		}
		
		toRemove.clear();
		for (Particle particle : particles) {
			if (particle.update(delta)) {
				toRemove.add(particle);
				layer.remove(particle.layer);
			}
		}
		particles.removeAll(toRemove);
		
		timer += delta;
		if (timer > lifespan && particles.size() == 0) {
			layer.destroy();
			return true;
		}
		return false;
	}

	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		for (Particle particle : particles) particle.paint(clock);
	}
	
	protected class Particle {
		public ImageLayer layer;
		public Vector velocity = new Vector();
		public int lifespan = 500;
		
		private int timer;
		
		public Particle(Image image, GroupLayer parent) {
			layer = graphics().createImageLayer(image);
			centerImageLayer(layer);
			parent.add(layer);
			double angle = Math.random() * Math.PI * 2;
			velocity.set((float)Math.cos(angle), (float)Math.sin(angle));
			velocity.scale((float)Math.random() * 0.1f, velocity);
		}
		
		protected boolean update(int delta) {
			timer += delta;
			return timer >= lifespan;
		}
		
		protected void paint(Clock clock) {
			layer.setTx(layer.tx() + velocity.x * clock.dt());
			layer.setTy(layer.ty() + velocity.y * clock.dt());
			velocity.y += 0.0001 * clock.dt();
		}
	}
}
