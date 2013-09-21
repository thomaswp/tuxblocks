package tuxkids.tuxblocks.core.defense.tower;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.PlayN;
import pythagoras.f.Vector;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.Cache.Key;
import tuxkids.tuxblocks.core.defense.projectile.Lightning;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.HashCode;

/**
 * The most powerful Tower in the game, it shoots a chaining
 * {@link Lightning} projectile that deals damage to a string
 * of {@link Walker}.
 */
public class Zapper extends Tower {

	private Vector projectileStart = new Vector();
	private static ImageKey key = new ImageKey();
	
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
		return 5;
	}
	
	@Override
	protected float damagePerLevel() {
		return 1;
	}

	@Override
	public int fireRate() {
		// decreases with level
		return 1750 - 200 * (upgradeLevel - 1);
	}

	@Override
	public float range() {
		return 7;
	}

	@Override
	public Projectile createProjectile() {
		// hits, range and damage factors increase with level
		return new Lightning(3 + upgradeLevel, 
				0.5f + 0.1f * upgradeLevel, 
				0.5f + 0.1f * upgradeLevel);
	}

	@Override
	public Tower copy() {
		return new Zapper();
	}

	@Override
	public String name() {
		return "Zapper";
	}

	@Override
	public int cost() {
		return 3;
	}
	
	@Override
	public Vector projectileStart() {
		projectileStart.set(position().x, position().y() - height() * 0.4f);
		return projectileStart;
	}
	
	@Override
	public Image createImage(float cellSize, int color) {
		Image cached = Cache.getImage(key.set(cellSize, color));
		
		// special stacked tower image
		if (cached == null) {
			int width = (int)(cellSize * cols()), height = (int)(cellSize * rows() * 1.3f);
			int padding = (int)(cellSize * 0.1f); 
			int rad = (int)(Math.min(width, height) * 0.1f);
			CanvasImage image = PlayN.graphics().createImage(width, height);
			image.canvas().setFillColor(color);
			image.canvas().setStrokeColor(Colors.BLACK);
			
			float ratio = 1.3f;
			
			float w = width - padding * 2;
			float h = w / ratio;
			float y = height - h / 2 - padding;
			float x = width / 2;
			for (int i = 0; i < 3; i++) {
				
				image.canvas().fillRoundRect(x - w / 2, y - h / 2, w, h, rad);
				image.canvas().strokeRoundRect(x - w / 2 + 0.5f, y - h / 2 + 0.5f, w - 1, h - 1, rad);
				
				y -= h / 2;
				w -= cellSize / 5;
				h = w / ratio;
				
			}
			cached = Cache.putImage(key, image);
		}
		
		return cached;
	}

	// used to identify the image in the cache
	// cannot just store it statically, as it is dependent
	// on Grid size and color
	private static class ImageKey extends Key {

		private float cellSize;
		private int color;
		
		private ImageKey set(float cellSize, int color) {
			this.cellSize = cellSize;
			this.color = color;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(cellSize);
			hashCode.addField(color);
		}

		@Override
		public Key copy() {
			return new ImageKey().set(cellSize, color);
		}
		
	}

	@Override
	public int upgradeCost() {
		return 3;
	}
	
	@Override
	protected boolean fire() {
		boolean fire = super.fire();
		if (fire) {
			Audio.se().play(Constant.SE_ZAP);
		}
		return fire;
	}

	@Override
	public TowerType type() {
		return TowerType.Zapper;
	}
}
