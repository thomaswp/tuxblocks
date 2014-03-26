package tripleplay.particle;

import playn.core.GroupLayer;
import playn.core.Image;
import react.Slot;

/**
 * This class, as well as the other "Tux" particle classes allow for non-GL
 * rendering of particle effects (in a limited capacity). This class mirrors
 * the {@link Particles} class.
 */
public class TuxParticles extends Particles {


    /**
     * Creates an emitter that supports up to {@code maxParticles} particles at any one time.
     *
     * @param image the image to use for each particle.
     * @param onLayer the layer to which to add the layer which will render the particles.
     */
	@Override
    public Emitter createEmitter (int maxParticles, Image image, GroupLayer onLayer) {
    	final TuxEmitter emitter = new TuxEmitter(this, maxParticles, image);
        emitter._conn = _onPaint.connect(new Slot<Now>() { 
        	@Override
			public void onEmit (Now now) {
            emitter.update(now.time, now.dt);
        }});
        onLayer.add(emitter.layer);
        onLayer.add(emitter.myLayer);
        return emitter;
    }
}
