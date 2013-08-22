package tripleplay.particle.init;

import playn.core.Layer;
import tripleplay.particle.Initializer;
import tripleplay.particle.ParticleBuffer;
import tripleplay.particle.TuxParticles;

/**
 * A version of the {@link Transform} Initter, which works correctly with
 * {@link TuxParticles} and non-GL platforms. 
 */
public class TuxTransform extends Transform {


    /**
     * Returns an initializer that configures a particle with the same transform (scale, rotation,
     * position) as the supplied layer. This will the the fully computed transform, not the layer's
     * local transform.
     */
    public static Initializer layer (final Layer layer) {
        return new Initializer() {
            @Override public void willInit (int count) {
            	// TODO: fix the commented-out portions so that particles can work in GL
            	// or non-GL modes. Currently we force non-GL mode because of bugs.
            	
//            	if (!GLStatus.enabled()) {
            		_matrix[0] = 1; _matrix[3] = 1;
            		return;
//            	}
            	
//                // concatenate the transform of all layers above our target layer
//                xform.setTransform(1, 0, 0, 1, 0, 0);
//                Layer xlayer = layer;
//                while (xlayer != null) {
//                    xform.preConcatenate((InternalTransform)xlayer.transform());
//                    xlayer = xlayer.parent();
//                }
//                // finally pre-concatenate the root transform as we're bypassing normal rendering
//                xform.preConcatenate(graphics().ctx().rootTransform());
//                xform.get(_matrix);
            }
            @Override public void init (int index, float[] data, int start) {
                System.arraycopy(_matrix, 0, data, start + ParticleBuffer.M00, 6);
            }
//            protected final InternalTransform xform = GLStatus.enabled() ? graphics().ctx().createTransform() : null;
            protected final float[] _matrix = new float[6];
        };
    }
}
