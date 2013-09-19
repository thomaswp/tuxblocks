package tuxkids.tuxblocks.core.defense.effect;

import java.util.Random;

import pythagoras.f.Vector;
import tripleplay.particle.Emitter;
import tripleplay.particle.Generator;
import tripleplay.particle.effect.Alpha;
import tripleplay.particle.effect.Move;
import tripleplay.particle.init.Angle;
import tripleplay.particle.init.Color;
import tripleplay.particle.init.Lifespan;
import tripleplay.particle.init.TuxTransform;
import tripleplay.particle.init.TuxVelocity;
import tripleplay.util.Colors;
import tripleplay.util.Interpolator;
import tripleplay.util.Randoms;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class MissileExplosion {
	
	public MissileExplosion(Grid grid, Vector position, int level) {
		int p = 30;
		Emitter emitter = grid.createEmitter(p, CanvasUtils.createCircleCached(2 + level, grid.towerColor()));
		emitter.generator = Generator.impulse(p);
        emitter.initters.add(Lifespan.constant(1));
        emitter.initters.add(Color.constant(Colors.WHITE));
        emitter.initters.add(TuxTransform.layer(emitter.layer));
        Randoms rando = Randoms.with(new Random());
        emitter.initters.add(TuxVelocity.randomCircle(rando, 50 + level * 20));
        emitter.effectors.add(new Move());
        emitter.effectors.add(Alpha.byAge(Interpolator.EASE_OUT, 1, 0));
        emitter.destroyOnEmpty();
        emitter.layer.setTranslation(position.x, position.y);
        
        emitter = grid.createEmitter(10, CanvasUtils.createRectCached(5 + level, 1, Colors.WHITE));
        emitter.generator = Generator.impulse(10);
        emitter.initters.add(Lifespan.constant(1));
        emitter.initters.add(Color.constant(Colors.BLACK));
        emitter.initters.add(TuxTransform.layer(emitter.layer));
        emitter.initters.add(TuxVelocity.randomCircle(rando, 50 + level * 20, 50 + level * 20));
        emitter.initters.add(Angle.byVelocity());
        emitter.effectors.add(new Move());
        emitter.effectors.add(Alpha.byAge(Interpolator.EASE_OUT, 1, 0));
        emitter.destroyOnEmpty();
        emitter.layer.setTranslation(position.x, position.y);
	}
}
