package tuxkids.tuxblocks.core.defense.round;

import tuxkids.tuxblocks.core.defense.tower.TowerType;

public class Reward {
	public final TowerType tower;
	public final int count;
	
	public Reward(TowerType tower, int count) {
		this.tower = tower;
		this.count = count;
	}
}
