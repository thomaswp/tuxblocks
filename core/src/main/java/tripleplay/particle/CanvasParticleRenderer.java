package tripleplay.particle;

import java.util.List;

import playn.core.Color;
import playn.core.Image;
import playn.core.Surface;
import tripleplay.particle.TuxEmitter.ParticleRenderer;
import tripleplay.particle.init.ColorEffector;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import static tripleplay.particle.ParticleBuffer.*;

public class CanvasParticleRenderer extends PlayNObject implements ParticleRenderer {

	private ImageLayerTintable imageLayer;
	
	public CanvasParticleRenderer(Image image) {
		imageLayer = new ImageLayerTintable(image);
		centerImageLayer(imageLayer);
	}
	
	@Override
	public void render(Surface surface, ParticleBuffer buffer, Image image, List<? extends Effector> effectors, float now) {
		ColorEffector colorEffector = null;
		for (Effector effector : effectors) {
			if (effector instanceof ColorEffector) colorEffector = (ColorEffector) effector;
		}
		
		int pp = 0, ppos = 0, rendered = 0;
		float[] data = buffer.data;
        for (int aa = 0; aa < buffer.alive.length; aa++) {
            int live = buffer.alive[aa], mask = 1;
            for (int end = pp+32; pp < end; pp++, ppos += NUM_FIELDS, mask <<= 1) {
                if ((live & mask) == 0) continue;
                if (data[ppos + ALPHA] == 0) continue;
                
                surface.save();
                surface.transform(data[ppos + M00], data[ppos + M01], data[ppos + M10], data[ppos + M11], data[ppos + TX], data[ppos + TY]);
                
                if (colorEffector != null) {
                	imageLayer.setTint(colorEffector.startColor(), colorEffector.endColor(), 
                			1 - colorEffector.getPerc(data, ppos, now));
                } else {
	                imageLayer.setTint(Color.rgb(
	                		(int)(255 * data[ppos + RED]),
	                		(int)(255 * data[ppos + GREEN]),
	                		(int)(255 * data[ppos + BLUE])
	                ));
                }
                imageLayer.setAlpha(data[ppos + ALPHA]);
                surface.drawLayer(imageLayer.layer());
                rendered++;
                surface.restore();
            }
        }
        buffer._live = rendered;
	}

}
