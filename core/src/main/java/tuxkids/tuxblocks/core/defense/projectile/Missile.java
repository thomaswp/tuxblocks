package tuxkids.tuxblocks.core.defense.projectile;

import java.util.Random;

import playn.core.Image;
import tripleplay.particle.Emitter;
import tripleplay.particle.Generator;
import tripleplay.particle.effect.Alpha;
import tripleplay.particle.effect.Gravity;
import tripleplay.particle.effect.Move;
import tripleplay.particle.init.Color;
import tripleplay.particle.init.Lifespan;
import tripleplay.particle.init.Transform;
import tripleplay.particle.init.Velocity;
import tripleplay.util.Colors;
import tripleplay.util.Interpolator;
import tripleplay.util.Randoms;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.effect.Explosion;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;

public class Missile extends BodyProjectile {

	@Override
	public float maxSpeed() {
		return 0.1f;
	}

	@Override
	public Image createImage() {
		return assets().getImage(Constant.IMAGE_PATH + "missile.png");
	}

	@Override
	public float acceleration() {
		return 0.003f;
	}
	
	@Override
	public void onFinish() {
		super.onFinish();
//		int p = 30;
//		Emitter emitter = grid.createEmitter(p, CanvasUtils.createCircle(3, Colors.WHITE));
//		emitter.generator = Generator.impulse(30);
//        emitter.initters.add(Lifespan.constant(3));
//        emitter.initters.add(Color.constant(grid.towerColor()));
//        emitter.initters.add(Transform.layer(emitter.layer));
//        Randoms rando = Randoms.with(new Random());
//        emitter.initters.add(Velocity.randomCircle(rando, 50));
//        emitter.effectors.add(new Move());
//        emitter.effectors.add(Alpha.byAge(Interpolator.EASE_OUT, 1, 0));
//        emitter.destroyOnEmpty();
//        emitter.layer.setTranslation(position.x, position.y);
		Explosion e = new Explosion();
		e.position().set(position);
		grid.addEffect(e);
	}

}
