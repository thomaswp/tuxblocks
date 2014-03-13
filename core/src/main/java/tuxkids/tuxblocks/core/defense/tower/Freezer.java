package tuxkids.tuxblocks.core.defense.tower;

import playn.core.Image;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.projectile.Snow;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.defense.walker.buff.Buff;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/**
 * A mid-grade utility Tower with low range that slows {@link Walker}s
 * when it hits them with its {@link Snow} projectile.
 */
public class Freezer extends Tower {

	@Override
	public int rows() {
		return 1;
	}

	@Override
	public int cols() {
		return 1;
	}

	@Override
	protected float baseDamage() {
		return 0.3f;
	}
	
	@Override
	protected float damagePerLevel() {
		return 0.15f;
	}

	@Override
	public int fireRate() {
		return 100;
	}

	@Override
	public float range() {
		return 3.1f;
	}

	@Override
	public Projectile createProjectile() {
		return new Snow();
	}

	@Override
	public Tower copy() {
		return new Freezer();
	}

	@Override
	public String nameKey() {
		return "freezer";
	}

	@Override
	public int cost() {
		return 2;
	}
	
	@Override
	public float splashRadius() {
		// small splash, increases with level
		return 0.5f + 0.2f * upgradeLevel;
	}

	@Override
	public void addBuffs(Walker walker) {
		// slow increases with level
		final float mod = 0.65f - 0.10f * (upgradeLevel - 1);
		walker.addBuff(new Buff(this) {
			@Override
			public float modifySpeed(float dt) {
				return dt * mod;
			}
			
			@Override
			protected int lifespan() {
				return 1000;
			}
		}, true);
	}

	@Override
	public int upgradeCost() {
		return 2;
	}
	
	/*
	 * Because the Freezer fires so quickly, rather than firing the sound effect
	 * every time it fires, it simply starts it playing and then stops it when
	 * it stops firing.
	 */
	
	private static int winds;
	private int windTime;
	private boolean blowing;
	@Override
	protected boolean fire() {
		boolean fire = super.fire();
		if (fire) {
			windTime = 500;
			if (winds == 0 || !Audio.se().isPlaying(Constant.SE_WIND)) {
				Audio.se().play(Constant.SE_WIND);
			}
			if (!blowing) {
				winds++;
				blowing = true;
			}
		}
		return fire;
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		if (blowing) {
			windTime -= clock.dt();
			if (windTime < 0) {
				blowing = false;
				winds--;
				if (winds == 0) {
					Audio.se().stop(Constant.SE_WIND);
				}
			}
		}
	}

	@Override
	public TowerType type() {
		return TowerType.Freezer;
	}
	
	@Override
	public Image createImage(float cellSize, int color) {
		return CanvasUtils.createCircleCached(cellSize / 2, color, 1, Colors.BLACK);
	}
}
