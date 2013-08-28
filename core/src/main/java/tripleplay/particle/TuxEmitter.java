//
// Triple Play - utilities for use in PlayN-based games
// Copyright (c) 2011-2013, Three Rings Design, Inc. - All rights reserved.
// http://github.com/threerings/tripleplay/blob/master/LICENSE

package tripleplay.particle;

import java.util.List;

import playn.core.Image;
import playn.core.ImmediateLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.PlayN;
import playn.core.Surface;
import tripleplay.util.Destroyable;

/**
 * This class, as well as the other "Tux" particle classes allow for non-GL
 * rendering of particle effects (in a limited capacity). This class mirrors
 * the {@link Emitter} class.
 */
class TuxEmitter extends Emitter
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
    	// TODO: fix the commented-out portions so that particles can work in GL
    	// or non-GL modes. Currently we force non-GL mode because of bugs.
//    	if (!GLStatus.enabled()) {
        	layer.setVisible(false);
        	renderer = new CanvasParticleRenderer(image);
//    	} else {
//    		renderer = null;
//    	}
    }
    
    @Override
    public void update(float now, float dt) {
    	super.update(now, dt);
    	myLayer.setTranslation(layer.tx(), layer.ty());
    	myLayer.setDepth(layer.depth());
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
