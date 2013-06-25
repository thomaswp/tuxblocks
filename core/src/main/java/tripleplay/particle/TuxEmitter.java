//
// Triple Play - utilities for use in PlayN-based games
// Copyright (c) 2011-2013, Three Rings Design, Inc. - All rights reserved.
// http://github.com/threerings/tripleplay/blob/master/LICENSE

package tripleplay.particle;

import java.util.ArrayList;
import java.util.List;

import react.Connection;
import react.Signal;
import react.UnitSlot;
import playn.core.Image;
import playn.core.ImmediateLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.PlayN;
import playn.core.Surface;
import static playn.core.PlayN.graphics;
import tripleplay.particle.Particles.Now;
import tripleplay.util.Destroyable;

/**
 * Emits and updates particles according to a particle system configuration.
 */
public class TuxEmitter extends Emitter
    implements Destroyable
{

	public final ImmediateLayer myLayer;
	public final ParticleRenderer renderer;
	
	protected float now;
	
    TuxEmitter (Particles parts, final int maxParticles, final Image image) {
    	super(parts, maxParticles, image);
    	myLayer = PlayN.graphics().createImmediateLayer(new Renderer() {
			@Override
			public void render(Surface surface) {
				if (renderer != null) renderer.render(surface, _buffer, image, effectors, now);
			}
		});
    	if (!GLStatus.enabled()) {
        	layer.setVisible(false);
        	renderer = new CanvasParticleRenderer(image);
    	} else {
    		renderer = null;
    	}
    }
    
    @Override
    public void update(float now, float dt) {
    	super.update(now, dt);
    	myLayer.setTranslation(layer.tx(), layer.ty());
    	this.now = now;
    }
    
    @Override
    public void destroy() {
    	super.destroy();
    	myLayer.destroy();
    }
    
	public interface ParticleRenderer {

		void render(Surface surface, ParticleBuffer buffer, Image image,
				List<? extends Effector> effectors, float now);
	}
}
