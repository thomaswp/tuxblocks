package tuxkids.tuxblocks.core.defense.tower;

/**
 * Enumerates the different types of {@link Tower}s in the
 * game.
 */
public enum TowerType {
	PeaShooter(new PeaShooter()),
	Freezer(new Freezer()),
	BigShooter(new BigShooter()),
	Zapper(new Zapper()),
	VerticalWall(new VerticalWall()),
	HorizontalWall(new HorizontalWall());
	
	private final Tower instance;
	
	/** 
	 * Returns an instance of this type of {@link Tower}. Do not
	 * store this instance as it is not a copy and modifying
	 * it will modify it for future calls to this method.
	 */
	public Tower instance() {
		return instance;
	}
	
	/** 
	 * Returns an new instance of this Type of {@link Tower}
	 * which can be stored and modified.
	 */
	public Tower newInstance() {
		return instance.copy();
	}
	
	TowerType(Tower instance) {
		this.instance = instance;
	}
}