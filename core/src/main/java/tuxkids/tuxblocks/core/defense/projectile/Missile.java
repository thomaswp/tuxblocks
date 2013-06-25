package tuxkids.tuxblocks.core.defense.projectile;

import java.util.Random;

import playn.core.Image;
import pythagoras.f.FloatMath;
import tripleplay.particle.Emitter;
import tripleplay.particle.Generator;
import tripleplay.particle.effect.Alpha;
import tripleplay.particle.effect.Gravity;
import tripleplay.particle.effect.Move;
import tripleplay.particle.init.Angle;
import tripleplay.particle.init.Color;
import tripleplay.particle.init.Lifespan;
import tripleplay.particle.init.ColorEffector;
import tripleplay.particle.init.TuxTransform;
import tripleplay.particle.init.TuxVelocity;
import tripleplay.particle.init.Velocity;
import tripleplay.util.Colors;
import tripleplay.util.Interpolator;
import tripleplay.util.Randoms;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.effect.MissileExplosion;
import tuxkids.tuxblocks.core.effect.anim.Animation;
import tuxkids.tuxblocks.core.effect.anim.AnimationEffect;
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
		new MissileExplosion(grid, position);
	}
}
