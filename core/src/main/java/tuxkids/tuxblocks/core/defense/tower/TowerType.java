package tuxkids.tuxblocks.core.defense.tower;

public enum TowerType {
	PeaShooter(new PeaShooter()),
	Freezer(new Freezer()),
	BigShooter(new BigShooter()),
	Zapper(new Zapper()),
	VerticalWall(new VerticalWall()),
	HorizontalWall(new HorizontalWall());
	
	private final Tower instance;
	
	public Tower instance() {
		return instance;
	}
	
	public Tower newInstance() {
		return instance.copy();
	}
	
	public int index() {
		return ordinal();
	}
	
	TowerType(Tower instance) {
		this.instance = instance;
	}
}