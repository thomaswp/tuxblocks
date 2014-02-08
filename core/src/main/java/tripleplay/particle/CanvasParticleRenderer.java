package tripleplay.particle;

import static tripleplay.particle.ParticleBuffer.ALPHA_RED;
import static tripleplay.particle.ParticleBuffer.GREEN_BLUE;
import static tripleplay.particle.ParticleBuffer.M00;
import static tripleplay.particle.ParticleBuffer.M01;
import static tripleplay.particle.ParticleBuffer.M10;
import static tripleplay.particle.ParticleBuffer.M11;
import static tripleplay.particle.ParticleBuffer.NUM_FIELDS;
import static tripleplay.particle.ParticleBuffer.TX;
import static tripleplay.particle.ParticleBuffer.TY;

import java.util.List;

import playn.core.Color;
import playn.core.Image;
import playn.core.Surface;
import tripleplay.particle.TuxEmitter.ParticleRenderer;
import tripleplay.particle.init.ColorEffector;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.utils.PlayNObject;

/**
 * Renders particles from a {@link TuxParticles} group using ImageLayers instead of
 * a GL shader to make them HTML5 Canvas compliant. 
 */
class CanvasParticleRenderer extends PlayNObject implements ParticleRenderer {

	private ImageLayerTintable imageLayer;
	
	public CanvasParticleRenderer(Image image) {
		imageLayer = new ImageLayerTintable(image);
		centerImageLayer(imageLayer);
	}
	
	@Override
	public void render(Surface surface, ParticleBuffer buffer, Image image, List<? extends Effector> effectors, float now) {
		// get the ColorEffector for tinting later
		ColorEffector colorEffector = null;
		for (Effector effector : effectors) {
			if (effector instanceof ColorEffector) colorEffector = (ColorEffector) effector;
		}
		
		// mirrors the iteration of particles found in the ParticleBuffer class
		int pp = 0, ppos = 0, rendered = 0;
		float[] data = buffer.data;
        for (int aa = 0; aa < buffer.alive.length; aa++) {
            int live = buffer.alive[aa], mask = 1;
            for (int end = pp+32; pp < end; pp++, ppos += NUM_FIELDS, mask <<= 1) {
                if ((live & mask) == 0) continue;
            	int r = (int)(data[ppos + ALPHA_RED] % 256.0);
                int a = Math.min(((int)data[ppos + ALPHA_RED] - r), 255);
                if (a == 0) continue;
                
                surface.save();
                surface.transform(data[ppos + M00], data[ppos + M01], data[ppos + M10], data[ppos + M11], data[ppos + TX], data[ppos + TY]);
                
                if (colorEffector != null) {
                	imageLayer.setTint(colorEffector.startColor(), colorEffector.endColor(), 
                			1 - colorEffector.getPerc(data, ppos, now));
                } else {
                    int b = (int)(data[ppos + GREEN_BLUE] % 256.0);
                    int g = Math.min(((int)data[ppos + GREEN_BLUE] - b), 255);
	                imageLayer.setTint(Color.rgb(r, g, b));
//	                debug("Shown: %d %d %d %d", a, r, g, b);
                }
                imageLayer.setAlpha(a);
                surface.drawLayer(imageLayer.layerAddable());
                rendered++;
                surface.restore();
            }
        }
        buffer._live = rendered;
	}

}
